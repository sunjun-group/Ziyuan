/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import slicer.javaslicer.IVariableCollectorContext;
import slicer.javaslicer.instruction.variable.InstVariableContext;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 *
 */
public class InstructionContext implements IVariableCollectorContext, ITreeContext {
	private String locId;
	private Map<Instruction, InstructionNode> instrMap;
	private Set<InstructionNode> varTreeRoots;
	private Map<InstructionType, InstructionHandler> instrHandlers;
	private InstVariableContext instVarContext;
	
	public InstructionContext() {
		instrMap = new HashMap<Instruction, InstructionNode>();
		varTreeRoots = new HashSet<InstructionNode>();
		instrHandlers = new HashMap<InstructionType, InstructionHandler>();
		instVarContext = new InstVariableContext();
	}
	
	@Override
	public void startContext(String locId) {
		this.locId = locId;
	}
	
	@Override
	public void endContext() {
		instrMap.clear();
		varTreeRoots.clear();
	}
	
	/**
	 * build dependency instruction tree.
	 */
	public void addLink(InstructionInstance from, InstructionInstance to,
			Variable variable) {
		boolean toInstExistInMap = true;
		InstructionNode toInstrNode = instrMap.get(to.getInstruction());
		if (toInstrNode == null) {
			toInstExistInMap = false;
			toInstrNode = new InstructionNode(to, getInstructionHandler(to));
		}
		/* check if from already exist, if not -> add a new root */
		InstructionNode fromInstNode;
		if (from != null && (fromInstNode = instrMap.get(from.getInstruction())) != null) {
			if (fromInstNode.addLink(toInstrNode, variable) && !toInstExistInMap) {
				instrMap.put(toInstrNode.getInstruction(), toInstrNode);
			}
		} else {
			addRoot(toInstrNode);
		}
	}

	@Override
	public void addRoot(InstructionNode instrNode) {
		if (CollectionUtils.existIn(instrNode.getInstruction().getType(), InstructionType.ARRAY,
				InstructionType.FIELD, InstructionType.VAR, InstructionType.IINC)) {
			varTreeRoots.add(instrNode);
			if (!instrMap.containsKey(instrNode.getInstruction())) {
				instrMap.put(instrNode.getInstruction(), instrNode);
			}
		}
	}

	private InstructionHandler getInstructionHandler(InstructionInstance to) {
		InstructionType type = to.getInstruction().getType();
		InstructionHandler handler = instrHandlers.get(type);
		if (handler == null) {
			handler = InstructionHandler.createHandler(type);
			instrHandlers.put(type, handler);
		}
		handler.setTreeContext(this);
		return handler;
	}

	@Override
	public List<BreakPoint.Variable> getVariables() {
		instVarContext.startContext();
		for (InstructionNode root : varTreeRoots) {
			traverseTree(root);
		}
		List<BreakPoint.Variable> bkpVars = instVarContext.getVariables();
		instVarContext.endContext();
		return bkpVars;
	}
	
	private void traverseTree(InstructionNode node) {
		instVarContext.accessInstruction(node.getInstruction(), node.getAdditionalInfo());
		List<InstructionNode> children = node.getTraverseNodes();
		for (InstructionNode child : children) {
			traverseTree(child);
		}
	}
	
	/* for debug */
	protected static List<Instruction> getInstructions(Instruction instruction, int line) {
		List<Instruction> result = new ArrayList<Instruction>();
		for (Instruction instr : instruction.getMethod().getInstructions()) {
			if (instr.getLineNumber() == line) {
				result.add(instr);
			}
		}
		return result;
	}
	
	public void setInstVarContext(InstVariableContext instVarContext) {
		this.instVarContext = instVarContext;
	}
	
	public String getLocId() {
		return locId;
	}
}
