package org.langrid.service.ml;

public class Transcript {
	public Transcript() {
	}
	public Transcript(int sentenceId, String sentence, boolean fixed, double accuracy) {
		super();
		this.sentenceId = sentenceId;
		this.sentence = sentence;
		this.fixed = fixed;
		this.accuracy = accuracy;
	}


	private int sentenceId;
	private String sentence;
	private boolean fixed;
	private double accuracy;
}
