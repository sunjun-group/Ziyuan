/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.MachineLearningUtils;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.calculator.MultiNotDividerBasedCategoryCalculator;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DecisionProbes extends CfgCoverage {
	private static Logger log = LoggerFactory.getLogger(DecisionProbes.class);
	private TargetMethod targetMethod;
	private List<BreakpointValue> testInputs;
	private List<ExecVar> originalVars;
	private List<ExecVar> learningVars;
	private List<String> labels;
	private int totalTestNum;

	/* cache the node list, but be careful with the update */
	/* map between cfgNode idx of decision node with its probe */
	private Map<Integer, DecisionNodeProbe> nodeProbeMap;

	public DecisionProbes(TargetMethod targetMethod, CfgCoverage cfgCoverage) {
		super(cfgCoverage.getCfg());
		this.targetMethod = targetMethod;
		transferCoverage(cfgCoverage);
		totalTestNum = cfgCoverage.getTestcases().size();
		testInputs = new CompositeList<BreakpointValue>();
	}

	public void setRunningResult(List<BreakpointValue> testInputs) {
		this.testInputs.addAll(testInputs);
		originalVars = BreakpointDataUtils.collectAllVars(testInputs);
		learningVars = MachineLearningUtils.createPolyClassifierVars(originalVars);
		initProbeMap(testInputs);
	}

	public void transferCoverage(CfgCoverage cfgCoverage) {
		setCfg(cfgCoverage.getCfg());
		setNodeCoverages(cfgCoverage.getNodeCoverages());
		for (NodeCoverage nodeCoverage : getNodeCoverages()) {
			nodeCoverage.setCfgCoverage(this);
		}
		this.dupTcMap = cfgCoverage.getDupTcs();
		setTestcases(cfgCoverage.getTestcases());
	}

	/* build precondition of a node based on its dominatees */
	// @Deprecated this method use cfg, but we should use cdg
	// public OrCategoryCalculator getPrecondition(CfgNode node) {
	// Precondition precondition = getNodeProbe(node).getPrecondition();
	// for (CfgNode dominator : CfgUtils.getPrecondInherentDominatee(node)) {
	// Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
	// List<Divider> domDividers = domPrecond.getDividers();
	// if (CollectionUtils.isEmpty(domDividers)) {
	// precondition.addPreconditions(domPrecond.getPreconditions());
	// } else {
	// BranchRelationship branchRel =
	// node.getBranchRelationship(dominator.getIdx());
	// CategoryCalculator condFromDividers = null;
	// if (branchRel == BranchRelationship.TRUE) {
	// condFromDividers = new MultiDividerBasedCategoryCalculator(domDividers);
	// }
	//// else if (dominator.isLoopHeaderOf(node)) {
	//// condFromDividers = new
	// MultiDividerBasedCategoryCalculator(domDividers);
	//// }
	// else {
	// List<Divider> clonedDividers = new ArrayList<>();
	// for(Divider d: domDividers){
	// double[] clonedThetas = new double[d.getThetas().length];
	// for(int i=0; i<clonedThetas.length; i++){
	// clonedThetas[i]=-1*d.getThetas()[i];
	// }
	//
	// Divider d0 = new Divider(clonedThetas, -1*d.getTheta0(), true);
	// clonedDividers.add(d0);
	// }
	// condFromDividers = new
	// MultiDividerBasedCategoryCalculator(clonedDividers);
	// }
	// if (condFromDividers != null) {
	// precondition.addPreconditions(domPrecond.getPreconditions(),
	// condFromDividers);
	// }
	// }
	// }
	// return new OrCategoryCalculator(precondition.getPreconditions(),
	// learningVars, originalVars);
	// }
	
	/**
	 * set the preconditions of node, and return it
	 * @param node
	 * @param check 
	 * @param list
	 * @return
	 */
	public Pair<OrCategoryCalculator, Boolean> getPrecondition(CfgNode node, HashMap<CfgNode, CfgNodeDomainInfo> dominationMap, boolean isLoopHeader) {
		List<CfgNode> dominators = dominationMap.get(node).getDominators();
		if (!isLoopHeader) {
			if (!precondFinished(dominators)) {
				return new Pair<OrCategoryCalculator, Boolean>(null, false);			
			}
		}else {
			if (!loopPrecondFinished(dominators, node, dominationMap)) {
				return new Pair<OrCategoryCalculator, Boolean>(null, false);			
			}
		}
		
		int maxSize = 20;
		Precondition precondition = getNodeProbe(node).getPrecondition();
		List<CfgNode> bothBranchNode = new LinkedList<>(); /** nodes could reach target node in false and true branch, 
																the BranchRelationship should be determined by other nodes reaching target node through it */
		HashMap<CfgNode, BranchRelationship> path = precondition.getPath(); // record determined branch dominator pick
		
		for (CfgNode dominator : dominators) {
			if (precondition.getPreconditions().size() > maxSize) {
				break;
			}
			Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
			List<Divider> domDividers = domPrecond.getDividers();
			BranchRelationship branchRel = node.getBranchRelationship(dominator.getIdx());
			path.putAll(domPrecond.getPath());
			if (CollectionUtils.isEmpty(domDividers)) {
				precondition.addPreconditions(domPrecond.getPreconditions());
				if (branchRel == BranchRelationship.TRUE || branchRel == BranchRelationship.FALSE) {
					path.put(dominator, branchRel);
				}
			} else {
				log.info("from "+dominator + " to "+node+" : "+branchRel);
				CategoryCalculator condFromDividers = null;
				if (branchRel == BranchRelationship.TRUE || branchRel == BranchRelationship.FALSE) {
					condFromDividers = getCalculator(domDividers, branchRel);
					path.put(dominator, branchRel);
				} else if (branchRel == BranchRelationship.TRUE_FALSE) {
					bothBranchNode.add(dominator);
				}
				if (condFromDividers != null) {
					precondition.addPreconditions(domPrecond.getPreconditions(), condFromDividers);
				}
			}
		}
		
		/** determine another branch of bothBranchNode to reach target node */
		for (CfgNode dominator : bothBranchNode) {
			if (precondition.getPreconditions().size() > maxSize) {
				break;
			}
			Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
			List<Divider> domDividers = domPrecond.getDividers();
			BranchRelationship branchRel = precondition.getPath().get(dominator);
			
			/**
			 * dominator node is direct dominator of target node, thus when dominator has TRUE_FALSE relationship,
			 * dominator must reach target node in one branch directly, 
			 * and reach target node through its another dominatee, that is also dominator of target node, 
			 * in the other branch we should have known  
			 * */
			if (branchRel == BranchRelationship.FALSE) {
				branchRel = BranchRelationship.TRUE;
			} else if (branchRel == BranchRelationship.TRUE){
				branchRel = BranchRelationship.FALSE;
			} else if (branchRel == BranchRelationship.TRUE_FALSE || branchRel == null){ 
				/** todo :
				 *  this situation is wrong, a single branch choice is regarded as both branch ,
				 *  here is a patch
				 *  
				 *  for example : from B to C, B should have just one branch, but B is labeled TRUE_FALSE to C
				 *   	  A
				 *  	/	\
				 *     B  -> C
				 *    	\	/  
				 *       Exit
				 *   
				 *  */
				for (CfgNode branch : dominator.getBranches()) {
					if (dominator.getBranchRelationship(branch.getIdx()) == BranchRelationship.TRUE) {
						branchRel = BranchRelationship.FALSE;
						path.put(dominator, branchRel);
						break;
					}else if (dominator.getBranchRelationship(branch.getIdx()) == BranchRelationship.FALSE) {
						branchRel = BranchRelationship.TRUE;
						path.put(dominator, branchRel);
						break;
					}
				}
			}
			log.info("from "+dominator + " to "+node+" : "+branchRel);
			CategoryCalculator condFromDividers = getCalculator(domDividers, branchRel);
			if (condFromDividers != null) {
				precondition.addPreconditions(domPrecond.getPreconditions(), condFromDividers);
			}
		}
		return new Pair<OrCategoryCalculator, Boolean>(new OrCategoryCalculator(precondition.getPreconditions(), learningVars, originalVars), true);
	}

	private boolean precondFinished(List<CfgNode> dominators) {
		for (CfgNode dominator : dominators) {
			Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
			if (!domPrecond.isVisited()) { // there is a dominator not been learned
				return false;
			}
		}
		return true;
	}
	
	private boolean loopPrecondFinished(List<CfgNode> dominators, CfgNode loopHeader, HashMap<CfgNode, CfgNodeDomainInfo> dominationMap) {
		assert loopHeader.isLoopHeader() : "The node must be loop node";
		for (CfgNode dominator : dominators) {
			Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
			if (!domPrecond.isVisited()) { // there is a dominator not been learned
				if (!inChain(dominator, loopHeader, dominationMap)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean inChain(CfgNode dominator, CfgNode loopHeader, HashMap<CfgNode, CfgNodeDomainInfo> dominationMap) {
		HashMap<CfgNode, Boolean> visited = new HashMap<>();
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(loopHeader);
		while (!queue.isEmpty()) {
			CfgNode node = queue.poll();
			visited.put(node, true);
			if (node == dominator) {
				return true;
			}else {
				List<CfgNode> children =  node.getBranches();
				if (children != null) {
					for (CfgNode cfgNode : children) {
						if (!visited.containsKey(cfgNode)) {
							if (cfgNode != null) {
								queue.add(cfgNode);
							}
						}
					}
				}
			}
			
		}
		return false;
	}

	private CategoryCalculator getCalculator(List<Divider> domDividers, BranchRelationship branchRel) {
		CategoryCalculator condFromDividers = null;
		if (branchRel == BranchRelationship.TRUE) {
			condFromDividers = new MultiDividerBasedCategoryCalculator(domDividers);
		} else if (branchRel == BranchRelationship.FALSE) {
			condFromDividers = new MultiNotDividerBasedCategoryCalculator(domDividers);
//			List<Divider> clonedDividers = new ArrayList<>();
//			for (Divider d : domDividers) {
//				double[] clonedThetas = new double[d.getThetas().length];
//				for (int i = 0; i < clonedThetas.length; i++) {
//					clonedThetas[i] = -1 * d.getThetas()[i];
//				}
//
//				Divider d0 = new Divider(clonedThetas, -1 * d.getTheta0(), true);
//				clonedDividers.add(d0);
//			}
//			condFromDividers = new MultiDividerBasedCategoryCalculator(clonedDividers);
		}
		return condFromDividers;
	}

	/**
	 * in order to avoid unnecessarily generate divider for a node, we check if
	 * it is needed to learn at the current node. it is needed iff one of its
	 * dependentees is not covered
	 **/
	public boolean doesNodeNeedToLearnPrecond(DecisionNodeProbe nodeProbe) {
		for (CfgNode dependentee : CollectionUtils.nullToEmpty(nodeProbe.getNode().getDependentees())) {
			DecisionNodeProbe dependenteeProbe = getNodeProbe(dependentee);
			if (dependenteeProbe.hasUncoveredBranch()) {
				return true;
			}
		}
		return false;
	}

	public List<DecisionNodeProbe> getNodeProbes() {
		return new ArrayList<DecisionNodeProbe>(getNodeProbeMap().values());
	}

	public Map<Integer, DecisionNodeProbe> getNodeProbeMap() {
		return nodeProbeMap;
	}

	private void initProbeMap(List<BreakpointValue> initTestInputs) {
		if (CollectionUtils.isEmpty(nodeProbeMap)) {
			nodeProbeMap = new HashMap<Integer, DecisionNodeProbe>();
			List<CfgNode> decisionNodes = getCfg().getDecisionNodes();
			for (CfgNode node : decisionNodes) {
				DecisionNodeProbe nodeProbe = new DecisionNodeProbe(this, getCoverage(node), initTestInputs);
				nodeProbeMap.put(node.getIdx(), nodeProbe);
			}

			/* update node dominatees */
			for (DecisionNodeProbe nodeProbe : nodeProbeMap.values()) {
				Set<CfgNode> nodeDominatees = nodeProbe.getNode().getDominatees();
				List<DecisionNodeProbe> dominatees = new ArrayList<DecisionNodeProbe>(
						CollectionUtils.getSize(nodeDominatees));
				if (nodeDominatees != null) {
					for (CfgNode node : nodeDominatees) {
						dominatees.add(getNodeProbe(node));
					}
				}
				nodeProbe.setDominatees(dominatees);
			}
		}
	}

	public List<BreakpointValue> getTestInputs() {
		return testInputs;
	}

	public DecisionNodeProbe getNodeProbe(CfgNode node) {
		return getNodeProbeMap().get(node.getIdx());
	}

	/**
	 * @return
	 */
	public List<String> getLabels() {
		if (labels == null) {
			labels = BreakpointDataUtils.extractLabels(learningVars);
		}
		return labels;
	}

	/**
	 * @return the originalVars
	 */
	public List<ExecVar> getOriginalVars() {
		return originalVars;
	}

	public boolean isOutOfDate() {
		return this.totalTestNum != super.getTestcases().size();
	}

	/**
	 * whenever the probes object is updated, this method should be call to make
	 * sure the getting data is not out of date.
	 * 
	 * @param newTcsFirstIdx
	 * @param newTestInputs
	 */
	public void update(int newTcsFirstIdx, List<BreakpointValue> newTestInputs) {
		if (nodeProbeMap != null && isOutOfDate()) {
			for (DecisionNodeProbe nodeProbe : nodeProbeMap.values()) {
				nodeProbe.update(newTcsFirstIdx, newTestInputs);
			}
			this.testInputs.addAll(newTestInputs);
			totalTestNum = getTestcases().size();
		}
	}

	public int getTotalTestNum() {
		return totalTestNum;
	}

	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public void setOriginalVars(List<ExecVar> originalVars) {
		this.originalVars = originalVars;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	public static List<CfgNode> getChildDecision(CfgNode node) {
		List<CfgNode> childDecisonNodes = new LinkedList<>();
		List<CfgNode> children = node.getBranches();
		for (CfgNode child : children) {
			getChildDecision(child, childDecisonNodes);
		}
		return childDecisonNodes;
	}

	private static void getChildDecision(CfgNode node, List<CfgNode> list) {
		List<CfgNode> children = node.getBranches();
		if (null == children || children.size() == 0) {
			;
		} else if (children.size() == 1) {
			getChildDecision(children.get(0), list);
		} else if (children.size() >= 2) { /** branch node */
			if (!list.contains(node)) {
				list.add(node);
			}
		}

	}
}
