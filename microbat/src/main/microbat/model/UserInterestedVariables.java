package microbat.model;

import java.util.ArrayList;
import java.util.List;

public class UserInterestedVariables {
	private List<String> varIDs = new ArrayList<>();
	
	public boolean contains(String varID){
		return this.varIDs.contains(varID);
	}
	
	public void add(String varID){
		this.varIDs.add(varID);
	}

	public void remove(String varID) {
		this.varIDs.remove(varID);
	}
}
