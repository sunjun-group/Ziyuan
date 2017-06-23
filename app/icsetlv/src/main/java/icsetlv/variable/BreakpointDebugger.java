/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.vm.SimpleDebugger;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 * This class allows to start a program in debug mode, and add breakpoints
 * in order to collect data.
 */
public abstract class BreakpointDebugger {
	private static final long DEFAULT_DEBUG_TIMEOUT = 5000l;
	protected static Logger log = LoggerFactory.getLogger(BreakpointDebugger.class);
	private VMConfiguration config;
	protected SimpleDebugger debugger = new SimpleDebugger();
	// map of classes and their breakpoints
	private Map<String, List<BreakPoint>> brkpsMap;
	protected List<BreakPoint> bkps;

	public void setup(VMConfiguration config) {
		this.config = config;
	}

	public final void run(List<BreakPoint> brkps) throws SavException, SAVExecutionTimeOutException {
		this.bkps = brkps;
		this.brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		/* before debugging */
		beforeDebugging();
		this.config.setDebug(true);
		
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
		boolean eventTimeout = false;
		Map<String, BreakPoint> locBrpMap = new HashMap<String, BreakPoint>();
		
		long startTime = System.currentTimeMillis();
		boolean exitTimeOutVM = false;
		
		try{
			while (!stop && !eventTimeout) {
				EventSet eventSet;
				try {
					eventSet = eventQueue.remove(3000);
				} catch (InterruptedException e) {
					// do nothing, just return.
					break;
				}
				if (eventSet == null) {
					log.warn("Time out! Cannot get event set!");
					eventTimeout = true;
					break;
				}
				for (Event event : eventSet) {

					if (SAVTimer.isTimeOut()) {
						throw new SAVExecutionTimeOutException("Time out at retrieving runtime data");
					}

					if (System.currentTimeMillis() - startTime > DEFAULT_DEBUG_TIMEOUT) {
						System.err.println("run time over 5s, stop");
						stop = true;
						eventTimeout = true;
						exitTimeOutVM = true;
					}

					if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
						stop = true;
						break;
					} else if (event instanceof ClassPrepareEvent) {
						// add breakpoint watch on loaded class
						ClassPrepareEvent classPrepEvent = (ClassPrepareEvent) event;
						handleClassPrepareEvent(vm, classPrepEvent);
						/* add breakpoint request */
						ReferenceType refType = classPrepEvent.referenceType();
						// breakpoints
						addBreakpointWatch(vm, refType, locBrpMap);
						System.currentTimeMillis();

					} else if (event instanceof BreakpointEvent) {
						BreakpointEvent bkpEvent = (BreakpointEvent) event;

						BreakPoint bkp = locBrpMap.get(bkpEvent.location().toString());
						handleBreakpointEvent(bkp, vm, bkpEvent);
					}
				}
				eventSet.resume();
			}
			if (!eventTimeout) {
				vm.resume();
				/* wait until the process completes */
				debugger.waitProcessUntilStop();
			}
			
			if(exitTimeOutVM){
				if(vm != null){
					try{
						vm.exit(0);					
					}catch(Exception e){}
				}
			}
			
			/* end of debug */
			afterDebugging();
		} catch (SAVExecutionTimeOutException e) {
			if (vm != null) {
				vm.exit(0);
			}
			throw new SAVExecutionTimeOutException("Time out at retrieving runtime data");
		} finally {
			if (vm != null) {
				try {
					vm.exit(0);
				} catch (Exception e) {
				}
				vm = null;
			}
		}

	}

	/** abstract methods */
	protected abstract void beforeDebugging() throws SavException;
	protected abstract void handleClassPrepareEvent(VirtualMachine vm, ClassPrepareEvent event);
	protected abstract void handleBreakpointEvent(BreakPoint bkp, VirtualMachine vm, BreakpointEvent bkpEvent) throws SavException;
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addBreakpointWatch(VirtualMachine vm, ReferenceType refType,
			Map<String, BreakPoint> locBrpMap) {
		List<BreakPoint> points = CollectionUtils.initIfEmpty(brkpsMap.get(refType.name()));
		Collections.sort((ArrayList)points, new Comparator<BreakPoint>() {
			@Override
			public int compare(BreakPoint o1, BreakPoint o2) {
				return o1.getLineNo()-o2.getLineNo();
			}
		});
		
		boolean canTheFirstPointSetBreakPoint = false;
		List<sav.strategies.dto.BreakPoint.Variable> variableList = null;
		for(BreakPoint point: points){
			List<sav.strategies.dto.BreakPoint.Variable> varList = point.getVars();
			if(varList != null && !varList.isEmpty()){
				variableList = varList;
				point.setVars(new ArrayList<Variable>());
				break;
			}
			System.currentTimeMillis();
		}
		
		for (int i=0; i<points.size(); i++) {
			BreakPoint brkp = points.get(i);
			int lineNumber = brkp.getLineNo();
			List<Location> locationList = addBreakpointWatch(vm, refType, lineNumber);
			
			if(!locationList.isEmpty() && !canTheFirstPointSetBreakPoint){
				brkp.setVars(variableList);
				canTheFirstPointSetBreakPoint = true;
			}

			for (Location location: locationList) {
				locBrpMap.put(location.toString(), brkp);
			} 
			
			
		}
	}
	
	protected final List<Location> addBreakpointWatch(VirtualMachine vm,
			ReferenceType refType, int lineNumber) {
		List<Location> returnLocations = new ArrayList<Location>();
		try {
			List<Location> locations = refType.locationsOfLine(lineNumber);
			for(Location location: locations) {
				BreakpointRequest breakpointRequest = vm.eventRequestManager()
						.createBreakpointRequest(location);
				breakpointRequest.setEnabled(true);
				returnLocations.add(location);
				
			} 
		} catch (AbsentInformationException e) {
			log.warn(e.getMessage());
		}
		
		return returnLocations;
	}
	
	public String getProccessError() {
		return debugger.getProccessError();
	}
	
	protected VMConfiguration getVmConfig() {
		return config;
	}
	
	public void setDebugger(SimpleDebugger debugger) {
		this.debugger = debugger;
	}
}
