package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * 根据相应格式获取当前时间
	 * @param fmt
	 * @return
	 */
	public static String now(String fmt) {
		if (fmt == null) {
			fmt = "yyyy-MM-dd";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(new Date());
	}
	/** 使用默认格式获取当前时间 */
	public static String now() {
		return now(null);
	}

	/**
	 * 给定一个时间字符串和时间格式,判断是否为当前时间
	 * @param dateStr 时间字符串
	 * @param fmt 时间格式
	 * @return
	 */
	public static boolean isNow(String dateStr, String fmt) {
		String now = now(fmt);
		return dateStr.equals(now);
	}

	public static void main(String[] args) {
		System.out.println(isNow("4月29日","M月d日"));
	}

}
