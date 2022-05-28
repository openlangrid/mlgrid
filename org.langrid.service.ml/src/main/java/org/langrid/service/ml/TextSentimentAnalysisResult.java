package org.langrid.service.ml;

public class TextSentimentAnalysisResult {
	public TextSentimentAnalysisResult(){
	}
	public TextSentimentAnalysisResult(TextSentimentLabel label, double accuracy) {
		this.label = label;
		this.accuracy = accuracy;
	}

	public TextSentimentLabel getLabel() {
		return label;
	}
	public void setLabel(TextSentimentLabel label) {
		this.label = label;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	private TextSentimentLabel label;
	private double accuracy;
}
