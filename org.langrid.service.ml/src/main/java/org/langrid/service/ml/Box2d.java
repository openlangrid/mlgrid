package org.langrid.service.ml;

public class Box2d {
	public Box2d() {
	}
	public Box2d(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public double getX() {
		return x;
	}
	public void setX(double top) {
		this.x = top;
	}
	public double getY() {
		return y;
	}
	public void setY(double left) {
		this.y = left;
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
	private double x;
	private double y;
	private double width;
	private double height;

}
