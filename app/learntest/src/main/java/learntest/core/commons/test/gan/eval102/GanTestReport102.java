/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan.eval102;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.gan.GanTestReport;
import learntest.core.commons.test.gan.eval102.BranchExportData102.CategoryTrainingData;
import learntest.core.commons.test.gan.eval102.RowData102.UpdateType;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gan.vm.BranchDataSet;
import learntest.core.gan.vm.BranchDataSet.Category;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GanTestReport102 extends GanTestReport {
	private Map<String, RowData102> rowDataMap = new HashMap<>();
	private GanTestExcelWriter102 excelWriter;
	private Map<String, BranchExportData102> nodeDataMap = new HashMap<String, BranchExportData102>();
	private String methodId;
	private String initCvg;
	
	public GanTestReport102(String fileName) throws Exception {
		excelWriter = new GanTestExcelWriter102(new File(fileName));
	}
	
	public void startRound(int i, learntest.core.LearnTestParams params) {
		nodeDataMap.clear();
		methodId = params.getTargetMethod().getMethodId();
	};
	
	@Override
	public void initCoverage(double firstCoverage, String cvgInfo) {
		this.initCvg = cvgInfo;
	}
	
	@Override
	public void trainingDatapoints(CfgNode node, DecisionBranchType branchType, BranchDataSet dataSet) {
		List<double[]> trueDps = dataSet.getDatapoints(Category.TRUE);
		List<double[]> falseDps = dataSet.getDatapoints(Category.FALSE);
		if (CollectionUtils.isEmpty(trueDps) || CollectionUtils.isEmpty(falseDps)) {
			return;
		}
		BranchExportData102 data = getNodeData(node, branchType);
		if (data.getVars() == null) {
			data.setVars(new ArrayList<String>(dataSet.getLabels()));
		}
		data.getTrueTrainData().updateTrainDps(trueDps);
		data.getFalseTrainData().updateTrainDps(falseDps);
		export(data, UpdateType.ALL);
	}
	
	@Override
	public void samplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult,
			DecisionBranchType branchType) {
		BranchExportData102 data = nodeDataMap.get(getNodeId(node, branchType));
		if (data == null) {
			return;
		}
		INodeCoveredData newData = samplingResult.getNewData(node);
		CategoryTrainingData branchData = data.getTrueTrainData();
		List<BreakpointValue> correctGenVals;
		if (branchType == DecisionBranchType.TRUE) {
			correctGenVals = newData.getTrueValues();
		} else {
			correctGenVals = newData.getFalseValues();
		}
		List<Integer> correctIdexies = DomainUtils.getCorrespondingSolutionIdx(allDatapoints, correctGenVals);
		List<double[]> correctDps = new ArrayList<>();
		List<double[]> wrongDps = new ArrayList<>();
		collectData(allDatapoints, correctIdexies, correctDps, wrongDps);
		branchData.updateCorrectGenDps(correctDps);
		branchData.updateWrongGenDps(wrongDps);
		export(data, UpdateType.ALL);
	}
	
	private void collectData(List<double[]> allDatapoints, List<Integer> correctIdexies, List<double[]> correctDps,
			List<double[]> wrongDps) {
		for (int i = 0; i < allDatapoints.size(); i++) {
			if (correctIdexies.contains(i)) {
				correctDps.add(allDatapoints.get(i));
			} else {
				wrongDps.add(allDatapoints.get(i));
			}
		}
	}

	@Override
	public void coverage(String cvgInfo, double cvg) {
		for (BranchExportData102 node : nodeDataMap.values()) {
			node.setCoverageInfo(cvgInfo);
			node.setCvg(cvg);
			node.setInitCoverageInfo(initCvg);
			export(node, UpdateType.COVERAGE);
		}
	}
	
	private BranchExportData102 getNodeData(CfgNode node, DecisionBranchType branchType) {
		String nodeId = getNodeId(node, branchType);
		int nodeIdx = node.getIdx();
		BranchExportData102 data = nodeDataMap.get(nodeId);
		if (data == null) {
			data = new BranchExportData102();
			data.setMethodId(methodId);
			data.setNodeIdx(nodeIdx);
			data.setBranchType(branchType);
			data.setLineNum(node.getLine());
			nodeDataMap.put(nodeId, data);
		}
		return data;
	}

	private String getNodeId(CfgNode node, DecisionBranchType branchType) {
		String nodeId = BranchExportData102.getId(methodId, node.getIdx(), branchType);
		return nodeId;
	}
	
	public void export(BranchExportData102 nodeData, UpdateType updateType) {
		try {
			String rowId = nodeData.getId();
			RowData102 rowData = rowDataMap.get(rowId);
			if (rowData == null) {
				rowData = new RowData102();
				rowData.setId(rowId);
				rowDataMap.put(rowId, rowData);
			}
			rowData.setBranchData(nodeData);
			rowData.setUpdateType(updateType);
			excelWriter.export(rowData);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
