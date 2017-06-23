package tzuyu.engine.model;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzClass;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.exception.TzRuntimeException;

public class Prestate {
	private List<ObjectInfo> valuation;

	private Prestate() {
		valuation = new ArrayList<ObjectInfo>();
	}

	private void addValue(ObjectInfo val) {
		this.valuation.add(val);
	}

	public final ObjectInfo getParameter(int index) {
		return valuation.get(index);
	}

	/**
	 * Get all the objectInfos defined on the specified level.
	 * 
	 * @param level
	 * @param filter
	 * @return
	 */
	public List<ObjectInfo> getValuesOnLevel(int level, List<Boolean> filter) {
		List<ObjectInfo> vals = new ArrayList<ObjectInfo>();
		for (int index = 0; index < valuation.size(); index++) {
			if (filter.get(index)) {
				vals.addAll(valuation.get(index).getValues(level));
			}
		}
		return vals;
	}

	/**
	 * Get all the objectInfos defined above the specified level (inclusive).
	 * 
	 * @param level
	 * @param filter
	 * @return
	 */
	public List<ObjectInfo> getValuesAboveLevel(int level, List<Boolean> filter) {
		List<ObjectInfo> vals = new ArrayList<ObjectInfo>();
		for (int index = 0; index <= level; index++) {
			vals.addAll(getValuesOnLevel(index, filter));
		}

		return vals;
	}

	public static Prestate log(List<Variable> vars, List<Object> values,
			TzClass project) {
		if (vars.size() != values.size()) {
			throw new TzRuntimeException("instrument for incompatable objects.");
		}

		Prestate prestate = new Prestate();
		for (int index = 0; index < vars.size(); index++) {
			Class<?> type = vars.get(index).getType();

			ClassInfo param = project.getClassInfo(type);

			ObjectInfo val = param.clone(values.get(index),
					project.getConfiguration());
			prestate.addValue(val);
		}

		return prestate;
	}
}
