package org.langrid.ml.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
/*@PropertySource(ignoreResourceNotFound = true, value={
"file:./webrecorder.properties.default",
"file:./webrecorder.properties"})
*/
public class WebSocketConfig {
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
