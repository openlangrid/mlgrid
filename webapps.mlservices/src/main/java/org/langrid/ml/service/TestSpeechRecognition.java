package org.langrid.ml.service;

import org.langrid.service.ml.SpeechRecognitionReceiverService;
import org.langrid.service.ml.SpeechRecognitionService;
import org.langrid.service.ml.Transcript;

public class TestSpeechRecognition implements SpeechRecognitionService{
	private SpeechRecognitionReceiverService receiver;
	@Override
	public String startRecognition(SpeechRecognitionReceiverService receiver) {
		this.receiver = receiver;
		return "" + (rid++);
	}
	private int rid;
	private int tid;
	@Override
	public void processRecognition(String recognitionId, byte[] audio) {
		receiver.onRecognitionResult("" + (tid++), new Transcript[] { new Transcript()});
		receiver.onRecognitionResult("" + (tid++), new Transcript[] { new Transcript()});
	}
	@Override
	public void stopRecognition(String recognitionId) {
	}
}
