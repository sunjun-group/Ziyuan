/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.extension;

import org.objectweb.asm.tree.MethodNode;

/**
 * @author LLT
 *
 */
public interface IAnalyzerListerner {

	void onEnterClass(String name, String signature);

	void onEnterMethodNode(MethodNode methodNode);

	void onExitMethodNode();

	public interface IHasAnalyzerListener {
		void setAnalyzerListener(IAnalyzerListerner listerner);
	}


}
