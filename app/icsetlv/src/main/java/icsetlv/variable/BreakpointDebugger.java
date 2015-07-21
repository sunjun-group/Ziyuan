/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.Logger;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.SimpleDebugger;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

/**
 * @author LLT
 * This class allows to start a program in debug mode, and add breakpoints
 * in order to collect data.
 */
public abstract class BreakpointDebugger {
	protected static final Logger<?> LOGGER = Logger.getDefaultLogger();
	protected VMConfiguration config;
	protected SimpleDebugger debugger;
	// map of classes and their breakpoints
	private Map<String, List<BreakPoint>> brkpsMap;
	protected List<BreakPoint> bkps;

	public void setup(VMConfiguration config) {
		debugger = new SimpleDebugger();
		this.config = config;
	}

	public final void run(List<BreakPoint> brkps) throws SavException {
		this.bkps = brkps;
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		/* before debugging */
		beforeDebugging();
		
		/* start debugger */
		VirtualMachine vm = debugger.run(config);
		if (vm == null) {
			throw new SavException(ModuleEnum.JVM, "cannot start jvm!");
		}
		/* add class watch */
		addClassWatch(vm.eventRequestManager());

		/* process debug events */
		EventQueue eventQueue = vm.eventQueue();
		boolean stop = false;
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		while (!stop) {
			EventSet eventSet;
			try {
				eventSet = eventQueue.remove(1000);
			} catch (InterruptedException e) {
				// do nothing, just return.
				break;
			}
			if (eventSet == null) {
				break;
				// TODO LLT: try to get event from queue again until time
				// out is reached.
			}
			for (Event event : eventSet) {
				if (event instanceof VMDeathEvent
						|| event instanceof VMDisconnectEvent) {
					stop = true;
					break;
				} else if (event instanceof ClassPrepareEvent) {
					// add breakpoint watch on loaded class
					ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
					onHandleClassPrepareEvent(vm, classPrepEvent);
					/* add breakpoint request */
					ReferenceType refType = classPrepEvent.referenceType();
					// breakpoints
					addBreakpointWatch(vm, refType, locBrpMap);
					
				} else if (event instanceof BreakpointEvent) {
					BreakpointEvent bkpEvent = (BreakpointEvent) event;
					BreakPoint bkp = locBrpMap.get(bkpEvent.location()
							.toString());
					onHandleBreakpointEvent(bkp, vm, bkpEvent);
				}
			}
			eventSet.resume();
		}
		vm.resume();
		/* wait until the process completes */
		debugger.waitProcessUntilStop();
		/* end of debug */
		afterDebugging();
	}

	/** abstract methods */
	protected abstract void beforeDebugging() throws SavException;
	protected abstract void onHandleClassPrepareEvent(VirtualMachine vm, ClassPrepareEvent event);
	protected abstract void onHandleBreakpointEvent(BreakPoint bkp, VirtualMachine vm, BreakpointEvent bkpEvent) throws SavException;
	protected abstract void afterDebugging() throws SavException ;


	/** add watch requests **/
	protected void addClassWatch(EventRequestManager erm) {
		/* add class watch for breakpoints */
		for (String className : brkpsMap.keySet()) {
			addClassWatch(erm, className);
		}
	}

	protected final void addClassWatch(EventRequestManager erm, String className) {
		ClassPrepareRequest classPrepareRequest = erm
				.createClassPrepareRequest();
		classPrepareRequest.addClassFilter(className);
		classPrepareRequest.setEnabled(true);
	}
	
	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
			Map<String, BreakPoint> locBrpMap) {
		for (BreakPoint brkp : CollectionUtils.nullToEmpty(brkpsMap.get(refType.name()))) {
			Location location = addBreakpointWatch(vm, refType, brkp.getLineNo());
			if (location != null) {
				locBrpMap.put(location.toString(), brkp);
			} else {
				LOGGER.warn("Cannot add break point " + brkp);
			}
		}
	}
	
	protected final Location addBreakpointWatch(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		// We assume that it is always possible to add the break point
		List<Location> locations;
		try {
			locations = refType.locationsOfLine(lineNumber);
		} catch (AbsentInformationException e) {
			LOGGER.warn((Object[]) e.getStackTrace());
			return null;
		}
		if (!locations.isEmpty()) {
			Location location = locations.get(0);
			BreakpointRequest breakpointRequest = vm.eventRequestManager()
					.createBreakpointRequest(location);
			breakpointRequest.setEnabled(true);
			return location;
		} 
		return null;
	}
}
