package org.langrid.service.ml;

public class Polygon2d {
	public Polygon2d() {
	}
	public Polygon2d(Point2d[] points) {
		this.points = points;
	}
	public Point2d[] getPoints() {
		return points;
	}
	public void setPoints(Point2d[] points) {
		this.points = points;
	}

	private Point2d[] points;
}
