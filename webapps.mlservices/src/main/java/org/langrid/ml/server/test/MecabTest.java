package org.langrid.ml.server.test;

import java.net.URL;

import org.langrid.client.ws.WebSocketJsonRpcClientFactory;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;

public class MecabTest {
	public static void main(String[] args) throws Throwable {
		MorphologicalAnalysisService s = new WebSocketJsonRpcClientFactory().create(
				MorphologicalAnalysisService.class,
				new URL("ws://127.0.0.1:8080/ws/Mecab"));
		for(Morpheme m : s.analyze("ja", "貴社の記者が汽車で帰社する。")) {
			System.out.println(m);
		}
	}
}
