package org.langrid.service.ml;

public interface ObjectDetectionService {
	ObjectDetectionResult[] detect(
			String imageFormat, byte[] image, String labelLanguage, int maxResults);
}
