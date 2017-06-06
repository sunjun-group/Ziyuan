/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.handler.filter.classfilter;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author LLT
 *
 */
public interface TargetClassFilter {

	public boolean isValid(CompilationUnit cu);

}
