package webmagic.xunlei;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import utils.DateUtil;
import webmagic.baidu.PhotoOnsolePipeline;

public class XunleiProcessor implements PageProcessor {
	private static final String AFX_URL = "http://www.aifenxiangvip.com/";// 爱分享网
	private static final String LBG_URL = "http://www.laobinggun.com/";// 爱分享网

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		String url = page.getUrl().toString();
		if (url.indexOf(AFX_URL) != -1) {// 爱分享网
			processAFX(page);
		} else if (url.indexOf(LBG_URL) != -1) {
			processLBG(page);
		}

	}

	/**
	 * 老冰棍网
	 * 
	 * @param page
	 */
	private void processLBG(Page page) {
		String url = page.getUrl().toString();
		final String xlUrl = "";// 迅雷页网址
		final String cdUrl = "";// 帐号页地址

		if (LBG_URL.equals(url)) {// 网站首页
			// page.getHtml().xpath("//")
		} else if (url.matches(xlUrl)) {// 迅雷页

		} else if (url.matches(cdUrl)) {// 帐号页

		}
	}

	/**
	 * 爱分享网
	 * 
	 * @param page
	 */
	private void processAFX(Page page) {
		String url = page.getUrl().toString();
		final String xlUrl = "http://www.aifenxiangvip.com/forum-\\d*-1.html";
		final String cdUrl = "http://www.aifenxiangvip.com/thread-\\d*-1-1.html";// 帐号页网址

		if (AFX_URL.equals(url)) {// 网站首页
			List<Selectable> nvNodes = page.getHtml().xpath("//div[@class='comiis_nvbox']/ul/li").nodes();
			for (Selectable node : nvNodes) {
				String nodeStr = node.toString();
				if (nodeStr.indexOf("迅雷") != -1) {
					page.addTargetRequests(node.links().all());// 进入迅雷帐号页面
				}
			}
		} else if (url.matches(xlUrl)) {// 迅雷帐号页
			Selectable titNode = page.getHtml().xpath("//span[@class='comiis_common']").nodes().get(2);
			String dateStr = titNode.toString().replaceAll(".*【", "").replaceAll("】.*", "");
			if (DateUtil.isNow(dateStr, "M月d日")) {
				page.addTargetRequests(titNode.xpath("//a[2]").links().all());// 进入帐号页面
			} else {
				System.out.println("今日没有最新的帐号信息");
			}
		} else if (url.matches(cdUrl)) {// 帐号列表页
			String userListStr = page.getHtml().xpath("//div[@class='t_f']").all().get(0);
			String[] userArr = userListStr.replaceAll("<div class=\"t_f\">", "").replaceAll("</div>", "")
					.replaceAll("\\s", "").split("<br>");
			System.out.println("爱分享网读取完毕 : \n帐号\t\t\t\t密码");
			for (String code : userArr) {
				String[] upPair = code.replaceAll("账号|帐号", "").replaceAll("密码", ",").split(",");
				System.out.println(upPair[0] + "\t\t" + upPair[1]);
			}
		}

	}

	public static void main(String[] args) {
		String url = "http://www.aifenxiangvip.com/";
		Spider.create(new XunleiProcessor()).addUrl(url).addPipeline(new PhotoOnsolePipeline()).thread(10).run();
	}

}
