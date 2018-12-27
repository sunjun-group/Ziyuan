package gentest.core;

import org.springframework.core.DefaultParameterNameDiscoverer;

import gentest.core.data.MethodCall;

public class MethodParamUtils {
	private static DefaultParameterNameDiscoverer paramNameDiscover = new DefaultParameterNameDiscoverer();
	
	public static String[] getParamNames(MethodCall methodCall) {
		if (methodCall.getParamNames() == null) {
			String[] paramNames = paramNameDiscover.getParameterNames(methodCall.getMethod());
			System.currentTimeMillis();
			methodCall.setParamNames(paramNames);
		}
		return methodCall.getParamNames();
	}
	
	public static int getIdxOfParamByName(MethodCall methodCall, String paramName) {
		String[] params = getParamNames(methodCall);
		for (int i = 0; i < params.length; i++) {
			if (paramName.equals(params[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException(String.format(
				"Cannot find parameter of method %s with name %s",
				methodCall.getMethod().getName(), paramName));
	}
}
