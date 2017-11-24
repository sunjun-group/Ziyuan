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
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.gan.GanTestReport;
import learntest.core.commons.test.gan.eval102.NodeExportData102.BranchTraningData;
import learntest.core.commons.test.gan.eval102.RowData102.UpdateType;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GanTestReport102 extends GanTestReport {
	private Map<String, RowData102> rowDataMap = new HashMap<>();
	private GanTestExcelWriter102 excelWriter;
	private Map<String, NodeExportData102> nodeDataMap = new HashMap<String, NodeExportData102>();
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
	public void trainingDatapoints(CfgNode node, NodeDataSet dataSet) {
		List<double[]> trueDps = dataSet.getDatapoints(Category.TRUE);
		List<double[]> falseDps = dataSet.getDatapoints(Category.FALSE);
		if (CollectionUtils.isEmpty(trueDps) || CollectionUtils.isEmpty(falseDps)) {
			return;
		}
		NodeExportData102 data = getNodeData(node);
		if (data.getVars() == null) {
			data.setVars(new ArrayList<String>(dataSet.getLabels()));
		}
		data.getTrueTrainData().updateTrainDps(trueDps);
		data.getFalseTrainData().updateTrainDps(falseDps);
		export(data, UpdateType.ALL);
	}
	
	@Override
	public void samplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult, Category category) {
		NodeExportData102 data = nodeDataMap.get(getNodeId(node));
		if (data == null) {
			return;
		}
		INodeCoveredData newData = samplingResult.getNewData(node);
		BranchTraningData branchData;
		List<BreakpointValue> correctGenVals;
//		List<BreakpointValue> wrongGenVals;
		if (category == Category.TRUE) {
			branchData = data.getTrueTrainData();
			correctGenVals = newData.getTrueValues();
//			wrongGenVals = newData.getFalseValues();
		} else {
			branchData = data.getFalseTrainData();
			correctGenVals = newData.getFalseValues();
//			wrongGenVals = newData.getTrueValues();
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
		for (NodeExportData102 node : nodeDataMap.values()) {
			node.setCoverageInfo(cvgInfo);
			node.setCvg(cvg);
			node.setInitCoverageInfo(initCvg);
			export(node, UpdateType.COVERAGE);
		}
	}
	
	private NodeExportData102 getNodeData(CfgNode node) {
		String nodeId = getNodeId(node);
		int nodeIdx = node.getIdx();
		NodeExportData102 data = nodeDataMap.get(nodeId);
		if (data == null) {
			data = new NodeExportData102();
			data.setMethodId(methodId);
			data.setNodeIdx(nodeIdx);
			data.setLineNum(node.getLine());
			nodeDataMap.put(nodeId, data);
		}
		return data;
	}

	private String getNodeId(CfgNode node) {
		String nodeId = NodeExportData102.getId(methodId, node.getIdx());
		return nodeId;
	}
	
	public void export(NodeExportData102 nodeData, UpdateType updateType) {
		try {
			String rowId = nodeData.getId();
			RowData102 rowData = rowDataMap.get(rowId);
			if (rowData == null) {
				rowData = new RowData102();
				rowData.setId(rowId);
				rowDataMap.put(rowId, rowData);
			}
			rowData.setNodeData(nodeData);
			rowData.setUpdateType(updateType);
			excelWriter.export(rowData);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
