/**
 * Copyright TODO
 */
package gentest.data;

import java.lang.reflect.Method;

import org.springframework.core.DefaultParameterNameDiscoverer;

/**
 * @author LLT
 * 
 */
public class MethodCall {
	private DefaultParameterNameDiscoverer paramNameDiscover = new DefaultParameterNameDiscoverer();
	private Class<?> declaringType;
	private Method method;
	private String alias;
	private String[] paramNames;
	
	public void execute() {

	}
	
	public Method getMethod() {
		return method;
	}
	
	public static MethodCall of(Method method) {
		MethodCall methodCall = new MethodCall();
		methodCall.method = method;
		return methodCall;
	}
	
	public String[] getParamNames() {
		if (paramNames == null) {
			paramNames = paramNameDiscover.getParameterNames(method);
		}
		return paramNames;
	}
	
	public Class<?> getDeclaringType() {
		if (declaringType == null) {
			declaringType = method.getDeclaringClass();
		}
		return declaringType;
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
}
