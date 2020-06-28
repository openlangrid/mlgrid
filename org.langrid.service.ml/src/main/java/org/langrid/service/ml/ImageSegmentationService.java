package org.langrid.service.ml;

public interface ImageSegmentationService {
	ImageSegmentationResult[] classify(String format, byte[] image, double threashold, int maxResults);

}
