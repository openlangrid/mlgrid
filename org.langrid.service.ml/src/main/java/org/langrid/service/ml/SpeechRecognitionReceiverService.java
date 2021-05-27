package org.langrid.service.ml;

public interface SpeechRecognitionReceiverService {
	void onRecognitionResult(String recognitionId, Transcript[] results);
}
