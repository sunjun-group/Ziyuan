package microbat.codeanalysis.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import microbat.model.BreakPoint;
import sav.strategies.dto.AppJavaClassPath;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.StepRequest;

@SuppressWarnings("restriction")
public class ExecutionStatementCollector {
	
	protected String[] stepWatchExcludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*", "junit.*", "junit.framework.*"};
	protected int steps = 0;
	
	public List<BreakPoint> collectBreakPoints(AppJavaClassPath appClassPath){
		List<BreakPoint> pointList = new ArrayList<>();
		
//		VMConfiguration vmConfig = new VMConfiguration(appClassPath);
//		vmConfig.setLaunchClass(Settings.lanuchClass);
//		vmConfig.setWorkingDirectory(appClassPath.getWorkingDirectory());
		
		VirtualMachine vm = new VMStarter(appClassPath).start();
		
		EventRequestManager erm = vm.eventRequestManager(); 
		addClassWatch(erm);
		
		EventQueue queue = vm.eventQueue();
		
		boolean connected = true;
		
		while(connected){
			try {
				EventSet eventSet = queue.remove(1000);
				if(eventSet != null){
					for(Event event: eventSet){
						if(event instanceof VMStartEvent){
							System.out.println("start collecting execution");
							
							ThreadReference thread = ((VMStartEvent) event).thread();
							addStepWatch(erm, thread);
							addExceptionWatch(erm);
						}
						else if(event instanceof VMDeathEvent
							|| event instanceof VMDisconnectEvent){
							connected = false;
						}
						else if(event instanceof StepEvent){
							StepEvent sEvent = (StepEvent)event;
							Location location = sEvent.location();
							
							String path = location.sourcePath();
							path = path.substring(0, path.indexOf(".java"));
							path = path.replace(File.separator, ".");
							
							int lineNumber = location.lineNumber();
							
							BreakPoint breakPoint = new BreakPoint(path, lineNumber);
							System.out.println(breakPoint);
							setSteps(getStepNum() + 1);
							
							if(!pointList.contains(breakPoint)){
								pointList.add(breakPoint);							
							}
						}
						else if(event instanceof ExceptionEvent){
							System.currentTimeMillis();
						}
					}
					
					eventSet.resume();
				}
				else{
					vm.exit(0);
					vm.dispose();
					connected = false;
				}
				
				
			} catch (InterruptedException e) {
				connected = false;
				e.printStackTrace();
			} catch (AbsentInformationException e) {
				e.printStackTrace();
			}
		}
		
		return pointList;
	}
	
	protected void addStepWatch(EventRequestManager erm, ThreadReference threadReference) {
		StepRequest sr = erm.createStepRequest(threadReference,  StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: stepWatchExcludes){
			sr.addClassExclusionFilter(ex);
		}
		sr.enable();
	}
	
	/** add watch requests **/
	protected void addClassWatch(EventRequestManager erm) {
		ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
//		classPrepareRequest.addClassFilter("com.Main");
		classPrepareRequest.setEnabled(true);
	}
	
	protected void addExceptionWatch(EventRequestManager erm) {
		
		ExceptionRequest request = erm.createExceptionRequest(null, true, true);
		request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: stepWatchExcludes){
			request.addClassExclusionFilter(ex);
		}
		request.enable();
	}

	public int getStepNum() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
}
