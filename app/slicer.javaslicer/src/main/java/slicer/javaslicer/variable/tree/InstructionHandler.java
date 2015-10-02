/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable.tree;

import org.objectweb.asm.Opcodes;

import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;

import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.variables.StackEntry;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 *
 */
public abstract class InstructionHandler {
	protected ITreeContext treeContext;

	public boolean addLink(InstructionNode from,
			InstructionNode to, Variable variable) {
		boolean linked = false;
		if (variable instanceof StackEntry) {
			int stackIdx = ((StackEntry) variable).getIndex();
			from.addInput(to, stackIdx);
			linked = true;
		} else {
			treeContext.addRoot(to);
		}
		return linked && onHandleAddLink(from, to, variable);
	}
	
	protected abstract boolean onHandleAddLink(InstructionNode from,
			InstructionNode to, Variable variable);
	
	public abstract InstructionNode getNextVarInterestNode(
			InstructionNode instructionNode);

	public InstructionNode executeAndGetOutput(InstructionNode instructionNode,
			int i) {
		return instructionNode;
	}
	
	public void setTreeContext(ITreeContext treeContext) {
		this.treeContext = treeContext;
	}
	
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
	
	private static class SimpleInstrHandler extends InstructionHandler {

		@Override
		public boolean onHandleAddLink(InstructionNode from,
				InstructionNode to, Variable variable) {
			return true;
		}

		/* (non-Javadoc)
		 * @see slicer.javaslicer.variable.tree.InstructionHandler#getNextInterestNode(slicer.javaslicer.variable.tree.InstructionNode)
		 */
		@Override
		public InstructionNode getNextVarInterestNode(
				InstructionNode instructionNode) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class VarInstrHandler extends InstructionHandler {

		@Override
		public boolean onHandleAddLink(InstructionNode from, InstructionNode to,
				Variable variable) {
			return true;
		}

		/* (non-Javadoc)
		 * @see slicer.javaslicer.variable.tree.InstructionHandler#getNextInterestNode(slicer.javaslicer.variable.tree.InstructionNode)
		 */
		@Override
		public InstructionNode getNextVarInterestNode(
				InstructionNode instructionNode) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class ArrayInstrHandler extends InstructionHandler {
		/**
		 * aload: arrayref, index ->
		 *			<- value
		 * astore:  arrayref, index, value ->
		 */
		@Override
		protected boolean onHandleAddLink(InstructionNode from,
				InstructionNode to, Variable variable) {
			ArrayInstruction fromInstr = from.getInstruction();
			if (isArrayLoad(fromInstr)) {
				/* 
				 * only the objectRef will be relevant to build variable.
				 * the first idx will be value, and the second idx will be objectRef.
				 */
				if (from.getInputSize() < 2) {
					treeContext.addRoot(to);
					return false;
				}
			} else {
				if (from.getInputSize() < 3) {
					treeContext.addRoot(to);
					return false;
				}
			}
			return true;
		}

		private boolean isArrayLoad(ArrayInstruction fromInstr) {
			return CollectionUtils.existIn(fromInstr.getOpcode(), 
					Opcodes.IALOAD, Opcodes.LALOAD,
					Opcodes.FALOAD, Opcodes.DALOAD,
					Opcodes.AALOAD, Opcodes.BALOAD,
					Opcodes.CALOAD, Opcodes.SALOAD);
		}

		/* (non-Javadoc)
		 * @see slicer.javaslicer.variable.tree.InstructionHandler#getNextInterestNode(slicer.javaslicer.variable.tree.InstructionNode)
		 */
		@Override
		public InstructionNode getNextVarInterestNode(
				InstructionNode instructionNode) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class FieldInstrHandler extends InstructionHandler {

		/**
		 * link from a node to its Predecessors on dependency graph.
		 */
		@Override
		public boolean onHandleAddLink(InstructionNode from,
				InstructionNode to, Variable variable) {
			FieldInstruction fromInstr = from.getInstruction();
			switch (fromInstr.getOpcode()) {
			case Opcodes.PUTFIELD:
				/* 
				 * only the objectRef will be relevant to build variable.
				 * the first idx will be value, and the second idx will be objectRef.
				 */
				if (from.getInputSize() < 2) {
					treeContext.addRoot(to);
					return false;
				}
				break;
			}
			return true;
		}

		/**
		 * PUTFIELD
		 * objectref, value ->
		 * GETFIELD
		 * objectref ->
		 * value
		 * GETSTATIC
		 * value
		 * PUTSTATIC
		 * value ->
		 * */
		@Override
		public InstructionNode getNextVarInterestNode(InstructionNode node) {
			int interestInputIdx = -1;
			switch (node.getInstruction().getOpcode()) {
			case Opcodes.PUTFIELD:
				interestInputIdx = 1;
				break;
			case Opcodes.GETFIELD:
				interestInputIdx = 0;
				break;
			}
			if (interestInputIdx < 0) {
				return null;
			}
			Pair<InstructionNode, Integer> nextNode = node.getInput().get(interestInputIdx);
			return nextNode.a.getOutput(nextNode.b);
		}
	}


}
