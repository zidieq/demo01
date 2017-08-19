package Desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import Util.FileUtil;
import Util.OtherUtil;

public class CountDataForTaobao {
	private static String cookie = FileUtil.getFileContext("cookie.txt").replace("\n", "").trim();
/**
 * 用于统计单品的实时趋势的数据
 * @param args
 * @throws IOException
 */
	public static void main(String[] args) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = sdf.format(new Date()).toString();
		while (true) {
			try {

				String[] spus = FileUtil.getFileContext("fh.txt").split("\n");
				for (String spu : spus) {
					timeString = sdf.format(new Date());
					String html = getEverage(spu);
					String result = timeString + "@" + spu + "@" + html;
					System.out.println(result);
					FileUtil.println(result, "record.txt");
				}
				System.out.println("****************************" + timeString + "******************************");
				OtherUtil.delaySomeTime(15000);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("出了点问题,将在15-30秒内进行再次尝试");
				OtherUtil.delaySomeTime(30000);
			}
		}
	}

	/**
	 * 获得单品的分时数据
	 * 
	 */
	public static String getEverage(String spu) throws IOException {
		StringBuilder sb = new StringBuilder();
		String postData = "";
		URL url = new URL("https://sycm.taobao.com/ipoll/live/summary/getItemHourTrend.json?device=0&itemId=" + spu
				+ "&token=5e81af27a&_=" + System.currentTimeMillis());
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Host", " sycm.taobao.com ");
		conn.addRequestProperty("Connection", " keep-alive ");
		conn.addRequestProperty("User-Agent",
				" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.16400.812 Safari/537.36 ");
		conn.addRequestProperty("Accept", " */* ");
		conn.addRequestProperty("Referer",
				" https://sycm.taobao.com/ipoll/rank.htm?spm=a21ag.7622617.LeftMenu.d180.y36Hnf ");
		conn.addRequestProperty("Accept-Encoding", "deflate ");
		conn.addRequestProperty("Accept-Language", " zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("Cookie", cookie);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
		out.println(postData);
		out.flush();
		out.close();
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "utf-8"));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}
}
