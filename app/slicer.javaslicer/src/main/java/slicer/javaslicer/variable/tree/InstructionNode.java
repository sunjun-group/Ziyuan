/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable.tree;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstanceInfo;
import de.unisb.cs.st.javaslicer.variables.Variable;


/**
 * @author LLT
 *
 */
public class InstructionNode {
	private int firstPopStackIdx;
	private List<Pair<InstructionNode, Integer>> input;
	private List<InstructionNode> output;
	private InstructionInstance instrInst;
	private InstructionHandler instrHandler;
	
	public InstructionNode(InstructionInstance instrInst, InstructionHandler instrHandler) {
		this.instrInst = instrInst;
		this.instrHandler = instrHandler;
	}
	
	public boolean addLink(InstructionNode toNode, Variable variable) {
		return instrHandler.addLink(this, toNode, variable);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Instruction> T getInstruction() {
		return (T)instrInst.getInstruction();
	}

	@SuppressWarnings("unchecked")
	public <T extends InstructionInstanceInfo> T getAdditionalInfo() {
		return (T) instrInst.getAdditionalInfo();
	}
	
	/**
	 * input of an instrInst is the value pop from stack.
	 */
	public void addInput(InstructionNode in, int stackIdx) {
		if (input == null) {
			input = new ArrayList<Pair<InstructionNode,Integer>>();
		}
		input.add(Pair.of(in, stackIdx));
		firstPopStackIdx = Math.min(firstPopStackIdx, stackIdx);
	}
	
	public void addOutput(InstructionNode out) {
		CollectionUtils.initIfEmpty(output).add(out);
	}
	
	/* 
	 * the first idx of stack after executing the instrInst will be calculated by:
	 * firstIdx = firstPopStackIdx - 1 + output.size (= number of value push into stack)
	 */
	public int getFirstStackIdx() {
		return firstPopStackIdx - 1 + CollectionUtils.getSize(output);
	}

	public int getInputSize() {
		return CollectionUtils.getSize(input);
	}
	
	public List<Pair<InstructionNode, Integer>> getInput() {
		return input;
	}

	@Override
	public String toString() {
		return "InstructionNode [firstPopStackIdx=" + firstPopStackIdx
				+ ", input=" + input + ", instrInst=" + instrInst + "]";
	}

	public InstructionNode getNextInterestNode() {
		return instrHandler.getNextVarInterestNode(this);
	}

	public InstructionNode getOutput(Integer stackIdx) {
		return instrHandler.executeAndGetOutput(this, stackIdx - getFirstStackIdx());
	}

}
