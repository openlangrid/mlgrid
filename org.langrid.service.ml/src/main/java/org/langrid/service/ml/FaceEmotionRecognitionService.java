package org.langrid.service.ml;

public interface FaceEmotionRecognitionService {
	FaceEmotionRecognitionResult[] recognize(
			String format, byte[] image, double threashold, int maxResults);
}
