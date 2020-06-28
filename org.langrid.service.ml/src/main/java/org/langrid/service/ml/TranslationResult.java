package org.langrid.service.ml;

public class TranslationResult {
	public TranslationResult() {
	}
	public TranslationResult(String result, double accuracy) {
		this.result = result;
		this.accuracy = accuracy;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	private String result;
	private double accuracy;
}
