package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	public int id;
	public String name;
	public String location;
	
	@JsonSetter("id")
	public void setId(int i) {
		this.id = i;
	}
	
	@JsonSetter("name")
	public void setName(String n) {
		this.name = n;
	}
	
	@JsonSetter("location")
	public void setLocation(String l) {
		this.location = l;
	}
}
