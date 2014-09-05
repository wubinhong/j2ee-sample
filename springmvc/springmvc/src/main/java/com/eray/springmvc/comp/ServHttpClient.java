package com.eray.springmvc.comp;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.eray.springmvc.GlobalConstant.Encoding;
import com.eray.springmvc.exception.ServException;
import com.eray.springmvc.exception.UtilException;
import com.eray.springmvc.web.Message;
import com.eray.springmvc.web.Message.Status;

/**
 * Provide service for HTTP client. Default encoding is UTF-8 for request data or reponse data.<br>
 * The following parameters can be used to customize the behavior of this
 * class:
 * <ul>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#PROTOCOL_VERSION}</li>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#STRICT_TRANSFER_ENCODING}</li>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#HTTP_ELEMENT_CHARSET}</li>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#USE_EXPECT_CONTINUE}</li>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#WAIT_FOR_CONTINUE}</li>
 *  <li>{@link org.apache.http.params.CoreProtocolPNames#USER_AGENT}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#TCP_NODELAY}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#SO_TIMEOUT}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#SO_LINGER}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#SO_REUSEADDR}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#SOCKET_BUFFER_SIZE}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#CONNECTION_TIMEOUT}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#MAX_LINE_LENGTH}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#MAX_HEADER_COUNT}</li>
 *  <li>{@link org.apache.http.params.CoreConnectionPNames#STALE_CONNECTION_CHECK}</li>
 *  <li>{@link org.apache.http.conn.params.ConnRoutePNames#FORCED_ROUTE}</li>
 *  <li>{@link org.apache.http.conn.params.ConnRoutePNames#LOCAL_ADDRESS}</li>
 *  <li>{@link org.apache.http.conn.params.ConnRoutePNames#DEFAULT_PROXY}</li>
 *  <li>{@link org.apache.http.cookie.params.CookieSpecPNames#DATE_PATTERNS}</li>
 *  <li>{@link org.apache.http.cookie.params.CookieSpecPNames#SINGLE_COOKIE_HEADER}</li>
 *  <li>{@link org.apache.http.auth.params.AuthPNames#CREDENTIAL_CHARSET}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#COOKIE_POLICY}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#HANDLE_AUTHENTICATION}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#HANDLE_REDIRECTS}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#MAX_REDIRECTS}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#ALLOW_CIRCULAR_REDIRECTS}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#VIRTUAL_HOST}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#DEFAULT_HOST}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#DEFAULT_HEADERS}</li>
 *  <li>{@link org.apache.http.client.params.ClientPNames#CONN_MANAGER_TIMEOUT}</li>
 * </ul>
 * @author shifengxuan
 *
 */
public class ServHttpClient {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected HttpClient httpClient;
	
	protected HttpContext httpContext;
	
	protected BasicCookieStore cookieStore;
	
	private String defaultEncoding = Encoding.UTF8;
	
	/** whether allowed https request */
	private Boolean isHttpsAllowed = true;
	
	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public Boolean getIsHttpsAllowed() {
		return isHttpsAllowed;
	}

	public void setIsHttpsAllowed(Boolean isHttpsAllowed) {
		this.isHttpsAllowed = isHttpsAllowed;
	}

	public HttpContext getHttpContext() {
		if(httpContext == null) setHttpContext(new BasicHttpContext());
		return httpContext;
	}

