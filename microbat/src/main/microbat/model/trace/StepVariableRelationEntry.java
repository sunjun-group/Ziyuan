package microbat.model.trace;

import java.util.ArrayList;
import java.util.List;

import microbat.model.variable.Variable;

public class StepVariableRelationEntry {
	private String varID;
	private Variable variable;
	
	/**
	 * Note that a consumer's producer will be its nearest producer on certain variable.
	 */
	private List<TraceNode> producers = new ArrayList<>();
	private List<TraceNode> consumer = new ArrayList<>();
	
	public StepVariableRelationEntry(String varID, Variable variable) {
		super();
		this.varID = varID;
		this.variable = variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((varID == null) ? 0 : varID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StepVariableRelationEntry other = (StepVariableRelationEntry) obj;
		if (varID == null) {
			if (other.varID != null)
				return false;
		} else if (!varID.equals(other.varID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StepVariableRelationEntry [varID=" + varID + ", variable="
				+ variable + "]";
	}

	public String getVarID() {
		return varID;
	}

	public void setVarID(String varID) {
		this.varID = varID;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public List<TraceNode> getProducers() {
		return producers;
	}

	public void setProducers(List<TraceNode> producers) {
		this.producers = producers;
	}

	public List<TraceNode> getConsumer() {
		return consumer;
	}

	public void setConsumer(List<TraceNode> consumer) {
		this.consumer = consumer;
	}

	public void addConsumer(TraceNode node) {
		if(!consumer.contains(node)){
			consumer.add(node);
		}
	}
	
	public void addProducer(TraceNode node) {
		if(!producers.contains(node)){
			producers.add(node);
		}
	}
	
	
}
