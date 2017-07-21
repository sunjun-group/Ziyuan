/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel.improvement.check;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import learntest.plugin.export.io.excel.TrialExcelReader;
import sav.common.core.utils.ResourceUtils;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class L2tResultImprovChecker {
	
	public ImprovementResult checkImprovement(String oldFileName, String newFileName) throws Exception {
		return checkImprovement(new File(ResourceUtils.appendPath(TrialExcelConstants.EXCEL_FOLDER, oldFileName)), 
				new File(ResourceUtils.appendPath(TrialExcelConstants.EXCEL_FOLDER, newFileName)));
	}
	
	public ImprovementResult checkImprovement(File oldResult, File newResult) throws Exception {
		TrialExcelReader reader = new TrialExcelReader(oldResult);
		Map<String, Trial> oldTrials = reader.readDataSheet();
		reader.reset(newResult);
		Map<String, Trial> newTrials = reader.readDataSheet();
		return checkImprovement(oldTrials, newTrials);
	}

	public  ImprovementResult checkImprovement(Map<String, Trial> oldTrials, Map<String, Trial> newTrials) {
		ImprovementResult result = new ImprovementResult();
		List<String> commonMethods = new ArrayList<>();

		/* 1.Whether we support new methods or fail to support old methods; */
		Set<String> oldTrialMethods = new HashSet<String>(oldTrials.keySet());
		for (String testMethod : newTrials.keySet()) {
			// if testMethod exists in oldTrial
			if (!oldTrialMethods.remove(testMethod)) {
				result.addNewMethod(testMethod);
			} else {
				commonMethods.add(testMethod);
			}
		}
		result.addMissingMethods(oldTrialMethods);

		/* 2. Whether the coverage of L2T approach is increased or decreased; */
		/*
		 * 3. Whether more/less methods present the advantage of L2T over
		 * randoop;
		 */
		for (String method : commonMethods) {
			Trial oldTrial = oldTrials.get(method);
			Trial newTrial = newTrials.get(method);
			double coverageDif = newTrial.getL2tRtInfo().getCoverage() - oldTrial.getL2tRtInfo().getCoverage();
			if (coverageDif > 0) {
				result.coverageIncreased(method, coverageDif);
			} else if (coverageDif < 0) {
				result.coverageDecreased(method, -coverageDif);
			}

			double advDif = newTrial.getAdvantage() - oldTrial.getAdvantage();
			if (advDif > 0) {
				result.moreAdvantage(method, advDif);
			} else if (advDif < 0) {
				result.lessAdvantage(method, -advDif);
			}
		}
		return result;
	}
	
	public class ImprovementResult {
		private List<String> newMethods;
		private List<String> missingMethods;
		private Map<String, Double> coverageIncr;
		private Map<String, Double> coverageDecr;
		private Map<String, Double> moreAdv;
		private Map<String, Double> lessAdv;

		public ImprovementResult() {
			newMethods = new ArrayList<>();
			missingMethods = new ArrayList<>();
			coverageIncr = new HashMap<>();
			coverageDecr = new HashMap<>();
			moreAdv = new HashMap<>();
			lessAdv = new HashMap<>();
		}

		public void lessAdvantage(String method, double dif) {
			lessAdv.put(method, dif);
		}

		public void moreAdvantage(String method, double dif) {
			moreAdv.put(method, dif);
		}

		public void coverageDecreased(String method, double dif) {
			coverageDecr.put(method, dif);
		}

		public void coverageIncreased(String method, double dif) {
			coverageIncr.put(method, dif);
		}

		public void addMissingMethods(Set<String> methods) {
			missingMethods.addAll(methods);
		}

		public void addNewMethod(String method) {
			newMethods.add(method);
		}

		@Override
		public String toString() {
			return String.format("There are %d missing methods: \n %s,\n %d new tested methods: \n %s, \n"
					+ "The code coverage is increased in %d methods: \n %s \n"
					+ "The code coverage is decreased in %d methods: \n %s \n"
					+ "The new approach shows less advantages in %d methods: \n %s \n"
					+ "The new approach shows more advantages in %d methods: \n %s \n", 
						missingMethods.size(), TextFormatUtils.printListSeparateWithNewLine(missingMethods),
						newMethods.size(), TextFormatUtils.printListSeparateWithNewLine(newMethods),
						coverageIncr.size(), TextFormatUtils.printMap(coverageIncr),
						coverageDecr.size(), TextFormatUtils.printMap(coverageDecr),
						lessAdv.size(),  TextFormatUtils.printMap(lessAdv),
						moreAdv.size(), TextFormatUtils.printMap(moreAdv));
		}
	}
}
