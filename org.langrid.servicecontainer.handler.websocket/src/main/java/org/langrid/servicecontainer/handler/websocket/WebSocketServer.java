package org.langrid.servicecontainer.handler.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.ServletContextServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;
import jp.go.nict.langrid.servicecontainer.handler.annotation.ServicesUtil;
import jp.go.nict.langrid.servicecontainer.handler.jsonrpc.JsonRpcDynamicHandler;

@ServerEndpoint("/ws/{serviceId}")
public class WebSocketServer {
	public WebSocketServer() {
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config, @PathParam("serviceId") String serviceId) {
		String httpSessionName = HttpSession.class.getName();
		HttpSession httpSession = (HttpSession)config.getUserProperties().get(httpSessionName);
		session.getUserProperties().put(httpSessionName, httpSession);
		ServiceContext sc = new ServletContextServiceContext(httpSession.getServletContext());
		ServiceLoader loader = new ServiceLoader(sc, ServicesUtil.getServiceFactoryLoaders(getClass()));
		session.getUserProperties().put("serviceLoader", loader);
		session.getUserProperties().put("serviceHandler", new JsonRpcDynamicHandler());
	}

	@OnClose
	public void onClose(
			Session session, CloseReason reason, @PathParam("serviceId") String serviceId) {
	}

	@OnError
	public void onError(
			Session session, Throwable error, @PathParam("serviceId") String serviceId) {
	}

	@OnMessage
	public String onMessage(
			Session session, String message, @PathParam("serviceId") String serviceId) {
		JsonRpcHandler h = (JsonRpcHandler)session.getUserProperties().get("serviceHandler");
		ServiceLoader loader = (ServiceLoader)session.getUserProperties().get("serviceLoader");
		HttpSession httpSession = (HttpSession)session.getUserProperties().get(HttpSession.class.getName());
		ServiceContext sc = new ServletContextServiceContext(httpSession.getServletContext());
		String res = h.handle(sc, loader, serviceId, message);
		return res;
	}
}
