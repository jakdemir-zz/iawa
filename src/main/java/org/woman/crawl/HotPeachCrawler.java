package org.woman.crawl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.woman.pojo.Organization;
import org.woman.util.Utils;

public class HotPeachCrawler {

	public static void main(String[] args) throws Exception {
		HotPeachCrawler.crawl();
	}
	
	public static void crawl() throws Exception{
		System.out.println("Reading NGOS");
		ArrayList<Organization> allOrganizations = HotPeachCrawler.getNGOFromFile("./data/hotpeach/ngo_add.txt");
		System.out.println("Generating NGOS Address");

		//Feth lat long
		//HotPeachCrawler.fetchAddress(allOrganizations);

		//Write to file
		//String line = "";
//		for (Organization organization : allOrganizations) {
//			line = line + "\n" + organization.getCountry() + ", " + organization.getLink() + ", " + organization.getName() + ", "
//					+ organization.getLat() + ", " + organization.getLng();
//		}
		//Utils.writeToFile("/Users/jak/Desktop/iawa/ngo_add.txt", line);

		Utils.postNGOs(allOrganizations);

	}
	

	private static void fetchAddress(ArrayList<Organization> allOrganizations) throws ClientProtocolException, IOException {
		// http://maps.googleapis.com/maps/api/geocode/json?address=Chicago%20Metropolitan%20Battered%20Women's%20Network%20,%20Illinois&sensor=false

		for (Organization organization : allOrganizations) {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			String link = "http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(organization.getName()) + ","
					+ organization.getCountry() + "&sensor=false";
			System.out.println(link);
			HttpGet httpGet = new HttpGet(link);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);

			try {

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
			} finally {
				response1.close();
			}
		}
	}

	public static ArrayList<Organization> getNGOFromFile(String fileName) throws IOException {

		ArrayList<Organization> orgList = new ArrayList<Organization>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			try {
				String[] lineArr = line.split(",");
				Organization organization = new Organization();
				organization.setCountry(lineArr[0]);
				organization.setLink(lineArr[1]);
				organization.setWebsite(lineArr[1]);
				organization.setName(lineArr[2]);
				organization.setLat(lineArr[3]);
				organization.setLng(lineArr[4]);
				organization.setSource("HOTPEACH");

				orgList.add(organization);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return orgList;
	}

	public static String getOrganizations(String link, HashMap<String, Organization> allOrgs) throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(link);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		String result = "";

		try {

			HttpEntity entity1 = response1.getEntity();
			String myString = IOUtils.toString(entity1.getContent(), "UTF-8");

			Scanner scanner = new Scanner(myString);
			String country = "";

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				try {
					// Africa splitter
					// if (line.contains("<a name=\"")) {
					// country = line.substring(line.indexOf("<a name=\"") + 9,
					// line.indexOf("\" id=\"")).replace(",", " ");
					// System.out.println("Country : " + country);
					// } else if (line.contains("<li><a href=\"")) {
					// // <li><a
					// href="http://www.webspawner.com/users/africanheritage/">Nafukwe
					// House of Hope</a></li>
					// String orgLink =
					// line.substring(line.indexOf("<li><a href=\"") + 13,
					// line.indexOf("\">")).replace(",", " ");
					// String orgName = line.substring(line.indexOf("\">") + 2,
					// line.indexOf("</a></li>")).replace(",", " ");
					// line = country + "," + orgLink + "," + orgName ;
					// result = result + "\n"+line;
					// System.out.println(line);
					// }

					// Auspac splitter
					System.out.println(line);

					if (line.contains("<a name=\"")) {
						country = line.substring(line.indexOf("<a name=\"") + 9, line.indexOf("\" id=\"")).replace(",", " ");
						System.out.println("Country : " + country);
					} else if (line.contains("<li><a href=\"")) {
						// <li><a
						// href="http://www.webspawner.com/users/africanheritage/">Nafukwe
						// House of Hope</a></li>
						String orgLink = line.substring(line.indexOf("<li><a href=\"") + 13, line.indexOf("\">")).replace(",", " ")
								.replace("\" title=\"", "");
						String orgName = line.substring(line.indexOf("\">") + 2, line.indexOf("</a></li>")).replace(",", " ");
						line = country + "," + orgLink + "," + orgName;
						line = line.replace("\" title=\"", "");
						if (line.contains("<") || line.contains(">")) {
							System.out.println("Skipped : " + line);
						} else {

							String domain = orgLink.substring(orgLink.indexOf("http://www.") + 11, orgLink.indexOf(".", 14));

							if (!allOrgs.containsKey(domain)) {
								Organization organization = new Organization();
								organization.setCountry(country);
								organization.setLink(orgLink);
								organization.setName(orgName);
								allOrgs.put(domain, organization);
							}
							result = result + "\n" + line;
						}
					}
				} catch (Exception e) {
					System.out.println("Exception : " + line);
				}
			}
		} finally {
			response1.close();
		}

		return result;
	}

	public static ArrayList<String> getLinks() {
		// http://hotpeachpages.net/africa/africa1.html
		// http://hotpeachpages.net/asia/asia1.html
		// http://hotpeachpages.net/auspac/aus.html,Australia
		// http://hotpeachpages.net/auspac/nz1.html,New Zealand
		// http://hotpeachpages.net/auspac/pacific1.html
		// http://hotpeachpages.net/camerica/camerica1.html
		// http://hotpeachpages.net/camerica/caribbean1.html
		// http://hotpeachpages.net/canada/index.html,Canada
		// http://hotpeachpages.net/europe/europe1.html
		// http://hotpeachpages.net/index.html,World
		// http://hotpeachpages.net/mideast/israel.html,Israel
		// http://hotpeachpages.net/mideast/mideast1.html
		// http://hotpeachpages.net/samerica/samerica1.html
		// http://hotpeachpages.net/usa/index.html,United States

		ArrayList<String> links = new ArrayList<String>();
		links.add("http://hotpeachpages.net/africa/africa1.html");
		links.add("http://hotpeachpages.net/asia/asia1.html");
		links.add("http://hotpeachpages.net/auspac/aus1.html");
		links.add("http://hotpeachpages.net/auspac/nz1.html");
		links.add("http://hotpeachpages.net/auspac/pacific1.html");
		links.add("http://hotpeachpages.net/camerica/camerica1.html");
		links.add("http://hotpeachpages.net/camerica/caribbean1.html");
		links.add("http://hotpeachpages.net/europe/europe1.html");
		links.add("http://hotpeachpages.net/mideast/israel.html");
		links.add("http://hotpeachpages.net/mideast/mideast1.html");
		links.add("http://hotpeachpages.net/samerica/samerica1.html");
		links.add("http://hotpeachpages.net/usa/states.html");
		links.add("http://hotpeachpages.net/canada/canada1.html");

		return links;
	}

	public static String getCountries() throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://hotpeachpages.net/a/countries.html");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		String result = "";

		try {

			HttpEntity entity1 = response1.getEntity();
			String myString = IOUtils.toString(entity1.getContent(), "UTF-8");

			Scanner scanner = new Scanner(myString);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// System.out.println(line);
				// <a href="../europe/europe1.html#Turkey">Turkey</a><br />

				if (line.contains("<a href=\"") && line.endsWith("</a><br />")) {
					String link = line.substring(line.indexOf("<a href=\"") + 9, line.indexOf("\">")).replace(",", " ")
							.replace("..", "http://hotpeachpages.net");
					String country = line.substring(line.indexOf(">") + 1, line.indexOf("</a><br />")).replace(",", " ");
					result = result + "\n" + link + "," + country;

					// System.out.println("Link : " + link + ", Country : " +
					// country);
				}
			}
		} finally {
			response1.close();
		}

		return result;
	}
}
