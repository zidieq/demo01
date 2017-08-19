package Desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import Util.FileUtil;
import Util.OtherUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class getValueOfTaobao {

	public static void main(String[] args) {
		// String valueString=getWeb("536450419180");
		// FileUtil.println(valueString, "in.txt");
		// System.out.println(valueString);
		String fileName = String.valueOf(System.currentTimeMillis());
		System.out.println("请手动输入要查询的淘宝连接的SPUID：");

		String spu = new Scanner(System.in).nextLine();
		int pageNo = 2;
		FileUtil.println(
				"<!doctype html><head><title>" + spu + "的评价数据</title><meta charset=\"utf-8\"/></head><body><table>",
				fileName + ".txt");
		StringBuilder sb = new StringBuilder();
		for (int pageIndex = 1; pageIndex < pageNo + 1; pageIndex++) {
			String valueString = "";
			int tryCount = 0;
			while (tryCount++ < 100) {
				try {
					valueString = getWeb(spu, String.valueOf(pageIndex));
					// System.out.println(valueString);
					if (valueString.indexOf("jsonp_tbcrate") > -1) {
						valueString = valueString.substring(valueString.indexOf("(") + 1, valueString.lastIndexOf(")"));
					} else {
						throw new IOException();
					}

					JSONObject job = JSONObject.fromObject(valueString);
					if (job.getInt("maxPage") > 1) {
						pageNo = job.getInt("maxPage");
					}
					JSONArray jarr = job.getJSONArray("comments");
					for (int i = 0; i < jarr.size(); i++) {
						JSONObject jo = jarr.getJSONObject(i);
						String sku = jo.getJSONObject("auction").getString("sku");
						String skuId = jo.getJSONObject("auction").getString("aucNumId");
						String dateString = jo.getString("date");
						String content = jo.getString("content");
						// OtherUtil.showJob(jo);
						sb.append("<tr><td>").append(sku).append("</td><td>").append(skuId).append("</td><td>")
								.append(dateString).append("</td><td>").append(content).append("</td></tr>");

						System.out.println(sb.toString());
						FileUtil.println(sb.toString(), fileName + ".txt");
						sb.delete(0, sb.length());
					}
					break;
				} catch (Exception e) {
					System.out.println("出了点问题，将在5-10秒后进行第" + tryCount + "次尝试");
					OtherUtil.delaySomeTime(10000);

					e.printStackTrace();
				}
			}
		}
		FileUtil.println("</table></body></html>", fileName + ".txt");
	}

	/**
	 * 获取淘宝的评价数据
	 */
	public static String getWeb(String spu, String pageNo) throws IOException {
		StringBuilder sb = new StringBuilder();
		String postData = "";
		String urlString = "https://rate.taobao.com/feedRateList.htm?auctionNumId=" + spu + "&currentPageNum=" + pageNo
				+ "&pageSize=20&rateType=&orderType=sort_weight&attribute=&sku=&hasSku=false&folded=0&_ksTS="
				+ System.currentTimeMillis() + "_" + (int) Math.random() * 1000
				+ "&callback=jsonp_tbcrate_reviews_list";
		// System.out.println(urlString);
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Host", " rate.taobao.com ");
		conn.addRequestProperty("Connection", " keep-alive ");
		conn.addRequestProperty("Accept", " */* ");
		conn.addRequestProperty("User-Agent",
				" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.16400.812 Safari/537.36 ");
		conn.addRequestProperty("Referer",
				" https://item.taobao.com/item.htm?spm=686.1000925.0.0.gPm4eO&id=43685519817 ");
		conn.addRequestProperty("Accept-Encoding", " gzip, deflate ");
		conn.addRequestProperty("Accept-Language", " zh-CN,zh;q=0.8 ");
		// conn.addRequestProperty("Cookie",
		// " cna=D3q/DxHJGDUCAT2xT2UIXGBr; thw=cn; miid=327281873017096483;
		// tk_trace=oTRxOWSBNwn9dPyscxqAz9fIOLh%2F60F2rFuXSJqGkbd9MOQbk78c6Smpi4mGnky1AhSJDcdGFjpOm%2FFo9mziqwl9c%2FAQhPbVAkyXbsnFCr1L7TxhKK5JfsHz9xuESg0HSqd3v4EuOb%2BtC3utrczI2S4H%2FUU%2FSwvpyoQvEu8ReoopreT7JiflCz4bxw5axYxblIUZxpRwEwAOi%2B5KyVw%2F%2FfYPUhtPyb%2FSKmjQI7ZH7FFTDR7pGkGn%2FY9%2BwTU3L4RhRVioxJZnxpTZg4wLufS4ZxWeM5ffpCSKLju%2F72PnbtiJP%2BDfWdAoAdKFATM75TtjinIUXJjmQoK6DdfL%2BfaU69YmgRSXbx2zuc2nT9NnPk7nKqvI%2FBTCHnRG6wr8J3OI%2BWCCKb9k8vFf;
		// _m_user_unitinfo_=unit|unsz; _m_unitapi_v_=1479901863626;
		// ctoken=IV0G0aUuVhIBaudBbZlnrhllor;
		// _m_h5_tk=63b6c8b3f3103a4077ff61aae13eddb4_1481197202014;
		// _m_h5_tk_enc=7ff8484b2e73f6ccc852e12df0978ad7;
		// linezing_session=EilIzHRpJZuTGtj5kNUJrhDN_1481194590817QfCv_1;
		// x=27796690;
		// uc3=sg2=VTq2JXMtA7k7oqZ9Obkl5vp7HG%2BrByJTabk7NuBEego%3D&nk2=&id2=&lg2=;
		// uss=BxBC6vHvwZlABhvh0rsXq0HAAGqqI9Nl9Mk%2FbQCx%2Fwiu8Eqgzf32NUkUTR4%3D;
		// tracknick=; sn=zxfnj%3A%E4%B8%81%E4%B8%81; skt=35137bd20262dfff;
		// unb=3009755826; t=b1329e19a7b0d532995b9fce8beb6fa6;
		// cookie2=130c2ff08188daf7c0d0e2c601b7a8fa; v=0;
		// _tb_token_=e38e3784885d3; mt=ci%3D-1_0;
		// uc1=cookie14=UoW%2FXG0jXFHDng%3D%3D&lng=zh_CN;
		// isg=AqamD5H_wnuhSJbSsBTDMmIm4xzXc8NY5x3aH5BPNUmgE0Qt-BQ3U2OlHYBA;
		// l=AsLCusLaubMJODEX-PHGb128ksIkNMap ");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(conn.getInputStream()), "gbk"));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}
}
