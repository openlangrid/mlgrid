/*
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2011 NICT Language Grid Project.
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
package org.langrid.servicecontainer.handler.websocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import org.langrid.client.ws.MethodUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;
import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.beanutils.ConverterForJsonRpc;
import jp.go.nict.langrid.commons.lang.ClassUtil;
import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.rpc.RpcFault;
import jp.go.nict.langrid.commons.rpc.RpcFaultUtil;
import jp.go.nict.langrid.commons.rpc.RpcHeader;
import jp.go.nict.langrid.commons.rpc.intf.RpcAnnotationUtil;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.commons.ws.MimeHeaders;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import jp.go.nict.langrid.servicecontainer.handler.RIProcessor;
import jp.go.nict.langrid.servicecontainer.handler.ServiceFactory;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

/**
 * @author Takao Nakaguchi
 */
public class WebSocketJsonRpcHandler {
	public WebSocketJsonRpcHandler(Session session) {
		this.session = session;
	}

	private Pair<Object, Method> getService(ServiceContext sc, ClassLoader cl, ServiceFactory f, String serviceName,
			WebSocketJsonRpcRequest req, WebSocketJsonRpcResponse res) {
		Collection<Class<?>> interfaceClasses = f.getInterfaces();
		int paramLength = req.getParams() == null ? 0 : req.getParams().length;
		Class<?> clazz = null;
		Method method = null;
		for(Class<?> clz : interfaceClasses){
			method = ClassUtil.findMethod(clz, req.getMethod(), paramLength);
			if(method == null) continue;
			clazz = clz;
			break;
		}
		if(method == null){
			return Pair.create(null, null);
		}
		Class<?> clz = clazz;
		Object service = services.computeIfAbsent(serviceName, sn -> f.createService(cl, sc, clz));
		initialize(serviceName, service);
		// Currently only array("[]") is supported, while JsonRpc accepts Object("{}")
		Object[] params = req.getParams();
		prepareCallbacks(cl, method, params);
		return Pair.create(service, method);
	}

