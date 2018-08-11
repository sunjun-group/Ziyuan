/**
 * Copyright TODO
 */
package gentest.core.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author LLT
 * 
 */
public class MethodCall {
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
		return paramNames;
	}
	
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
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

	public boolean requireReceiver() {
		return !Modifier.isStatic(method.getModifiers());
	}
	
	public String toString() {
		return method.toString();
	}
	
}
