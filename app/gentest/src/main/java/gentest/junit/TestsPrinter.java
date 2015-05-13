/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import gentest.core.data.Sequence;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class TestsPrinter implements ITestsPrinter {
	public static final int INFINITIVE_METHODS_PER_CLASS = -1;
	private ICompilationUnitPrinter cuPrinter;
	private boolean separatePassFail = true; 
	private int methodSPerClass = INFINITIVE_METHODS_PER_CLASS;
	private String pkg;
	private String failPkg;
	private String methodPrefix;
	private String classPrefix;
	private int classIdx;
	
	public TestsPrinter(String pkg, String failPkg, String methodPrefix, String classPrefix,
			String srcPath) {
		this(pkg, failPkg, methodPrefix, classPrefix,
				new FileCompilationUnitPrinter(srcPath));
	}
	
	public TestsPrinter(String pkg, String failPkg,
			String methodPrefix, String classPrefix, ICompilationUnitPrinter cuPrinter) {
		this.pkg = pkg;
		this.failPkg = failPkg;
		this.methodPrefix = methodPrefix;
		this.classPrefix = classPrefix;
		if (failPkg == null) {
			separatePassFail = false;
		}
		classIdx = 1;
		this.cuPrinter = cuPrinter;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void printTests(Pair<List<Sequence>, List<Sequence>> testSeqs) {
		List<CompilationUnit> units;
		if (!separatePassFail) {
			List<Sequence> allTests = CollectionUtils.join(
					testSeqs.a, testSeqs.b);
			units = createCompilationUnits(allTests, pkg);
		} else {
			units = createCompilationUnits(testSeqs.a, pkg);
			units.addAll(createCompilationUnits(testSeqs.b, failPkg));
		}
		/* print all compilation units */
		cuPrinter.print(units);
	}
	
	public List<CompilationUnit> createCompilationUnits(List<Sequence> seqs,
			String pkgName) {
		JWriter jwriter = new JWriter();
		jwriter.setPackageName(pkgName);
		jwriter.setMethodPrefix(methodPrefix);
		List<List<Sequence>> subSeqs = divideSequencess(seqs);
		List<CompilationUnit> units = new ArrayList<CompilationUnit>();
		for (List<Sequence> subSeq : subSeqs) {
			jwriter.setClazzName(getClassName());
			CompilationUnit cu = jwriter.write(subSeq);
			units.add(cu);
		}
		return units;
	}

	/**
	 * divide sequences into small parts depend on methods per class
	 * configuration.
	 */
	private List<List<Sequence>> divideSequencess(List<Sequence> seqs) {
		if (methodSPerClass == INFINITIVE_METHODS_PER_CLASS) {
			return CollectionUtils.listOf(seqs);
		}
		List<List<Sequence>> subSeqs = new ArrayList<List<Sequence>>();
		List<Sequence> curSubSeq = new ArrayList<Sequence>();
		subSeqs.add(curSubSeq);
		for (Sequence seq : seqs) {
			if (curSubSeq.size() >= methodSPerClass) {
				curSubSeq = new ArrayList<Sequence>();
				subSeqs.add(curSubSeq);
			}
			curSubSeq.add(seq);
		}
		return subSeqs;
	}

	private String getClassName() {
		return classPrefix + "Test" + (classIdx ++);
	}

	public void setSeparatePassFail(boolean separatePassFail) {
		this.separatePassFail = separatePassFail;
	}

	public void setMethodsPerClass(int methodSPerClass) {
		this.methodSPerClass = methodSPerClass;
	}

	public void setCuPrinter(ICompilationUnitPrinter cuPrinter) {
		this.cuPrinter = cuPrinter;
	}
	
}
