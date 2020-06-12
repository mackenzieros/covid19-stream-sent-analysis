package org.mack.streams.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class for Jackson's deserialization of Twitter's Tweet JSON object.
 * For more details on the Tweet object: https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
	public String text;
	public User user;
	public Place place;
	
	@JsonSetter("text")
	public void setText(String text) {
		this.text = text;
	}
	
	@JsonSetter("user")
	public void setUser(User user) {
		this.user = user;
	}
	
	@JsonSetter("place")
	public void setPlace(Place place) {
		this.place = place;
	}
}
