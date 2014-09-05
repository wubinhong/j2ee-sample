package com.eray.springmvc;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.eray.springmvc.GlobalConstant.DateFormat;

@ContextConfiguration(locations="classpath:conf/**/app-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestBase {
	
	private boolean inJsonFormat = false;

	protected void setPrintOutInJsonFormat(boolean inJsonFormat) {
		this.inJsonFormat = inJsonFormat;
	}
	
	protected <T> void print(List<T> list){
		if(list == null){
			System.out.println("---> null");
		} else if(list.isEmpty()) {
			System.out.println("---> empty");
		} else {
			int num = 1;
			for(T ele: list){
				System.out.println(String.format("---> %2d | %s", num++, format(ele)));
			}
		}
	}

	/**
	 * @see #print(List)
	 */
	public void print(Object... objs) {
		if(objs == null) {
			System.out.println("---> null");
		} else {
			List<Object> list = new ArrayList<Object>();
			for(Object obj : objs) {
				list.add(obj);
			}
			print(list);
		}
	}
	
	public String format(Object obj) {
		String ret = null;
		if(inJsonFormat) {
			ret = JSONObject.toJSONStringWithDateFormat(obj, DateFormat.LONG_FORMAT);
		} else {
			ret = obj==null ? null : obj.toString();
		}
		return ret;
	}
	
}
