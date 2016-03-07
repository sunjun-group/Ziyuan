package microbat.model.trace;

import java.util.ArrayList;
import java.util.List;

import microbat.model.value.VarValue;
import microbat.recommendation.LabelPathIncompatibleException;


public class PotentialCorrectPattern {
	private List<PathInstance> instanceList = new ArrayList<>();
	
	/**
	 * The first path which forms this pattern. This path must have its start node and end node label the
	 * correctness of their read/written variables.
	 */
	private PathInstance labelInstance;

	
	@SuppressWarnings("unchecked")
	public PotentialCorrectPattern clone(){
		PotentialCorrectPattern clonedPattern = new PotentialCorrectPattern();
		clonedPattern.labelInstance = labelInstance;
		
		ArrayList<PathInstance> list = (ArrayList<PathInstance>)instanceList;
		clonedPattern.instanceList = (List<PathInstance>) list.clone();
		
		return clonedPattern;
	}
	
	public List<PathInstance> getInstanceList() {
		return instanceList;
	}

	public void setInstanceList(List<PathInstance> instanceList) {
		this.instanceList = instanceList;
	}
	
	public void addPathInstance(PathInstance path){
		if(this.instanceList.isEmpty()){
			this.instanceList.add(path);
			
			try {
				checkLabelInstance(path);
				this.labelInstance = path;
			} catch (LabelPathIncompatibleException e) {
				//e.printStackTrace();
			}
		}
		else{
			if(!this.instanceList.contains(path)){
				this.instanceList.add(path);
			}			
		}
	}
	
	private void checkLabelInstance(PathInstance labelPath) throws LabelPathIncompatibleException{
		TraceNode startNode = labelPath.getStartNode();
		TraceNode endNode = labelPath.getEndNode();
		
		boolean isValid = false;
		
		valid:
		for(VarValue readVar: endNode.getReadVariables()){
			for(VarValue writtenVar: startNode.getWrittenVariables()){
				if(readVar.getVarID().equals(writtenVar.getVarID())){
					isValid = true;
					break valid;
				}
			}
		}
		
		if(!isValid){
			String message = "For the label with start node " + startNode + " and end node " + endNode + 
					", there is no variable supporting their dominance relation!" ;
			throw new LabelPathIncompatibleException(message);
		}
		
	}

	public PathInstance getLabelInstance() {
		return labelInstance;
	}

	public void setLabelInstance(PathInstance labelInstance) {
		this.labelInstance = labelInstance;
	}
	
}
