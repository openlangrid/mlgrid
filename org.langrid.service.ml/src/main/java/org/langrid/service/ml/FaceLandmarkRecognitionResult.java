package org.langrid.service.ml;

public class FaceLandmarkRecognitionResult {
	public FaceLandmarkRecognitionResult() {
	}
	public FaceLandmarkRecognitionResult(Box2d boundingBox, double accuracy, Point2d[] landmarks) {
		this.boundingBox = boundingBox;
		this.accuracy = accuracy;
		this.landmarks = landmarks;
	}
	public Box2d getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(Box2d boundingBox) {
		this.boundingBox = boundingBox;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public Point2d[] getLandmarks() {
		return landmarks;
	}
	public void setLandmarks(Point2d[] landmarks) {
		this.landmarks = landmarks;
	}

	private Box2d boundingBox;
	private double accuracy;
	private Point2d[] landmarks;
}
