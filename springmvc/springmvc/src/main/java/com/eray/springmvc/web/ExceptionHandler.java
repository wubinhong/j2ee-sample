package com.eray.springmvc.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.GlobalConstant.DateFormat;
import com.eray.springmvc.GlobalConstant.Encoding;
import com.eray.springmvc.exception.ErayException;
import com.eray.springmvc.util.ErayEncryptUtil;
import com.eray.springmvc.util.GzipUtil;
import com.eray.springmvc.web.Message.Status;

public class ExceptionHandler implements HandlerExceptionResolver {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		boolean isCompressed = false;
		boolean isEncrypted = false;
		String encoding = Encoding.UTF8;
		String dateFormat = DateFormat.LONG_FORMAT;
		
		if(handler!=null && handler instanceof HandlerMethod){
			HandlerMethod hnd = (HandlerMethod) handler;
			ParserAnno rm = hnd.getMethodAnnotation(ParserAnno.class);
			if(rm!=null){
				isCompressed = rm.isCompressed();
				isEncrypted = rm.isEncrypted();
				encoding = rm.encoding();
				dateFormat = rm.dateFormat();
			}
		}
		
		String rStr = getMessage(ex, dateFormat);
		
		byte[] rByte = null;
		try {
			rByte = compile(rStr, encoding, isCompressed, isEncrypted);
		} catch (ErayException e) {
			encoding = Charset.defaultCharset().name();
			
			logger.error("result to byte[] exception={}", e.getMessage());
			logger.error("use default encoding={}", encoding);
			
			rByte = compile(rStr, encoding, isCompressed, isEncrypted);
			//modelAndView.addObject(generalPage.getAttrName(), rByte);
		}
		try{	
			response.getOutputStream().write(rByte);
		}catch (IOException e) {
			logger.error("respose IOException");
		}
		
		return new ModelAndView();
	}
	
	protected String getMessage(Exception ex, String dateFormat){
		Message<String> r = null;
		if (ex instanceof ErayException) {
			logger.warn(ex.getMessage());
			r = new Message<String>(Status.FAILURE, ex.getMessage());
		} else {
			logger.error(ExceptionUtils.getFullStackTrace(ex));
			r = new Message<String>(Status.ERROR, ex.getMessage());
		}
		
		String rStr = JSONObject.toJSONStringWithDateFormat(r, dateFormat);
		return rStr;
	}
	
	
	protected byte[] compile(String data, String encoding, boolean isCompressed, boolean isEncrypted){
		byte[] rByte;
		try {
			rByte = data.getBytes(encoding);
			if (isCompressed) {
				rByte = GzipUtil.compress(rByte);
			}
			if (isEncrypted) {
				rByte = ErayEncryptUtil.encrypt(rByte);
			}
			return rByte;
		} catch (UnsupportedEncodingException e) {
			throw new ErayException("UnsupportedEncodingException encoding="+encoding);
		}
	}
	
}
