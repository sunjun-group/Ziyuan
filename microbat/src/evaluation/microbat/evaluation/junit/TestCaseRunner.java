package microbat.evaluation.junit;

import java.util.ArrayList;
import java.util.List;

import microbat.codeanalysis.runtime.VMStarter;
import microbat.model.BreakPoint;
import sav.strategies.dto.AppJavaClassPath;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.BooleanType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
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
public class TestCaseRunner {
	
	private static final int FINISH_LINE_NO = 37;

	private String[] stepWatchExcludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*"};
	
	private boolean isPassingTest = false;
	
	public List<BreakPoint> collectBreakPoints(AppJavaClassPath appClassPath){
		
		appendStepWatchExcludes(appClassPath);
		
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
							path = path.replace("\\", ".");
							
							int lineNumber = location.lineNumber();
							
							BreakPoint breakPoint = new BreakPoint(path, lineNumber);
							
							if(isAboutToFinishTestRunner(breakPoint)){
								checkTestCaseSucessfulness(((StepEvent) event).thread(), location);
							}
							
							if(!isInTestRunner(breakPoint) && !pointList.contains(breakPoint)){
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
	
	private void checkTestCaseSucessfulness(ThreadReference thread, Location location) {
		StackFrame currentFrame = null;
		try {
			for (StackFrame frame : thread.frames()) {
				if (frame.location().equals(location)) {
					currentFrame = frame;
				}
			}
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
		}
		
		if(currentFrame != null){
			ReferenceType refTpe = currentFrame.thisObject().referenceType();
			
			for(Field field: refTpe.allFields()){
				if(field.name().equals("successful")){
					Value value = currentFrame.thisObject().getValue(field);
					if(value.type() instanceof BooleanType){
						BooleanValue booleanValue = (BooleanValue)value;
						this.isPassingTest = booleanValue.booleanValue();
					}
				}
			}
		}
	}

	private boolean isAboutToFinishTestRunner(BreakPoint breakPoint) {
		if(isInTestRunner(breakPoint)){
			return breakPoint.getLineNo() == FINISH_LINE_NO;
		}
		return false;
	}

	private boolean isInTestRunner(BreakPoint breakPoint) {
		return breakPoint.getDeclaringCompilationUnitName().equals(TestCaseParser.TEST_RUNNER);
	}

	private void appendStepWatchExcludes(AppJavaClassPath appClassPath) {
		List<String> exList = new ArrayList<>();
		for(String ex: stepWatchExcludes){
			exList.add(ex);
		}
		
		if(appClassPath.getOptionalTestClass() != null){
			exList.add(appClassPath.getOptionalTestClass());			
		}
		
//		exList.add(TestCaseParser.TEST_RUNNER);
		
		this.stepWatchExcludes = exList.toArray(new String[0]);
	}

	private void addStepWatch(EventRequestManager erm, ThreadReference threadReference) {
		StepRequest sr = erm.createStepRequest(threadReference,  StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: stepWatchExcludes){
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
	
	private void addExceptionWatch(EventRequestManager erm) {
		
		ExceptionRequest request = erm.createExceptionRequest(null, true, true);
		request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		for(String ex: stepWatchExcludes){
			request.addClassExclusionFilter(ex);
		}
		request.enable();
	}

	public boolean isPassingTest() {
		return isPassingTest;
	}

	public void setPassingTest(boolean isPassingTest) {
		this.isPassingTest = isPassingTest;
	}
}
