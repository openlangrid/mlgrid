package org.langrid.service.ml;

public interface ImageClassificationService {
	ImageClassificationResult[] classify(
			String format, byte[] image, String labelLanguage,
			double threashold, int maxResults);
}
