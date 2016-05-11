package webmagic.baidu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class TiebaUser implements PageProcessor {

	private static final String URL = "http://tieba.baidu.com/home/main?un=";

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private static List<String> list = new ArrayList<String>();

	@Override
	public void process(Page page) {
		String name = page.getHtml().xpath("//span[@class='userinfo_username']/text()").all().get(0);
		String imgUrl = page.getHtml().xpath("//a[@class='userinfo_head']/img/@src").all().get(0);
		if (!list.contains(name)) {
			downImg(name, imgUrl);
			list.add(name);
		}

		List<String> urls = page.getHtml().links().all();
		for (String url : urls) {
			if (url.startsWith(URL)) {
				page.addTargetRequest(url);
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void downImg(final String name, final String url) {
		new Thread() {
			public void run() {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				try {
					CloseableHttpResponse response = httpClient.execute(get);
					byte[] data = EntityUtils.toByteArray(response.getEntity());
					if (data != null) {
						FileOutputStream fos = new FileOutputStream(new File("E:\\http\\tiebaUser\\" + name + ".jpg"));
						fos.write(data);
						fos.flush();
						fos.close();
						System.out.println(name + "下载成功 ~ ");
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						httpClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public static void main(String[] args) {
		Spider.create(new TiebaUser()).addUrl("http://tieba.baidu.com/home/main/?un=SYQSYQQQ&ie=utf-8&fr=frs")
				.addPipeline(new PhotoOnsolePipeline()).thread(20).run();
	}
}
