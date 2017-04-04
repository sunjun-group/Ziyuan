/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

import cfgextractor.CFG;
import cfgextractor.CFGConstructor;
import learntest.cfg.bytecode.CfgHandler;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.model.MethodInfo;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgHandlerBcInitializer {
	private static CfgHandlerBcInitializer INSTANCE;
	private AppJavaClassPath appClassPaths;
	
	public CfgHandler create(AppJavaClassPath appClassPath, LearnTestParams params) throws LearnTestException {
		this.appClassPaths = appClassPath;
		
		try {
			CFG cfg = parseCode(params.getTestMethodInfo());
			return new CfgHandler(cfg);
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
	
	private CFG parseCode(MethodInfo methodInfo) throws ClassNotFoundException {
		/** current evaluation does not change line number, so we can keep the cache to speed up the progress */
		Repository.clearCache();				
		ClassPath classPath = new ClassPath(appClassPaths.getClasspathStr());
		Repository.setRepository(SyntheticRepository.getInstance(classPath));
		
		JavaClass clazz = Repository.lookupClass(methodInfo.getClassName());
		Method method = getMethod(clazz, methodInfo);
		Code code = method == null ? null : method.getCode();
		if(code == null){
			return null;
		}
		
		CFG cfg = new CFGConstructor().buildCFGWithControlDomiance(code);
		return cfg;
	}
	
	private Method getMethod(JavaClass clazz, MethodInfo methodInfo) {
		for(Method method: clazz.getMethods()){
			if (method.getName().equals(methodInfo.getMethodName()) &&
					method.getSignature().equals(methodInfo.getMethodSignature())) {
				return method;
			}
		}
		return null;
	}
	
	public static CfgHandlerBcInitializer getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new CfgHandlerBcInitializer();
		}
		return INSTANCE;
	}
}
