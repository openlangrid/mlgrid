package org.langrid.service.ml;

public interface FacialExpressionRecognitionService {
	FacialExpressionRecognitionResult[] recognize(
			String format, byte[] image, String labelLanguage, double threashold, int maxResults);
}
