package org.langrid.service.ml;

public class TextToTextGenerationResult {
	public TextToTextGenerationResult(){
	}
	public TextToTextGenerationResult(byte[] image, double accuracy) {
		this.image = image;
		this.accuracy = accuracy;
	}

	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	private byte[] image;
	private double accuracy;
}
