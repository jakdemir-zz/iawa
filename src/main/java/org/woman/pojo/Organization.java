package org.woman.pojo;

public class Organization {
	private String name;
	private String link;
	private String address;
	private String country;
	private String city;
	private String state;
	private String lat;
	private String lng;
	private String website;
	private String phone;
	private String email;
	private String source;
	private String coordinates;

	public Organization() {
		this.name = "na";
		this.link = "na";
		this.address = "na";
		this.country = "na";
		this.city = "na";
		this.state = "na";
		this.lat = "na";
		this.lng = "na";
		this.website = "na";
		this.phone = "na";
		this.email = "na";
		this.source = "na";
		this.coordinates = "na";
	}

	
	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCoordinates() {
		return coordinates;
	}


	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}
	
	
//	Organization is : 
//		name : FAIR Girls, Washington DC, USA
//		phone : 202-265-1505
//
//		email : info@fairgirls.org
//		link : http://www.global-womens-network.org/wiki/FAIR_Girls,_Washington_DC,_USA
//		address : 2100 M Street, NW
//		Ste. 170-254
//		Washington, DC 20037-1233
//
//
//		city : Washington, DC
//
//		country : United States
//		coordinates : 38° 54' 18" N, 77° 2' 49" W
//
//		lat : na
//		lng : na
//		website : http://fairgirls.org/
//		source : GWN
//


	
	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		result.append("Organization is : \n");
		result.append("name : "+this.name+"\n");
		result.append("phone : "+this.phone+"\n");
		result.append("email : "+this.email+"\n");
		result.append("link : "+this.link+"\n");
		result.append("address : "+this.address+"\n");
		result.append("city : "+this.city+"\n");
		result.append("country : "+this.country+"\n");
		result.append("coordinates : "+this.coordinates+"\n");
		result.append("lat : "+this.lat+"\n");
		result.append("lng : "+this.lng+"\n");
		result.append("website : "+this.website+"\n");
		result.append("source : "+this.source+"\n");

		return result.toString();
	}

}
