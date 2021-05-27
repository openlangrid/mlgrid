package org.langrid.ml.server.test;

import java.net.URL;

import org.langrid.client.ws.WebSocketJsonRpcClientFactory;
import org.langrid.service.ml.SpeechRecognitionService;

public class TestSpeechRecognitionClient {
	public static void main(String[] args) throws Throwable {
		SpeechRecognitionService s = new WebSocketJsonRpcClientFactory().create(
				SpeechRecognitionService.class,
				new URL("ws://127.0.0.1:8080/ws/TestSpeechRecognition"));
		String id = s.startRecognition((rid, results) -> {
			System.out.println("callback: " + rid);
		});
		System.out.println("id: " + id);
		s.processRecognition(id, new byte[] {});
		s.processRecognition(id, new byte[] {});
		s.stopRecognition(id);
	}
}
