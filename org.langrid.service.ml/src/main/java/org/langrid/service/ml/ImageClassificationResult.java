package org.langrid.service.ml;

public class ImageClassificationResult {
	public ImageClassificationResult() {
	}
	public ImageClassificationResult(String label, double accuracy) {
		this.label = label;
		this.accuracy = accuracy;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}


	private String label;
	private double accuracy;
}
