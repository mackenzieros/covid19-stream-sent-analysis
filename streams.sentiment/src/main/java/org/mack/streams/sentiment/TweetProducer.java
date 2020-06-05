package org.mack.streams.sentiment;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class TweetProducer {
	public static final String[] mockTweets = new String[] {
			"Fiji has just cleared the last of our active #COVID19 patients. \r\n" + 
			"\r\n" + 
			"And even with our testing numbers climbing by the day, it's now been 45 days since we recorded our last case. With no deaths, our recovery rate is 100%.\r\n" + 
			"\r\n" + 
			"Answered prayers, hard work, and affirmation of science!",
			"THERE ARE NOW ZERO ACTIVE #COVID19 CASES IN BRITISH COLUMBIA OUTSIDE THE LOWER MAINLAND",
			"Around the world, we are seeing examples of how we can live and thrive even as we work towards a cure or vaccine for #COVID19.\r\n" + 
			"\r\n" + 
			"Today, we take further steps in easing some restrictions on public gatherings, and other decisions as we adjust to this reality.",
			"Meanwhile, another 245 cases of COVID19 were reported today in Washington State. The daily -- yes, daily -- reminder of why the border must remain closed for now. #COVID19",
			"This playground is a short drive from my house.  Cute, huh? Stunning actually.  It was completely demolished and disintegrated- not a trace of it left - just a patch of empty space - because of #COVID19- which hates kids. I mean the COVIDIOTS HATE kids obviously",
			"'Could derail' a rotten agenda more like..\r\n" + 
			"Bill Gates certainly craves that mandatory nefarious #COVID19 vaccine.. The vaccine clearly weaved into the global PLANdemic agenda as an End Game..\r\n" + 
			"Resistance will not be futile.."
	};

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		Producer<Integer, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < mockTweets.length; ++i) {
			producer.send(new ProducerRecord<> (NegNeuPosBranchApp.CONTENT_TOPIC_NAME, mockTweets[i]));
		}
		producer.close();
	}
}
