package org.langrid.servicecontainer.handler.websocket;

import javax.websocket.server.ServerEndpoint;

import jp.go.nict.langrid.servicecontainer.handler.annotation.Service;
import jp.go.nict.langrid.servicecontainer.handler.annotation.Services;

@Services(
		@Service(name="TestMaskedWordPrediction", impl=TestMaskedWordPrediction.class))
@ServerEndpoint(value="/ws/{serviceId}", configurator = WebSocketServerConfigurator.class)
public class TestServer extends WebSocketServer {
	
}
