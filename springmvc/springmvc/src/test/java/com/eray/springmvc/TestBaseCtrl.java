package com.eray.springmvc;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.comp.ServHttpClientSuit;
import com.eray.springmvc.util.ErayEncryptUtil;


public class TestBaseCtrl extends TestBase {
	
	public final static String KEY = "Crackers and the thief will suffer misfortune";
	/** term of validity in second */
	public final static int expiry = 60;
	
	protected ServHttpClientSuit iServHttpClientSuit = new ServHttpClientSuit();
	
	/**
	 * construct data to be a full feature parameter, and encrypt it
	 * @param data
	 * @return
	 */
	protected String encryptCode(JSONObject data) {
		JSONObject code = new JSONObject();
		code.put("data", data);
		code.put("expiry", System.currentTimeMillis() + expiry * 1000);
		return ErayEncryptUtil.encryptToBase64Str(code.toString(), KEY);
	}
	/**
	 * post http request to @param url
	 * @param url
	 * @param data
	 * @return
	 */
	protected String post(String url, JSONObject data) {
		return iServHttpClientSuit.post(url, new BasicNameValuePair("code", encryptCode(data))).getData();
	}
}
