package icsetlv.trial.model;

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
	
	
}
