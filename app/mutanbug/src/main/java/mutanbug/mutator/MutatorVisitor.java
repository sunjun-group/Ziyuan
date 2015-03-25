/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.mutator;

import japa.parser.ast.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;

import mutanbug.commons.utils.DefaultGenericVisitor;

/**
 * @author LLT
 * 
 */
public class MutatorVisitor<T extends Node> extends
		DefaultGenericVisitor<List<T>, Boolean> {
	protected Mutator mutator;
	private Map<Node, List<Node>> mutations;

	public MutatorVisitor(Mutator mutator) {
		this.mutator = mutator;
		mutations = new HashMap<Node, List<Node>>();
	}

	public List<T> visitNode(Node node) {
		List<T> mutatedNodes = super.visitNode(node, true);
		if (CollectionUtils.isNotEmpty(mutatedNodes)) {
			CollectionUtils.getListInitIfEmpty(mutations, node)
					.addAll(mutatedNodes);
		}
		return mutatedNodes;
	}

	protected boolean accept(Node node) {
		return mutator.needToMutate(node);
	};

	@Override
	protected List<T> getDefaultReturnValue() {
		return new ArrayList<T>();
	}

	public Map<Node, List<Node>> getMutations() {
		return mutations;
	}
	
	public void reset() {
		mutations.clear();
	}
}
