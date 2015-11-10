package icsetlv.trial.model;

import icsetlv.common.dto.BreakpointValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stands for a trace for an execution
 * @author "linyun"
 *
 */
public class Trace {
	private List<TraceNode> exectionList = new ArrayList<>();

	public List<TraceNode> getExectionList() {
		return exectionList;
	}

	public void setExectionList(List<TraceNode> exectionList) {
		this.exectionList = exectionList;
	}
	
	public void addTraceNode(TraceNode node){
		this.exectionList.add(node);
	}
	
}
