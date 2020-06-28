package org.langrid.servicecontainer.handler.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class WebSocketServerTest {
	public static void main(String[] args) throws Throwable {
		Server server = null;
		try {
			server = new Server(8080);
			server.addConnector( new ServerConnector(server));

			// ServletContextHandlerを生成
			ServletContextHandler context = new ServletContextHandler(
					ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);

			// ServerContainerを生成
			WebSocketServerContainerInitializer.configure(context, (servletContext, serverContainer) -> {
				serverContainer.addEndpoint(TestServer.class);
			});
			// Server起動
			server.start();
			
			
		} finally {
			// Server終了
			if (server != null) {
				server.stop();
			}
		}
	}
}
