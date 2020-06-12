package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class for Jackson's deserialization of Twitter's User JSON object.
 * For more details on the User object: https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/user-object
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	public int id;
	public String name;
	public String location;
	
	@JsonSetter("id")
	public void setId(int id) {
		this.id = id;
	}
	
	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonSetter("location")
	public void setLocation(String location) {
		this.location = location;
	}
}
