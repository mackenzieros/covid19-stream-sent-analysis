package org.mack.streams.sentiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NegNeuPosBranchAppTest {
	private TopologyTestDriver testDriver = null;
	private TestInputTopic<Integer, String> contentInputTopic = null;
	private TestOutputTopic<Integer, String> negativeOutputTopic = null;
	private TestOutputTopic<Integer, String> neutralOutputTopic = null;
	private TestOutputTopic<Integer, String> positiveOutputTopic = null;
	private Serde<Integer> integerSerde = new Serdes.IntegerSerde();
	private Serde<String> stringSerde = new Serdes.StringSerde();
	
	@BeforeEach
	void setUp() throws Exception {
		Properties props = NegNeuPosBranchApp.createProperties();
		Topology topology = NegNeuPosBranchApp.createTopology();
		testDriver = new TopologyTestDriver(topology, props);
		contentInputTopic = testDriver.createInputTopic(NegNeuPosBranchApp.CONTENT_TOPIC_NAME,
				integerSerde.serializer(), stringSerde.serializer());
		negativeOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.NEGATIVE_TOPIC_NAME,
				integerSerde.deserializer(), stringSerde.deserializer());
		neutralOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.NEUTRAL_TOPIC_NAME,
				integerSerde.deserializer(), stringSerde.deserializer());
		positiveOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.POSITIVE_TOPIC_NAME,
				integerSerde.deserializer(), stringSerde.deserializer());
	}
	
	@AfterEach
	void tearDown() throws Exception {
		testDriver.close();
	}
	
	@Test
	void testNeg() {
		int key = 0;
		String value = "This movie was actually neither that funny , nor super witty.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, String> keyValue = negativeOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, "1 - Negative"));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}

	@Test
	void testNeu() {
		int key = 0;
		String value = "The movie was meh.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, String> keyValue = neutralOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, "2 - Neutral"));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}

	@Test
	void testPos() {
		int key = 0;
		String value = "I liked watching that movie.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, String> keyValue = positiveOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, "3 - Positive"));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}
}
