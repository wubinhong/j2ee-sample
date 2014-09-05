package com.eray.springmvc.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.eray.springmvc.GlobalConstant.DateFormat;
import com.eray.springmvc.GlobalConstant.Encoding;

/**
 * use to compile result that method of controller response to client.
 * default: <br>
 * <li>isCompressed=false, <br>
 * <li>isEncrypted=false, <br>
 * <li>encoding=UTF-8, <br>
 * <li>dateFormat=yyyy-MM-dd HH:mm:ss
 * 
 * @author shifengxuan
 *
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParserAnno {
	boolean isCompressed() default false;
	boolean isEncrypted() default false;
	String encoding() default Encoding.UTF8;
	String dateFormat() default DateFormat.LONG_FORMAT;
}
