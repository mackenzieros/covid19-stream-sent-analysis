package org.mack.streams.sentiment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class TweetDeserializeTest {

	@Test
	void deserializeUsingJackson_thenCorrect() throws IOException {
		String jsonInput = "{\"created_at\": \"Wed Oct 10 20:19:24 +0000 2018\"," +
				"\"id\": 1050118621198921728," +
				"\"id_str\": \"1050118621198921728\"," +
				"\"text\": \"To make room for more expression, we will now count all emojis as equal—including those with gender‍‍‍ ‍‍and skin t… https://t.co/MkGjXf9aXm\"," +
				"\"user\": {}," +
				"\"place\": {}}";
		ObjectMapper mapper = new ObjectMapper();
		Tweet testTweet = mapper.readValue(jsonInput, Tweet.class);
		assertTrue(testTweet.text.length() > 0);
	}

	@Test
	void tweetIsSerializable() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Tweet testTweet = new Tweet();
		String tweetAsStr = mapper.writeValueAsString(testTweet);
		assertAll("Should not have any fields",
				() -> assertTrue(tweetAsStr.contains("text"), "Tweet JSON contains \"text\" field"),
				() -> assertTrue(tweetAsStr.contains("user"), "Tweet JSON contains \"user\" field"),
				() -> assertTrue(tweetAsStr.contains("place"), "Tweet JSON contains \"place\" field"));
	}

}
