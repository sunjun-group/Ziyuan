package tzuyu.engine.model.dfa;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Action;
import tzuyu.engine.utils.StringUtils;

/**
 * A set of the actions that compose the alphabet of the DFA
 * 
 * @author Spencer Xiao
 * 
 */
public class Alphabet<T extends Action> {
	private Action epsilon = Action.epsilon;
	private List<T> actions;

	public Alphabet() {
		actions = new ArrayList<T>();
	}

	public void addSymbol(T action) {
		actions.add(action);
	}

	public int getFullSize() {
		return actions.size() + 1; // all actions plus epsilon action.
	}
	
	public int getActionsSize() {
		return actions.size();
	}
	
	public boolean isEmpty() {
		// alphabet is always contains at least 1 action which is epsilon
		return actions.isEmpty();
	}

	public void clear() {
		actions.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Alphabet) {
			Alphabet<?> sigma = (Alphabet<?>) o;
			return sigma.actions.equals(this.actions);
		} else {
			return false;
		}
	}
	
	/**
	 * all actions excludes epsilon action.
	 */
	public List<T> getActions() {
		return actions;
	}
	
	public Action getEpsilon() {
		return epsilon;
	}

	@Override
	public int hashCode() {
		return this.actions.hashCode();
	}

	@Override
	public String toString() {
		return StringUtils.join(", ", 
				StringUtils.join(actions, ", "));
	}
}
