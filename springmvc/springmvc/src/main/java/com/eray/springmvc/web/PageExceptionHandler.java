package com.eray.springmvc.web;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.eray.springmvc.BaseCtrlSpringmvc;
import com.eray.springmvc.GlobalConstant.DateFormat;

public class PageExceptionHandler extends ExceptionHandler {
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		
		String rStr = getMessage(ex, DateFormat.LONG_FORMAT);
		// page interaction request handling
		if(StringUtils.startsWith(request.getRequestURI(), request.getContextPath())) {
			return new ModelAndView(BaseCtrlSpringmvc.PATH_VIEW + "/500", Collections.singletonMap("info", rStr));
		}
		// other request handling
		return super.resolveException(request, response, handler, ex);
	}
}
