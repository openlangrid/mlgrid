package org.langrid.service.ml;

public interface ImageClassificationService {
	ImageClassificationResult[] classify(String format, byte[] image, double threashold, int maxResults);
}
