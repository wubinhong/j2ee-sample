package com.eray.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.exception.CtrlException;
import com.eray.springmvc.util.ErayEncryptUtil;

public class BaseCtrlSpringmvc extends BaseCtrl {
	
	public final static String PATH_VIEW = "";
	public final static String KEY = "Crackers and the thief will suffer misfortune";
	
	/**
	 * decrypt code parameter to json
	 * @param code
	 * @return e.g. {"data":{},"expiry":1409898421333}
	 */
	protected JSONObject decryptCode(String code) {
		if(StringUtils.isBlank(code)) throw new CtrlException("invalid request: code is required!");
		return JSON.parseObject(ErayEncryptUtil.decryptFromBase64Str(code, KEY));
	}
	/**
	 * validate request
	 * @param code
	 */
	protected void validateReq(JSONObject code) {
		long expiry = code.getLongValue("expiry");
		if(expiry < System.currentTimeMillis()) {
			throw new CtrlException("request is expired!");
		}
	}
	/**
	 * decrypt @param code, and validate it, and then return the business data object
	 * @param code
	 * @return
	 */
	protected JSONObject getData(HttpServletRequest req, String code) {
		logger.info("<== {} <-- code: {}", req.getServletPath(), code);
		JSONObject codeObj = decryptCode(code);
		validateReq(codeObj);
		return codeObj.getJSONObject("data");
	}
	
}
