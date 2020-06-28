package org.langrid.servicecontainer.handler.websocket;

import org.langrid.service.ml.MaskedWordPredictionResult;
import org.langrid.service.ml.MaskedWordPredictionService;

public class TestMaskedWordPrediction implements MaskedWordPredictionService{
	@Override
	public MaskedWordPredictionResult[] predict(String[] tokens) {
		return new MaskedWordPredictionResult[] {
				new MaskedWordPredictionResult(tokens)
		};
	}
}
