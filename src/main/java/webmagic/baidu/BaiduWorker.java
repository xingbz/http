package webmagic.baidu;

import java.util.Scanner;

import us.codecraft.webmagic.Spider;

public class BaiduWorker {

	private static final String NM_REG = "[a-zA-Z]+";

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入您关注的明星姓名 ( 如果无法输入中文,请输入姓名拼音全写,系统将为您自动匹配 ( 结果来源于百度搜索 ) ) : ");

		String input;
		String name = "";

		while (!"y".equals(input = scan.nextLine().trim())) {
			name = input;
			if (input.matches(NM_REG)) {
				name = NameQueryProcessor.queryNam(input);
			}
			System.out.println("您关注的明星是" + name + " , 确认请输入y , 或者重新输入明星姓名 ~ ");

		}

		final String kw = name;
		scan.close();

		new Thread() {
			public void run() {
				Spider.create(new BaiduPhototProcessor(kw))
						.addUrl("http://tieba.baidu.com/photo/g?kw=" + kw + "&cat_id=all")
						.addPipeline(new PhotoOnsolePipeline()).thread(10).run();
			}
		}.start();

		new Thread() {
			public void run() {
				Spider.create(new TieziPhtotProcessor(kw)).addUrl("http://tieba.baidu.com/f/good?kw=" + kw)
						.addPipeline(new PhotoOnsolePipeline()).thread(10).run();
			}
		}.start();

	}

	public static void startBaiduPhoto(String kw) {

	}
}
