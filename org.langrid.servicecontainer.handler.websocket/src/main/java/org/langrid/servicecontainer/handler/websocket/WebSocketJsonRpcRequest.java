package org.langrid.servicecontainer.handler.websocket;

import java.util.Map;

import jp.go.nict.langrid.commons.rpc.json.JsonRpcRequest;

public class WebSocketJsonRpcRequest extends JsonRpcRequest{
	private Map<String, String> mimeHeaders;
	public Map<String, String> getMimeHeaders() {
		return mimeHeaders;
	}
	public void setMimeHeaders(Map<String, String> mimeHeaders) {
		this.mimeHeaders = mimeHeaders;
	}
}
