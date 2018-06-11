/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.xml.coverage.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfg.DecisionBranchType;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.ExecValueUtils;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.xml.coverage.report.element.BranchElement;
import learntest.core.commons.xml.coverage.report.element.BranchesElement;
import learntest.core.commons.xml.coverage.report.element.InputValueElement;
import learntest.core.commons.xml.coverage.report.element.InputValueSetElement;
import learntest.core.commons.xml.coverage.report.element.MethodElement;
import learntest.core.commons.xml.coverage.report.element.MethodsElement;
import learntest.core.commons.xml.coverage.report.element.ParameterElement;
import learntest.core.commons.xml.coverage.report.element.ParametersElement;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;

/**
 * @author LLT
 *
 */
public class ProbesXmlConverter {
	private Map<BreakpointValue, InputValueSetElement> valueEleMap;
	
	public ProbesXmlConverter() {
		valueEleMap = new HashMap<BreakpointValue, InputValueSetElement>();
	}
	
	public MethodsElement toMethodsElement(DecisionProbes probes) {
		return toMethodsElement(CollectionUtils.listOf(probes, 1));
	}

	public MethodsElement toMethodsElement(List<DecisionProbes> probesList) {
		MethodsElement methods = new MethodsElement();
		for (DecisionProbes probes : probesList) {
			methods.add(toMethodElement(probes));
		}
		return methods;
	}

	public MethodElement toMethodElement(DecisionProbes probes) {
		MethodElement method = new MethodElement();
		method.setTotalTcs(probes.getTotalTestNum());
		method.setName(probes.getCfg().getId());
		method.setParams(toParamsElement(probes.getTargetMethod().getParams(), 
				probes.getTargetMethod().getParamTypes()));
		method.setBranches(toBranchesElement(probes));
		return method;
	}

	private ParametersElement toParamsElement(List<String> params, List<String> paramTypes) {
		if (CollectionUtils.isEmpty(params)) {
			return null;
		}
		List<ParameterElement> paramEles = new ArrayList<ParameterElement>(params.size());
		for (int i = 0; i < params.size(); i++) {
			paramEles.add(new ParameterElement(params.get(i), paramTypes.get(i)));
		}
		return new ParametersElement(paramEles);
	}
	
	private BranchesElement toBranchesElement(DecisionProbes probes) {
		List<DecisionNodeProbe> nodeProbes = probes.getNodeProbes();
		if (CollectionUtils.isEmpty(nodeProbes)) {
			return null;
		}
		List<BranchElement> branchEles = new ArrayList<BranchElement>(nodeProbes.size());
		for (DecisionNodeProbe nodeProbe : nodeProbes) {
			/* true branch */
			CollectionUtils.addIfNotNull(branchEles, toBranchElement(nodeProbe, nodeProbe.getTrueValues(),
					DecisionBranchType.TRUE.name()));
			CollectionUtils.addIfNotNull(branchEles, toBranchElement(nodeProbe, nodeProbe.getFalseValues(), 
					DecisionBranchType.FALSE.name()));
		}
		return new BranchesElement(branchEles);
	}

	private BranchElement toBranchElement(DecisionNodeProbe nodeProbe, List<BreakpointValue> trueValues,
			String branchType) {
		if (CollectionUtils.isNotEmpty(trueValues)) {
			BranchElement branchEle = new BranchElement(String.valueOf(nodeProbe.getNode().getIdx()),
					nodeProbe.getNode().getLine(), branchType);
			List<InputValueSetElement> valueSetEle = new ArrayList<InputValueSetElement>(trueValues.size());
			for (BreakpointValue value : trueValues) {
				InputValueSetElement inputValueElement = valueEleMap.get(value);
				if (inputValueElement != null) {
					valueSetEle.add(inputValueElement);
				} else {
					valueSetEle.add(toInputValuesElement(value));
				}
			}
			branchEle.setInputValues(valueSetEle);
			return branchEle;
		}
		return null;
	}

	private InputValueSetElement toInputValuesElement(BreakpointValue value) {
		List<InputValueElement> valueEles = new ArrayList<InputValueElement>();
		List<ExecValue> flatternValues = ExecValueUtils.flattern(value);
		for (ExecValue val : flatternValues) {
			valueEles.add(new InputValueElement(val.getVarId(), val.getStrVal()));
		}
		return new InputValueSetElement(valueEles);
	}
	
}
