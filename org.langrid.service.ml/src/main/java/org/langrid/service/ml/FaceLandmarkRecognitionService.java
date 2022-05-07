package org.langrid.service.ml;

public interface FaceLandmarkRecognitionService {
	FacialExpressionRecognitionResult[] recognize(
			String format, byte[] image, double threashold, int maxResults);
}
