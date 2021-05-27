package org.langrid.ml.server.test;

import java.net.URL;

import org.langrid.client.ws.WebSocketJsonRpcClientFactory;
import org.langrid.service.ml.MaskedWordPredictionService;

import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;

public class WebSocketServerTestClient {
	public static void main(String[] args) throws Throwable {
		MaskedWordPredictionService s = new WebSocketJsonRpcClientFactory().create(
				MaskedWordPredictionService.class,
				new URL("ws://127.0.0.1:8080/ws/TestMaskedWordPrediction"));
		System.out.println(JSON.encode(s.predict(new String[] {"こんにちは"}), true));
		System.out.println(JSON.encode(s.predict(new String[] {"こんにちは"}), true));
		System.out.println(JSON.encode(s.predict(new String[] {"こんにちは"}), true));
	}
}
