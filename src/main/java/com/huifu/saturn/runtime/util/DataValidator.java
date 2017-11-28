/*
 * @(#)DataValidator.java 1.0 2009-12-28下午02:49:32
 *
 * 上海汇付网络科技有限公司
 * Copyright (c) 2006-2010 ChinaPnR, Inc. All rights reserved.
 */
package com.huifu.saturn.runtime.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.Assert;


/**
 * <dl>
 *       <dt><b>Title:从天天盈迁移</b></dt>
 *    <dd>
 *    	数据校验器
 *    </dd>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *    	<p>facade of all kinds of relative utils,mainly be designed for using outside the framework
 *    </dd>
 * </dl>
 *
 * @author eric
 * @version 1.0, 2009-12-28
 * @since framework-1.4
 * 
 */
public class DataValidator {
	 /**
	 * 检验 日期是否在指定区间内，如果格式错误，返回false
	 * 
	 * 如果maxDateStr或minDateStr为空则比较时变为正负无穷大，如果都为空，则返回false
	 * 
	 * @param aDate 
	 * @param minDateStr 必须是yyyy-MM-dd格式，时分秒为00:00:00
	 * @param maxDateStr 必须是yyyy-MM-dd格式，时分秒为00:00:00
	 * @return
	 */
	public static final boolean isDateBetween(Date aDate, String minDateStr, String maxDateStr){
		return DateUtils.isDateBetween(aDate, minDateStr, maxDateStr);
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * 没办法,有的程序员不习惯用Apache Common或Spring的工具类
	 * 
	 * @param str
	 * @return
	 */
	public static final boolean isStrNotBlank(String str){
		return StringUtils.isNotBlank(str);
	}
	
	/**
	 * 检查字符串是否为空
	 * @param str
	 */
	public static final void checkStringNotBlank(String str){
		Assert.hasText(str);
	}

	/**
	 * 检查字符串是否为空，并且长度是否为指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenEqual(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()==l));
	}
	
	/**
	 * 检查字符串是否为空，并且长度是否小于指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenLess(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()<l));
	} 
	
	/**
	 * 检查字符串是否为空，并且长度是否小等于指定值
	 * @param str 
	 * @param l 长度
	 * @return
	 */
	public static final boolean isStrLenLessEqual(String str,int l){
		return (StringUtils.isNotBlank(str)&&(str.trim().length()<=l));
	} 
	
	/**
	 * 判断（如"0123","123L","12.3S"等带有小数点和后缀的）字串，是否代表数字类型
	 * @param str
	 * @return
	 */
	public static final boolean isNumber(String str){
		return NumberUtils.isNumber(str);
	}
	
	/**
	 * 检查（如"0123","123L","12.3S"等带有小数点和后缀的）字串，是否代表数字类型
	 * @param str
	 */
	public static final void checkNumber(String str){
		Assert.isTrue(isNumber(str),"'"+str+"' must be a Number format here.");
	}
	
	/**
	 * 判断字符串中只含有数字字符
	 * @param str
	 * @return
	 */
	public static final boolean isDigits(String str){
		return NumberUtils.isDigits(str);
	}
	
	/**
	 * 判断Long、Integer、Short、Double、Float等数字是否为空或者0
	 * @param number
	 * @return
	 */
	public static final boolean isNumberNotNullOrZero(Number number){
		return (number!=null&& number.doubleValue()!=0);
	}
	
	/**
	 * 检查Long、Integer、Short、Double、Float等数字是否为空或者0
	 * @param number
	 */
	public static final void checkNumberNotNullOrZero(Number number){
		Assert.isTrue(isNumberNotNullOrZero(number),"Number must not be null or zero.");
	}
	
	/**
	 * 判断字串是否符合yyyy-MM-dd格式
	 * @param str
	 * @return
	 */
	public static final boolean isShortDateStr(String aDateStr){
		try {
			DateUtils.parseShortDateString(aDateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 检查字串是否符合yyyy-MM-dd格式
	 * @param number
	 */
	public static final void checkShortDateStr(String aDateStr){
		Assert.isTrue(isShortDateStr(aDateStr),"The str-'"+aDateStr+"' must match 'yyyy-MM-dd' format.");
	}
	
	/**
	 * 判断字串是否符合yyyy-MM-dd HH:mm:ss格式
	 * @param str
	 * @return
	 */
	public static final boolean isLongDateStr(String aDateStr){
		try {
			DateUtils.parseLongDateString(aDateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;		
	}
	
	/**
	 * 检查字串是否符合yyyy-MM-dd HH:mm:ss格式
	 * @param str
	 */
	public static final void checkLongDateStr(String aDateStr){
		Assert.isTrue(isLongDateStr(aDateStr),"The str-'"+aDateStr+"' must match 'yyyy-MM-dd HH:mm:ss' format.");
	}
	
	/**
	 * 判断字串是否符合yyyyMMddHHmmss格式
	 * @param str
	 * @return
	 */
	public static final boolean isMailDateStr(String aDateStr){
		try {
			DateUtils.parseMailDateString(aDateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;		
	}
	
	/**
	 * 判断字串是否符合yyyyMMdd格式
	 * @param str
	 * @return
	 */
	public static final boolean isMailDateDtPartStr(String aDateStr){
		try {
			DateUtils.parseMailDateDtPartString(aDateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;		
	}
	
	/**
	 * 检查字串是否符合yyyyMMddHHmmss格式
	 * @param str
	 */
	public static final void checkMailDateStr(String aDateStr){
		Assert.isTrue(isMailDateStr(aDateStr),"The str-'"+aDateStr+"' must match 'yyyyMMddHHmmss' format.");
	}
	
	/**
	 * 判断字串是否符合指定的日期格式
	 * @param str
	 * @return
	 */	
	public static final boolean isDateStrMatched(String aDateStr,String formatter){
		try {
			DateUtils.parser(aDateStr, formatter);
		} catch (ParseException e) {
			return false;
		}
		return true;		
	}	
	
	/**
	 * 判断对象数组是否为空
	 * @param <T>
	 * @param object
	 * @return
	 */
	public static final boolean isArrayNotEmpty(Object[] object){
		return !ArrayUtils.isEmpty(object);
	}
	
	/**
	 * 检查对象数组是否为空
	 * @param <T>
	 * @param object
	 */
	public static final void checkArrayNotEmpty(Object[] object){
		Assert.notEmpty(object);
	}
	
	/**
	 * 判断身份证号是否正确
	 * @param ip
	 * @return
	 */
	public static final boolean isCertId(String certId){
 		return RegexHelper.isMatch(certId,"\\d{15}|\\d{17}[\\dXx]");
	}
	
	/**
	 * 判断ip格式是否正确
	 * @param ip
	 * @return
	 */
	public static final boolean isIPAddress(String ip){
 		return RegexHelper.isMatch(ip,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
	}
	
	/**
	 * 判断密码是否6到12位
	 * false 为非6-12位或汉字
	 * */
	public static final boolean isPasswordFormat(String password){
 		return RegexHelper.isMatch(password,"^(.{6,12})$")&&RegexHelper.isMatch(password,"^([\\x00-\\xff]+)");
	}
	
	/**
	 * 判断email格式是否正确
	 * @param email
	 * @return
	 */
	public static final boolean isEmail(String email){
		return RegexHelper.isMatch(email,"^([a-zA-Z0-9_\\.\\-]+)(@{1})([a-zA-Z0-9_\\.\\-]+)(\\.[a-zA-Z0-9]{1,3})$");
	}
	
	/**
	 * 判断电话号码格式是否正确
	 * @param phoneNum
	 * @return
	 */
	public static final boolean isPhoneNum(String phoneNum){
		return RegexHelper.isMatch(phoneNum,"^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$");
	}
	
	/**
	 * 判断手机号码格式是否正确
	 * @param cellPhoneNum
	 * @return
	 */
	public static final boolean isCellPhoneNum(String cellPhoneNum){
		/*
		 * 2011-05-09 lele.feng
		 * 手机号格式判断1开头11位数字
		 * start
		 */
		//return RegexHelper.isMatch(cellPhoneNum,"^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1(3|5|8)\\d{9}$");
		return RegexHelper.isMatch(cellPhoneNum,"^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1\\d{10}$");
		/*
		 * 2011-05-09 lele.feng
		 * end
		 */
	}
	
	/**
	 * 判断邮编格式是否正确
	 * @param postalcode
	 * @return
	 */
	public static final boolean isPostalcode(String postalcode){
		return RegexHelper.isMatch(postalcode,"^[1-9]\\d{5}$");
	}
	
	/**
	 * 判断是否是客户号
	 * @param custId
	 * @return
	 */
	public static final boolean isCustId(String custId){
		return RegexHelper.isMatch(custId, "[6][0]{4}[0-9]{11}");
	}
	
	/**
	 * 判断是否是商户号
	 * @param merId
	 * @return
	 */
	public static final boolean isMerId(String merId){
		return RegexHelper.isMatch(merId, "[6|8][0-9]{5}");
	}
	
	/**
	 * 判断是否是商户银行
	 * @param merId
	 * @return
	 */
	public static final boolean isMerBank(String merCode){
		return RegexHelper.isMatch(merCode, "(^E0[0-9]{6}$)");
	}
	
	/**
	 * 判断是否正确银行卡号
	 */
	public static final boolean isCardNo(String cardNo){
		return RegexHelper.isMatch(cardNo, "(^[0-9]{12,25}$)");
	}
	private DataValidator(){}
	
	
}
