package webmagic.baidu;


import com.alibaba.fastjson.JSON;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class NameQueryProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(1).setSleepTime(10);
	private static String result;

	@Override
	public void process(Page page) {// window.baidu.sug(
		String resultJson = page.getRawText().substring("window.baidu.sug(".length(), page.getRawText().length() - 2);
		String resultNm = JSON.parseObject(resultJson).getJSONArray("s").getString(0);
		NameQueryProcessor.result = resultNm;
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static String queryNam(String wd) {
		Spider.create(new NameQueryProcessor())
			.addUrl("https://sp0.baidu.com/5a1Fazu8AA54nxGko9WTAnF6hhy/su?wd=" + wd)
			.addPipeline(new PhotoOnsolePipeline())
			.run();
		return result;
	}
}
