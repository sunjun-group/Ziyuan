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
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;

/**
 * @author LLT
 *
 */
public class GanTestReport102 extends GanTestReport {
	private Map<String, RowData102> rowDataMap = new HashMap<>();
	private GanTestExcelWriter102 excelWriter;
	private Map<String, NodeExportData102> nodeDataMap = new HashMap<String, NodeExportData102>();
	private String methodId;
	
	public GanTestReport102(String fileName) throws Exception {
		excelWriter = new GanTestExcelWriter102(new File(fileName));
	}
	
	public void startRound(int i, learntest.core.LearnTestParams params) {
		nodeDataMap.clear();
		methodId = params.getTargetMethod().getMethodId();
	};
	
	@Override
	public void trainingDatapoints(CfgNode node, NodeDataSet dataSet) {
		NodeExportData102 data = getNodeData(node);
		if (data.getVars() == null) {
			data.setVars(new ArrayList<String>(dataSet.getLabels()));
		}
		data.getTrueTrainData().updateTrainDps(dataSet.getDatapoints(Category.TRUE));
		data.getFalseTrainData().updateTrainDps(dataSet.getDatapoints(Category.FALSE));
		export(data);
	}
	
	@Override
	public void samplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult, Category category) {
		NodeExportData102 data = getNodeData(node);
		INodeCoveredData newData = samplingResult.getNewData(node);
		BranchTraningData branchData;
		List<BreakpointValue> correctGenVals;
		List<BreakpointValue> wrongGenVals;
		if (category == Category.TRUE) {
			branchData = data.getTrueTrainData();
			correctGenVals = newData.getTrueValues();
			wrongGenVals = newData.getFalseValues();
		} else {
			branchData = data.getFalseTrainData();
			correctGenVals = newData.getFalseValues();
			wrongGenVals = newData.getTrueValues();
		}
		branchData.updateCorrectGenDps(DomainUtils.getCorrespondingSolution(allDatapoints, correctGenVals));
		branchData.updateWrongGenDps(DomainUtils.getCorrespondingSolution(allDatapoints, wrongGenVals));
		export(data);
	}
	
	private NodeExportData102 getNodeData(CfgNode node) {
		int nodeIdx = node.getIdx();
		String nodeId = NodeExportData102.getId(methodId, nodeIdx);
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
	
	public void export(NodeExportData102 nodeData) {
		try {
			String rowId = nodeData.getId();
			RowData102 rowData = rowDataMap.get(rowId);
			if (rowData == null) {
				rowData = new RowData102();
				rowData.setId(rowId);
				rowDataMap.put(rowId, rowData);
			}
			rowData.setNodeData(nodeData);
			excelWriter.export(rowData);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
