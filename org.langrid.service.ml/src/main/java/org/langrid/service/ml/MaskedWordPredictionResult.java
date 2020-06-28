package org.langrid.service.ml;

public class MaskedWordPredictionResult {
	public MaskedWordPredictionResult() {
	}
	public MaskedWordPredictionResult(String[] predictions) {
		this.predictions = predictions;
	}
	public String[] getPredictions() {
		return predictions;
	}
	public void setPredictions(String[] predictions) {
		this.predictions = predictions;
	}

	private String[] predictions;
}
