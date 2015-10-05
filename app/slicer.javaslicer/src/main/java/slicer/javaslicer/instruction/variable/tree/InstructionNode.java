/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int firstPopStackIdx = Integer.MAX_VALUE;
	private Map<Integer, InstructionNode> input;
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
			input = new HashMap<Integer, InstructionNode>();
		}
		input.put(stackIdx, in);
		firstPopStackIdx = Math.min(firstPopStackIdx, stackIdx);
	}
	
	public void addOutput(InstructionNode out) {
		CollectionUtils.initIfEmpty(output).add(out);
	}
	
	public int getFirstPopStackIdx() {
		return firstPopStackIdx;
	}

	public int getInputSize() {
		return input == null ? 0 : input.size();
	}
	
	public Map<Integer, InstructionNode> getInput() {
		return input;
	}
	
	@Override
	public String toString() {
		return "InstructionNode " + instrInst;
	}

	public InstructionNode getOutput(Integer stackIdx) {
		return instrHandler.getOutput(this, stackIdx - firstPopStackIdx);
	}

	public List<InstructionNode> getTraverseNodes() {
		return instrHandler.getTraverseNodes(this);
	}


}
