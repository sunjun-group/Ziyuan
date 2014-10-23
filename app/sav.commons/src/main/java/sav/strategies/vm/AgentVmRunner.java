/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class AgentVmRunner extends VMRunner {
	protected String agentJarPath;
	protected Map<String, String> agentParams;

	public AgentVmRunner(String agentJarPath) {
		this.agentJarPath = agentJarPath;
		agentParams = new HashMap<String, String>();
	}
	
	@Override
	protected void buildVmOption(CollectionBuilder<String, ?> builder,
			VMConfiguration config) {
		StringBuilder sb = new StringBuilder("-javaagent:").append(agentJarPath);
		List<String> agentParams = getAgentParams();
		if (agentParams != null) {
			sb.append("=")
				.append(StringUtils.join(agentParams, ","));
		}
		builder.add(sb.toString());
	}

	private List<String> getAgentParams() {
		ArrayList<String> params = new ArrayList<String>();
		if (!agentParams.isEmpty()) {
			for (Entry<String, String> entry : agentParams.entrySet()) {
				params.add(newAgentOption(entry.getKey(), entry.getValue()));
			}
		}
		appendAgentParams(params);
		return params;
	}

	protected void appendAgentParams(ArrayList<String> params) {
		// override if needed.
	}

	protected String newAgentOption(String opt, String value) {
		return StringUtils.join("=", opt, value);
	}
}
