package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {
	public String id;
	public String url;
	public String placeType;
	public String name;
	public String fullName;
	public String countryCode;
	
	@JsonSetter("id")
	public void setId(String i) {
		this.id = i;
	}
	
	@JsonSetter("url")
	public void setUrl(String u) {
		this.url = u;
	}
	
	@JsonSetter("place_type")
	public void setPlaceType(String pt) {
		this.placeType = pt;
	}
	
	@JsonSetter("name")
	public void setName(String n) {
		this.name = n;
	}
	
	@JsonSetter("full_name")
	public void setFullName(String fn) {
		this.fullName = fn;
	}
	
	@JsonSetter("country_code")
	public void setCountryCode(String c) {
		this.countryCode = c;
	}
}
