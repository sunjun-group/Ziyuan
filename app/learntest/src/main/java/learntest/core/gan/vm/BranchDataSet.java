/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfg.DecisionBranchType;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class BranchDataSet {
	private String nodeId;
	private DecisionBranchType branchType;
	private List<String> labels;
	private Map<Category, List<double[]>> dataset;
	
	public BranchDataSet(int nodeIdx) {
		this();
		this.nodeId = String.valueOf(nodeIdx);
	}
	
	public BranchDataSet() {
		dataset = new HashMap<Category, List<double[]>>();
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	
	public void setDatapoints(Category category, List<double[]> dataPoint) {
		dataset.put(category, dataPoint);
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public Map<Category, List<double[]>> getDataset() {
		return dataset;
	}
	
	public List<double[]> getDatapoints(Category category) {
		return getDataset().get(category);
	}
	
	public void setDataset(Map<Category, List<double[]>> dataset) {
		this.dataset = dataset;
	}
	
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public DecisionBranchType getBranchType() {
		return branchType;
	}

	public void setBranchType(DecisionBranchType branchType) {
		this.branchType = branchType;
	}

	public static enum Category {
		TRUE,
		FALSE
	}

	public List<double[]> getAllDatapoints() {
		List<double[]> dataPoints = null;
		for (List<double[]> dps : dataset.values()) {
			if (dataPoints == null) {
				dataPoints = new ArrayList<double[]>(dps);
			} else {
				dataPoints.addAll(dps);
			}
		}
		return dataPoints;
	}
	
	@Override
	public String toString() {
		return "NodeDataSet [nodeId=" + nodeId + ", labels=" + labels + ", dataset=" + TextFormatUtils.printMap(dataset)
				+ "]";
	}
	
}
