package analyzer;

import java.lang.reflect.Method;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.MethodInfo;

public class MethodInfoImpl implements MethodInfo {

	private ClassInfo parent;
	private String name;
	private int modifiers;
	private ClassInfo returnType;
	private ClassInfo[] parameterTypes;
	private ClassInfo[] exceptions;
	private Method method;

	public MethodInfoImpl(ClassInfo father, Method method, String nm,
			int access, ClassInfo ret, ClassInfo[] inputs, ClassInfo[] excepts) {
		this.parent = father;
		this.name = nm;
		this.modifiers = access;
		this.returnType = ret;
		this.parameterTypes = inputs;
		this.method = method;
		this.exceptions = excepts;
	}

	public ClassInfo getDeclaringClass() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public int getModifiers() {
		return modifiers;
	}

	public ClassInfo getReturnType() {
		return returnType;
	}

	public ClassInfo[] getParameterTypes() {
		return parameterTypes;
	}

	public String[] getParemeterNames() {
		return new String[parameterTypes.length];
	}

	public ClassInfo[] getExceptionTypes() {
		return exceptions;
	}

	public Method getMethod() {
		return method;
	}

}
