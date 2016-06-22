package org.woman.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.woman.pojo.Organization;
import org.woman.util.Utils;

public class GlobalWomenNetworkCrawler {

	public static final String NGO_LINKS_FILE = "./data/gwn/ngoLinks.txt";

	public static void main(String[] args) throws Exception {
		GlobalWomenNetworkCrawler.crawl();
	}

	public static void crawl() throws Exception {
		// 1- getGWNLinks
		// 2- writelinks to file
		// 3- getLinksFromFile
		// 4- fetch location etc from links.
		// 5- write to file
		// 6- read from file - send to azure

		// String ngoLinks = getGWNLinks();
		// Utils.writeToFile(NGO_LINKS_FILE, ngoLinks);
		List<Organization> organizations = getOrganizationLinks(NGO_LINKS_FILE);
		// Fetch Details
		for (Organization organization : organizations) {
			setDetailsOfOrganizationFromLink(organization);
			GlobalWomenNetworkCrawler.fetchAddress(organization);
			System.out.println(organization.toString());
		}

		// Send to Azure
		Utils.postNGOs(organizations);

	}

	private static void fetchAddress(Organization organization) throws ClientProtocolException, IOException {
		// http://maps.googleapis.com/maps/api/geocode/json?address=Chicago%20Metropolitan%20Battered%20Women's%20Network%20,%20Illinois&sensor=false

		System.out.println("Fetching Lat ");
		try {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		String link = "";

		if (organization.getCoordinates() == null || organization.getCoordinates().trim().equals("")) {
			System.out.println("Switching");
			link = "http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(organization.getAddress()+ " "+organization.getCity()+" "+organization.getCountry()+"", "UTF-8")+"&sensor=false";
		}else{
			link = "http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(organization.getCoordinates().replaceAll(" ", ""), "UTF-8") + "&sensor=false";
		}
		System.out.println(link);
		HttpGet httpGet = new HttpGet(link);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);


			HttpEntity entity1 = response1.getEntity();
			String myString = IOUtils.toString(entity1.getContent(), "UTF-8");

			Scanner scanner = new Scanner(myString);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.contains("\"location\" : {")) {
					// "lat" : 40.6331249,
					// "lng" : -89.3985283
					String latRaw = scanner.nextLine();
					String lngRaw = scanner.nextLine();

					String lat = latRaw.substring(latRaw.indexOf("\"lat\" : ") + 8, latRaw.indexOf(","));
					String lng = lngRaw.substring(lngRaw.indexOf("\"lng\" : ") + 8);
					organization.setLat(lat);
					organization.setLng(lng);
				}
			}
			
			httpclient.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static void setDetailsOfOrganizationFromLink(Organization organization) throws ClientProtocolException, IOException, XPatherException {

		String input = Utils.getContentFromLink(organization.getLink());
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = cleaner.clean(input);

		try {
			String address = ((TagNode) (node.evaluateXPath("//table/tbody/tr[3]/td[2]")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setAddress(address);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String city = ((TagNode) (node.evaluateXPath("//table/tbody/tr[4]/td[2]")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setCity(city);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String state = ((TagNode) (node.evaluateXPath("//table/tbody/tr[5]/td[2]")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setState(state);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String country = ((TagNode) (node.evaluateXPath("//table/tbody/tr[6]/td[2]/a")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setCountry(country);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String email = ((TagNode) (node.evaluateXPath("//table/tbody/tr[11]/td[2]/a[@class='external text']")[0])).getText().toString()
					.replaceAll("\n", " ").trim();
			organization.setEmail(email);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String name = ((TagNode) (node.evaluateXPath("//table/tbody/tr[1]/th/big")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setName(name);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String phone = ((TagNode) (node.evaluateXPath("//table/tbody/tr[10]/td[2]")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setPhone(phone);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String website = ((TagNode) (node.evaluateXPath("//table/tbody/tr[12]/td[2]/a[@class='external text']")[0])).getText().toString()
					.replaceAll("\n", " ").trim();
			organization.setWebsite(website);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String coordinates = ((TagNode) (node.evaluateXPath("//table/tbody/tr[8]/td[2]")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setCoordinates(coordinates);

		} catch (Exception e) {
			e.printStackTrace();
		}

		organization.setSource("GWN");

	}

	private static List<Organization> getOrganizationLinks(String ngoLinksFile) throws IOException {
		List<Organization> organizationLinks = new ArrayList<Organization>();

		BufferedReader br = new BufferedReader(new FileReader(ngoLinksFile));
		String link = "";
		while ((link = br.readLine()) != null) {
			Organization organization = new Organization();
			organization.setLink(link);
			organizationLinks.add(organization);
		}
		br.close();

		return organizationLinks;
	}

	public static String getGWNLinks() throws Exception {

		// <td class="Organization"><a href="/wiki/A_Coin_A_Day_(Keeps_Poverty_Away),_Washington,_D.C.,_USA" title="A Coin A Day (Keeps Poverty Away), Washington, D.C., USA">A Coin A Day (Keeps Poverty Away), Washington, D.C., USA</a></td>
		// <td class="Organization#"><a href="/wiki/A_Coin_A_Day_(Keeps_Poverty_Away),_Washington,_D.C.,_USA" title="A Coin A Day (Keeps Poverty Away), Washington, D.C., USA">A Coin A Day (Keeps Poverty Away), Washington, D.C., USA</a></td>
		// <td class="Country"><a href="/wiki/United_States" title="United States">United States</a></td>

		String link = "http://www.global-womens-network.org/w/index.php?title=Special:Ask&offset=&limit=500&q=%5B%5BCategory%3AOrganization%5D%5D&p=mainlabel%3DOrganization%2Fformat%3Dbroadtable&po=%3F%3DOrganization%23%0A%3FCountry%0A&order=RANDOM";
		String content = Utils.getContentFromLink(link);

		Scanner scanner = new Scanner(content);

		String result = "";
		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();
			try {
				if (line.contains("class=\"Organization\"")) {
					int linkStart = line.indexOf("href=\"");
					int linkEnd = line.indexOf("\" title=\"");
					String ngoLink = line.substring(linkStart + 6, linkEnd);
					result = result + "http://www.global-womens-network.org" + ngoLink + "\n";
				}

			} catch (Exception e) {
				System.out.println("Exception : " + line);
			}
		}

		return result;
	}
}
