/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gentest.core.data.Sequence;
import gentest.junit.JWriter;
import japa.parser.ast.CompilationUnit;
import sav.common.core.utils.ClassUtils;

/**
 * @author LLT
 *
 */
public class LearntestJWriter extends JWriter {
	private Map<String, Sequence> testcaseSequenceMap;
	private boolean extractTestcaseSequenceMap;
	private List<Sequence> sequences;
	private String className;
	
	public LearntestJWriter(boolean extractTestcaseSequenceMap) {
		this.extractTestcaseSequenceMap = extractTestcaseSequenceMap;
	}

	@Override
	public CompilationUnit write(List<Sequence> methods, String pkgName, String classSimpleName, String methodPrefix) {
		this.sequences = methods;
		className = ClassUtils.getCanonicalName(pkgName, classSimpleName);
		return super.write(methods, pkgName, classSimpleName, methodPrefix);
	}
	
	@Override
	protected String getMethodName(int sequenceIdx) {
		String methodName = super.getMethodName(sequenceIdx);
		updateTestcaseSequenceMap(sequenceIdx, methodName);
		return methodName;
	}
	
	private void updateTestcaseSequenceMap(int sequenceIdx, String methodName) {
		if (extractTestcaseSequenceMap) {
			if (testcaseSequenceMap == null) {
				testcaseSequenceMap = new HashMap<String, Sequence>();
			}
			testcaseSequenceMap.put(ClassUtils.toClassMethodStr(className, methodName), sequences.get(sequenceIdx));
		}
	}

	public Map<String, Sequence> getTestcaseSequenceMap() {
		return testcaseSequenceMap;
	}
}
