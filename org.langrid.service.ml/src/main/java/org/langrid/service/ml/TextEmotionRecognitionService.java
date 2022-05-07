package org.langrid.service.ml;

public interface TextEmotionRecognitionService {
	TextEmotionRecognitionResult[] recognize(String language, String text, int maxResults);
}
