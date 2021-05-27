package org.langrid.client.ws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.lang.ClassUtil;

public class MethodUtil {
	public static Object invokeMethod(Object instance, Method method, Object[] params, Converter converter)
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

}
