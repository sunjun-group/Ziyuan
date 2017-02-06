/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.variables.StackEntry;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 *
 */
public abstract class InstructionHandler {
	protected ITreeContext treeContext;

	public static InstructionHandler createHandler(InstructionType type) {
		switch (type) {
		case FIELD:
			return new FieldInstrHandler();
		case VAR:
			return new VarInstrHandler();
		case ARRAY:
			return new ArrayInstrHandler();
		}
		return new SimpleInstrHandler();
	}
	
	public boolean addLink(InstructionNode from,
			InstructionNode to, Variable variable) {
		boolean linked = false;
		if (variable instanceof StackEntry) {
			int stackIdx = ((StackEntry) variable).getIndex();
			from.addInput(to, stackIdx);
			linked = true;
		} else {
			treeContext.addRoot(to);
			linked = false;
		}
		return linked;
	}
	
	public InstructionNode getOutput(InstructionNode node, int outputIdx) {
		return node;
	}
	
	protected final InstructionNode getInput(InstructionNode node, int inputIdx) {
		int stackIdx = inputIdx + node.getFirstPopStackIdx();
		InstructionNode input = node.getInput().get(stackIdx);
		return input.getOutput(stackIdx);
	}

	public void setTreeContext(ITreeContext treeContext) {
		this.treeContext = treeContext;
	}
	
	public final List<InstructionNode> getTraverseNodes(InstructionNode node) {
		if (node.getInputSize() == 0) {
			return Collections.emptyList();
		}
		List<InstructionNode> children = new ArrayList<InstructionNode>();
		for (int inputIdx : getTraverseOrder(node)) {
			children.add(getInput(node, inputIdx));
		}
		
		return children;
	}

	protected int[] getTraverseOrder(InstructionNode node) {
		int inputSize = node.getInputSize();
		if (inputSize == 0) {
			return new int[0];
		}
		int [] order = new int[inputSize];
		for (int i = 0; i < inputSize; i++) {
			order[i] = i;
		}
		return order;
	}

	private static class SimpleInstrHandler extends InstructionHandler {

		/**
		 * stack manipulating operations: POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP
		 */
		@Override
		public InstructionNode getOutput(InstructionNode node, int outputIdx) {
			int inputSize = node.getInputSize();
			switch (node.getInstruction().getOpcode()) {
			/*	DUP: value -> ,value, value */
			case Opcodes.DUP:
			/* DUP_X1: value2, value1 -> , value1, value2, value1 */	
			case Opcodes.DUP_X1:
			/* DUP_X2:
			 *  Form 1: value3, value2, value1 ->, value1, value3, value2, value1
			 *  Form 2: value2, value1 -> , value1, value2, value1 */
			case Opcodes.DUP_X2:
				int inputIdx = outputIdx - 1;
				if (inputIdx == -1) {
					inputIdx += inputSize; 
				}
				return getInput(node, inputIdx);
			/* DUP2: 
			 * Form 1: value2, value1 ->, value2, value1, value2, value1
			 * Form 2: value ->, value, value
			 * */	
			case Opcodes.DUP2:
				if (inputSize == 1) {
					return getInput(node, 0);
				} else if (outputIdx >= 2){
					return getInput(node, outputIdx - 2);
				} else {
					return getInput(node, outputIdx);
				}
			/* DUP2_X1:
			 * Form 1: value3, value2, value1 ->, value2, value1, value3, value2, value1
			 * Form 2: value2, value1 -> value1, value2, value1 */
			case Opcodes.DUP2_X1:
			/* DUP2_X2:
			 * Form 1: value4, value3, value2, value1 ->, value2, value1, value4, value3, value2, value1
			 * Form 2: value3, value2, value1 ->, value1, value3, value2, value1
			 * Form 3: value3, value2, value1 ->, value2, value1, value3, value2, value1
			 * Form 4: value2, value1 ->, value1, value2, value1
			 * */	
			case Opcodes.DUP2_X2:
				int offset = Math.max(inputSize - 1, 2);
				inputIdx = outputIdx - offset;
				if (inputIdx < 0) {
					inputIdx += inputSize; 
				}
				return getInput(node, inputIdx); 
			/* TODO: COMPLETE missing Operation */
			default:
				break;
			}
			return super.getOutput(node, outputIdx);
		}
		
		@Override
		protected int[] getTraverseOrder(InstructionNode node) {
			switch (node.getInstruction().getOpcode()) {
			case Opcodes.DUP:
			case Opcodes.DUP_X1:
			case Opcodes.DUP_X2:
			case Opcodes.DUP2:
			case Opcodes.DUP2_X1:
			case Opcodes.DUP2_X2:
				return new int[0];
			default:
				return super.getTraverseOrder(node);
			}
		}
	}
	
	private static class VarInstrHandler extends InstructionHandler {
		
		@Override
		protected int[] getTraverseOrder(InstructionNode node) {
			/* priority is always the first idx, so keep the default order */
			return super.getTraverseOrder(node);
		}
	}
	
	private static class ArrayInstrHandler extends InstructionHandler {
		/**
		 * aload: arrayref, index ->
		 *			<- value
		 * astore:  arrayref, index, value ->
		 */
		@Override
		protected int[] getTraverseOrder(InstructionNode node) {
			/* priority is always the first idx, so keep the default order */
			return super.getTraverseOrder(node);
		}
		
	}
	
	private static class FieldInstrHandler extends InstructionHandler {

		@Override
		protected int[] getTraverseOrder(InstructionNode node) {
			/* priority is always the first idx, so keep the default order */
			return super.getTraverseOrder(node);
		}
	}

}
