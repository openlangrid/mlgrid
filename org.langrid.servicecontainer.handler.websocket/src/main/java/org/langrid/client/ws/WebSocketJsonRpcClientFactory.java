/*
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.langrid.client.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jp.go.nict.langrid.client.ClientFactory;
import jp.go.nict.langrid.client.RequestAttributes;
import jp.go.nict.langrid.client.ResponseAttributes;
import jp.go.nict.langrid.client.RpcRequestAttributes;
import jp.go.nict.langrid.client.RpcResponseAttributes;
import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.lang.ClassUtil;
import jp.go.nict.langrid.commons.rpc.RpcFaultUtil;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcRequest;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcResponse;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSONException;

public class WebSocketJsonRpcClientFactory implements ClientFactory{
	static {
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			@Override
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if(protocol.contentEquals("ws")) {
					return new URLStreamHandler() {
						@Override
						protected URLConnection openConnection(URL u) throws IOException {
							return null;
						}
					};
				}
				return null;
			}
		});
	}
	class JsonRpcInvocationHandler implements InvocationHandler{
		WebSocket ws;
		BlockingQueue<JsonRpcResponse> retValueQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Type> retTypeQueue = new LinkedBlockingQueue<>();
		public JsonRpcInvocationHandler(URL url)
		throws ExecutionException, InterruptedException, URISyntaxException{
			this.url = url;
			HttpClient client = HttpClient.newHttpClient();
			WebSocket.Builder wsb = client.newWebSocketBuilder();
			WebSocket.Listener listener = new WebSocket.Listener() {
				@Override
				public void onOpen(WebSocket webSocket){
//					webSocket.request(2);
				}
				@Override
				public void onError(WebSocket webSocket, Throwable error) {
					error.printStackTrace();
				}
				@Override
				public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
//					webSocket.request(2);
					ws.request(100000);
					if(data.charAt(0) == 'R') {
						JsonRpcResponse ret;
						try{
							ret = JSON.decode(data.subSequence(2, data.length()).toString(), JsonRpcResponse.class);
						} catch(JSONException e){
							throw new RuntimeException("Parse error for JSON:" + data, e);
						}
//						if(ret.getHeaders() != null){
//							resAttrs.loadAttributes(con, Arrays.asList(ret.getHeaders()));
//						}
						retValueQueue.offer(ret);
					} else if(data.charAt(0) == 'C'){
						int idx = -1;
						for(int i = 2; i < data.length(); i++) {
							if(data.charAt(i) != ':') continue;
							idx = i;
							break;
						}
						if(idx == -1) {
							System.out.println("unknown message: " + data);
							return null;
						}
						String cid = data.subSequence(2, idx).toString();
						Pair<Class<?>, Object> c = callbacks.get(cid);
						if(c == null) {
							System.out.println("callback not exist: " + data);
							return null;
						}
						String cr = data.subSequence(idx + 1, data.length()).toString();
						JsonRpcRequest req = JSON.decode(cr, JsonRpcRequest.class);
						
						int paramLength = req.getParams() == null ? 0 : req.getParams().length;
						Method method = ClassUtil.findMethod(c.getFirst(), req.getMethod(), paramLength);
						if(method == null) {
							System.out.println("method not exist in " + c.getFirst() + ":  " + data.toString());
							return null;
						}
						try {
							MethodUtil.invokeMethod(c.getSecond(), method, req.getParams(), new Converter());
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("unknown message: " + data);
					}
					return null;
				}
			};
			CompletableFuture<WebSocket> comp = wsb.buildAsync(url.toURI(), listener);
			this.ws = comp.get();
		}
		public JsonRpcInvocationHandler(URL url, int connectionTimeout, int readTimeout){
			this.url = url;
		}
		public URL getUrl() {
			return url;
		}
		public RpcRequestAttributes getReqAttrs() {
			return reqAttrs;
		}
		public RpcResponseAttributes getResAttrs() {
			return resAttrs;
		}
		private Map<String, Pair<Class<?>, Object>> callbacks = new HashMap<>();
		private void prepareCallbacks(Method method, Object[] args) {
			Class<?>[] pts= method.getParameterTypes();
			for(int i = 0; i < pts.length; i++) {
				Class<?> pc = pts[i];
				if(!pc.isInterface()) continue;
				String cid = "callback_" + callbacks.size();
				callbacks.put(cid, Pair.create(pc, args[i]));
				args[i] = cid;
			}
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable{
			Class<?> clz = method.getDeclaringClass();
			if(clz.equals(RequestAttributes.class)){
				return method.invoke(reqAttrs, args);
			} else if(clz.equals(ResponseAttributes.class)){
				return method.invoke(resAttrs, args);
			} else{
				try{
					prepareCallbacks(method, args);
					String j = JSON.encode(JsonRpcUtil.createRequest(reqAttrs.getAllRpcHeaders(), method, args));
					ws.request(100000);
					ws.sendText(j, true).get();
					JsonRpcResponse res = retValueQueue.poll(10, TimeUnit.SECONDS);
					if(res == null) {
						throw new TimeoutException();
					}
					if(res.getError() != null){
						throw RpcFaultUtil.rpcFaultToThrowable(res.getError());
					}
					return converter.convert(res.getResult(), method.getGenericReturnType());
				} finally{
				}
			}
		}
		private URL url;
		private RpcRequestAttributes reqAttrs = new RpcRequestAttributes();
		private RpcResponseAttributes resAttrs = new RpcResponseAttributes();
	}

	@Override
	public <T> T create(Class<T> interfaceClass, URL url) {
		try {
			return create(interfaceClass, new JsonRpcInvocationHandler(url));
		} catch (ExecutionException | InterruptedException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T create(Class<T> interfaceClass, URL url, String userId, String password) {
		try {
			return create(interfaceClass, new JsonRpcInvocationHandler(url));
		} catch (ExecutionException | InterruptedException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T create(Class<T> interfaceClass, InvocationHandler handler){
		return interfaceClass.cast(Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader()
				, new Class<?>[]{interfaceClass, RequestAttributes.class, ResponseAttributes.class}
				, handler
				));
	}

	public void setRequestDumpStream(OutputStream stream){
		this.requestDumpStream = stream;
	}

	public void setResponseDumpStream(OutputStream stream){
		this.responseDumpStream = stream;
	}

	private OutputStream requestDumpStream;
	private OutputStream responseDumpStream;
	private static Converter converter = new Converter();
}
