/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.iface.IBugAnalyzer;
import icsetlv.iface.IManager;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;

/**
 * @author Jingyi
 * 
 */
public class BugAnalyzer implements IBugAnalyzer {
	private IManager manager;
	private List<String> passTestcases;
	private List<String> failTestcases;

	public BugAnalyzer(IManager manager, List<String> passTestcases,
			List<String> failTestcases) {
		this.manager = manager;
		this.passTestcases = passTestcases;
		this.failTestcases = failTestcases;
	}
	
	@Override
	public List<BreakPoint> analyze(List<BreakPoint> initBkps)
			throws IcsetlvException, SavException {
		List<BreakPoint> rootCause = new ArrayList<BreakPoint>();
		List<BreakPoint> allBkps = initBkps;
		boolean firstRound = true;
		while (!allBkps.isEmpty()) {
			List<BreakPoint> executeBkps = next(allBkps);
			TcExecResult execResult = manager.getTestcasesExecutor().execute(
					passTestcases, failTestcases, executeBkps);
			for (BreakPoint bkp : executeBkps) {
				if (manager.getBugExpert().isRootCause(execResult.getPassValues(bkp),
						execResult.getFailValues(bkp))) {
					rootCause.add(bkp);
				} else if (firstRound){
					List<BreakPoint> sliceResult;
					try {
						//TODO to adapt
						sliceResult = manager.getSlicer().slice(executeBkps, new ArrayList<String>());
						allBkps.addAll(sliceResult);
						firstRound = false;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return rootCause;
	}
	
	private List<BreakPoint> next(List<BreakPoint> allBkps) {
		return CollectionUtils.listOf(allBkps.remove(0));
	}
}
