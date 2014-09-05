package javacocoWrapper;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;



public class RequestExecution implements Runnable{

	private Request request;
	private Boolean isPassed;
	
	public void run() {
		JUnitCore core = new JUnitCore();
		Result result = core.run(request);
		
		this.isPassed = (result.getFailureCount() <= 0);
	}
	
	public Boolean getResult(){
		return isPassed;
	}
	
	public void setRequest(Request request){
		this.request = request;
	}

}
