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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.beanutils.ConverterForJsonRpc;
import jp.go.nict.langrid.commons.lang.ClassUtil;
import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.rpc.RpcFault;
import jp.go.nict.langrid.commons.rpc.RpcFaultUtil;
import jp.go.nict.langrid.commons.rpc.RpcHeader;
import jp.go.nict.langrid.commons.rpc.intf.RpcAnnotationUtil;
import jp.go.nict.langrid.commons.ws.MimeHeaders;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import jp.go.nict.langrid.servicecontainer.handler.RIProcessor;
import jp.go.nict.langrid.servicecontainer.handler.ServiceFactory;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

/**
 * @author Takao Nakaguchi
 */
public class JsonRpcHandler {
	/**
	 * 
	 * 
	 */
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
				service = f.createService(cl, sc, clazz);
				initialize(serviceName, service);
				// Currently only array("[]") is supported, while JsonRpc accepts Object("{}")
				result = invokeMethod(service, method, req.getParams(), converter);
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

	static Object invokeMethod(Object instance, Method method, Object[] params, Converter converter)
	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Type[] ptypes = method.getGenericParameterTypes();
		Object[] args = new Object[ptypes.length];
		for(int i = 0; i < args.length; i++){
			if(params[i].equals("")){
				if(ptypes[i].equals(String.class)){
					args[i] = "";
				} else if(ptypes[i] instanceof Class){
					Class<?> clz = (Class<?>)ptypes[i];
					if(clz.isPrimitive()){
						args[i] = ClassUtil.getDefaultValueForPrimitive(clz);
					}
				}
			} else{
				args[i] = converter.convert(params[i], ptypes[i]);
			}
		}
		return method.invoke(instance, args);
	}

	private RpcFault newRpcFault404() {
		return new RpcFault("404", "Service Not Found.", "Service Not Found.");
	}
	protected RpcFault newRpcFault(Throwable exception){
		return RpcFaultUtil.throwableToRpcFault("Server.userException", exception);
	}

	private Converter converter = new ConverterForJsonRpc();
	private static Logger logger = Logger.getLogger(JsonRpcHandler.class.getName());
}
