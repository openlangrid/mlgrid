package org.langrid.service.ml;

public class FacialExpressionRecognitionResult {
	public FacialExpressionRecognitionResult() {
	}
	public FacialExpressionRecognitionResult(
			Box2d boundingBox,
			FacialExpressionRecognitionResultExpression[] expressions) {
		this.boundingBox = boundingBox;
		this.expressions = expressions;
	}
	public Box2d getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(Box2d boundingBox) {
		this.boundingBox = boundingBox;
	}
	public FacialExpressionRecognitionResultExpression[] getExpressions() {
		return expressions;
	}
	public void setExpressions(FacialExpressionRecognitionResultExpression[] expressions) {
		this.expressions = expressions;
	}

	private Box2d boundingBox;
	private FacialExpressionRecognitionResultExpression[] expressions;
}
