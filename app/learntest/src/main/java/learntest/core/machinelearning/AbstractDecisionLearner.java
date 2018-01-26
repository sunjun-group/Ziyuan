/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.utils.CfgUtils;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVTimer;
import variable.Variable;

/**
 * @author LLT 
 */
public abstract class AbstractDecisionLearner extends AbstractLearningComponent implements IInputLearner {
	private static Logger log = LoggerFactory.getLogger(AbstractDecisionLearner.class);
	protected HashMap<CfgNode, CfgNodeDomainInfo> dominationMap = new HashMap<>();
	
	public AbstractDecisionLearner(LearningMediator mediator) {
		super(mediator);
	}

	public DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException {
		List<CfgNode> decisionNodes = inputProbes.getCfg().getDecisionNodes();
		DecisionProbes probes = inputProbes;

		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()),
				inputProbes.getCfg().getDecisionNodes());

		HashMap<Integer, List<CfgNode>> indexMap = new HashMap<>();
		for (CfgNode cfgNode : decisionNodes) {
			int line = cfgNode.getLine();
			if (indexMap.containsKey(line)) {
				indexMap.get(line).add(cfgNode);
			} else {
				List<CfgNode> list = new LinkedList<>();
				list.add(cfgNode);
				indexMap.put(line, list);
			}
		}
		try {
			prepareDataBeforeLearn(inputProbes, relevantVarMap);
			learn(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()), probes, new ArrayList<Integer>(decisionNodes.size()),
					indexMap);
		} catch(Exception e) {
			e.printStackTrace();
			log.debug(TextFormatUtils.printObj(e.getStackTrace()));
			throw e;
		} finally {
			onFinishLearning();
		}
		return probes;
	}

	protected void onFinishLearning() {
		// do nothing by default.
	}

	protected abstract void prepareDataBeforeLearn(DecisionProbes inputProbes,
			Map<Integer, List<Variable>> relevantVarMap) throws SavException;

	private void learn(CfgNode node, DecisionProbes probes, List<Integer> visitedNodes,
			HashMap<Integer, List<CfgNode>> indexMap) throws SavException {
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(node);
		int loopTimes = 0;
		while (!queue.isEmpty()) {
			if (SAVTimer.isTimeOut()) {
				break;
			}
			loopTimes++;
			node = queue.poll();
			log.debug("parsing the node in line " + node.getLine() + "(" + node + ")");

			DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
			CfgNode queueNode = learn(nodeProbe, visitedNodes, loopTimes);
			if (queueNode != null) {
				queue.add(queueNode);
				continue;
			}

			visitedNodes.add(node.getIdx());
			List<CfgNode> childDecisonNodes = dominationMap.get(node).getDominatees();
			/**
			 * handle the situation that first branch node does not dominate
			 * fellow branch node
			 */
			for (CfgNode cfgNode : DecisionProbes.getChildDecision(node)) {
				if (!childDecisonNodes.contains(cfgNode) && !visitedNodes.contains(cfgNode.getIdx())) {
					childDecisonNodes.add(cfgNode);
				}
			}

			childDecisonNodes.sort(new DomainationComparator(dominationMap));
			for (CfgNode dependentee : CollectionUtils.nullToEmpty(childDecisonNodes)) {
				if (null != dependentee && !visitedNodes.contains(dependentee.getIdx())
						&& !queue.contains(dependentee)) {
					queue.add(dependentee);
				}
			}
			if (queue.isEmpty()) {
				checkLearnedComplete(visitedNodes, probes.getCfg().getDecisionNodes(), queue);
			}
		}
	}

	/**
	 * return node need to be added to queue. (would be current node)
	 */
	protected abstract CfgNode learn(DecisionNodeProbe nodeProbe, List<Integer> visitedNodes, int loopTimes) throws SavException;

	/**
	 * check if all decision nodes are learned, otherwise poll all
	 */
	private void checkLearnedComplete(List<Integer> visitedNodes, List<CfgNode> decisionNodes, Queue<CfgNode> queue) {
		for (CfgNode cfgNode : decisionNodes) {
			if (!visitedNodes.contains(cfgNode.getIdx())) {
				queue.add(cfgNode);
				log.debug("this node is missed : " + cfgNode);
			}
		}
	}
	
	protected List<CfgNode> getDominators(CfgNode node) {
		CfgNodeDomainInfo info = dominationMap.get(node);
		if (info == null) {
			return Collections.EMPTY_LIST;
		}
		return info.getDominators();
	}

	protected boolean needToLearn(DecisionNodeProbe nodeProbe) {
		if (!nodeProbe.areAllbranchesUncovered()) {
			return true;
		} else {
			log.debug("All branches are uncovered!");
			DecisionProbes probes = nodeProbe.getDecisionProbes();
			for (CfgNode dependentee : dominationMap.get(nodeProbe.getNode()).getDominatees()) {
				DecisionNodeProbe dependenteeProbe = probes.getNodeProbe(dependentee);
				if (dependenteeProbe.hasUncoveredBranch()) {
					return true;
				}
			}
			return false;
		}
	}
	
	public boolean isUsingPrecondApproache() {
		return true;
	}

	public HashMap<CfgNode, CfgNodeDomainInfo> getDominationMap() {
		return dominationMap;
	}

	@Override
	public String getLogFile() {
		return null;
	}

	@Override
	public void cleanup() {
		getTrueSample().clear();
		getFalseSample().clear();
	}
	
}
