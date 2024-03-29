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

import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.ServletContextServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;
import jp.go.nict.langrid.servicecontainer.handler.annotation.ServicesUtil;

@ServerEndpoint(value="/ws/{serviceId}")
public class WebSocketServer {
	public WebSocketServer() {
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config, @PathParam("serviceId") String serviceId) {
		String httpSessionName = HttpSession.class.getName();
		HttpSession httpSession = (HttpSession)config.getUserProperties().get(httpSessionName);
		ServiceLoader loader = null;
		if(httpSession != null) {
			session.getUserProperties().put(httpSessionName, httpSession);
			ServiceContext sc = new ServletContextServiceContext(httpSession.getServletContext());
			loader = new ServiceLoader(sc, ServicesUtil.getServiceFactoryLoaders(getClass()));
		} else {
			loader = new ServiceLoader(new LocalServiceContext(), ServicesUtil.getServiceFactoryLoaders(getClass()));
		}
		session.getUserProperties().put("serviceLoader", loader);
		session.getUserProperties().put("serviceHandler", new WebSocketJsonRpcHandler(session));
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
	public String onMessage(Session session, String message, @PathParam("serviceId") String serviceId) {
		WebSocketJsonRpcHandler h = (WebSocketJsonRpcHandler)session.getUserProperties().get("serviceHandler");
		ServiceLoader loader = (ServiceLoader)session.getUserProperties().get("serviceLoader");
		ServiceContext sc = null;
		HttpSession httpSession = (HttpSession)session.getUserProperties().get(HttpSession.class.getName());
		if(httpSession != null) {
			sc = new ServletContextServiceContext(httpSession.getServletContext());
		} else {
			sc = new LocalServiceContext();
		}
		return h.handle(sc, loader, serviceId, message);
	}

	@OnMessage
	public String onMessage(Session session, byte[] message, @PathParam("serviceId") String serviceId) {
		WebSocketJsonRpcHandler h = (WebSocketJsonRpcHandler)session.getUserProperties().get("serviceHandler");
		ServiceLoader loader = (ServiceLoader)session.getUserProperties().get("serviceLoader");
		ServiceContext sc = null;
		HttpSession httpSession = (HttpSession)session.getUserProperties().get(HttpSession.class.getName());
		if(httpSession != null) {
			sc = new ServletContextServiceContext(httpSession.getServletContext());
		} else {
			sc = new LocalServiceContext();
		}
		return h.handle(sc, loader, serviceId, message);
	}
}
