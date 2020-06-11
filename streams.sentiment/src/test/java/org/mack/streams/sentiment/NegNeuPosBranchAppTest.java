package org.mack.streams.sentiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.Properties;

class NegNeuPosBranchAppTest {
	private TopologyTestDriver testDriver = null;
	private TestInputTopic<Integer, String> contentInputTopic = null;
	private TestOutputTopic<Integer, Double> negativeOutputTopic = null;
	private TestOutputTopic<Integer, Double> neutralOutputTopic = null;
	private TestOutputTopic<Integer, Double> positiveOutputTopic = null;
	private Serde<Integer> integerSerde = new Serdes.IntegerSerde();
	private Serde<String> stringSerde = new Serdes.StringSerde();
	private Serde<Double> doubleSerde = new Serdes.DoubleSerde();
	
	@BeforeEach
	void setUp() throws Exception {
		Properties props = NegNeuPosBranchApp.createProperties();
		Topology topology = NegNeuPosBranchApp.createTopology();
		testDriver = new TopologyTestDriver(topology, props);
		contentInputTopic = testDriver.createInputTopic(NegNeuPosBranchApp.CONTENT_TOPIC_NAME,
		    integerSerde.serializer(), stringSerde.serializer());
		negativeOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.NEGATIVE_TOPIC_NAME,
		    integerSerde.deserializer(), doubleSerde.deserializer());
		neutralOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.NEUTRAL_TOPIC_NAME,
		    integerSerde.deserializer(), doubleSerde.deserializer());
		positiveOutputTopic = testDriver.createOutputTopic(NegNeuPosBranchApp.POSITIVE_TOPIC_NAME,
		    integerSerde.deserializer(), doubleSerde.deserializer());
	}
	
	@AfterEach
	void tearDown() throws Exception {
		testDriver.close();
	}
	
	@Test
	void testNeg() {
		int key = 0;
		String value = "This movie was actually terrible.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, Double> keyValue = negativeOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, 1.0));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}

	@Test
	void testNeu() {
		int key = 0;
		String value = "The movie was meh.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, Double> keyValue = neutralOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, 2.0));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}

	@Test
	void testPos() {
		int key = 0;
		String value = "I liked watching that movie.";
		contentInputTopic.pipeInput(key, value);
		KeyValue<Integer, Double> keyValue = positiveOutputTopic.readKeyValue();
		assertEquals(keyValue, new KeyValue<>(key, 3.0));
		assertTrue(negativeOutputTopic.isEmpty());
		assertTrue(neutralOutputTopic.isEmpty());
		assertTrue(positiveOutputTopic.isEmpty());
	}
}
