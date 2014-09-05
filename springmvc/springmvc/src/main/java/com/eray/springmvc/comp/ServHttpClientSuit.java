package com.eray.springmvc.comp;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;

import com.eray.springmvc.util.ErayEncryptUtil;
import com.eray.springmvc.util.GzipUtil;
import com.eray.springmvc.web.Message;
import com.eray.springmvc.web.Message.Status;

/**
 * a suit of ZHttpClientServ,ErayEncryptUtil,GzipUtil
 * 
 * @author shifengxuan
 *
 */
public class ServHttpClientSuit extends ServHttpClient {
	/**
	 * See {@link #ZHttpClientServ(HttpClientParams, HttpConnectionManager)}
	 */
	public ServHttpClientSuit() {
		super();
	}
	
	/**
	 * See {@link #ZHttpClientServ(Map, HttpConnectionManager)}
	 * 
	 * @param params
	 */
	public ServHttpClientSuit(Map<String, Object> params){
		super(params);
	}
	
	/**
	 * See {@link #ZHttpClientServ(HttpClientParams, HttpConnectionManager)}
	 * 
	 * @param params
	 *            used to set parameters of HttpClientParams
	 * @param httpConnectionManager
	 */
	public ServHttpClientSuit(Map<String, Object> params, ClientConnectionManager httpConnectionManager) {
		super(httpConnectionManager, params);
	}
	
	/**
	 * See {@link #ZHttpClientServ(HttpClientParams, HttpConnectionManager)}
	 * 
	 * @param params
	 */
	public ServHttpClientSuit(HttpParams params){
		super(params);
	}
	
	/**
	 * Creates an instance of ZHttpClientServ using a user specified.
	 * {@link HttpClientParams}, {@link HttpConnectionManager}
	 * @param params
	 * @param httpConnectionManager
	 */
	public ServHttpClientSuit(HttpParams params, ClientConnectionManager httpConnectionManager){
		super(httpConnectionManager, params);
	}
	
	/**
	 * See {@link #ZHttpClientServ(HttpClientParams, HttpConnectionManager)}
	 * 
	 * @param httpConnectionManager
	 */
	public ServHttpClientSuit(ClientConnectionManager httpConnectionManager){
		super(httpConnectionManager, (HttpParams)null);
	}
	
	public Message<String> postAsStream(String url, String msg,
			String reqEncoding, String respEncoding,
			boolean isForceCloseConnection, boolean isEncrypted,
			boolean isDecrypted, boolean isCompressed, boolean unCompressed) {

		Message<String> message = new Message<String>();
		try {
			byte[] param = null;
			if (msg != null) {
				param = msg.getBytes(reqEncoding);
				if (isCompressed) {
					param = GzipUtil.compress(param);
				}
				if (isEncrypted) {
					param = ErayEncryptUtil.encrypt(param);
				}
			}
			Message<byte[]> r = postAsStreamRtnBytes(url, param, isForceCloseConnection);
			byte[] d = r.getData();
			if (r.getStatus().equals(Status.SUCCESS)) {
				handleReqBody(d, message, isDecrypted, unCompressed, respEncoding);
				message.setStatus(Status.SUCCESS);
			} else {
				message.setStatus(r.getStatus());
				message.setMsg(r.getMsg());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			message.setStatus(Status.ERROR);
			message.setMsg(e.getMessage());
		}
		logger.debug("==> resp in string: {}", message.getData());
		return message;

	}
	/**
	 * handle request body in byte format, and set data handled to data field of @param message
	 * @param d
	 * @param message
	 * @param isDecrypted
	 * @param unCompressed
	 * @param respEncoding
	 * @throws UnsupportedEncodingException
	 */
	private void handleReqBody(byte[] d, Message<String> message, boolean isDecrypted, boolean unCompressed, String respEncoding) throws UnsupportedEncodingException {
		if (d != null) {
			if (isDecrypted) {
				d = ErayEncryptUtil.decrypt(d);
			}
			if (unCompressed) {
				d = GzipUtil.uncompress(d);
			}
		}
		if (d != null)
			message.setData(new String(d, respEncoding));
	}
	
	public Message<String> postAsStream(String url, String msg, String reqEncoding, String respEncoding, boolean isForceCloseConnection){
		return postAsStream(url, msg, reqEncoding, respEncoding, isForceCloseConnection, false, false, false, false);
	}
	
	public Message<String> postAsStream(String url, String msg, boolean isForceCloseConnection, boolean isEncrypted,
			boolean isDecrypted){
		return postAsStream(url, msg, getDefaultEncoding(), getDefaultEncoding(), isForceCloseConnection, isEncrypted, isDecrypted, false, false);
	}
	
	public Message<String> postAsStream(String url, String msg, boolean isForceCloseConnection, boolean isEncrypted,
			boolean isDecrypted, boolean isCompressed, boolean unCompressed){
		return postAsStream(url, msg, getDefaultEncoding(), getDefaultEncoding(), isForceCloseConnection, isEncrypted, isDecrypted, isCompressed, unCompressed);
	}
	
}
