package com.eray.springmvc;

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
	 * @return
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
	 * get business data object
	 * @param code
	 * @return
	 */
	protected JSONObject getData(JSONObject code) {
		return code.getJSONObject("data");
	}
	
}
