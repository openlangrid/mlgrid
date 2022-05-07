package org.langrid.service.ml;

public interface SpeechRecognitionService {
	String startRecognition(SpeechRecognitionReceiverService receiver);
	void processRecognition(String rid, byte[] audio);
	void stopRecognition(String rid);
}
