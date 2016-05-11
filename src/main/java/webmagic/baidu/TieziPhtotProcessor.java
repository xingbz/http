package webmagic.baidu;

import java.io.File;
import java.util.List;

import us.codecraft.webmagic.Page;

public class TieziPhtotProcessor extends PhototProcessor {

	private static final String TSB_LIST = "http://tieba.baidu.com/f/good?kw=";// 精品帖标签页访问地址
	private static final String IMG_LIST = "http://tieba.baidu.com/p/";// 帖子内图片集合的访问地址

	public TieziPhtotProcessor(String kw) {
		TieziPhtotProcessor.kw = kw;
	}

	@Override
	public void process(Page page) {
		String url = page.getUrl().toString();

		if (url.startsWith(TSB_LIST) && url.indexOf("&cid=") != -1) {
			String folderNm = "帖子_" + url.substring(url.lastIndexOf("#!") + 2);
			List<String> tieList = page.getHtml().xpath("//div[@class='threadlist_title']/a[@class='j_th_tit']").all();// 获取当前页的帖子请求
			for (String tie : tieList) {
				String title = getLegalPath(tie.substring(tie.lastIndexOf("\">") + 2, tie.lastIndexOf("<")));
				String tieUrl = tie.substring(tie.indexOf("href=\"") + 6, tie.indexOf("\" title"));
				page.addTargetRequest(tieUrl + "#!" + ABSOLUTE_PATH + kw + SEPARATOR + folderNm + SEPARATOR + title);
			}

			page.addTargetRequests(page.getHtml().xpath("//div[@class='pager']").links().all());// 获取其它页
		}

		if (url.startsWith(TSB_LIST)) {// 获取tab图
			List<String> tabList = page.getHtml().xpath("//div[@class='frs_good_nav_wrap']//a").all();
			for (String tabStr : tabList) {
				if (tabStr.indexOf("图") != -1) {
					String folderNm = tabStr.substring(tabStr.lastIndexOf("\">") + 2, tabStr.lastIndexOf("<"));
					String tabUrl = tabStr.substring(tabStr.indexOf("href=\"") + 6, tabStr.lastIndexOf("\">"))
							.replaceAll("&amp;", "&");
					page.addTargetRequest(tabUrl + "#!" + folderNm);
				}
			}
		}

		if (url.startsWith(IMG_LIST)) {
			String folderPath = url.substring(url.lastIndexOf("#!") + 2).replace(SEPARATOR, "//");
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			List<String> imgUrls = page.getHtml().xpath("//img[@class='BDE_Image']/@src").all();
			for (int i = 0; i < imgUrls.size(); i++) {
				String index = i < 9 ? "00" + (i + 1) : (i < 99 ? "0" + (i + 1) : (i + 1) + "");
				downloadImg(imgUrls.get(0), new File(folder + File.separator + index + ".jpg"));
			}
		}
	}

}
