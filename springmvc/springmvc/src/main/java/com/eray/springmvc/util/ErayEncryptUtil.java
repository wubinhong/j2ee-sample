package com.eray.springmvc.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 提供加解密字节数据的方法
 * @author kevin
 */
public class ErayEncryptUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ErayEncryptUtil.class);
	
	/** 默认私钥 */
	private static String DEFAULT_KEY = "Crackers and the thief will suffer misfortune";
	/** 字符串转化默认编码 */
	private static String DEFAULT_ENCODING = "UTF-8";

	public static String getKey() {
		return DEFAULT_KEY;
	}

	public static void setKey(String key) {
		ErayEncryptUtil.DEFAULT_KEY = key;
	}

	public static String getEncoding() {
		return DEFAULT_ENCODING;
	}

	public static void setEncoding(String encoding) {
		ErayEncryptUtil.DEFAULT_ENCODING = encoding;
	}
	
	/**
	 * 加密算法
	 * @param buffer 要加密的字节数组
	 * @param key 加密私钥
	 * @return 加密后的字节数据
	 */
	public static byte[] encrypt(byte[] buffer, String key) {
		try {
			byte[] ret = Arrays.copyOf(buffer, buffer.length);
			int pos, keylen;
			pos = 0;
			byte[] KEYVALUE = null;
			KEYVALUE = key.getBytes(DEFAULT_ENCODING);
			keylen = KEYVALUE.length;
			for (int i = 0; i < ret.length; i++) {
				ret[i] ^= KEYVALUE[pos];
				pos++;
				if (pos == keylen)
					pos = 0;
			}
			return ret;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * @see #encrypt(byte[], String)
	 */
	public static byte[] encrypt(byte[] buffer) {
		return encrypt(buffer, getKey());
	}
	/**
	 * convert @param encryptStr with default encoding UTF-8
	 * @see #encrypt(byte[])
	 * @param encryptStr
	 * @return
	 */
	public static byte[] encrypt(String encryptStr) {
		byte[] bytes;
		try {
			bytes = encryptStr.getBytes(DEFAULT_ENCODING);
			return encrypt(bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 解密算法
	 * @param buffer 解密的字节数组
	 * @return 解密后的字节数组
	 */
	public static byte[] decrypt(byte[] buffer, String key) {
		return encrypt(buffer, key);
	}
	
	public static byte[] decrypt(byte[] buffer) {
		return decrypt(buffer, getKey());
	}
	/**
	 * 使用默认的编码格式（UTF-8）将buffer转化成字符串
	 * @param buffer 要编码的字节数组
	 * @return
	 */
	public static String getString(byte[] buffer) {
		try {
			return new String(buffer, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 使用默认的编码格式（UTF-8）将str转化成字节数组
	 * @param str 
	 * @return
	 */
	public static byte[] getByteArray(String str) {
		try {
			return str.getBytes(DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * encrypt @src and convert it to base64 String, corresponding to {@link #decryptFromBase64Str(String)}
	 * @param src
	 * @return
	 */
	public static String encryptToBase64Str(String src, String key) {
		byte[] orignalByte = getByteArray(src);
		byte[] encrypted = encrypt(orignalByte, key);
		byte[] encodeBase64 = Base64.encodeBase64(encrypted);
		String ret = getString(encodeBase64);
		logger.trace("==> {} --> {} --> {} --> {} --> {}", src, Arrays.toString(orignalByte), Arrays.toString(encrypted),
				Arrays.toString(encodeBase64), ret);
		return ret;
	}
	/**
	 * @see #encryptToBase64Str(String, String)
	 */
	public static String encryptToBase64Str(String src) {
		return encryptToBase64Str(src, getKey());
	}
	/**
	 * descrypt base64 String to original String, corresponding to {@link #encryptToBase64Str(String)}
	 * @param src
	 * @return
	 */
	public static String decryptFromBase64Str(String src, String key) {
		byte[] encodeBase64 = getByteArray(src);
		byte[] decodeBase64 = Base64.decodeBase64(encodeBase64);
		byte[] decrypted = decrypt(decodeBase64, key);
		String ret = getString(decrypted);
		logger.trace("==> {} --> {} --> {} --> {} --> {}", src, Arrays.toString(encodeBase64), 
				Arrays.toString(decodeBase64), Arrays.toString(decrypted), ret);
		return ret;
	}
	/**
	 * @see #decryptFromBase64Str(String, String)
	 */
	public static String decryptFromBase64Str(String src) {
		return decryptFromBase64Str(src, getKey());
	}
	
}
