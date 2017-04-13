/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.extension;

import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.internal.analysis.StringPool;
import org.jacoco.core.internal.flow.Instruction;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cfgcoverage.jacoco.extension.IAnalyzerListerner.IHasAnalyzerListener;
import cfgcoverage.jacoco.extension.orginal.ClassAnalyzer;
import cfgcoverage.jacoco.extension.orginal.MethodAnalyzer;

/**
 * 
 * @author LLT
 *
 */
public class ExtClassAnalyzer extends ClassAnalyzer implements IHasAnalyzerListener {
	private IAnalyzerListerner listerner; // LLT add
	private IInstructionHandler insnHandler; // LLT add

	public ExtClassAnalyzer(final ClassCoverageImpl coverage,
			final boolean[] probes, final StringPool stringPool) {
		super(coverage, probes, stringPool);
	}

	@Override
	protected MethodProbesVisitor createMethodAnalyzer(String name, String desc, String signature) {
		return new MethodAnalyzer(coverage.getName(), coverage.getSuperName(),
				stringPool.get(name), stringPool.get(desc),
				stringPool.get(signature), probes) {
			
			@Override
			protected Instruction createInsn(AbstractInsnNode node, int line) {
				return insnHandler.createInstruction(node, line);
			}
			
			@Override
			public void accept(MethodNode methodNode, MethodVisitor methodVisitor) {
				listerner.onEnterMethodNode(methodNode);
				super.accept(methodNode, methodVisitor);
			}
			
			@Override
			public void visitEnd() {
				super.visitEnd();
				final IMethodCoverage methodCoverage = getCoverage();
				if (methodCoverage.getInstructionCounter().getTotalCount() > 0) {
					// Only consider methods that actually contain code
					coverage.addMethod(methodCoverage);
				}
				listerner.onExitMethodNode();
			}
		};
	}

	@Override
	public void setAnalyzerListener(IAnalyzerListerner listerner) {
		this.listerner = listerner;
	}
	
	public void setInsnHandler(IInstructionHandler insnHandler) {
		this.insnHandler = insnHandler;
	}
}
