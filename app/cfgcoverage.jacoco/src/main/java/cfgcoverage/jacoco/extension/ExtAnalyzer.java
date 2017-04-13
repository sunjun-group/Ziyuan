/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.extension;

import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.internal.flow.ClassProbesVisitor;

import cfgcoverage.jacoco.extension.IAnalyzerListerner.IHasAnalyzerListener;
import cfgcoverage.jacoco.extension.orginal.Analyzer;

/**
 * 
 * @author LLT
 *
 */
public class ExtAnalyzer extends Analyzer implements IHasAnalyzerListener {
	private IAnalyzerListerner listener;
	private IInstructionHandler insnHandler;

	public ExtAnalyzer(ExecutionDataStore executionData, ICoverageVisitor coverageVisitor) {
		super(executionData, coverageVisitor);
	}

	@Override
	protected ClassProbesVisitor createClassAnalyzer(boolean[] probes, final ClassCoverageImpl coverage) {
		/* LLT: replace ClassAnalyzer with ExtClassAnalyzer */
		final ExtClassAnalyzer analyzer = new ExtClassAnalyzer(coverage, probes, stringPool) {
			
			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				listener.onEnterClass(name, signature);
				super.visit(version, access, name, signature, superName, interfaces);
			}
			
			@Override
			public void visitEnd() {
				super.visitEnd();
				coverageVisitor.visitCoverage(coverage);
			}
		};
		analyzer.setAnalyzerListener(listener);
		analyzer.setInsnHandler(insnHandler);
		return analyzer;
	}

	@Override
	public void setAnalyzerListener(IAnalyzerListerner listerner) {
		this.listener = listerner;
	}
	
	public void setInsnHandler(IInstructionHandler insnHandler) {
		this.insnHandler = insnHandler;
	}
}
