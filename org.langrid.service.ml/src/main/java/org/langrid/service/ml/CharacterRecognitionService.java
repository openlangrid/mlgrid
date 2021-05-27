package org.langrid.service.ml;

public interface CharacterRecognitionService {
	CharacterRecognitionResult[] recognize(String imageFormat, byte[] image,
			double threashold, int maxResults);
}
