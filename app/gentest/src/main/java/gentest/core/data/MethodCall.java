/**
 * Copyright TODO
 */
package gentest.core.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.core.DefaultParameterNameDiscoverer;

/**
 * @author LLT
 * 
 */
public class MethodCall {
	private DefaultParameterNameDiscoverer paramNameDiscover;
	private Method method;
	private String alias;
	private String[] paramNames;
	private Class<?> receiverType;
	
	public void execute() {

	}
	
	public Method getMethod() {
		return method;
	}
	
	public static MethodCall of(Method method, Class<?> receiverType) {
		MethodCall methodCall = new MethodCall();
		methodCall.method = method;
		methodCall.receiverType = receiverType;
		return methodCall;
	}
	
	public String[] getParamNames() {
		if (paramNames == null) {
			paramNames = getParamNameDiscoverer().getParameterNames(method);
		}
		return paramNames;
	}

	private DefaultParameterNameDiscoverer getParamNameDiscoverer() {
		if (paramNameDiscover == null) {
			paramNameDiscover = new DefaultParameterNameDiscoverer();
		}
		return paramNameDiscover;
	}
	
	public Class<?> getReceiverType() {
		return receiverType;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getIdxOfParamByName(String paramName) {
		String[] params = getParamNames();
		for (int i = 0; i < params.length; i++) {
			if (paramName.equals(params[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException(String.format(
				"Cannot find parameter of method %s with name %s",
				method.getName(), paramName));
	}

	public boolean requireReceiver() {
		return !Modifier.isStatic(method.getModifiers());
	}
}
