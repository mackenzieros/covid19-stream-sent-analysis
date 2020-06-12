package org.mack.streams.sentiment;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import java.util.concurrent.CountDownLatch;
import java.util.Properties;

/**
 * An Apache Kafka Stream app that splits a stream of records based on the sentiment values of record values.
 * Creates a stream that expects Integer keys and String values. Topology is one source processor and three
 * sink processors (for negative, neutral, and positive).
 */
public class NegNeuPosBranchApp {
  /**
   * Topic names
   */
	public static final String CONTENT_TOPIC_NAME = "content";
	public static final String NEGATIVE_TOPIC_NAME = "negative-sentiment";
	public static final String NEUTRAL_TOPIC_NAME = "neutral-sentiment";
	public static final String POSITIVE_TOPIC_NAME = "positive-sentiment";
	
	/** 
	 * Returns a Properties object with configurations to be used in the stream. 
	 */
	public static Properties createProperties() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "neg-neu-pos-branch");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		return props;
	}

	/**
	 * Returns the topology of the Kafka data flow.
	 * Creates and registers a state store. 
	 * Builds and branches our stream based on sentiment values.
	 * Transforms all incoming records' values (tweets as Strings) into sentiment values (represented as Doubles).
	 */
	@SuppressWarnings({ "unchecked" })
	public static Topology createTopology() {
	  // sentiment thresholds as described by Stanford CoreNLP's RNNCoreAnnotations class
	  final double NEGATIVE_THRESHOLD = 2.0;
	  final double POSITIVE_THRESHOLD = 3.0;
		final StreamsBuilder builder = new StreamsBuilder();
		// create store
		StoreBuilder<KeyValueStore<Integer, String>> keyValueStoreBuilder = 
			Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("myValueTransformState"),
			    Serdes.Integer(),
			    Serdes.String());
		// register store
		builder.addStateStore(keyValueStoreBuilder);
		KStream<Integer, String> stream = builder.stream(CONTENT_TOPIC_NAME);
		KStream<Integer, Double>[] branches = stream
				.transformValues(new ValueTransformerSupplier<String, Double>() {  
					public ValueTransformer<String, Double> get() {
						return new ValueTransformer<String, Double>() {
							private StateStore state;
							
							public void init(ProcessorContext context) {
								this.state = context.getStateStore("myValueTransformState");
							}
							
							// able to access this.state
							public Double transform(String value) {
								return SentimentAnalyzer.findSentiment(value);
							}
							
							public void close() {}
						};
					}
				}, "myValueTransformState")
				.branch(  // branch our stream such that records which satisfy the following predicates are sent to the appropriate topic
				    (key, value) -> (value < NEGATIVE_THRESHOLD),	// predicate for a negative sentiment
				    (key, value) -> (value >= NEGATIVE_THRESHOLD && value < POSITIVE_THRESHOLD),	// predicate for a neutral sentiment
				    (key, value) -> (value >= POSITIVE_THRESHOLD)	// predicate for a positive sentiment
				);

		branches[0]
		    .peek((key, value) -> System.out.printf("Negative: %f\n", value))
		    .to(NEGATIVE_TOPIC_NAME, Produced.with(Serdes.Integer(), Serdes.Double()));
		branches[1]
		    .peek((key, value) -> System.out.printf("Neutral: %f\n", value))
		    .to(NEUTRAL_TOPIC_NAME, Produced.with(Serdes.Integer(), Serdes.Double()));
		branches[2]
  			.peek((key, value) -> System.out.printf("Positive: %f\n", value))
  			.to(POSITIVE_TOPIC_NAME, Produced.with(Serdes.Integer(), Serdes.Double()));
		
		return builder.build();
	}
	
	/**
	 * Driver for the application.
	 * Builds the stream using the properties and topology.
	 * Sets a latch for program termination.
	 * Starts and closes the stream.
	 */
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
