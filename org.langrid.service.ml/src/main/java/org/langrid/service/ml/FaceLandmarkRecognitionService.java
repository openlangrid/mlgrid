package org.langrid.service.ml;

public interface FaceLandmarkRecognitionService {
	FaceEmotionRecognitionResult[] recognize(
			String format, byte[] image, double threashold, int maxResults);
}
