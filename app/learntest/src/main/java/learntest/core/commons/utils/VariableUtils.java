/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sav.strategies.dto.execute.value.ExecVar;
import variable.Variable;

/**
 * @author LLT Extracted from PreconditionDecisionLearner.
 */
public class VariableUtils {

	private VariableUtils() {
	}

	/**
	 * determine the responding relationship of vars between two CFG
	 * 
	 * @param relevantVarMap
	 *            map from linyun'ss CFG
	 * @param list
	 *            nodes from LLY's CFG
	 * @return
	 */
	public static List<VarInfo> varsTransform(Map<Integer, List<Variable>> relevantVarMap, List<ExecVar> originalVars) {
		HashMap<String, ExecVar> execVarMap = new HashMap<>();
		for (ExecVar execVar : originalVars) {
			execVarMap.put(execVar.getLabel(), execVar);
		}
		List<VarInfo> list = new ArrayList<>(relevantVarMap.size());
		
		List<Entry<Integer, List<Variable>>> entries = new ArrayList<>();
		entries.addAll(relevantVarMap.entrySet());
		Collections.sort(entries, new Comparator<Entry<Integer, List<Variable>>>(){
			@Override
			public int compare(Entry<Integer, List<Variable>> e1, Entry<Integer, List<Variable>> e2) {
				return e1.getKey() - e2.getKey();
			}			
		});
		for (Entry<Integer, List<Variable>> entry : entries) {
			VarInfo info = new VarInfo(entry.getKey(), entry.getValue());
			info.setExecVar(execVarMap, originalVars);
			list.add(info);
		}
		list.sort(new VarInfoComparator());
		return list;
	}

	public static List<String> getLabels(VarInfo varInfo) {
		List<String> labels = new ArrayList<String>(varInfo.getExecVars().size());
		for (ExecVar var : varInfo.getExecVars()) {
			labels.add(var.getLabel());
		}
		return labels;
	}

	public static class VarInfoComparator implements Comparator<VarInfo> {

		@Override
		public int compare(VarInfo o1, VarInfo o2) {
			return o1.offset - o2.offset;
		}

	}

	public static class VarInfo {
		Integer offset;
		List<Variable> variables;
		List<ExecVar> execVars;

		VarInfo(Integer integer, List<Variable> vars) {
			this.offset = integer;
			this.variables = vars;
		}

		public void setExecVar(HashMap<String, ExecVar> execVarMap, List<ExecVar> originalVars) {
			List<VarInfo> infos = new ArrayList<>(variables.size());
			for (Variable var : variables) {
				boolean found = false;
				if (var.getVarID() != null) {
					for (String execVarId : execVarMap.keySet()) {
						if (execVarId.startsWith(var.getVarID())) {
							ExecVar execVar = execVarMap.get(execVarId);
							VarInfo info = new VarInfo(originalVars.indexOf(execVar), null);
							info.execVars = new LinkedList<>();
							info.execVars.add(execVar);
							infos.add(info);
							found = true;
						}
					}
				} else {
					// log.info("{} does not exist in original ExecVars.", var);
				}
				if (!found) {
					// log.info("{} does not exist in original ExecVars.", var);
				}
			}
			infos.sort(new VarInfoComparator()); // keep the sequence in
													// originalVars
			execVars = new ArrayList<>(variables.size());
			for (VarInfo varInfo : infos) {
				execVars.add(varInfo.execVars.get(0));
			}
		}

		public Integer getOffset() {
			return offset;
		}

		public List<Variable> getVariables() {
			return variables;
		}

		public List<ExecVar> getExecVars() {
			return execVars;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(offset + " : ");
			sb.append("Variables : " + variables + ";  ");
			sb.append("ExecVars : " + execVars + "\n");
			return sb.toString();
		}
	}

}
