package org.langrid.ml.server;

import javax.websocket.server.ServerEndpoint;

import org.langrid.ml.service.Mecab;
import org.langrid.ml.service.TestMaskedWordPrediction;
import org.langrid.ml.service.TestSpeechRecognition;
import org.langrid.servicecontainer.handler.websocket.WebSocketServer;
import org.springframework.stereotype.Component;

import jp.go.nict.langrid.servicecontainer.handler.annotation.Service;
import jp.go.nict.langrid.servicecontainer.handler.annotation.Services;

@Component
@ServerEndpoint(value="/ws/{serviceId}")
@Services({
		@Service(name="TestMaskedWordPrediction", impl=TestMaskedWordPrediction.class),
		@Service(name="TestSpeechRecognition", impl=TestSpeechRecognition.class),
		@Service(name="Mecab", impl=Mecab.class)
})
public class Server extends WebSocketServer {
}
