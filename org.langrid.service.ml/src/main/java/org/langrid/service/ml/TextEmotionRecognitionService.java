package org.langrid.service.ml;

public interface TextEmotionRecognitionService {
	EmotionRecognitionResult[] recognize(String language, String text);
}
