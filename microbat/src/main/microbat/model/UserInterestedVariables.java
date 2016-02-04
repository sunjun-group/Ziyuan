package microbat.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserInterestedVariables {
	private Map<String, Double> varIDs = new HashMap<>();
	
	public boolean contains(String varID){
		return this.varIDs.keySet().contains(varID);
	}
	
	public void add(String varID, double checkTime){
		this.varIDs.put(varID, checkTime);
	}

	public void remove(String varID) {
		this.varIDs.remove(varID);
	}
	
	public Set<String> getVarIDs(){
		return this.varIDs.keySet();
	}
	
	public double getVarScore(String varID){
		return varIDs.get(varID);
	}
	
	public String getNewestVarID(){
		String newestID = null;
		for(String varID: this.varIDs.keySet()){
			if(newestID == null){
				newestID = varID;
			}
			else{
				if(this.varIDs.get(newestID) < this.varIDs.get(varID)){
					newestID = varID;
				}
			}
		}
		return newestID;
	}
}
