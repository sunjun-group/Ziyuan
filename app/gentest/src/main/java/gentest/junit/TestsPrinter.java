/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import java.util.ArrayList;
import java.util.List;

import gentest.core.data.Sequence;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.TypeDeclaration;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JavaFileUtils;

/**
 * @author LLT
 *
 */
public class TestsPrinter implements ITestsPrinter {
	public static final int INFINITIVE_METHODS_PER_CLASS = -1;
	private ICompilationUnitPrinter cuPrinter;
	private ICompilationUnitWriter cuWriter;
	private boolean separatePassFail = true; 
	private int methodSPerClass = INFINITIVE_METHODS_PER_CLASS;
	private int classIdx;
	private PrinterParams params;
	
	public TestsPrinter(String pkg, String failPkg, String methodPrefix, String classPrefix,
			String srcPath) {
		this(pkg, failPkg, methodPrefix, classPrefix, srcPath,
				PrintOption.OVERRIDE);
	}
	
	public TestsPrinter(String pkg, String failPkg, String methodPrefix, String classPrefix,
			String srcPath, PrintOption printOption) {
		this(new PrinterParams(pkg, failPkg, methodPrefix, classPrefix, srcPath, printOption),
				new FileCompilationUnitPrinter(srcPath));
	}

	public TestsPrinter(String pkg, String failPkg,
			String methodPrefix, String classPrefix, ICompilationUnitPrinter cuPrinter) {
		this(PrinterParams.of(pkg, failPkg, methodPrefix, classPrefix), cuPrinter);
	}
	
	public TestsPrinter(PrinterParams params) {
		this(params, new FileCompilationUnitPrinter(params.getSrcPath()));
	}
	
	public TestsPrinter(PrinterParams params, ICompilationUnitPrinter cuPrinter) {
		this.params = params;
		classIdx = 1;
		if (params.printOption == PrintOption.APPEND) {
			// check if there is any test class which name has the same format, reset classIdx if found
			classIdx = getMaxIdxOfExistingClass(params.srcPath, params.pkg, params.classPrefix);
			if (params.failPkg != null) {
				classIdx = Math.max(classIdx, getMaxIdxOfExistingClass(params.srcPath, params.pkg, params.classPrefix));
			}
			classIdx++;
		}
		
		if (params.failPkg == null) {
			separatePassFail = false;
		}
		this.cuPrinter = cuPrinter;
	}

	private int getMaxIdxOfExistingClass(String srcPath, String pkg,
			String classPrefix) {
		List<String> existedFiles = JavaFileUtils.listJavaFileNames(
				JavaFileUtils.getClassFolder(srcPath, pkg), classPrefix);
		return getMaxClassIdx(existedFiles, classPrefix);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> printTests(Pair<List<Sequence>, List<Sequence>> testSeqs) {
		List<CompilationUnit> units;
		if (!separatePassFail) {
			List<Sequence> allTests = CollectionUtils.join(
					testSeqs.a, testSeqs.b);
			units = createCompilationUnits(allTests, params.pkg);
			System.currentTimeMillis();
		} else {
			units = createCompilationUnits(testSeqs.a, params.pkg);
			units.addAll(createCompilationUnits(testSeqs.b, params.failPkg));
		}
		/* print all compilation units */
		cuPrinter.print(units);
		return getJunitClassNames(units);
	}
	
	public static List<String> getJunitClassNames(List<CompilationUnit> units) {
		List<String> result = new ArrayList<String>(units.size());
		for (CompilationUnit cu : units) {
			String className = getJunitClassName(cu);
			result.add(className);
		}
		return result;
	}

	public static String getJunitClassName(CompilationUnit cu) {
		TypeDeclaration type = cu.getTypes().get(0);
		String className = ClassUtils.getCanonicalName(cu.getPackage().getName().getName(), 
				type.getName());
		return className;
	}
	
	public List<CompilationUnit> createCompilationUnits(List<Sequence> seqs,
			String pkgName) {
		ensureCuWriter();
		List<List<Sequence>> subSeqs = divideSequencess(seqs);
		List<CompilationUnit> units = new ArrayList<CompilationUnit>();
		for (List<Sequence> subSeq : subSeqs) {
			CompilationUnit cu = cuWriter.write(subSeq, pkgName, getClassName(),
					params.methodPrefix);
			units.add(cu);
		}
		return units;
	}
	
	private void ensureCuWriter() {
		if (cuWriter == null) {
			cuWriter = new JWriter();
		}
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
		return params.classPrefix + (classIdx ++);
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
	
	public ICompilationUnitPrinter getCuPrinter() {
		return cuPrinter;
	}
	
	public void setCuWriter(ICompilationUnitWriter cuWriter) {
		this.cuWriter = cuWriter;
	}

	public enum PrintOption {
		OVERRIDE,
		APPEND
	}
}
