package org.langrid.service.ml;

public interface SpeechRecognitionService {
	String startRecognition(SpeechRecognitionReceiverService receiver);
	void processRecognition(String recognitionId, byte[] audio);
	void stopRecognition(String recognitionId);
}
