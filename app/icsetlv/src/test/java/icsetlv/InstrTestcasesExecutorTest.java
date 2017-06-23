/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakpointData;
import icsetlv.variable.DebugValueInstExtractor;
import icsetlv.variable.TestcasesExecutor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.Machine.DataPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import testdata.testcasesexecutor.test1.TcExSum;
import testdata.testcasesexecutor.test1.TcExSumTest;

/**
 * @author LLT
 *
 */
public class InstrTestcasesExecutorTest extends AbstractTest {
	private AppJavaClassPath appClasspath;
	private TestcasesExecutor varExtr;
	
	@Before
	public void setup() {
		appClasspath = initAppClasspath();
		appClasspath.addClasspath(TestConfiguration.getTestTarget(ICSETLV));
		varExtr = new TestcasesExecutor(3);
	}
	
	@Test
	public void instrLocalObjField() throws Exception {
		Data data = new Data(TcExSum.class, TcExSumTest.class);
		// breakpoints
		data.breakpoint(32);
		Variable var = new Variable("innerClass", "innerClass.b", VarScope.UNDEFINED);
		data.instrVal(var, 1000);
		List<BreakpointData> result = runTcExecutor(data);
		System.out.println(result);
		validate(result, var.getId(), 1000);
	}
	
	@Test
	public void instrThisObjField() throws Exception {
		Data data = new Data(TcExSum.class, TcExSumTest.class);
		// breakpoints
		data.breakpoint(32);
		Variable var = new Variable("a", "a", VarScope.THIS);
		data.instrVal(var, 1000);
		List<BreakpointData> result = runTcExecutor(data);
		System.out.println(result);
		validate(result, var.getId(), 1000);
	}
	
	@Test
	public void instrLocalVar() throws Exception {
		Data data = new Data(TcExSum.class, TcExSumTest.class);
		// breakpoints
		data.breakpoint(32);
		data.instrVal("a", 1000);
		List<BreakpointData> result = runTcExecutor(data);
		System.out.println(result);
		validate(result, "a", 1000);
	}
	
	@Test
	public void instrArrEleVar() throws Exception {
		varExtr.setValRetrieveLevel(5);
		Data data = new Data(TcExSum.class, TcExSumTest.class);
		// breakpoints
		data.breakpoint(32);
		Variable var = new Variable("innerClass", "innerClass.arr[1]");
		data.instrVal(var , 1000);
		List<BreakpointData> result = runTcExecutor(data);
		System.out.println(result);
		validate(result, "innerClass.arr[1]", 1000);
	}

	private void validate(List<BreakpointData> result, String var, double val) {
		List<DataPoint> datapoints = result.get(0).toDatapoints(Arrays.asList(var));
		for (DataPoint dp : datapoints) {
			for (int i = 0; i < dp.getNumberOfFeatures(); i++) {
				double value = dp.getValue(i);
				Assert.assertEquals(val, value, 0);
			}
		}
	}

	private List<BreakpointData> runTcExecutor(Data data)
			throws Exception {
		List<String> tests = JunitUtils.extractTestMethods(CollectionUtils
				.listOf(data.testClass.getName()));
		varExtr.setup(appClasspath, tests);
		varExtr.setValueExtractor(new DebugValueInstExtractor(varExtr.getValRetrieveLevel(), data.instVals));
		varExtr.run(data.getBkps());
		List<BreakpointData> result = varExtr.getResult();
		return result;
	}
	
	private static class Data {
		Class<?> testClass;
		Class<?> targetClass;
		Map<String, Object> instVals;
		BreakPoint bkp;
		
		Data(Class<?> targetClass, Class<?> testClass) {
			this.testClass = testClass;
			this.targetClass = targetClass;
			instVals = new HashMap<String, Object>();
		}
		
		public void instrVal(Variable var, Object val) {
			bkp.addVars(var);
			instVals.put(var.getId(), val);			
		}

		public void instrVal(String var, Object val) {
			instVals.put(var, val);			
			bkp.addVars(new Variable(var));
		}

		public void breakpoint(int lineNo) {
			bkp = new BreakPoint(targetClass.getCanonicalName(), lineNo);
		}
		
		public List<BreakPoint> getBkps() {
			return CollectionUtils.listOf(bkp);
		}
	}
}
