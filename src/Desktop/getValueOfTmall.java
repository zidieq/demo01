package Desktop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Util.FileUtil;
import Util.OtherUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class getValueOfTmall {
	public static void main(String[] args) throws IOException {
		// String
		// html=getPicturesFromEplatform.getWebPage("https://detail.tmall.com/item.htm?spm=a21ag.7790924.0.0.iOLAL9&id=39043251095");
		// FileUtil.println(html, "in.txt");
		System.out.println("请手工输入要导出的天猫的连接SPUID");
		Scanner sc=new Scanner(System.in);
		String spu = sc.nextLine();
		String html = OtherUtil.getWebPage("https://detail.tmall.com/item.htm?id=" + spu);// FileUtil.getFileContext("in.txt");
		 System.out.println(html);
		String fileName=String.valueOf(System.currentTimeMillis());
		html = html.substring(html.indexOf("TShop.Setup"));
		html = html.substring(html.indexOf("(") + 1).trim();
		html = html.substring(0, html.indexOf(")")).trim();
		JSONObject job = JSONObject.fromObject(html);
		OtherUtil.showJob(job);
		// 找到评价地址中的各项参数
		String shopId = job.getString("rstShopId");
		JSONObject itemDo = job.getJSONObject("itemDO");
		String categoryId = itemDo.getString("categoryId");
		String sellerId = itemDo.getString("userId");
		String brandId = itemDo.getString("brandId");
		String itemId = itemDo.getString("itemId");
		String spuId = itemDo.getString("spuId");
		System.out.println(shopId + "," + categoryId + "," + sellerId + "," + brandId + "," + itemId);
		// 轮询每页
		Map<String, String> skuMap = new HashMap<>();
		JSONArray skuList = job.getJSONObject("valItemInfo").getJSONArray("skuList");
		for (int skuIndex = 0; skuIndex < skuList.size(); skuIndex++) {
			JSONObject sku = skuList.getJSONObject(skuIndex);
			skuMap.put(sku.getString("skuId"), sku.getString("names"));
		}
		System.out.println(skuMap);
		// System.exit(0);
		int rowNo = 1002, pageCount = 2;

		for (int pageNo = 1; pageNo < pageCount; pageNo++) {
			rowNo += Math.random() * 100 + 1;
			OtherUtil.delaySomeTime(5000);
			int tempTimes = 1;
			while (true) {

				try {
					html = getValuePage(spuId, shopId, categoryId, sellerId, brandId, itemId, pageNo, skuMap, rowNo,fileName);
					// System.out.println(html);
					try {
						int cc = JSONObject.fromObject(html).getJSONObject("rateDetail").getJSONObject("paginator")
								.getInt("lastPage");
						if (cc > 1) {
							pageCount = cc;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("出现了问题，将延时5-10秒后进行第" + tempTimes++ + "次尝试");
					OtherUtil.delaySomeTime(10000);
					if (tempTimes > 100) {
						System.out.println("尝试了" + tempTimes + "次都没成功，可能是真的有问题了的");
					}
				}
			}
		}
		System.out.println(html);
		System.out.println("ok");
	}

	private static String getValuePage(String spu, String shopId, String categoryId, String sellerId, String brandId,
			String itemId, int pageNo, Map<String, String> map, int rowNo,String fileName) throws IOException {
		String html;
		JSONObject job;
		// 拼接评价地址
		String valueUrl = "https://aldcdn.tmall.com/recommend.htm?appId=03067&itemId=" + spu + "&vid=0&curPage="
				+ pageNo + "&step=100&categoryId=" + categoryId + "&sellerId=" + sellerId + "&shopId=" + shopId
				+ "&brandId=" + brandId + "&refer=&callback=jsonpAldTabWaterfall";
		valueUrl = "https://rate.tmall.com/list_detail_rate.htm?tbpm=3&itemId=" + itemId + "&spuId=" + spu
				+ "&sellerId=" + sellerId + "&order=3&currentPage=" + pageNo
				+ "&append=0&content=1&tagId=&posi=&picture=&needFold=0&_ksTS=" + System.currentTimeMillis() + "_"
				+ rowNo + "&callback=jsonp" + ++rowNo;
		System.out.println(valueUrl);
		// System.exit(0);
		html = null;
		job = null;
		html = OtherUtil.getWebPage(valueUrl);
		html = html.substring(html.indexOf("(") + 1, html.lastIndexOf(")")).trim();
		JSONArray jarr = JSONObject.fromObject(html).getJSONObject("rateDetail").getJSONArray("rateList");
		for (int i = 0; i < jarr.size(); i++) {
			job = jarr.getJSONObject(i);
			String id = job.getJSONObject("attributesMap").getString("sku");
			String name = job.getString("auctionSku");
			String value = job.getString("rateContent");
			String datestr = job.getString("rateDate");
			// datestr = OtherUtil.longToDate(datestr);
			// String
			// enableTime=job.getJSONObject("attributesMap").getString("enableTime");
			// enableTime=OtherUtil.longToDate(enableTime);
			System.out.println(datestr + "@" + id + "@" + name + "@" + value);
			FileUtil.println(datestr + "@" + id + "@" + name + "@" + value, fileName+".txt");
		}
		return html;
	}

}
