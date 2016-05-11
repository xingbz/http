package webmagic.baidu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/** 相册爬取抽象父类 */
public abstract class PhototProcessor implements PageProcessor {

	static final String SEPARATOR = "@p@";// 文件分隔符
	static final String ABSOLUTE_PATH = "E:" + SEPARATOR + "http" + SEPARATOR + "tiebaPhoto" + SEPARATOR;// 图片存储路径
	static String kw;// 搜索关键词

	Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	
	@Override
	public Site getSite() {
		return site;
	}

	String getLegalPath(String str) {
		String path = str.replaceAll("[\\/\\\\:*?<>|]", "").replaceAll("\\s+", "_").replaceAll("\\.", "");
		if (path.length() > 20) {
			path = path.substring(15);
		}
		return path;
	}

	void downloadImg(String url, File imgFile) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		try {
			CloseableHttpResponse response = httpClient.execute(get);
			byte[] data = EntityUtils.toByteArray(response.getEntity());
			if (data.length > 0) {
				if (imgFile.exists()) {
					System.out.println("已存在 ! ! !" + imgFile);
				} else {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
					bos.write(data);
					bos.flush();
					bos.close();
					System.out.println("下载成功~~~" + imgFile);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
