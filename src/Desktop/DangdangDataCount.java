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
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import Util.FileUtil;
import Util.OtherUtil;
import net.sf.json.JSONObject;
/**
 * 
 * 
 * @author Administrator
 *
 */
public class DangdangDataCount {
	private static final String shopId = "18486";
	private static final String cookie = " __permanent_id=20161220083012805312990272436939122; permanent_key=20161220083010709723399669ddf293; login.dangdang.com=.AYH=2016122008301112111930999&.ASPXAUTH=5NgJY3moVMAN9XdfH2ku5w==; dangdang.com=email=emlkaWVxQHFxLmNvbQ==&nickname=&display_id=9224415430634&customerid=rVoBwGpX3xmWmsZ1h6jVFg==&viptype=c72jjkBnHT4=&show_name=zidieq; ddoy=email=zidieq%40qq.com&nickname=&agree_date=1&validatedflag=0&uname=zidieq%40qq.com&utype=0&.ALFG=on&.ALTM=1482193817; sessionID=pc_29d8efaa7ba57d49642b0a69a9bdc02acb1294b1f98d3a6f1e8d32253cf8ad0d; __dd_token_id=2016122008301740984031480168b1da; unique_id=18621c7cfecad43dbea82688a6ff33f3; PHPSESSID=t5pock9nqqraogrr7bmbjig656; MDD_sid=baa8307a98f2c74158df8401b8cebae5; MDD_permanent_id=20161220150125743558443244578553010; MDD_ddclick_visit=2; MDD_ddclickc=1; MDD_producthistoryids=1273927136%257C1268640436%257C1268581636; shopid=18486; shop_type=0; shopfullname=%D2%DA%BC%D2%B4%EF%C6%EC%BD%A2%B5%EA; shopname=%D2%DA%BC%D2%B4%EF%C6%EC%BD%A2%B5%EA; username=%D5%C5%C7%EC%C7%BF; shopstatus=1; shopserverid=4; shopdbid=7; shopeditor=tinymce; custid=180440965; email=zidieq%40qq.com; nickname=null; userTel=15253150860; xdata=U63jcaifamefT51te1Za9d9093y6cqc20d90bcN79h36e1ee; mobileValidate=9C2A3415E5D57A801588709D91C18C86; ddscreen=2; LOGIN_TIME=1482295561633; _jzqco=%7C%7C%7C%7C%7C1.1809842106.1482201991228.1482243001074.1482295561768.1482243001074.1482295561768.0.0.0.38.38; __xsptplus100=100.9.1482295561.1482295561.1%234%7C%7C%7C%7C%7C%23%23fPPXa2GtFpcIrFJWg_VUVpGVUqspiT5-%23; producthistoryid=1269035536%2C1274364836%2C1273927136%2C1273148036%2C1273147436%2C1269037536%2C1269035736%2C1268592036%2C1273268636%2C1273907036; nTalk_CACHE_DATA={uid:dd_1000_ISME9754_180440965,tid:1482242597684909}; NTKF_T2D_CLIENTID=guest0EBF7268-AC16-985F-BB95-1A1F928BD77E; pos_1_start=1482295562975; pos_1_end=1482295562987; __visit_id=20161221124601639366678267502994055; __out_refer=; __trace_id=20161221124604770379738930426347921; dest_area=country_id%3D9000%26province_id%3D132%26city_id%3D1608%26district_id%3D1320405%26town_id%3D132040502; expiretime=1482332079; topBarIndex=0; report_user=kXTvTpOgViO01jIzY5HWFlfOjzzFq7Y7PrZCnDgYPtQadeNIes22USFAE175m%2FAjxP1%2BXw%2FwZrGFM4IoxbogaQHxtpjw6iKPQ%2FeDZ0NzWiHJjUVczXMjjnxiRRnffimUJ2zMwwclKFd%2FHHkDVm514u%2F0PC1oxDyceZLuz4d%2BZEg%3D; report_key=5162 ";

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = sdf.format(new Date()).toString();
		while (true) {
			try {
				timeString = sdf.format(new Date());
				String result = getHotSellList(shopId);
				// System.out.println(timeString+","+result.substring(0, 100));
				if (result != null && result.startsWith("real")) {
					result = "{\"" + result;
				}
				FileUtil.println(timeString + "," + result, "dangdangdatarecord.txt");
				JSONObject jobShop = JSONObject.fromObject(result).getJSONObject("shop_data");
				Iterator<String> shops = jobShop.keys();
				while (shops.hasNext()) {
					String id = shops.next();
					JSONObject job = jobShop.getJSONObject(id);
					String shopId = job.containsKey("shop_id") ? job.getString("shop_id") : "";//
					String shopName = job.containsKey("shop_name") ? job.getString("shop_name") : "";// job.getJSONObject(id).getString("shop_name");
					String hotSell = job.containsKey("hot_sell") ? job.getString("hot_sell") : "";// job.getJSONObject(id).getString("hot_sell");
					String rs = id + "," + shopId + "," + shopName + "," + hotSell;
					System.out.println(rs);
				}
				System.out.println("****************************" + timeString + "******************************");
			} catch (Exception e) {
				
				e.printStackTrace();
				System.out.println("出了点问题,将在15-30秒内进行再次尝试");
				OtherUtil.delaySomeTime(30000);
			}
			OtherUtil.delaySomeTime(15000);
		}
	}

	/**
	 * 获取当当-数据开放平台-流量-实时流量-今日家具分类排行TOP50店铺下面的数据
	 * 
	 * @param shopId
	 *            店铺ID
	 */
	public static String getHotSellList(String shopId) throws IOException {
		StringBuilder sb = new StringBuilder();
		String postData = "terminal_type_id=0&is_v=2&shop_id=" + shopId;
		URL url = new URL("http://report.dangdang.com/shop-flow-ifgetreal");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("Host", " report.dangdang.com ");
		conn.addRequestProperty("Connection", " keep-alive ");
		conn.addRequestProperty("Content-Length", " 39 ");
		conn.addRequestProperty("Accept", " application/json, text/javascript, */*; q=0.01 ");
		conn.addRequestProperty("Origin", " http://report.dangdang.com ");
		conn.addRequestProperty("X-Requested-With", " XMLHttpRequest ");
		conn.addRequestProperty("User-Agent",
				" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0 ");
		conn.addRequestProperty("Content-Type", " application/x-www-form-urlencoded ");
		conn.addRequestProperty("Referer", " http://report.dangdang.com/shop-flow-real?shop_id=18486 ");
		conn.addRequestProperty("Accept-Encoding", "deflate ");
		conn.addRequestProperty("Accept-Language", " zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("Cookie", cookie);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
		out.println(postData);
		out.flush();
		out.close();
		BufferedReader br;
		try {

			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream()), "gbk"));
		} catch (Exception e) {
			br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "gbk"));
		}
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}
}
