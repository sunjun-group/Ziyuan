/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.request.EventRequestManager;

/**
 * @author LLT
 *
 */
public abstract class JunitDebugger extends BreakpointDebugger {
	private static final String JUNIT_RUNNER_CLASS_NAME = JunitRunner.class.getName();
	protected List<String> allTests;
	
	/* for internal purpose */
	private int testIdx = 0;
	private Location junitLoc;
	private String jResultFile;
	private boolean jResultFileDeleteOnExit = false;
	
	public void setup(VMConfiguration config, List<String> allTests) {
		super.setup(config);
		this.allTests = allTests;
	}
	
	/**
	 * call {@link JunitDebugger#setup(VMConfiguration, List)} instead.
	 */
	@Override
	@Deprecated 
	public void setup(VMConfiguration config) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected final void beforeDebugging() throws SavException {
		testIdx = 0;
		junitLoc = null;
		jResultFile = createExecutionResultFile();
		config.setLaunchClass(JunitRunner.class.getName());
		List<String> args = new JunitRunnerProgramArgBuilder()
				.methods(allTests).destinationFile(jResultFile).build();
		config.setProgramArgs(args);
		onStart();
	}
	
	@Override
	protected final void addClassWatch(EventRequestManager erm) {
		/* class watch request for breakpoint */
		super.addClassWatch(erm);
		/* class watch request for junitRunner start point */
		addClassWatch(erm, JUNIT_RUNNER_CLASS_NAME);
	}
	
	@Override
	protected final void onHandleClassPrepareEvent(VirtualMachine vm,
			ClassPrepareEvent event) {
		/* add junitRunner breakpoint */
		ReferenceType refType = event.referenceType();
		if (refType.name().equals(JUNIT_RUNNER_CLASS_NAME)) {
			junitLoc = addBreakpointWatch(vm, refType,
					JunitRunner.TESTCASE_PROCESS_START_LINE_NO);
		} 
	}
	
	@Override
	protected final void onHandleBreakpointEvent(BreakPoint bkp, VirtualMachine vm,
			BreakpointEvent bkpEvent) throws SavException {
		try {
			if (areLocationsEqual(bkpEvent.location(), junitLoc)) {
				onEnterTestcase(testIdx++);
			} else {
				onEnterBreakpoint(bkp, bkpEvent);
			}
		} catch (AbsentInformationException e) {
			LOGGER.error(e);
		}
	}

	@Override
	protected final void afterDebugging() throws SavException {
		try {
			JunitResult jResult = JunitResult.readFrom(jResultFile);
			onFinish(jResult);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.JVM, "cannot read junitResult in temp file");
		}
	}

	private String createExecutionResultFile() throws SavException {
		try {
			File tempFile = File.createTempFile("tcsExResult", ".txt");
			if (jResultFileDeleteOnExit) {
				tempFile.deleteOnExit();
			}
			return tempFile.getAbsolutePath();
		} catch (IOException e1) {
			throw new SavException(ModuleEnum.JVM, "cannot create temp file");
		}
	}
	
	private boolean areLocationsEqual(Location location1, Location location2) throws AbsentInformationException {
		return location1.compareTo(location2) == 0;
	}
	
	public void setjResultFileDeleteOnExit(boolean jResultFileDeleteOnExit) {
		this.jResultFileDeleteOnExit = jResultFileDeleteOnExit;
	}
	
	/** abstract methods */
	protected abstract void onStart();
	protected abstract void onEnterTestcase(int testIdx);
	protected abstract void onEnterBreakpoint(BreakPoint bkp, BreakpointEvent bkpEvent) throws SavException;
	protected abstract void onFinish(JunitResult jResult);
}
