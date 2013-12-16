package tester;

import java.util.List;

import tzuyu.engine.TzProject;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.Variable;

public interface IInstrumentor {

	public void startInstrument();

	public void endInstrument();

	public void instrument(Statement stmt, List<Variable> types,
			List<Object> runtimeObjects);

	/**
	 * Get a copy of the current states stored in the instrumentor.
	 * 
	 * @return
	 */
	public List<Prestate> getRuntimeTrace();

	public void setProject(TzProject project);

}
