package org.langrid.service.ml;

public class TextGuidedImageConversionResult {
	public TextGuidedImageConversionResult() {
	}
	public TextGuidedImageConversionResult(byte[] image) {
		this.image = image;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}

	private byte[] image;
}
