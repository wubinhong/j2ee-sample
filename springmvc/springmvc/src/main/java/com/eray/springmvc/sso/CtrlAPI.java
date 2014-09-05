package com.eray.springmvc.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.BaseCtrlSpringmvc;
import com.eray.springmvc.web.Message;
import com.eray.springmvc.web.Message.Status;

@Controller("CtrlAPI")
@RequestMapping(value = "/sso")
public class CtrlAPI extends BaseCtrlSpringmvc {
	
	public static final String PAGE_PATH_PREFIX = "/sso";
	
	@RequestMapping(value="/verify/usr-pwd.do")
	public void verifyUserPwd(HttpServletRequest req, HttpServletResponse resp, String code) {
		// parse and validate
		JSONObject data = getData(req, code);
		String password = data.getString("password");
		if("123456".equals(password)) { // match
			JSONObject retData = new JSONObject();
			retData.put("nickname", "小楼");
			write(new Message<JSONObject>(Status.SUCCESS, "match", retData), resp);
		} else {
			// dismatch
			write(new Message<String>(Status.FAILURE, "dismatch"), resp);
		}
	}
	
	@RequestMapping(value="/verify/username.do")
	public void verifyUsername(HttpServletRequest req, HttpServletResponse resp, String code) {
		// parse and validate
		JSONObject data = getData(req, code);
		String username = data.getString("username");
		if (!StringUtils.equals(username, "kevin")) {
			// match
			write(new Message<String>(Status.SUCCESS, "available username"), resp);
		} else {
			// dismatch
			write(new Message<String>(Status.FAILURE, "unavailable username"), resp);
		}
	}
	
	@RequestMapping(value="/sync/login.do")
	public void syncLogin(HttpServletRequest req, HttpServletResponse resp, String code) {
		// parse and validate
		JSONObject data = getData(req, code);
		String username = data.getString("username");
		String password = data.getString("password");
		if("kevin".equals(username) && "123456".equals(password)) {
			// login sync success
			write(new Message<String>(Status.SUCCESS, "login sync success"), resp);
		} else {
			// login sync failure
			write(new Message<String>(Status.FAILURE, "login sync failure"), resp);
		}
	}
	
	@RequestMapping(value="/sync/logout.do")
	public void syncLogout(HttpServletRequest req, HttpServletResponse resp, String code) {
		// parse and validate
		JSONObject data = getData(req, code);
		System.out.println(data);
		// logout sync success
		write(new Message<String>(Status.SUCCESS, "logout sync success"), resp);
		// logout sync failure
//		write(new Message<String>(Status.FAILURE, "logout sync failure"), resp);
	}
	
	@RequestMapping(value="/sync/update.do")
	public void syncUpdate(HttpServletRequest req, HttpServletResponse resp, String code) {
		// parse and validate
		JSONObject data = getData(req, code);
		System.out.println(data);
		// update sync success
		write(new Message<String>(Status.SUCCESS, "update sync success"), resp);
		// update sync failure
//		write(new Message<String>(Status.FAILURE, "update sync failure"), resp);
	}
	
}
