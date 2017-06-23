/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import java.util.List;

import gentest.core.data.Sequence;
import japa.parser.ast.CompilationUnit;

/**
 * @author LLT
 *
 */
public interface ICompilationUnitWriter {
	public CompilationUnit write(List<Sequence> methods, String pkgName, String className, String methodPrefix);
}
