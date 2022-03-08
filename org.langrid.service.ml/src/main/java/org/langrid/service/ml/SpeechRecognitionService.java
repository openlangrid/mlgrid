package org.langrid.service.ml;

public interface SpeechRecognitionService {
	void startRecognition(SpeechRecognitionReceiverService receiver);
	void processRecognition(byte[] audio);
	void stopRecognition();
}
