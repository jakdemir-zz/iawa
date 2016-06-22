package org.woman.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.woman.pojo.Organization;
import org.woman.util.Utils;

//The National Council of Women's Organizations (NCWO) 
//http://www.womensorganizations.org/index.php?option=com_content&view=article&id=114
//http://www.womensorganizations.org/index.php?option=com_content&view=article&id=66


public class NCWO{

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// 1- getGWNLinks
		// 2- fetch location etc from links.
		// 3- send to azure
	
		List<Organization> organizations = NCWO.getOrganizationLinks("");
		// Fetch Details
		for (Organization organization : organizations) {
			NCWO.setDetailsOfOrganizationFromLink(organization);
		}

		// Send to Azure
		//Utils.postNGOs(organizations);

	}
	
	public static void setDetailsOfOrganizationFromLink(Organization organization) throws ClientProtocolException, IOException {
		String input = Utils.getContentFromLink(organization.getLink());
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = cleaner.clean(input);

		try {
			String address = ((TagNode) (node.evaluateXPath("//h2[2]/p[1]/span/span")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setAddress(address);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String email = ((TagNode) (node.evaluateXPath("//p[2]/span[1]/span/strong/span/a")[0])).getText().toString()
					.replaceAll("\n", " ").trim();
			organization.setEmail(email);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String name = ((TagNode) (node.evaluateXPath("//h2[@class='contentheading']")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setName(name);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String phone = ((TagNode) (node.evaluateXPath("//h2[2]/p[2]/span/span")[0])).getText().toString().replaceAll("\n", " ").trim();
			organization.setPhone(phone);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String website = ((TagNode) (node.evaluateXPath("//p[2]/span[1]/a[2]/span/span")[0])).getText().toString()
					.replaceAll("\n", " ").trim();
			organization.setWebsite(website);

		} catch (Exception e) {
			e.printStackTrace();
		}
		organization.setSource("NCWO");

		System.out.println(organization.toString());


	}

	public static  List<Organization> getOrganizationLinks(String ngoLinksFile) {
		List<Organization> organizations = new ArrayList<Organization>();
		
		for (int i = 66; i <= 114; i++) {
			Organization organization = new Organization();
			organization.setLink("http://www.womensorganizations.org/index.php?option=com_content&view=article&id="+i);
			organizations.add(organization);
		}
		
		return organizations;
	}

}
