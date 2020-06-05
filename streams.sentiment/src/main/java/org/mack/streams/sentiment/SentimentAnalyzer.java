package org.mack.streams.sentiment;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {
	public static double findSentiment(String content) {
		System.out.printf("Content: %s\n", content);	// for testing
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		double totalSentiment = 0;
		int count = 0;
		if (content != null && content.length() > 0) {
			Annotation annotation = pipeline.process(content);
			for (CoreMap sentence: annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentAnnotatedTree.class);
				totalSentiment += RNNCoreAnnotations.getPredictedClass(tree);
				++count;
			}
		}
		return totalSentiment/count;
	}
	
	public static void main(String[] args) {
		String s = "The move was terrible. I hated that movie. There were some good parts I admit.";
		double res = findSentiment(s);
		System.out.println(res);
	}
}
