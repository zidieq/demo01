package Desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import Util.FileUtil;
import Util.OtherUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MingdanForTaobao {
	private static String cookie = "cookie:thw=cn; CNZZDATA1256793290=734712545-1481779132-https%253A%252F%252Fsell.taobao.com%252F%7C1482190588; miid=404491236919170189; _m_user_unitinfo_=unit|unsz; _m_unitapi_v_=1482385378664; _m_h5_tk=0f4eae5744675a4853b1bdaea602c058_1482482662072; _m_h5_tk_enc=d5b4242e3e7f70749df8b1dc7ebb275e; cna=D3q/DxHJGDUCAT2xT2UIXGBr; ctoken=kKR6psiJDjGN19u1iej8rhllor; ali_ab=58.216.143.142.1481863086806.8; v=0; uc3=sg2=UNbUP0rjTUNWX%2BuHmKt5ddHJrbHcMLHWW07k5yLtbMc%3D&nk2=GdBoSu4%3D&id2=UU8PYim%2FEUQ%3D&vt3=F8dARHfArPy2gqIX7eg%3D&lg2=UtASsssmOIJ0bQ%3D%3D; existShop=MTQ4MjYyNTk2MQ%3D%3D; uss=V3kWbVlIVifMywx%2BScrP%2BhuG%2FiFSKzK6X1GAhnVkbdqe1S3wJA2mbouy6Q%3D%3D; lgc=zxfnj; tracknick=zxfnj; cookie2=17a5dcd84d3d55335258214033d725ea; sg=j02; mt=np=&ci=54_1&cyk=14_0; cookie1=B0Fh8OfKpeiw4aV0Ws5W78xz1ti55ovSMyB7t1PLw7I%3D; unb=27796690; skt=b67ff8b611df3397; t=d917f385aafde3efb19699ee30901af5; publishItemObj=Ng%3D%3D; _cc_=W5iHLLyFfA%3D%3D; tg=0; _l_g_=Ug%3D%3D; _nk_=zxfnj; cookie17=UU8PYim%2FEUQ%3D; uc1=cookie14=UoW%2FX9vkGm40FA%3D%3D&lng=zh_CN&cookie16=UtASsssmPlP%2Ff1IHDsDaPRu%2BPw%3D%3D&existShop=true&cookie21=V32FPkk%2Fhodroid7SeaDgw%3D%3D&tag=5&cookie15=UtASsssmOIJ0bQ%3D%3D&pas=0; _tb_token_=DGBbwGz3wReuI7HfzABf; apushac2c6d0fba5825c4d7898605715340b7=%7B%22ts%22%3A1482628514723%2C%22parentId%22%3A1482628497662%7D; l=AqGhnt-Tyl4OTXuOTQ3Vh2q5MWe5hRVD; isg=AiIimCV_jiIVRJL9YVRmum-vZ6jsF28iCn0kmmy5hRVDP8i5VwHdnVT5GfQi";

	public static void main(String[] args) throws IOException {
		// 获取fh.txt文件中的每一行的SPU对应的订单列表，打印到order.txt中
		getOrderList();

	}

	/**
	 * 获取订单的付款时间
	 * 
	 * @param orderNo
	 * @return
	 * @throws IOException
	 */
	private static String getPayTimeOfOrder(String orderNo) throws IOException {
		String html = getOrderDetail(orderNo);
		html = html.substring(html.indexOf("var data =") + 10);
		html = html.substring(0, html.indexOf("</script>")).trim();
		JSONObject job = JSONObject.fromObject(html).getJSONObject("mainOrder").getJSONObject("orderInfo");
		JSONArray jarr = job.getJSONArray("lines");
		for (int i = 0; i < jarr.size(); i++) {
			JSONArray lineArr = jarr.getJSONObject(i).getJSONArray("content");
			boolean isPay = false;
			String payTime = "";
			for (int j = 0; j < lineArr.size(); j++) {
				JSONObject lineJob = lineArr.getJSONObject(j);
				if (lineJob.getJSONObject("value").getString("name").indexOf("付款时间") > -1) {
					payTime = lineJob.getJSONObject("value").getString("value");
					return payTime;
				}
			}
		}
		return null;
	}

	/**
	 * 获取fh.txt文件中的每一行的SPU对应的订单列表，打印到order.txt中
	 * 
	 * @throws IOException
	 */
	private static void getOrderList() throws IOException {
		// String html=getWeb();
		// System.out.println(html);
		// FileUtil.println(html, "order.txt");
		String[] spus = FileUtil.getFileContext("fh.txt").split("\n");
		FileUtil.println("SPU,订单编号,交易状态,下单时间,付款时间,付款金额,昵称", "order.txt");

		// 以下为对文件fh.txt中的每一个连接进行查询订单编号列表的过程
		for (String spu : spus) {

			// 先假设订单只有一页
			int pageCount = 1;

			// 查询该SPU下每一页的订单列表
			for (int pageIndex = 1; pageIndex <= pageCount; pageIndex++) {

				// 获取网页信息
				String jstr = getOrderList(spu, pageIndex);

				// 打印出来，主要是调试使用
				System.out.println(jstr);

				// 解析实际的页数，如果大于1，就根据实际页数来调整
				int pageCounts = JSONObject.fromObject(jstr).getJSONObject("page").getInt("totalPage");
				if (pageCounts > 1) {
					pageCount = pageCounts;
				}

				//
				JSONArray jarr = JSONObject.fromObject(jstr).getJSONArray("mainOrders");

				//
				for (int i = 0; i < jarr.size(); i++) {
					JSONObject job = jarr.getJSONObject(i);
					System.out.println(job);
					// 订单编号
					String orderId = job.getString("id");
					// 下单时间
					String cTime = job.getJSONObject("orderInfo").getString("createTime");
					// 订单状态
					String status = job.getJSONObject("statusInfo").getString("text");
					// 买家昵称
					String nick = job.getJSONObject("buyer").getString("nick");
					// 付款时间
					String payTime = "";
					payTime = getPayTimeOfOrder(orderId);
					// 实付款金额
					String fee = job.getJSONObject("payInfo").getString("actualFee");

					String result = spu + ",'" + orderId + "," + status + "," + cTime + "," + payTime + "," + fee + ","
							+ nick;
					System.out.println(result);
					FileUtil.println(result, "order.txt");
				}
			}

		}
	}

	/**
	 * 根据连接SPUID来获取对应的淘宝订单编号和创建时间
	 * 
	 * @param spu
	 *            连接
	 * @param pageNum
	 *            第几页的
	 */
	public static String getOrderList(String spu, int pageNum) throws IOException {
		StringBuilder sb = new StringBuilder();
		// 1481472000788
		// 1481473200917
		String postData = "auctionType=0&close=0&pageNum=" + pageNum
				+ "&pageSize=15&queryMore=true&rxAuditFlag=0&rxHasSendFlag=0&rxOldFlag=0&rxSendFlag=0&rxSuccessflag=0&tradeTag=0&useCheckcode=false"
				+ "&useOrderInfo=false&errorCheckcode=false&action=itemlist%2FSoldQueryAction"
				+ "&dateBegin=1482595200019" + "&dateEnd=1482624000042"
				+ "&prePageNo=1&buyerNick=&logisticsService=&orderStatus=&queryOrder=desc&rateStatus=&refund=&sellerNick=&tabCode=latest3Months&auctionId="
				+ spu + "&itemTitle=";
		URL url = new URL(
				"https://trade.taobao.com/trade/itemlist/asyncSold.htm?event_submit_do_query=1&_input_charset=utf8");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Host", " trade.taobao.com ");
		conn.addRequestProperty("Connection", " keep-alive ");
		conn.addRequestProperty("Content-Length", " 436 ");
		conn.addRequestProperty("Accept", " application/json, text/javascript, */*; q=0.01 ");
		conn.addRequestProperty("Origin", " https://trade.taobao.com ");
		conn.addRequestProperty("X-Requested-With", " XMLHttpRequest ");
		conn.addRequestProperty("User-Agent",
				" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.16400.812 Safari/537.36 ");
		conn.addRequestProperty("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8 ");
		conn.addRequestProperty("Referer",
				" https://trade.taobao.com/trade/itemlist/list_sold_items.htm?spm=686.1000925.a1zvx.d28.mv10ww&mytmenu=ymbb ");
		conn.addRequestProperty("Accept-Encoding", "deflate ");
		conn.addRequestProperty("Accept-Language", " zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("Cookie", cookie);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
		out.println(postData);
		out.flush();
		out.close();
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "gbk"));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}

	/**
	 * 查询指定订单编号的付款时间
	 * 
	 * @param orderNo
	 *            订单编号
	 */
	public static String getOrderDetail(String orderNo) throws IOException {
		StringBuilder sb = new StringBuilder();
		String postData = "";
		URL url = new URL("https://trade.taobao.com/trade/detail/trade_order_detail.htm?biz_order_id=" + orderNo);
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8 ");
		conn.addRequestProperty("accept-encoding", "deflate ");
		conn.addRequestProperty("accept-language", "zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("cache-control", "max-age=0 ");
		conn.addRequestProperty("cookie", cookie);
		conn.addRequestProperty("referer",
				"https://trade.taobao.com/trade/itemlist/list_sold_items.htm?spm=686.1000925.a1zvx.d28.eJpoCK&mytmenu=ymbb ");
		conn.addRequestProperty("upgrade-insecure-requests", "1 ");
		conn.addRequestProperty("user-agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.16400.16 Safari/537.36");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "gbk"));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}
}
