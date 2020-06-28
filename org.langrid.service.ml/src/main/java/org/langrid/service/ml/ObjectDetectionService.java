package org.langrid.service.ml;

public interface ObjectDetectionService {
	ObjectDetectionResult[] detect(String imageFormat, byte[] image, int maxResults);
}
