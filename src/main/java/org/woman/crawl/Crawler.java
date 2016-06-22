package org.woman.crawl;

public class Crawler {
	public static void main(String[] args) throws Exception {
		GlobalWomenNetworkCrawler.crawl();
		HotPeachCrawler.crawl();
	}
}
