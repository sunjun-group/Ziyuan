/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jacoco.core.internal.analysis.AbstractMethodAnalyzer;
import org.jacoco.core.internal.flow.IFrame;
import org.jacoco.core.internal.flow.Instruction;
import org.jacoco.core.internal.flow.LabelInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cfgcoverage.jacoco.analysis.data.ExtInstruction;

/**
 * @author LLT
 *
 */
public class FreqProbesMethodAnalyzer extends AbstractMethodAnalyzer {
	private final int[] probes;
	private CfgCoverageBuilder coverageBuilder;
	private Map<Instruction, Integer> coveredProbes;
	private final List<Jump> allJumps = new ArrayList<Jump>();
	private Instruction thisLastInsn;
	
	public FreqProbesMethodAnalyzer(CfgCoverageBuilder coverageBuilder, String className, String superClassName, int[] probes) {
		super(className, superClassName);
		this.probes = probes;
		this.coverageBuilder = coverageBuilder;
		coveredProbes = new HashMap<Instruction, Integer>();
	}
	
	@Override
	public void accept(MethodNode methodNode, MethodVisitor methodVisitor) {
		coverageBuilder.startMethod(methodNode);
		super.accept(methodNode, methodVisitor);
	}
	
	@Override
	protected Instruction createInsn(AbstractInsnNode node, int line) {
		return coverageBuilder.instruction(node, line);
	}
	
	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
		if (!LabelInfo.isSuccessor(label)) {
			thisLastInsn = null;
		}
	}
	
	@Override
	protected void visitInsn() {
		super.visitInsn();
		if (thisLastInsn != null) {
			((ExtInstruction) lastInsn).setNodePredecessor((ExtInstruction) thisLastInsn);
		}
		this.thisLastInsn = lastInsn;
	}
	
	@Override
	public void visitJumpInsnWithProbe(int opcode, Label label, int probeId, IFrame frame) {
		super.visitJumpInsnWithProbe(opcode, label, probeId, frame);
		allJumps.add(new Jump(lastInsn, label));
	}
	
	@Override
	public void visitLookupSwitchInsnWithProbes(Label dflt, int[] keys, Label[] labels, IFrame frame) {
		super.visitLookupSwitchInsnWithProbes(dflt, keys, labels, frame);
		visitSwitchInsn(dflt, labels);
	}
	
	@Override
	public void visitTableSwitchInsnWithProbes(int min, int max, Label dflt, Label[] labels, IFrame frame) {
		super.visitTableSwitchInsnWithProbes(min, max, dflt, labels, frame);
		visitSwitchInsn(dflt, labels);
	}
	
	private void visitSwitchInsn(final Label dflt,
			final Label[] labels) {
		LabelInfo.resetDone(dflt);
		LabelInfo.resetDone(labels);
		allJumps.add(new Jump(lastInsn, dflt));
		for (final Label l : labels) {
			if (!LabelInfo.isDone(l)) {
				allJumps.add(new Jump(lastInsn, l));
				LabelInfo.setDone(l);
			}
		}
	}
	
	@Override
	public void visitEnd() {
		super.visitEnd();
		allJumps.addAll(jumps);
		/* update predecessor and branches for nodes */
		for (Jump j : allJumps) {
			ExtInstruction target = (ExtInstruction) LabelInfo.getInstruction(j.getTarget());
			ExtInstruction source = (ExtInstruction) j.getSource();
			target.setNodePredecessor(source);
		}
		coverageBuilder.endMethod();
	}

	@Override
	protected void setCovered() {
		for (Entry<Instruction, Integer> entry : coveredProbes.entrySet()) {
			((ExtInstruction)entry.getKey()).setCovered(entry.getValue());
		}
	}

	@Override
	protected void addProbe(int probeId) {
		int count = 0;
		if (probes != null &&  (count = probes[probeId]) > 0) {
			coveredProbes.put(lastInsn, count);
		}
	}

}
