package tzuyu.engine.instrument;

import java.util.ArrayList;
import java.util.List;

import tester.IInstrumentor;
import tzuyu.engine.TzClass;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.Variable;
import tzuyu.engine.model.exception.TzRuntimeException;

public class TzuYuInstrumentor implements IInstrumentor {

	private boolean isInProgress;

	private List<Prestate> prestates;
	private TzClass project;

	public TzuYuInstrumentor() {
		isInProgress = false;
		prestates = new ArrayList<Prestate>();
	}

	public List<Prestate> getRuntimeTrace() {
		List<Prestate> retCopy = new ArrayList<Prestate>();
		retCopy.addAll(prestates);

		// NOTE: we need a copy of the states, don't directly return the
		// prestates
		return retCopy;
	}

	public void instrument(Statement stmt, List<Variable> vars,
			List<Object> runtimeObjects) {
		Prestate state = Prestate.log(vars, runtimeObjects, project);
		prestates.add(state);
	}

	public void startInstrument() {
		if (isInProgress) {
			throw new TzRuntimeException(
					"the instrumentation is alreay in progress");
		} else {
			isInProgress = true;
			prestates.clear();
		}
	}

	public void endInstrument() {
		if (!isInProgress) {
			throw new TzRuntimeException("the instrumentation is finished");
		} else {
			isInProgress = false;
		}

	}

	public void setProject(TzClass project) {
		this.project = project;
	}
}
