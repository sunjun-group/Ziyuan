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

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Constants;
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
		this(pkg, failPkg, methodPrefix, classPrefix, srcPath,
				PrintOption.OVERRIDE);
	}
	
	public TestsPrinter(String pkg, String failPkg, String methodPrefix, String classPrefix,
			String srcPath, PrintOption printOption) {
		this(pkg, failPkg, methodPrefix, classPrefix, new FileCompilationUnitPrinter(srcPath));
		if (printOption == PrintOption.APPEND) {
			// check if there is any test class which name has the same format, reset classIdx if found
			classIdx = getMaxIdxOfExistingClass(srcPath, pkg, classPrefix);
			if (failPkg != null) {
				classIdx = Math.max(classIdx, getMaxIdxOfExistingClass(srcPath, pkg, classPrefix));
			}
			classIdx++;
		}
	}

	private int getMaxIdxOfExistingClass(String srcPath, String pkg,
			String classPrefix) {
		List<String> existedFiles = PrinterUtils.listJavaFileNames(
				PrinterUtils.getClassFolder(srcPath, pkg), classPrefix);
		return getMaxClassIdx(existedFiles, classPrefix);
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
			System.currentTimeMillis();
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
		return classPrefix + (classIdx ++);
	}
	
	private int getMaxClassIdx(List<String> existedFiles, String classPrefix) {
		int maxIdx = 0;
		for (String fileName : existedFiles) {
			String suffix = fileName.substring(classPrefix.length(),
					fileName.length() - Constants.JAVA_EXT_WITH_DOT.length());
			try {
				int idx = Integer.valueOf(suffix);
				maxIdx = Math.max(maxIdx, idx);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return maxIdx;
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

	public enum PrintOption {
		OVERRIDE,
		APPEND
	}
}
