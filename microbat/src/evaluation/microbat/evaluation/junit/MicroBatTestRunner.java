package microbat.evaluation.junit;

import java.net.URLClassLoader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

public class MicroBatTestRunner {
	
	private boolean successful = false;
	
	public MicroBatTestRunner(){
		
	}
	
	public static void main(String[] args){
//		String[] classAndMethod = args[0].split("#");
		String className = args[0];
		String methodName = args[1];
		
		MicroBatTestRunner testRunner = new MicroBatTestRunner();
		testRunner.runTest(className, methodName);
	}
	
	
	public void runTest(String className, String methodName){
		Request request;
		try {
			request = Request.method(Class.forName(className), methodName);
			Result result = new JUnitCore().run(request);
			setSuccessful(result.wasSuccessful());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.currentTimeMillis();
	}
	
	public static boolean isTestSuccessful(String className, String methodName, URLClassLoader classLoader){
		Request request;
		try {
			if(classLoader == null){
				request = Request.method(Class.forName(className), methodName);				
			}
			else{
				request = Request.method(Class.forName(className, false, classLoader), methodName);
			}
			Result result = new JUnitCore().run(request);
			boolean successful = result.wasSuccessful();
			
			return successful;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
