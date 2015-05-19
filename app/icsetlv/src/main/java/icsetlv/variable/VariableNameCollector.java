/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.VariableUtils;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.Logger;
import sav.common.core.SavRtException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;


/**
 * @author LLT
 *
 */
public class VariableNameCollector {
	private Logger<?> log = Logger.getDefaultLogger();
	private List<String> srcFolders;
	
	public VariableNameCollector(List<String> srcFolders) {
		this.srcFolders = srcFolders;
	}
	
	public void updateVariables(List<BreakPoint> brkps) throws IcsetlvException {
		Map<String, List<BreakPoint>> brkpsMap = BreakpointUtils.initBrkpsMap(brkps);

		for (String clzName : brkpsMap.keySet()) {
			List<Integer> lines = BreakpointUtils.extractLineNo(brkpsMap.get(clzName));
			
			VarNameVisitor visitor = new VarNameVisitor(lines);
			CompilationUnit cu;
			try {
				cu = JavaParser.parse(getSourceFile(clzName));
				cu.accept(visitor, true);
				Map<Integer, List<Variable>> map = visitor.getResult();
				
				List<BreakPoint> breakpoints = brkpsMap.get(clzName);
				for(BreakPoint breakpoint: breakpoints){
					Integer lineNumber = breakpoint.getLineNo();
					breakpoint.setVars(map.get(lineNumber));
				}
			} catch (ParseException e) {
				log.error(e.getMessage());
				throw new SavRtException(e);
			} catch (IOException e) {
				log.error(e.getMessage());
				throw new SavRtException(e);
			}
		}	
	}

	private File getSourceFile(String clzName) {
		for (String srcFolder : srcFolders) {
			File file = new File(ClassUtils.getJFilePath(srcFolder, clzName));
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	private static class VarNameVisitor extends DefaultVoidVisitor {
		private Map<Integer, List<Variable>> result;
		private List<Integer> lines;
		
		public VarNameVisitor(List<Integer> lines) {
			this.lines = lines;
			result = new HashMap<Integer, List<Variable>>();
			for (Integer line : lines) {
				result.put(line, new ArrayList<BreakPoint.Variable>());
			}
		}
		
		@Override
		protected boolean beforehandleNode(Node node) {
			return lines.contains(node.getBeginLine());
		}
		
		@Override
		protected boolean beforeVisit(Node node) {
			for (Integer lineNo : lines) {
				if (lineNo >= node.getBeginLine() && lineNo <= node.getEndLine()) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public boolean handleNode(NameExpr n) {
			Variable var =VariableUtils.toBreakpointVarName(n);
			add(n.getBeginLine(), var);
			
			return true;
		}
		
		private void add(int lineNumber, Variable var){
			CollectionUtils.addIfNotNullNotExist(result.get(lineNumber), var);
		}

		public Map<Integer, List<Variable>> getResult() {
			return result;
		}
		
		
		
	}
	
	
}