	private void prepareCallbacks(ClassLoader cl, Method method, Object[] params) {
		Class<?>[] pts= method.getParameterTypes();
		for(int i = 0; i < pts.length; i++) {
			Class<?> pc = pts[i];
			if(!pc.isInterface()) continue;
			String cid = params[i].toString();
			params[i] = Proxy.newProxyInstance(cl, new Class<?>[] {pc}, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if(method.getDeclaringClass().isInterface()) {
						String j = JSON.encode(JsonRpcUtil.createRequest(Collections.emptyList(), method, args));
						session.getBasicRemote().sendText("C:" + cid + ":" + j);
						return null;
					} else {
						return method.invoke(cid, args);
					}
				}
			});
		}
	}

	public String handle(
			ServiceContext sc, ServiceLoader sl, String serviceName,
			String request){
		WebSocketJsonRpcRequest req = JSON.decode(request, WebSocketJsonRpcRequest.class);
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<RpcHeader> resHeaders = new ArrayList<RpcHeader>();
		Class<?> clazz = null;
		Object result = null;
		WebSocketJsonRpcResponse res = new WebSocketJsonRpcResponse();
		try{
			ServiceFactory f = sl.loadServiceFactory(cl, serviceName);
			if(f == null){
				res.setError(newRpcFault404());
				return JSON.encode(res);
			}
			RIProcessor.start(sc);
			var sm = getService(sc, cl, f, serviceName, req, res);
			Object service = sm.getFirst();
			Method method = sm.getSecond();
			if(method == null) {
				int paramLength = req.getParams() == null ? 0 : req.getParams().length;
				logger.warning(String.format(
						"method \"%s(%s)\" not found in service \"%s\"."
						, req.getMethod(), StringUtil.repeatedString("arg", paramLength, ",")
						, serviceName));
				res.setError(newRpcFault404());
				return JSON.encode(res);
			}
			try{
				result = MethodUtil.invokeMethod(service, method, req.getParams(), converter);
			} finally{
				MimeHeaders resMimeHeaders = new MimeHeaders();
				RIProcessor.finish(resMimeHeaders, resHeaders);
				res.setMimeHeaders(resMimeHeaders);
			}
			res.setId(req.getId());
			res.setHeaders(resHeaders.toArray(new RpcHeader[]{}));
			res.setResult(result);
			Method implMethod = service.getClass().getMethod(method.getName(), method.getParameterTypes());
			int depth = RpcAnnotationUtil.getMethodMaxReturnObjectDepth(implMethod, method);
			return new JSON(depth + 1).format(res);
		} catch(InvocationTargetException e){
			Throwable t = e.getTargetException();
			logger.log(Level.SEVERE, "failed to handle request for " + serviceName
					+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod()
					, t);
			res.setError(newRpcFault(t));
			return JSON.encode(res);
		} catch(Exception e){
			logger.log(Level.SEVERE, "failed to handle request for " + serviceName
					+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod()
					, e);
			res.setError(newRpcFault(e));
			return JSON.encode(res);
		}
	}

	public String handle(
			ServiceContext sc, ServiceLoader sl, String serviceName,
			byte[] request){
		ObjectMapper mapper = new ObjectMapper(new BsonFactory());
		WebSocketJsonRpcRequest req;
		try {
			req = mapper.readValue(request, WebSocketJsonRpcRequest.class);
		} catch (IOException e1) {
			WebSocketJsonRpcResponse res = new WebSocketJsonRpcResponse();
			res.setError(newRpcFault(e1));
			try {
				return mapper.writeValueAsString(res);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<RpcHeader> resHeaders = new ArrayList<RpcHeader>();
		Class<?> clazz = null;
		Object result = null;
		WebSocketJsonRpcResponse res = new WebSocketJsonRpcResponse();
		try{
			ServiceFactory f = sl.loadServiceFactory(cl, serviceName);
			if(f == null){
				res.setError(newRpcFault404());
				return JSON.encode(res);
			}
			RIProcessor.start(sc);
			Object service = null;
			Method method = null;
			try{
				Collection<Class<?>> interfaceClasses = f.getInterfaces();
				int paramLength = req.getParams() == null ? 0 : req.getParams().length;
				for(Class<?> clz : interfaceClasses){
					method = ClassUtil.findMethod(clz, req.getMethod(), paramLength);
					if(method == null) continue;
					clazz = clz;
					break;
				}
				if(method == null){
					logger.warning(String.format(
							"method \"%s(%s)\" not found in service \"%s\"."
							, req.getMethod(), StringUtil.repeatedString("arg", paramLength, ",")
							, serviceName));
					res.setError(newRpcFault404());
					return JSON.encode(res);
				}
				Class<?> clz = clazz;
				service = services.computeIfAbsent(serviceName, sn -> f.createService(cl, sc, clz));
				initialize(serviceName, service);
				// Currently only array("[]") is supported, while JsonRpc accepts Object("{}")
				Object[] params = req.getParams();
				prepareCallbacks(cl, method, params);
				result = MethodUtil.invokeMethod(service, method, params, converter);
			} finally{
				MimeHeaders resMimeHeaders = new MimeHeaders();
				RIProcessor.finish(resMimeHeaders, resHeaders);
				res.setMimeHeaders(resMimeHeaders);
			}
			res.setId(req.getId());
			res.setHeaders(resHeaders.toArray(new RpcHeader[]{}));
			res.setResult(result);
			Method implMethod = service.getClass().getMethod(method.getName(), method.getParameterTypes());
			int depth = RpcAnnotationUtil.getMethodMaxReturnObjectDepth(implMethod, method);
			return new JSON(depth + 1).format(res);
		} catch(InvocationTargetException e){
			Throwable t = e.getTargetException();
			logger.log(Level.SEVERE, "failed to handle request for " + serviceName
					+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod()
					, t);
			res.setError(newRpcFault(t));
			return JSON.encode(res);
		} catch(Exception e){
			logger.log(Level.SEVERE, "failed to handle request for " + serviceName
					+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod()
					, e);
			res.setError(newRpcFault(e));
			return JSON.encode(res);
		}
	}

	protected void initialize(String serviceName, Object service) {}

	private RpcFault newRpcFault404() {
		return new RpcFault("404", "Service Not Found.", "Service Not Found.");
	}
	protected RpcFault newRpcFault(Throwable exception){
		return RpcFaultUtil.throwableToRpcFault("Server.userException", exception);
	}

	private Session session;
	private Map<String, Object> services = new HashMap<>();
	private Converter converter = new ConverterForJsonRpc();
	private static Logger logger = Logger.getLogger(WebSocketJsonRpcHandler.class.getName());
}
