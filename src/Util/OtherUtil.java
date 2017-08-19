package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 采用MD5加密解密
 * 
 * @author tfq
 * @datetime 2011-10-13
 */
public class OtherUtil {

	// 测试主函数
	public static void main(String args[]) throws ParseException {
		// String s = new String("123456");
		// System.out.println("原始：" + s);
		// System.out.println("MD5后：" + string2MD5(s));
		// System.out.println("加密的：" + convertMD5(s));
		// System.out.println("解密的：" + convertMD5(convertMD5(s)));
		String d = "2016-09-30";
		// System.out.println(dateAdd(d, -20));
		for (int i = 0; i < 40; i++) {

			System.out.println(dateAdd(d, -i));
		}
	}

	/**
	 * 展示一个jsonObject的第一层数据
	 * 
	 * @param job
	 */
	public static void showJob(JSONObject job) {
		Iterator<String> itor = job.keys();
		while (itor.hasNext()) {
			String key = itor.next();
			String str = job.getString(key);
			System.out.println(key + ":" + str);
		}
	}

	public static void showJobArr(JSONArray jarr) {
		for(int i=0;i<jarr.size();i++){
			System.out.println("the data index of:"+i);
			System.out.println(jarr.get(i));
		}
	}

	/**
	 * 在yyyy-mm-dd的日期上加addDay天后的日期的字符串
	 * 
	 * @param datestr
	 * @param addDay
	 * @return
	 * @throws ParseException
	 */
	public static String dateAdd(String datestr, Integer addDay) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(datestr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(cal.DAY_OF_YEAR, addDay);
		date = cal.getTime();
		return sdf.format(date);
	}

	/***
	 * MD5加码 生成32位md5码
	 */
	public static String string2MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}

	/**
	 * 加密解密算法 执行一次加密，两次解密
	 */
	public static String convertMD5(String inStr) {

		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;

	}

	/**
	 * add specfied hours to startDate
	 * 
	 * @param startDate
	 * @param addHours
	 */
	public static String dateAddHours(String startDate, Integer addHours) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dStart = sdf.parse(startDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dStart);
			cal.add(cal.HOUR, 1);
			dStart = cal.getTime();
			startDate = sdf.format(dStart);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;

	}

	/**
	 * just delay some time for avoid request too frequently to blocked by web
	 */
	public static void delaySomeTime() {
		try {
			Thread.sleep(200 + (int) (Math.random() * 800));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 延迟从milis的一半到milis长的时间
	 * 
	 * @param milis
	 */
	public static void delaySomeTime(long milis) {
		try {
			Thread.sleep(milis / 2 + (int) (Math.random() * milis / 2));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用干净的请求头获取webpage的页面内容
	 * 
	 * @param urlstr
	 * @return
	 * @throws IOException
	 */
	public static String getWebPage(String urlstr) throws IOException {
		int n = 0;
		String result = null;
		while (n++ < 10) {
			try {
				result = OtherUtil.getWebPage(urlstr, "gbk");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result != null && result.length() > -1) {
				return result;
			}
		}
		// return null;
		throw new IOException();
	}

	public static String getWebPage(String urlstr, String encode) throws IOException {
	
		StringBuilder sb = new StringBuilder();
		String postData = "";
		URL url = new URL(urlstr);
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8 ");
		conn.addRequestProperty("Accept-Encoding", "deflate, sdch ");
		conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("Cache-Control", "no-cache ");
		conn.addRequestProperty("Connection", "keep-alive ");
		conn.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), encode));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}

	/**
	 * 把long值的时间转换为yyyy-MM-dd HH:mm:ss的日期
	 * @param datestr
	 * @return
	 */
	public static String longToDate(String datestr) {
		Date date=new Date(Long.parseLong(datestr));
		datestr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return datestr;
	}
}