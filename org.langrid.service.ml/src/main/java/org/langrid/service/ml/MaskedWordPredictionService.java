package org.langrid.service.ml;

public interface MaskedWordPredictionService {
	// use "[MASK]" for masked token.
	MaskedWordPredictionResult[] predict(String[] tokens);
}
