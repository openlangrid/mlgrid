package org.langrid.service.ml;

public class ImageSegmentationResult {
	public ImageSegmentationResult() {
	}
	public ImageSegmentationResult(double x, double y, double width, double height,
			String label, double accuracy, Polygon2d segment) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = label;
		this.accuracy = accuracy;
		this.segment = segment;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
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
	public Polygon2d getSegment() {
		return segment;
	}
	public void setSegment(Polygon2d segment) {
		this.segment = segment;
	}

	private double x;
	private double y;
	private double width;
	private double height;
	private String label;
	private double accuracy;
	private Polygon2d segment;
}
