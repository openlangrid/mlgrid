package org.langrid.service.ml;

public class HumanPoseEstimation2dResult {
	public HumanPoseEstimation2dResult() {
		// TODO Auto-generated constructor stub
	}
	public HumanPoseEstimation2dResult(Point2d[] points) {
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
