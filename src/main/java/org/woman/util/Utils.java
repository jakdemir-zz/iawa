package org.woman.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.woman.pojo.Organization;

public class Utils {
	public static void writeToFile(String filename, String content) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
		writer.print(content);
		writer.close();
	}

	public static String getContentFromLink(String link) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(link);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);

		HttpEntity entity1 = response1.getEntity();
		String content = IOUtils.toString(entity1.getContent(), "UTF-8");

		return content;
	}

	public static void postNGOs(List<Organization> allOrganizations) {
		// curl -X POST -H "Content-Type: application/json" -d '{"Name":"mor
		// cati org","link":"http://morcati.org","country":"turkey"}'
		// https://womencast.azure-mobile.net/tables/NGO

		CloseableHttpClient httpClient = null;
		HttpPost httpost = null;
		CloseableHttpResponse response = null;

		try {

			for (Organization organization : allOrganizations) {
				httpClient = HttpClients.createDefault();
				httpost = new HttpPost("https://womencast.azure-mobile.net/tables/NGO2");
				httpost.addHeader("content-type", "application/json");

				String inputStr = "{\"name\":\"" + StringEscapeUtils.escapeJson(organization.getName()) 
						+ "\",\"link\":\"" + StringEscapeUtils.escapeJson(organization.getLink()) 
						+ "\",\"country\":\"" + StringEscapeUtils.escapeJson(organization.getCountry()) 
						+ "\",\"lat\":\"" + StringEscapeUtils.escapeJson(organization.getLat()) 
						+ "\",\"lng\":\"" + StringEscapeUtils.escapeJson(organization.getLng())
						+ "\",\"address\":\"" + StringEscapeUtils.escapeJson(organization.getAddress()) 
						+ "\",\"city\":\"" + StringEscapeUtils.escapeJson(organization.getCity()) 
						+ "\",\"email\":\"" + StringEscapeUtils.escapeJson(organization.getEmail()) 
						+ "\",\"phone\":\"" + StringEscapeUtils.escapeJson(organization.getPhone()) 
						+ "\",\"website\":\"" + StringEscapeUtils.escapeJson(organization.getWebsite())
						+ "\",\"coordinates\":\"" + StringEscapeUtils.escapeJson(organization.getCoordinates()) 
						+ "\",\"state\":\"" + StringEscapeUtils.escapeJson(organization.getState()) 
						+ "\",\"source\":\"" + StringEscapeUtils.escapeJson(organization.getSource())  
						+ "\",\"citylower\":\"" + StringEscapeUtils.escapeJson(organization.getCity().toLowerCase()) 
						+ "\",\"statelower\":\"" + StringEscapeUtils.escapeJson(organization.getState().toLowerCase()) 						
						+ "\",\"countrylower\":\"" + StringEscapeUtils.escapeJson(organization.getCountry().toLowerCase())  
						+ "\"}";
//				String inputStr = "{\"name\":\"" + organization.getName() + "\",\"link\":\"" + organization.getLink() + "\",\"country\":\""
//						+ organization.getCountry() + "\",\"lat\":\"" + organization.getLat() + "\",\"lng\":\"" + organization.getLng()
//						+ "\",\"address\":\"" + organization.getAddress() + "\",\"city\":\"" + organization.getCity() + "\",\"email\":\""
//						+ organization.getEmail() + "\",\"phone\":\"" + organization.getPhone() + "\",\"website\":\"" + organization.getWebsite()
//						+ "\",\"coordinates\":\"" + organization.getCoordinates() + "\",\"source\":\"" + organization.getSource() + "\"}";

				
				//inputStr = StringEscapeUtils.escapeJson(inputStr);
	

				// String inputStr = "{\"name\":\"" + organization.getName() + "\",\"link\":\"" + organization.getLink() + "\",\"country\":\""
				// + organization.getCountry() +"\",\"lat\":\""+organization.getLat()+"\",\"lng\":\""+organization.getLng()+"\"}";

				System.out.println(inputStr);
				// StringEntity input = new StringEntity("{\"Name\":\"2mor cati org\",\"link\":\"http://morcati.org\",\"country\":\"turkey\"}");
				StringEntity input = new StringEntity(inputStr, "UTF-8");
				input.setContentType("application/json");
				httpost.setEntity(input);
				response = httpClient.execute(httpost);
				System.out.println(response.getStatusLine());
				
				try {
					response.close();
					httpClient.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
		}

	}

}
