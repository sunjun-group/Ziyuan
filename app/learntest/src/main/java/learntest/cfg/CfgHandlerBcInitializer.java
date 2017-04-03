/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import cfgextractor.CFG;
import cfgextractor.CFGCreator;
import learntest.cfg.bytecode.CfgHandler;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestConfig;
import learntest.main.LearnTestParams;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgHandlerBcInitializer {
	private static CfgHandlerBcInitializer INSTANCE;
	
	public CfgHandler create(AppJavaClassPath appClassPath, LearnTestParams params) throws LearnTestException {
		CFGCreator builder = new CFGCreator();
		int methodLineNumber = Integer.valueOf(LearnTestConfig.methodLineNumber);
		CFG cfg;
		try {
			cfg = builder.parsingCFG(appClassPath, LearnTestConfig.testClassName, LearnTestConfig.testMethodName,
					methodLineNumber);
			return new CfgHandler(cfg);
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
	
	public static CfgHandlerBcInitializer getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new CfgHandlerBcInitializer();
		}
		return INSTANCE;
	}
}
