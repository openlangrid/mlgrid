package org.langrid.service.ml;

public class CharacterRecognitionResult {
	public CharacterRecognitionResult() {
	}
	public CharacterRecognitionResult(Box2d boundingBox, String text) {
		this.boundingBox = boundingBox;
		this.text = text;
	}
	public Box2d getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(Box2d boundingBox) {
		this.boundingBox = boundingBox;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	private Box2d boundingBox;
	private String text;
}
