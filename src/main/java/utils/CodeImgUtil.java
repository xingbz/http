package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class CodeImgUtil {

	/** 验证码本地存储路径 */
	private static final String ABSOLUTE_PATH = "E:" + File.separator + "http" + File.separator + "code"
			+ File.separator;

	public static void getCodeImg(String url, Header[] hs) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.setHeader(new BasicHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0"));
		try {
			if (hs != null) {
				get.setHeaders(hs);
			}
			CloseableHttpResponse response = httpClient.execute(get);
			byte[] data = EntityUtils.toByteArray(response.getEntity());
			if (data == null || data.length == 0) {
				System.out.println("获取验证码失败 ! ");
			} else {
				File codeFile = getFile(url);
				FileOutputStream fos = new FileOutputStream(codeFile);
				fos.write(data);
				fos.flush();
				fos.close();
				System.out.println("验证码获取成功 ~ 路径 : " + codeFile);
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

	private static File getFile(String url) {
		String name = url.substring(url.indexOf(".") + 1, url.indexOf(".", url.indexOf(".") + 1));
		File file = new File(ABSOLUTE_PATH + name + File.separator + System.currentTimeMillis() + ".jpg");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}
}
