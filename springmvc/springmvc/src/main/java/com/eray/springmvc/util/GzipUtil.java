package com.eray.springmvc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
/**
 * 提供压缩字节数据的方法
 * @author kevin
 */
public class GzipUtil {

	/**
	 * 压缩
	 * @param compressByte
	 * @return 如果compressByte为null或者长度为0，则直接返回compressByte
	 */
	public static byte[] compress(byte[] compressByte) {
		if (compressByte == null || compressByte.length == 0) {
			return compressByte;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(compressByte);
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * 解压缩
	 * @param uncompressByte
	 * @return 如果compressByte为null或者长度为0，则直接返回compressByte
	 */
	public static byte[] uncompress(byte[] uncompressByte) {
		if (uncompressByte == null || uncompressByte.length == 0) {
			return uncompressByte;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(uncompressByte);
		GZIPInputStream gunzip;
		try {
			gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	
	public static void main(String[] args) throws Exception {
		String str = "的发生了大幅浪费的";
		for(int i=0; i<10; i++) {
			str += str;
		}
		System.out.println("原始字符串长度：" + str.length());
		byte[] bytes = str.getBytes("utf-8");
		System.out.println("压缩前大小：" + bytes.length);
		byte[] compress = compress(str.getBytes("utf-8"));
		System.out.println("压缩后大小：" + compress.length);
		byte[] uncompress = uncompress(compress);
		System.out.println("解压后大小：" + uncompress.length);
		System.out.println("解压缩内容：" + new String(uncompress, "utf-8"));
	}


}