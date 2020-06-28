package org.langrid.service.ml;

public class FaceEmotionRecognitionResult {
	public FaceEmotionRecognitionResult() {
	}
	public FaceEmotionRecognitionResult(Box2d boundingBox, String label, double accuracy) {
		this.boundingBox = boundingBox;
		this.label = label;
		this.accuracy = accuracy;
	}
	public Box2d getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(Box2d boundingBox) {
		this.boundingBox = boundingBox;
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


	private Box2d boundingBox;
	private String label;
	private double accuracy;
}
