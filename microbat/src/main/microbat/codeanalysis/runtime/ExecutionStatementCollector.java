package microbat.codeanalysis.runtime;

import java.util.ArrayList;
import java.util.List;

import microbat.model.BreakPoint;
import microbat.util.Settings;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

@SuppressWarnings("restriction")
public class ExecutionStatementCollector {
	
	private String[] excludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*"};
	
	public List<BreakPoint> collectBreakPoints(AppJavaClassPath appClassPath){
		List<BreakPoint> pointList = new ArrayList<>();
		
		VMConfiguration vmConfig = new VMConfiguration(appClassPath);
		vmConfig.setLaunchClass(Settings.lanuchClass);
		vmConfig.setWorkingDirectory(appClassPath.getWorkingDirectory());
		
		VirtualMachine vm = new VMStarter(vmConfig).start();
		
		EventRequestManager erm = vm.eventRequestManager(); 
		addClassWatch(erm);
		
		EventQueue queue = vm.eventQueue();
		
		boolean connected = true;
		
		while(connected){
			try {
				EventSet eventSet = queue.remove(1000);
				for(Event event: eventSet){
					if(event instanceof VMStartEvent){
						System.out.println("start collecting execution");
						
						ThreadReference thread = ((VMStartEvent) event).thread();
						addStepWatch(erm, thread);
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
						path = path.replace("\\", ".");
						
						int lineNumber = location.lineNumber();
						
						BreakPoint breakPoint = new BreakPoint(path, lineNumber);
						System.out.println(breakPoint);
						if(!pointList.contains(breakPoint)){
							pointList.add(breakPoint);							
						}
					}
				}
				
				eventSet.resume();
			} catch (InterruptedException e) {
				connected = false;
				e.printStackTrace();
			} catch (AbsentInformationException e) {
				e.printStackTrace();
			}
		}
		
		return pointList;
	}
	
	private void addStepWatch(EventRequestManager erm, ThreadReference threadReference) {
		StepRequest sr = erm.createStepRequest(threadReference,  StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: excludes){
			sr.addClassExclusionFilter(ex);
		}
		sr.enable();
	}
	
	/** add watch requests **/
	private final void addClassWatch(EventRequestManager erm) {
		ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
//		classPrepareRequest.addClassFilter("com.Main");
		classPrepareRequest.setEnabled(true);
	}
}
