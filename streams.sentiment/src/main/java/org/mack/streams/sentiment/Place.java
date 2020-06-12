package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class for Jackson's deserialization of Twitter's Geo JSON object (Place).
 * For more details on the Place object: https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/geo-objects
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {
	public String id;
	public String url; // URL pointing to additional place metadata
	public String placeType;
	public String name;
	public String fullName;
	public String countryCode;
	
	@JsonSetter("id")
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonSetter("url")
	public void setUrl(String url) {
		this.url = url;
	}
	
	@JsonSetter("place_type")
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	
	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonSetter("full_name")
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@JsonSetter("country_code")
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
