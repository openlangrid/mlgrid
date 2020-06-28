package org.langrid.servicecontainer.handler.websocket;

import jp.go.nict.langrid.commons.rpc.json.JsonRpcResponse;
import jp.go.nict.langrid.commons.ws.MimeHeaders;

public class WebSocketJsonRpcResponse extends JsonRpcResponse{
	private MimeHeaders mimeHeaders;
	public MimeHeaders getMimeHeaders() {
		return mimeHeaders;
	}
	public void setMimeHeaders(MimeHeaders mimeHeaders) {
		this.mimeHeaders = mimeHeaders;
	}
}
