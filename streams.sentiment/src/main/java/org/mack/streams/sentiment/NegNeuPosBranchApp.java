package org.mack.streams.sentiment;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

public class NegNeuPosBranchApp {
	public static final String CONTENT_TOPIC_NAME = "content";
	public static final String NEGATIVE_TOPIC_NAME = "negative-sentiment";
	public static final String NEUTRAL_TOPIC_NAME = "neutral-sentiment";
	public static final String POSITIVE_TOPIC_NAME = "positive-sentiment";
	
	public static Properties createProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "neg-neu-pos-branch");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		return props;
	}
	
	public static Topology createTopology() {
		final StreamsBuilder builder = new StreamsBuilder();
		KStream<Integer, String> stream = builder.stream(CONTENT_TOPIC_NAME);
		KStream<Integer, String>[] branches = stream
				.branch(
						(key, value) -> true,	// predicate for neg
						(key, value) -> true,	// predicate for neu
						(key, value) -> true	// predicate for pos
				);
		branches[0]
				.peek((key, value) -> System.out.printf("Negative: %s", value))
				.to(NEGATIVE_TOPIC_NAME);
		branches[1]
				.peek((key, value) -> System.out.printf("Neutral: %s", value))
				.to(NEUTRAL_TOPIC_NAME);
		branches[2]
				.peek((key, value) -> System.out.printf("Positive: %s", value))
				.to(POSITIVE_TOPIC_NAME);
		
		return builder.build();
	}
	
	public static void main(String[] args) {
		Properties props = createProperties();
		
		final Topology topology = createTopology();
		final KafkaStreams streams = new KafkaStreams(topology, props);
		final CountDownLatch latch = new CountDownLatch(1);
		
		Runtime.getRuntime().addShutdownHook(new Thread("kafka-streams-shutdown-hook") {
			@Override
			public void run() {
				streams.close();
				latch.countDown();
			}
		});
		
		try {
			streams.start();
			latch.await();
		} catch (Throwable e) {
			System.exit(1);
		}
		System.exit(0);
	}
}