	public void setHttpContext(HttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public BasicCookieStore getCookieStore() {
		// handle the null value
		if(cookieStore == null) {
			setCookieStore(new BasicCookieStore());
		}
		// override the cookie store with the one customized by API invoker
		Object csInHc = getHttpContext().getAttribute(ClientContext.COOKIE_STORE);
		if(csInHc!=null && csInHc instanceof BasicCookieStore) {
			cookieStore = (BasicCookieStore) csInHc;
		}
		getHttpContext().setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	/**
	 * Creates a new HTTP client from parameters and a connection manager.
	 * @see DefaultHttpClient#DefaultHttpClient(ClientConnectionManager, HttpParams)
	 * 
	 * @param conman
	 * @param params
	 */
	public ServHttpClient(final ClientConnectionManager conman,
			final HttpParams params) {
		httpClient = getDefaultHttpClient(conman, params);
	}
	
	public ServHttpClient(final ClientConnectionManager conman,
			Map<String, Object> params) {
		httpClient = getDefaultHttpClient(conman, null);
		
		Iterator<String> it = params.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			this.setParameter(key, params.get(key));
		}
	}
	
	private DefaultHttpClient getDefaultHttpClient(final ClientConnectionManager conman,
			final HttpParams params) {
		DefaultHttpClient ret = new DefaultHttpClient(conman, params);
		if(isHttpsAllowed) {
			allowHttpsReq(ret);
		}
		return ret;
	}
	/**
	 * allow https protocol request
	 * @param httpClient
	 */
	private void allowHttpsReq(HttpClient httpClient) {
		try {
			TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException {
					return true;
				}
			};
			SSLSocketFactory sf = null;;
			sf = new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, sf));
		} catch (Exception e) {
			throw new UtilException(e);
		}
	}
	
	public ServHttpClient(final ClientConnectionManager conman) {
		this(conman, (HttpParams)null);
	}
	
	public ServHttpClient(final HttpParams params) {
		this(null, params);
	}
	
	public ServHttpClient(Map<String, Object> params) {
		this(null, params);
	}
	
	public ServHttpClient(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	public ServHttpClient() {
		this(null, (HttpParams)null);
	}
	
	public HttpClient getHttpClient(){
		return httpClient;
	}
	
	public ClientConnectionManager getConnectionManager(){
		return httpClient.getConnectionManager();
	}
	
	public HttpParams getParams(){
		return httpClient.getParams();
	}
	
	public void setParameter(String name, Object value){
		HttpParams httpParams = httpClient.getParams();
		httpParams.setParameter(name, value);
	}
	
	public Object getParameter(String name){
		HttpParams httpParams = httpClient.getParams();
		return httpParams.getParameter(name);
	}
	
	/**
	 * Http get method
	 * 
	 * @param url
	 *            request url
	 * @param respEncoding
	 *            response String's encoding
	 * @param isForceCloseConnection
	 *            true: indicate server close current connection when finish the
	 *            request. isForceCloseConnection is true, together with
	 *            http.protocol.expect-continue is false, can solve problem
	 *            about 'Too many open files' in linux when create many
	 *            {@link ServHttpClient} instance. <br>
	 *            false: the connection is managed by HttpConnectionManager, not
	 *            ensure it is closed.
	 * @return {@link Message}
	 */
	public Message<String> get(String url, String respEncoding, boolean isForceCloseConnection) {
		HttpGet httpGet = new HttpGet(url);
		return execute(httpGet, isForceCloseConnection, respEncoding);
	}
	
	/**
	 * See {@link #get(String, String, boolean)}, respEncoding=
	 * {@link #getDefaultEncoding()}.
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<String> get(String url, boolean isForceCloseConnection) {
		return this.get(url, getDefaultEncoding(), isForceCloseConnection);
	}
	
	/**
	 * See {@link #get(String, String, boolean)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param respEncoding
	 * @return
	 */
	public Message<String> get(String url, String respEncoding) {
		return this.get(url, respEncoding, false);
	}
	
	/**
	 * See {@link #get(String, String, boolean)}, respEncoding=
	 * {@link #getDefaultEncoding()},isForceCloseConnection=false.
	 * 
	 * @param url
	 * @return
	 */
	public Message<String> get(String url) {
		return this.get(url, getDefaultEncoding(), false);
	}
	
	/**
	 * See {@link #get(String, String, boolean)}, difference is response data,
	 * i.e. {@link Message#getData()}, is bytes.
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<byte[]> getRtnBytes(String url, boolean isForceCloseConnection){
		HttpGet httpGet = new HttpGet(url);
		return this.executeRtnBytes(httpGet, isForceCloseConnection);
	}
	
	/**
	 * See {@link #getRtnBytes(String, boolean)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @return
	 */
	public Message<byte[]> getRtnBytes(String url){
		return this.getRtnBytes(url, false);
	}
	
	/**
	 * Http post method. Not explaining parameters see
	 * {@link #get(String, String, boolean)}.
	 * 
	 * @param url
	 * @param respEncoding
	 *            response String's encoding.
	 * @param isForceCloseConnection
	 * @param entity
	 *            the entity to send.
	 * @return {@link Message}
	 */
	public Message<String> post(String url, String respEncoding, boolean isForceCloseConnection, HttpEntity entity){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		return this.execute(httpPost, isForceCloseConnection, respEncoding);
	}
	
	/**
	 * {@link #post(String, String, boolean, HttpEntity)}, respEncoding={@link #getDefaultEncoding()}
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @param entity
	 * @return
	 */
	public Message<String> post(String url, boolean isForceCloseConnection, HttpEntity entity){
		return post(url, getDefaultEncoding(), isForceCloseConnection, entity);
	}
	
	/**
	 * {@link #post(String, String, boolean, HttpEntity)}, respEncoding={@link #getDefaultEncoding()}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param entity
	 * @return
	 */
	public Message<String> post(String url, HttpEntity entity){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		return this.execute(httpPost, false, this.getDefaultEncoding());
	}
	
	/**
	 * Http post method, reponse data type is bytes. Parameters see {@link #post(String, String, boolean, HttpEntity). 
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @param entity
	 * @return
	 */
	public Message<byte[]> postRtnBytes(String url, boolean isForceCloseConnection, HttpEntity entity){
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		return this.executeRtnBytes(httpPost, isForceCloseConnection);
	}
	
	/**
	 * Http post method. Not explaining parameters see {@link #get(String, String, boolean)}.
	 * 
	 * @param url
	 * @param reqEncoding
	 *            encoding the name/value pairs be encoded with
	 * @param respEncoding
	 * @param isForceCloseConnection
	 * @param nameValuePairs
	 *            name/value pairs
	 * @return
	 */
	public Message<String> post(String url, String reqEncoding, String respEncoding, boolean isForceCloseConnection, NameValuePair...nameValuePairs){
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs==null? new ArrayList<NameValuePair>(0): Arrays.asList(nameValuePairs), reqEncoding));
			return this.execute(httpPost, isForceCloseConnection, respEncoding);
		} catch (UnsupportedEncodingException e) {
			throw new ServException(e);
		}
	}
	
	/**
	 * See {@link #post(String, String, String, boolean, NameValuePair...)},
	 * reqEncoding={@link #getDefaultEncoding()}, respEncoding=
	 * {@link #getDefaultEncoding()}.
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @param nameValuePairs
	 * @return
	 */
	public Message<String> post(String url, boolean isForceCloseConnection, NameValuePair...nameValuePairs){
		String encoding = getDefaultEncoding();
		return this.post(url, encoding, encoding, isForceCloseConnection, nameValuePairs);
	}
	
	/**
	 * See {@link #post(String, String, String, boolean, NameValuePair...)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param reqEncoding
	 * @param respEncoding
	 * @param nameValuePairs
	 * @return
	 */
	public Message<String> post(String url, String reqEncoding, String respEncoding, NameValuePair...nameValuePairs){
		return this.post(url, reqEncoding, respEncoding, false, nameValuePairs);
	}
	
	/**
	 * See {@link #post(String, String, String, boolean, NameValuePair...)},
	 * reqEncoding={@link #getDefaultEncoding()}, respEncoding=
	 * {@link #getDefaultEncoding()}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 */
	public Message<String> post(String url, NameValuePair...nameValuePairs){
		String encoding = getDefaultEncoding();
		return this.post(url, encoding, encoding, false, nameValuePairs);
	}
	
	/**
	 * Http post method, reponse data type is bytes. Parameters see {@link #post(String, String, String, boolean, NameValuePair...)}
	 * 
	 * @param url
	 * @param reqEncoding
	 * @param isForceCloseConnection
	 * @param nameValuePairs
	 * @return
	 */
	public Message<byte[]> postRtnBytes(String url, String reqEncoding, boolean isForceCloseConnection, NameValuePair...nameValuePairs){
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(nameValuePairs), reqEncoding));
			return this.executeRtnBytes(httpPost, isForceCloseConnection);
		} catch (UnsupportedEncodingException e) {
			throw new ServException(e);
		}
	}
	
	/**
	 * See {@link #postRtnBytes(String, String, boolean, NameValuePair...)}, reqEncoding={@link #getDefaultEncoding()}.
	 * 
	 * @param url
	 * @param isForceCloseConnection
	 * @param nameValuePairs
	 * @return
	 */
	public Message<byte[]> postRtnBytes(String url, boolean isForceCloseConnection, NameValuePair...nameValuePairs){
		return this.postRtnBytes(url, getDefaultEncoding(), isForceCloseConnection, nameValuePairs);
	}
	
	/**
	 * See {@link #postRtnBytes(String, String, boolean, NameValuePair...)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param reqEncoding
	 * @param nameValuePairs
	 * @return
	 */
	public Message<byte[]> postRtnBytes(String url, String reqEncoding, NameValuePair...nameValuePairs){
		return this.postRtnBytes(url, reqEncoding, false, nameValuePairs);
	}
	
	/**
	 * See {@link #postRtnBytes(String, String, boolean, NameValuePair...)},
	 * reqEncoding={@link #getDefaultEncoding()}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 */
	public Message<byte[]> postRtnBytes(String url, NameValuePair...nameValuePairs){
		return this.postRtnBytes(url, getDefaultEncoding(), false, nameValuePairs);
	}
	
	protected HttpPost prepareHttpPostAsStream(String url, String msg, String reqEncoding) throws UnsupportedEncodingException{
		HttpPost httpPost = new HttpPost(url);
		byte[] bytes = msg==null? new byte[0]: msg.getBytes(reqEncoding);
		InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
		reqEntity.setContentType("binary/octet-stream");
        reqEntity.setChunked(true);
        httpPost.setEntity(reqEntity);
        return httpPost;
	}
	
	protected HttpPost prepareHttpPostAsStream(String url, byte[] msg) throws UnsupportedEncodingException{
		HttpPost httpPost = new HttpPost(url);
		byte[] bytes = msg==null? new byte[0]: msg;
		InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
		reqEntity.setContentType("binary/octet-stream");
        reqEntity.setChunked(true);
        httpPost.setEntity(reqEntity);
        return httpPost;
	}
	
	/**
	 * Http post method, request msg is as stream.
	 * 
	 * @param url
	 *            request url
	 * @param msg
	 *            request message
	 * @param reqEncoding
	 *            request encoding
	 * @param respEncoding
	 *            reponse encoding
	 * @param isForceCloseConnection
	 *            true: indicate server close current connection when finish the
	 *            request. isForceCloseConnection is true, together with
	 *            http.protocol.expect-continue is false, can solve problem
	 *            about 'Too many open files' in linux when create many
	 *            HttpClientServ instance. false: the connection is managed by
	 *            HttpConnectionManager, not ensure it is closed.
	 * @return
	 */
	public Message<String> postAsStream(String url, String msg, String reqEncoding, String respEncoding, boolean isForceCloseConnection){
		try {
			HttpPost httpPost = prepareHttpPostAsStream(url, msg, reqEncoding);
            return this.execute(httpPost, isForceCloseConnection, respEncoding);
		} catch (UnsupportedEncodingException e) {
			throw new ServException(e);
		}
	}

	/**
	 * See {@link #postAsStream(String, String, String, String, boolean)},
	 * reqEncoding={{@link #getDefaultEncoding()}, respEncoding={
	 * {@link #getDefaultEncoding()}.
	 * 
	 * @param url
	 * @param msg
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<String> postAsStream(String url, String msg, boolean isForceCloseConnection){
		String encoding = getDefaultEncoding();
		return this.postAsStream(url, msg, encoding, encoding, isForceCloseConnection);
	}
	
	/**
	 * See {@link #postAsStream(String, String, String, String, boolean)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param msg
	 * @param reqEncoding
	 * @param respEncoding
	 * @return
	 */
	public Message<String> postAsStream(String url, String msg, String reqEncoding, String respEncoding){
		return this.postAsStream(url, msg, reqEncoding, respEncoding, false);
	}
	
	/**
	 * See {@link #postAsStream(String, String, String, String, boolean)}
	 * ,reqEncoding={{@link #getDefaultEncoding()}, respEncoding={
	 * {@link #getDefaultEncoding()}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param msg
	 * @return
	 */
	public Message<String> postAsStream(String url, String msg){
		String encoding = getDefaultEncoding();
		return this.postAsStream(url, msg, encoding, encoding, false);
	}
	
	/**
	 * Http post method, request msg is as stream, reponse data type is bytes. 
	 * @see HttpClientServ#postAsStream(String, String, String, String, boolean).
	 * 
	 * @param url
	 * @param msg
	 * @param reqEncoding
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<byte[]> postAsStreamRtnBytes(String url, String msg, String reqEncoding, boolean isForceCloseConnection){
		try {
			HttpPost httpPost = prepareHttpPostAsStream(url, msg, reqEncoding);
			return this.executeRtnBytes(httpPost, isForceCloseConnection);
		} catch (UnsupportedEncodingException e) {
			throw new ServException(e);
		}
	}
	
	/**
	 * See {@link #postAsStream(String, String, String, String, boolean)}
	 * 
	 * @param url
	 * @param msg
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<byte[]> postAsStreamRtnBytes(String url, byte[] msg, boolean isForceCloseConnection){
		try {
			HttpPost httpPost = prepareHttpPostAsStream(url, msg);
			return this.executeRtnBytes(httpPost, isForceCloseConnection);
		} catch (UnsupportedEncodingException e) {
			throw new ServException(e);
		}
	}
	
	/**
	 * {@link #postAsStreamRtnBytes(String, String, String, boolean)}, reqEncoding={@link #getDefaultEncoding()}.
	 * 
	 * @param url
	 * @param msg
	 * @param isForceCloseConnection
	 * @return
	 */
	public Message<byte[]> postAsStreamRtnBytes(String url, String msg, boolean isForceCloseConnection){
		return this.postAsStreamRtnBytes(url, msg, getDefaultEncoding(), isForceCloseConnection);
	}
	
	/**
	 * {@link #postAsStreamRtnBytes(String, String, String, boolean)}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param msg
	 * @param reqEncoding
	 * @return
	 */
	public Message<byte[]> postAsStreamRtnBytes(String url, String msg, String reqEncoding){
		return this.postAsStreamRtnBytes(url, msg, reqEncoding, false);
	}
	
	/**
	 * {@link #postAsStreamRtnBytes(String, String, String, boolean)},
	 * reqEncoding={@link #getDefaultEncoding()}, isForceCloseConnection=false.
	 * 
	 * @param url
	 * @param msg
	 * @return
	 */
	public Message<byte[]> postAsStreamRtnBytes(String url, String msg){
		return this.postAsStreamRtnBytes(url, msg, getDefaultEncoding(), false);
	}
	/**
	 * configure the client cookie
	 * @param request
	 */
	private void clientCookieConfig(HttpUriRequest request) {
		// get domain(host)
		String domain = request.getURI().getHost();
		for(Cookie cookie : getCookieStore().getCookies()) {
			if(cookie instanceof SetCookie) {
				SetCookie setCookie = (SetCookie) cookie;
				if(StringUtils.isEmpty(setCookie.getDomain())) setCookie.setDomain(domain);
			}
		}
		logger.debug("==> cookies: {}", JSON.toJSONString(getCookieStore().getCookies(), true));
	}
	
	Message<String> execute(HttpUriRequest request, boolean isForceCloseConnection, String respEncoding){
		if(isForceCloseConnection)
			forceCloseConnection(request);
		try {
			clientCookieConfig(request);
			HttpResponse resp = httpClient.execute(request, httpContext);
			int status = resp.getStatusLine().getStatusCode();
			logger.debug("==> status code: {}", status);
			HttpEntity entity = resp.getEntity();
			String data = EntityUtils.toString(entity, respEncoding);
			logger.debug("==> resp in string: {}", data);
			if(status == HttpStatus.SC_OK){
				return new Message<String>(Status.SUCCESS, "Http request success", data);
			} else {
				request.abort();
				return new Message<String>(Status.FAILURE, "Http request failure, status code:" + status, data);
			}
		} catch (Exception e) {
			if(!request.isAborted()){
				request.abort();
			}
			throw new ServException(e);
		}	 
	}
	
	Message<byte[]> executeRtnBytes(HttpUriRequest request, boolean isForceCloseConnection){
		long beginTime = System.currentTimeMillis();
		if(isForceCloseConnection)
			forceCloseConnection(request);
		HttpEntity entity = null;
		try {
			clientCookieConfig(request);
			HttpResponse resp = httpClient.execute(request, httpContext);
			int status = resp.getStatusLine().getStatusCode();
			logger.debug("==> status code: {}", status);
			entity = resp.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			logger.debug("==> resp in byte[]: {}", data);
			if(status == HttpStatus.SC_OK){
				return new Message<byte[]>(Status.SUCCESS, "Http request success", data);
			} else {
				EntityUtils.consume(entity);
				request.abort();
				return new Message<byte[]>(Status.FAILURE, "Http request failure, status code:" + status);
			}
		} catch (Exception e) {
			loggerHandle(request, beginTime);
			if(!request.isAborted()){
				EntityUtils.consumeQuietly(entity);
				request.abort();
			}
			throw new ServException(e);
		}
	}
	/**
	 * handle the logger info when an exception occur
	 * @param request
	 * @param data
	 * @param beginTime
	 */
	private void loggerHandle(HttpUriRequest request, long beginTime) {
		long endTime = System.currentTimeMillis();
		logger.warn("==> Exception occured while sending http request, details as following described:");
		logger.warn("==> url: {}", request.getURI());
		logger.warn("==> params: {}", JSON.toJSONString(request.getParams()));
		logger.warn("==> time cost: {} second", (endTime - beginTime)/1000);
	}
	
	protected void forceCloseConnection(HttpMessage method){
		method.setHeader("Connection", "close");
	}
}
