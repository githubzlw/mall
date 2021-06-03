package com.macro.mall.shopify.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {

	private StrUtils(){}
	/**Object转String
	 * @param object
	 * @return
	 */
	public static String object2Str(Object object){

		return object == null ? "" : object.toString();
	}
	/**Object转数字String
	 * @param object
	 * @return
	 */
	public static String object2NumStr(Object object){
		if(object == null || StringUtils.isBlank(object.toString())){
			return "0";
		}
		return isNum(object.toString()) ?  object.toString() : "0" ;
	}

	/**
	 * 字符串是否为产品区间价格
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @return
	 */
	public static boolean isRangePrice(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		str = str.replaceAll("(\\s*-\\s*)", "-");
		return Pattern.compile("(\\d+(\\.\\d+){0,1}(-\\d+(\\.\\d+){0,1}){0,1})")
				.matcher(str).matches();
	}

	/**
	 * 字符串是否为数字字符串
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		if (str == null) {
			return false;
		}
		return Pattern.compile("(\\d+)").matcher(str).matches();
	}

	/**
	 * 是否为带有小数点的数字字符串
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @return
	 */
	public static boolean isDotNum(String str) {
		if (str == null) {
			return false;
		}
		return Pattern.compile("(\\d+(\\.\\d+)?)").matcher(str).matches();

	}
	/**
	 * 匹配出带有小数点的数字字符串
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @return
	 */
	public static String matchDotNum(String str) {
		if (str == null) {
			return "";
		}
		Pattern p = Pattern.compile("(\\d+(\\.\\d+)?)");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return "";

	}

	/**
	 * 输入的字符串是否匹配正则表达式
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @param reg
	 * @return
	 */
	public static boolean isMatch(String str, String reg) {
		if (str == null || reg == null || reg.isEmpty()) {
			return false;
		}
		return Pattern.compile(reg).matcher(str).matches();
	}

	/**
	 * 输入的字符串中是否包含满足正则表达式的字符串
	 *
	 * @date 2016年4月28日
	 * @author abc
	 * @param str
	 * @param reg
	 * @return
	 */
	public static boolean isFind(String str, String reg) {
		if (str == null || reg == null || reg.isEmpty()) {
			return false;
		}
		return Pattern.compile(reg).matcher(str).find();
	}

	/**
	 * 匹配满足正则的字符串
	 *
	 * @date 2016年5月9日
	 * @author abc
	 * @param str
	 * @param reg
	 * @return
	 */
	public static String matchStr(String str, String reg) {
		if (StringUtils.isBlank(str) || StringUtils.isBlank(reg)) {
			return "";
		}
		if (reg.indexOf("(") == -1) {
			reg = "(" + reg;
		}
		if (reg.indexOf(")") == -1) {
			reg = reg + ")";
		}
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	/**
	 * 正则匹配相应格式的内容
	 *
	 * @date 2016年5月9日
	 * @author abc
	 * @param reg
	 * @param str
	 * @return
	 */
	public static List<String> matchStrList(String reg, String str) {
		List<String> mobj = new ArrayList<String>();
		if (str != null && !str.isEmpty()) {
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(str);
			while (m.find()) {
				mobj.add(m.group(1));
			}
		}
		return mobj;
	}

	public static int GetCharInStringCount(String text, String content) {
		if (StringUtils.isBlank(content)) {
			return 0;
		}
		String str = content.replace(text, "");
		return (content.length() - str.length()) / text.length();

	}

	/**
	 * Json中的数据去除特殊字符。
	 *
	 * @param s
	 * @return
	 */
	public static String stringToJson(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {

			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			// case '\\': //如果不处理单引号，可以释放此段代码，若结合下面的方法处理单引号就必须注释掉该段代码
			// sb.append("\\\\");
			// break;
			case '/':
				sb.append("\\/");
				break;
			case '\b': // 退格
				sb.append("\\b");
				break;
			case '\f': // 走纸换页
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n"); // 换行
				break;
			case '\r': // 回车
				sb.append("\\r");
				break;
			case '\t': // 横向跳格
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}

		String ors = sb.toString();
		ors = ors == null ? "" : ors;
		StringBuffer buffer = new StringBuffer(ors);
		int i = 0;
		while (i < buffer.length()) {
			if (buffer.charAt(i) == '\'' || buffer.charAt(i) == '\\') {
				buffer.insert(i, '\\');
				i += 2;
			} else {
				i++;
			}
		}
		return buffer.toString();

	}

	/**Object转数字String
	 * @param object
	 * @return
	 */
	public static double object2Double(Object object){
		if(object == null || StringUtils.isBlank(object.toString())){
			return 0.0;
		}
		return isDotNum(object.toString()) ?  Double.parseDouble(object.toString()) : 0.0 ;
	}
}
