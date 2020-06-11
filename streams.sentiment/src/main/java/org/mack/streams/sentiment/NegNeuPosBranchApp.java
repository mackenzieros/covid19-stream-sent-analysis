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

	@SuppressWarnings({ "unchecked" })
	public static Topology createTopology() {
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
				.branch(
				    (key, value) -> (value > 0.0 && value < 2.0),	// predicate for negative
				    (key, value) -> (value >= 2.0 && value < 3.0),	// predicate for neutral
				    (key, value) -> (value >= 3.0)	// predicate for positive
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
