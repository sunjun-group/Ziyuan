/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	protected static Logger log = LoggerFactory.getLogger(VariableNameCollector.class);
	private List<String> srcFolders;
	private VarNameCollectionMode collectionMode;
	private boolean appendPrevLineVars = false;
	
	public VariableNameCollector(VarNameCollectionMode collectionMode, String... srcFolders) {
		this.srcFolders = new ArrayList<String>();
		CollectionUtils.addIfNotNullNotExist(this.srcFolders, srcFolders);
		this.collectionMode = collectionMode;
	}
	
	public VariableNameCollector(VarNameCollectionMode collectionMode, List<String> srcFolders) {
		this.srcFolders = new ArrayList<String>();
		this.srcFolders = srcFolders;
		this.collectionMode = collectionMode;
	}
	
	public void updateVariables(Collection<BreakPoint> brkps) throws IcsetlvException {
		Map<String, List<BreakPoint>> brkpsMap = BreakpointUtils.initBrkpsMap(brkps);

		for (String clzName : brkpsMap.keySet()) {
			File sourceFile = getSourceFile(clzName);
			if (sourceFile == null) {
				log.debug("Classs", clzName, "doesn't exist in source folder(s)", srcFolders);
				continue;
			}
			List<Integer> lines = BreakpointUtils.extractLineNo(brkpsMap.get(clzName));
			
			VarNameVisitor visitor = getVarNameVisitor(lines);
			CompilationUnit cu;
			try {
				cu = JavaParser.parse(sourceFile);
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

	private VarNameVisitor getVarNameVisitor(List<Integer> lines) {
		if (appendPrevLineVars) {
			return new VarNameAppendModeVisitor(collectionMode, lines);
		}
		return new VarNameVisitor(collectionMode, lines);
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
	
	public void setAppendPrevLineVars(boolean appendPrevLineVars) {
		this.appendPrevLineVars = appendPrevLineVars;
	}
}
