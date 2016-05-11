package webmagic.baidu;

import java.io.File;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

/** 百度相册的图片爬取类 */
public class BaiduPhototProcessor extends PhototProcessor {

	private static final String ALBUM_LIST = "http://tieba.baidu.com/photo/g?kw=";// 相册列表的请求URL
	private static final String IMGCD_LIST = "http://tieba.baidu.com/photo/g/bw/picture/list?kw=";// 图片集合的请求
	private static final String IMG_URL = "http://imgsrc.baidu.com/forum/pic/item/";// 图片请求

	public BaiduPhototProcessor(String kw) {
		BaiduPhototProcessor.kw = kw;
	}

	@Override
	public void process(Page page) {
		Selectable urlSe = page.getUrl();

		if (urlSe.toString().startsWith(ALBUM_LIST) && urlSe.toString().endsWith("cat_id=all")) {// 获取页数信息
			String lastPageHref = page.getHtml().xpath("//a[@class='last']/@href").toString();
			if (lastPageHref == null) {
				page.addTargetRequest(ALBUM_LIST + kw + "&pn=1&rn=30&cat_id=all&_=" + System.currentTimeMillis());
			} else {
				int totalPage = Integer.parseInt(lastPageHref.substring(lastPageHref.lastIndexOf("p") + 1));
				getAlbums(totalPage, page);
			}
		}

		if (urlSe.toString().startsWith(ALBUM_LIST) && urlSe.toString().indexOf("&pn=") != -1) {// 所有获取相册列表html的请求,分析出相册code并发送请求
			if (page.getRawText().startsWith("<!DOCTYPE")) {
				System.out.println("当前贴吧没有任何相册~");
				System.exit(0);
			}

			String html = page.getJson().jsonPath("$.data.catalog_detail_list_html").toString();
			Document doc = Jsoup.parse(html);
			Elements alunmEls = doc.select(".grbm_ele_title > a");

			for (Element alunmEl : alunmEls) {
				String albumNm = getLegalPath(alunmEl.text());
				System.out.println("正在分析相册 : " + albumNm);
				String href = alunmEl.attr("href");
				String albumCode = href.substring(href.lastIndexOf("/") + 1);
				String imgsUrl = IMGCD_LIST + kw + "&alt=jview&rn=200&tid=" + albumCode + "&pn=1&pe=40&info=1&_="
						+ (System.currentTimeMillis() + "").substring(0, 13) + "#图册_" + albumNm;// 根据wd和code获得该相册下所有的图片信息
				page.addTargetRequest(imgsUrl);
			}
		}

		if (urlSe.toString().startsWith(IMGCD_LIST)) {// 发送请求之后获取相册json,包含所有图片的cid,请求所有图片
			List<String> urls = page.getJson().jsonPath("$.data.pic_list[*].pic_id").all();
			for (int i = 0; i < urls.size(); i++) {
				String url = urls.get(i);
				String folderNm = urlSe.toString().substring(urlSe.toString().lastIndexOf("#") + 1);// 目录名称
				String index = i < 9 ? "00" + (i + 1) : (i < 99 ? "0" + (i + 1) : (i + 1) + "");
				String filePath = ABSOLUTE_PATH + kw + SEPARATOR + folderNm + SEPARATOR + index + ".jpg";// 目录路径
				System.out.println("准备下载图片 : " + IMG_URL + url + ".jpg");
				page.addTargetRequest(IMG_URL + url + ".jpg#!" + filePath);
			}
		}

		if (urlSe.toString().startsWith(IMG_URL)) {// 输出图片
			String url = urlSe.toString();
			String fileStr = url.substring(url.lastIndexOf("#!") + 2);
			final String imgUrl = url.substring(0, url.lastIndexOf("#!"));
			final File imgFile = new File(fileStr.replaceAll("@p@", "//"));
			if (!imgFile.getParentFile().exists()) {
				imgFile.getParentFile().mkdirs();
			}

			new Thread() {
				public void run() {
					downloadImg(imgUrl, imgFile);
				}
			}.start();
		}
	}

	private void getAlbums(int totalPage, Page page) {
		for (int i = 1; i <= totalPage; i++) {
			System.out.println("当前共 " + totalPage + "页相册 , 正在读取第 " + i + "页");
			page.addTargetRequest(
					ALBUM_LIST + kw + "&alt=jview&pn=" + i + "&rn=30&cat_id=all&_=" + System.currentTimeMillis());
		}
	}
}
