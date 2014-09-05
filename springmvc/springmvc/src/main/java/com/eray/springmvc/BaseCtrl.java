package com.eray.springmvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.exception.CtrlException;
import com.eray.springmvc.util.ErayEncryptUtil;
import com.eray.springmvc.util.GzipUtil;


public class BaseCtrl {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected String encoding = "UTF-8";
	protected String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static final String LOG_ENTER = "enter: {}";
	public static final String LOG_ENTER_PARAMS = "enter: {}, params: {}";
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * add an entry logger to logger system
	 * @param req
	 */
	public void loggerEnter(HttpServletRequest req) {
		logger.trace(LOG_ENTER, req.getRequestURI());
	}
	
	public byte[] parseReqParamToByte(InputStream in){
		ByteArrayOutputStream param = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		try {
			while((len=in.read(b)) != -1) {
				param.write(b, 0, len);
			}
			return param.toByteArray();
		} catch (IOException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new CtrlException(e);
		}
	}
	/**
	 * recommend {@link #parseReqParam(HttpServletRequest, boolean, boolean)} for add logger for entry info
	 */
	public String parseReqParam(InputStream in, boolean isDecrypt, boolean isDecompress){
		try {
			byte[] b = parseReqParamToByte(in);
			if (isDecrypt) {
				b = ErayEncryptUtil.decrypt(b);
			}
			if(isDecompress){
				b = GzipUtil.uncompress(b);
			}
			return new String(b, encoding);
		} catch (IOException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new CtrlException(e);
		}
	}
	/**
	 * @see #parseReqParam(InputStream, boolean, boolean)
	 */
	public String parseReqParam(InputStream in, boolean isDecrypt) {
		return parseReqParam(in, isDecrypt, false);
	}
	/**
	 * @see #parseReqParam(InputStream, boolean)
	 */
	public String parseReqParam(InputStream in) {
		return parseReqParam(in, false);
	}
	/**
	 * parse parameter stored in HttpRequestBody
	 * @param req
	 * @param isDecrypt
	 * @param isDecompress
	 * @return
	 */
	public String parseReqParam(HttpServletRequest req, boolean isDecrypt, boolean isDecompress){
		try {
			loggerEnter(req);
			String params = parseReqParam(req.getInputStream(), isDecrypt, isDecompress);
			logger.trace(LOG_ENTER_PARAMS, req.getRequestURI(), params);
			return params;
		} catch (IOException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new CtrlException(e);
		}
	}
	/**
	 * @see #parseReqParam(HttpServletRequest, boolean, boolean)
	 */
	public String parseReqParam(HttpServletRequest req, boolean isDecrypt) {
		return parseReqParam(req, isDecrypt, false);
	}
	/**
	 * @see #parseReqParam(HttpServletRequest, boolean)
	 */
	public String parseReqParam(HttpServletRequest req) {
		return parseReqParam(req, false);
	}
	
	protected void write(InputStream data, OutputStream out){
		try{
			byte[] b = new byte[1024];
			int len = 0;
			while((len=data.read(b))!=-1){
				out.write(b, 0, len);
			}
			data.close();
		}catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	protected void write(Object obj, HttpServletResponse resp){
		write(obj, resp, false, false);
	}
	
	protected void write(Object obj, HttpServletResponse resp, boolean encrypted){
		write(obj, resp, encrypted, false);
	}
	
	/**
	 * 将对象obj转化成json字符串后输出至客户端
	 * @param obj 输出结果
	 * @param resp
	 * @param encrypted 输出结果是否需要加密
	 * @param compressed 输出结果是否需要压缩
	 * @throws IOException
	 */
	protected void write(Object obj, HttpServletResponse resp, boolean encrypted,
			boolean compressed) {
		try {
			String content = null;
			if (obj instanceof String) {
				content = String.valueOf(obj);
			} else {
				content = JSONObject
						.toJSONStringWithDateFormat(obj, dateFormat);
			}

			logger.trace("==> write result --> {}", content);
			
			byte[] response;

			response = content.getBytes(encoding);

			if (compressed) {
				response = GzipUtil.compress(response);
			}
			if (encrypted) {
				response = ErayEncryptUtil.encrypt(response);
			}

			resp.getOutputStream().write(response);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	@InitBinder
	public void myInitBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		webDataBinder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
