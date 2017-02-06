package tzuyu.core.main;

import java.util.List;

public class AssertionGenerationParams extends FaultLocateParams {
	
	public List<String> listOfMethods;
	
	public void setListOfMethods(List<String> methodNames) {
		this.listOfMethods = methodNames;
	}

	public List<String> getListOfMethods() {
		return listOfMethods;
	}
	
}
