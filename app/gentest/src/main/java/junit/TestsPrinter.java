/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package junit;

import gentest.data.Sequence;

import japa.parser.ast.CompilationUnit;

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
	private String srcPath;
	private String methodPrefix;
	private String classPrefix;
	private int classIdx;
	
	public TestsPrinter(String srcPath, String pkg, String failPkg,
			String methodPrefix, String classPrefix) {
		this.srcPath = srcPath;
		this.pkg = pkg;
		this.failPkg = failPkg;
		this.methodPrefix = methodPrefix;
		this.classPrefix = classPrefix;
		if (failPkg == null) {
			separatePassFail = false;
		}
		classIdx = 1;
	}
	
	@SuppressWarnings("unchecked")
	public void printTests(Pair<List<Sequence>, List<Sequence>> testSeqss) {
		List<CompilationUnit> units;
		if (!separatePassFail) {
			List<Sequence> allTests = CollectionUtils.join(
					testSeqss.a, testSeqss.b);
			units = creatCompilationUnits(allTests, pkg);
		} else {
			units = creatCompilationUnits(testSeqss.a, pkg);
			units.addAll(creatCompilationUnits(testSeqss.b, failPkg));
		}
		/* print all compilation units */
		cuPrinter.print(srcPath, units);
	}
	
	public List<CompilationUnit> creatCompilationUnits(List<Sequence> seqs,
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
		return classPrefix + (classIdx ++);
	}

	public void setSeparatePassFail(boolean separatePassFail) {
		this.separatePassFail = separatePassFail;
	}

	public void setMethodSPerClass(int methodSPerClass) {
		this.methodSPerClass = methodSPerClass;
	}

	public ICompilationUnitPrinter getCuPrinter() {
		if (cuPrinter == null) {
			cuPrinter = new FileCompilationUnitPrinter();
		}
		return cuPrinter;
	}

	public void setCuPrinter(ICompilationUnitPrinter cuPrinter) {
		this.cuPrinter = cuPrinter;
	}
}
