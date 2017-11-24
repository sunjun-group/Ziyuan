/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.model.TestInput;
import jdart.model.TestVar;
import main.RunJPF;
import sav.common.core.Pair;

/**
 * @author ??
 * extracted from RunJDartHandler.
 */
public class JDartCore {
	private Logger log = LoggerFactory.getLogger(JDartCore.class);
	public static long timeLimit = 30 * 1000;

	public List<TestInput> run_on_demand(JDartParams jdartParams, String jdartInitTc) {

		timeLimit = jdartParams.getTimeLimit() > 0 ? jdartParams.getTimeLimit() : timeLimit;
		String[] config = constructConfig(jdartParams, true);
		RunJPF jpf = new RunJPF();
		List<TestInput> init_value = null;
		LinkedList<TestVar> paramList = new LinkedList<>();
		String result = null;
		try {
			init_value = jpf.run(config);
	        for(Entry<List<int[]>, String[]> entry : jpf.getPathMap().entrySet()) {
	        	result = entry.getValue()[1];
	        }
	        paramList = init_value.get(0).getParamList();
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
        
        if(result == null) {
        	if (jdartInitTc != null && jdartInitTc.length() > 0) {
            	jdartParams.setMainEntry(jdartInitTc);
			}
        	config = constructConfig(jdartParams, false);
        	init_value = jpf.run(config);
        	for(Entry<List<int[]>, String[]> entry : jpf.getPathMap().entrySet()) {
            	List<int[]> tempPath = entry.getKey();
            	for(int i = 0; i < tempPath.size(); i++) {
            		int[] node_branch = tempPath.get(i);
            		if(node_branch[0] == jdartParams.getExploreNode() && node_branch[1] == jdartParams.getExploreBranch()){
                		result = entry.getValue()[1];
                		break;
                	}
            	}
            	if(result != null)
            		break;
            }
	        paramList = init_value.get(0).getParamList();
        }

        if(result != null) {
        	String[] values = result.split(",");
        	for(String value : values) {
        		if(value.contains("[")) {
        			String[] temp = value.split(":=");
        			String arrayName = temp[0].substring(0, temp[0].indexOf("["));
        			int index = Integer.valueOf(temp[0].substring(temp[0].indexOf('[')+1, temp[0].indexOf(']')));
        			for(int i = 0; i < paramList.size(); i++){
        				if(paramList.get(i).getName().equals(arrayName)) {
        					paramList.get(i).getChildren().get(index).setValue(temp[1]);
        				}
        			}
        		}
        		else {
    				String[] temp = value.split(":=");
    				for(int i = 0; i < paramList.size(); i++){
    					if(paramList.get(i).getName().equals(temp[0]))
    						paramList.get(i).setValue(temp[1]);
    				}
        		}
        	}
        	List<TestInput> value = new LinkedList<>();
        	TestInput input = new TestInput();
        	input.setParamList(paramList);
        	value.add(input);
        	return value;
        }
		return null;
	}
	
	/**
	 * 
	 * @param jdartParams
	 * @return perhaps NULL
	 */
	public Pair<List<TestInput>, Integer> run(JDartParams jdartParams) {
		timeLimit = jdartParams.getTimeLimit() > 0 ? jdartParams.getTimeLimit() : timeLimit;
		String[] config = constructConfig(jdartParams, false);
		RunJPF jpf = new RunJPF();
		List<TestInput> inputList = jpf.run(config);
		int solveCount = jpf.getSolveCount();
		log.debug("solve count : " + solveCount);
		Pair<List<TestInput>, Integer> pair = new Pair<List<TestInput>, Integer>(inputList, solveCount);
		return pair;
	}
	
	private static String[] constructConfig(JDartParams params, boolean onDemand) {
		return  new String[]{
				"+app=" + params.getAppProperties(),
				"+site=" + (onDemand ? params.getOnDemandSiteProperties() : params.getSiteProperties()),
				"+jpf-jdart.classpath+=" + params.getClasspathStr(),
				"+target=" + params.getMainEntry(),
				"+concolic.method=" + params.getMethodName(),
				"+concolic.method." + params.getMethodName() + "=" +params.getClassName()+"."+ params.getMethodName() + params.getParamString(),
				"+concolic.method." + params.getMethodName() + ".config=all_fields_symbolic",
				"+jdart.tree.dont.print=true", // do not print tree
				"+search.min_free="+params.getMinFree(),
				"+search.timeLimit="+params.getTimeLimit(),
				"+explore.node=" + params.getExploreNode(),
				"+explore.branch=" + params.getExploreBranch()
		};
	}
	
	public static int socketWaiteTime() {
		int wait = 15 * 1000; //ms
		return wait;
	}
}
