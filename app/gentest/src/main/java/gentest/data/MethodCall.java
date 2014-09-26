/**
 * Copyright TODO
 */
package gentest.data;

import java.lang.reflect.Method;

/**
 * @author LLT
 * 
 */
public class MethodCall {
	private Class<?> declaringType;
	private Method method;
	
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
	
	public Class<?> getDeclaringType() {
		if (declaringType == null) {
			declaringType = method.getDeclaringClass();
		}
		return declaringType;
	}
}
