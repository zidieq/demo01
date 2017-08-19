package Desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFileChooser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Util.FileUtil;
import Util.OtherUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DDBak {
	private static String jstr = "";

	public static void main(String[] args) {
		getFirstMainPicture(1, 0, false);
	}

	/**
	 * 下载大店指定SPU的详情页面主图的第一张，并以序号和最低价，以及最低价的9折命名
	 * 
	 * @param salary
	 *            要打的折扣
	 * @param start
	 *            开始序号减1
	 * @param cc
	 *            如果只要下载主图请设置为0
	 * @throws IOException
	 */
	private static void getFirstMainPicture(double salary, int start, boolean onlyMainPicture) {

		// int start = 0;// 如果要调整第一个的序号，可以写在此处，默认为0
		// double salary = 0.4;// 活动折扣0.9这样的
		// int cc = 1;// 如果只要下载主图请设置为0

		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String[] spus;
		if (jfc.showOpenDialog(null) == JFileChooser.FILES_ONLY) {

			spus = FileUtil.getFileContext(jfc.getSelectedFile().getAbsolutePath()).split("\n");
		} else {
			System.exit(0);
			return;
		}

		String path = "F:\\百度云同步盘\\图片\\首图和SKU图备份\\" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "\\";// 定义图片所存放的根目录
		new File(path).mkdirs();
		for (int i = 0; i < spus.length; i++) {
			// 获得要下载的图片的SKU
			String spu = spus[i];
			// 生成详情页面地址
			String urlstr = "https://item.taobao.com/item.htm?id=" + spu;
			// 下载详情页面
			String html;
			try {
				html = OtherUtil.getWebPage(urlstr);
				// System.out.println(html);
				// System.exit(0);
				// 得到第一张主图的地址
				String picurl = getMainPicAddr(html, 1); // 要第几张主图
				// ===============================
				if (picurl == null)
					continue;
				// ================================
				// 获取最低价
				Double d = getMinPrice(spu);

				// 计算活动价
				// *****************************************************************
				String p = String.valueOf(d * salary);

				// *****************************************************************

				// String filePath = path + String.valueOf(i + 1 + start) +
				// ",活动价" + p.substring(0, p.indexOf(".") + 2)
				// + ",售价" + d + "," + spu +
				// picurl.substring(picurl.lastIndexOf("."));
				String filePath = path + String.valueOf(i + 1 + start) + ",【" + d + "】," + spu
						+ picurl.substring(picurl.lastIndexOf("."));
				getWebPic("https:" + picurl, filePath);
				// ===========================================================================
				if (onlyMainPicture) {
					continue;
				}
				// 以序号来命名文件夹，和主图的序号一致的
				File picDir = new File(path + String.valueOf(i + 1 + start));
				if (!picDir.exists()) {
					picDir.mkdirs();
				}
				Document doc = Jsoup.parse(html);// 装载主图所在页面的源码
				Elements uls = doc.getElementsByClass("tb-img");
				// 解析出所有SKU的图片的地址
				if (uls.size() > 0) {
					Element ul = uls.get(0);
					Elements lis = ul.getElementsByTag("a");
					if (lis.size() > 0) {
						for (int j = 0; j < lis.size(); j++) {
							try {

								String picaddr = lis.get(j).attr("style");
								picaddr = picaddr.substring(picaddr.indexOf("alicdn.com") - 4,
										picaddr.lastIndexOf("_"));
								String backAdd = picaddr.substring(picaddr.lastIndexOf("."));
								String skuName = lis.get(j).text();
								String skuId = lis.get(j).parent().attr("data-value");
								Double price = Double.parseDouble(getPriceFromSpuPrice(skuId));
								// 计算价格
								// ******************************************************************************************
								// String np = String.valueOf(price * salary);
								// 双十二活动价
								// np = getDouble12Price(skuId);
								// ******************************************************************************************
								// np = np.substring(0, np.indexOf(".") + 2);
								String picName = (j < 10 ? "0" + String.valueOf(j) : j) + "," + skuName + "," + price
										+ "," + skuId + backAdd;

								File picPath = new File(picDir, windowsFileNameFormat(picName));
								getWebPic("https://" + picaddr, picPath.getAbsolutePath());
								System.out.println(skuName + "," + picName + "," + picaddr);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("ok");
		}
	}

	/**
	 * 根据skuid在priceString里面找到价格
	 * 
	 * @param skuId
	 * @return
	 */
	public static String getPriceFromSpuPrice(String skuId) {
		JSONObject job = JSONObject.fromObject(jstr).getJSONObject("data").getJSONObject("promotion")
				.getJSONObject("promoData");
		Iterator<String> skus = job.keys();
		String price = "";
		while (skus.hasNext()) {
			String sku = skus.next();
			// System.out.println("skus:" + sku + "," + "skuId:" + skuId);
			if (sku.indexOf(skuId) > -1) {
				price = job.getJSONArray(sku).getJSONObject(0).getString("price");
				return price;
			}
		}
		return price;
	}

	/**
	 * 获得指定SPU下的所有连接的最低价
	 * 
	 * @param spu
	 * @return
	 * @throws IOException
	 */
	public static Double getMinPrice(String spu) throws IOException {
		return getMinPrice(spu, false);
	}

	/**
	 * 获得指定SPU下的所有连接的最低价
	 * 
	 * @param spu
	 * @return 返回最低价，并打印最低价的SKU的信息
	 * @throws IOException
	 */
	public static Double getMinPrice(String spu, boolean printMsg) throws IOException {
		String html = getSPUPrice(spu);
		jstr = html;// 得到价格所在的请求
		Double d = 1000000.0;
		if (jstr.indexOf("onSibRequestSuccess") > -1) {
			jstr = jstr.substring(jstr.indexOf("(") + 1, jstr.lastIndexOf(")"));
			JSONObject job = JSONObject.fromObject(jstr).getJSONObject("data").getJSONObject("promotion")
					.getJSONObject("promoData");
			// jstr = job.toString();
			Set<String> skus = job.keySet();
			for (String sku : skus) {
				try {

					Double pr = Double.parseDouble(job.getJSONArray(sku).getJSONObject(0).getString("price"));
					if (pr < d) {
						d = pr;
					}
				} catch (Exception e) {
					throw new IOException();
				}
			}
			if (printMsg) {
				for (String sku : skus) {
					Double pr = Double.parseDouble(job.getJSONArray(sku).getJSONObject(0).getString("price"));
					if (pr == d) {
						System.out.println(spu + "," + sku + "," + d);
					}
				}
			}
		}
		return d;
	}

	/**
	 * 获得指定SPU的价格信息
	 */
	public static String getSPUPrice(String spu) throws IOException {
		System.out.println("SPU:" + spu);
		StringBuilder sb = new StringBuilder();
		String postData = "";
		URL url = new URL("https://detailskip.taobao.com/service/getData/1/p1/item/detail/sib.htm?itemId=" + spu
				+ "&sellerId=27796690&modules=qrcode,viewer,price,contract,duty,xmpPromotion,dynStock,delivery,sellerDetail,activity,fqg,zjys,coupon,soldQuantity&callback=onSibRequestSuccess");
		URLConnection conn = url.openConnection();
		conn.addRequestProperty(":authority", "detailskip.taobao.com ");
		conn.addRequestProperty(":method", "GET ");
		conn.addRequestProperty(":path", "/service/getData/1/p1/item/detail/sib.htm?itemId=" + spu
				+ "&sellerId=27796690&modules=qrcode,viewer,price,contract,duty,xmpPromotion,dynStock,delivery,sellerDetail,activity,fqg,zjys,coupon,soldQuantity&callback=onSibRequestSuccess ");
		conn.addRequestProperty(":scheme", "https ");
		conn.addRequestProperty("accept", "*/* ");
		conn.addRequestProperty("accept-encoding", "deflate");
		conn.addRequestProperty("accept-language", "zh-CN,zh;q=0.8 ");
		conn.addRequestProperty("cache-control", "max-age=0 ");
		conn.addRequestProperty("referer", "https://item.taobao.com/item.htm?id=" + spu);
		conn.addRequestProperty("user-agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15897.14 Safari/537.36");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "utf-8"));
		String line = null;
		sb.delete(0, sb.length());
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return sb.toString();
	}

	/**
	 * 将指定网址的图片下载到指定的目录下
	 * 
	 * @param urlstr
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String getWebPic(String urlstr, String filePath) throws IOException {
		URL url = new URL(urlstr);
		System.out.println("download:" + urlstr);
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		System.out.println(filePath);
		FileOutputStream fos = new FileOutputStream(filePath);
		byte[] buf = new byte[1024 * 10];
		int len = 0;
		while ((len = in.read(buf)) > -1) {
			fos.write(buf, 0, len);
		}
		return "";
	}

	/**
	 * 获得淘宝详情页面信息中的主图的信息
	 */
	private static String getMainPicAddr(String html) {
		try {

			return getMainPicAddr(html, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得淘宝详情页面信息中的主图的信息
	 * 
	 * @param html
	 * @param picIndex
	 *            第几张主图，从1开始
	 * @return
	 */
	private static String getMainPicAddr(String html, int picIndex) {
		try {

			String s = html.substring(html.indexOf("auctionImages"));
			s = s.substring(s.indexOf("["), s.indexOf("]") + 1);
			System.out.println("getMainPicAddr:" + s);
			JSONArray jrr = JSONArray.fromObject(s);
			return jrr.getString(picIndex - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用于处理windows下对文件名称的禁用符号的问题
	 * 
	 * @param src
	 *            将要被处理的串
	 * @param repstr
	 *            如发现有windows不允许使用的符号将替换成此处指定的串
	 * @return
	 */
	public static String windowsFileNameFormat(String src, String repstr) {
		return src.replaceAll("[\"\\\\|<>?/*:]+", repstr);
	}

	/**
	 * 重载，默认替换成@
	 * 
	 * @param src
	 *            将要被处理的串
	 * @return
	 */
	public static String windowsFileNameFormat(String src) {
		return windowsFileNameFormat(src, "@");
	}
}
