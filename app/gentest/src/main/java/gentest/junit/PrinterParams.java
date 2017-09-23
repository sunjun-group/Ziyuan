/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

/**
 * @author LLT
 *
 */
public class PrinterParams {
	String pkg;
	String failPkg;
	String methodPrefix;
	String classPrefix;

	public PrinterParams(String pkg, String failPkg, String methodPrefix, String classPrefix) {
		this.pkg = pkg;
		this.failPkg = failPkg;
		this.methodPrefix = methodPrefix;
		this.classPrefix = classPrefix;
	}

	public static PrinterParams of(String pkg, String failPkg, String methodPrefix, String classPrefix) {
		return new PrinterParams(pkg, failPkg, methodPrefix, classPrefix);
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getFailPkg() {
		return failPkg;
	}

	public void setFailPkg(String failPkg) {
		this.failPkg = failPkg;
	}

	public String getMethodPrefix() {
		return methodPrefix;
	}

	public void setMethodPrefix(String methodPrefix) {
		this.methodPrefix = methodPrefix;
	}

	public String getClassPrefix() {
		return classPrefix;
	}

	public void setClassPrefix(String classPrefix) {
		this.classPrefix = classPrefix;
	}

}
