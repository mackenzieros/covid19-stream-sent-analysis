package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
	public String text;
	public User user;
	public Place place;
	
	@JsonSetter("text")
	public void setText(String t) {
		this.text = t;
	}
	
	@JsonSetter("user")
	public void setUser(User u) {
		this.user = u;
	}
	
	@JsonSetter("place")
	public void setPlace(Place p) {
		this.place = p;
	}
}
