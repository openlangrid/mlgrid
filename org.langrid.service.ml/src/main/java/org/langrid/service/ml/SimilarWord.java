package org.langrid.service.ml;

public class SimilarWord {
	public SimilarWord() {
	}

	public SimilarWord(String word, double similarity) {
		this.word = word;
		this.similarity = similarity;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	private String word;
	private double similarity;
}
