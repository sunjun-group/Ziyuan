/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import japa.parser.ast.CompilationUnit;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface ICompilationUnitPrinter {
	
	public void print(List<CompilationUnit> compilationUnits);
}
