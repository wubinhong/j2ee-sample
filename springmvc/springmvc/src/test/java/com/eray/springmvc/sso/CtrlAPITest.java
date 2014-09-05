package com.eray.springmvc.sso;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.TestBaseCtrl;

public class CtrlAPITest extends TestBaseCtrl {

	String url = "http://localhost:8080/springmvc";
	
	@Test
	public void verifyUsername() {
		url += "/sso/verify/username.do";
		JSONObject data = new JSONObject();
		data.put("username", "kevin");
		print(post(url, data));
	}
	
}
