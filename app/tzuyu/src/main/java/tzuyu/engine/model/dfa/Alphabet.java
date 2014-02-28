package tzuyu.engine.model.dfa;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Action;

/**
 * A set of the actions that compose the alphabet of the DFA
 * 
 * @author Spencer Xiao
 * 
 */
public class Alphabet {

	private List<Action> actions;

	public Alphabet() {
		actions = new ArrayList<Action>();
		actions.add(Action.epsilon);
	}

	public Action getAction(int index) {
		if (index < 0 && index > actions.size() - 1)
			return null;
		return actions.get(index);
	}

	public int addSymbol(Action action) {
		actions.add(action);
		return actions.size() - 1;
	}

	public int getSize() {
		return actions.size();
	}

	public int getElpsilonActionIndex() {
		return 0;
	}

	public void clear() {
		actions.clear();
		actions.add(Action.epsilon);
	}

	public void removeSymbol(int index) {
		this.actions.remove(index);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof Alphabet) {
			Alphabet sigma = (Alphabet) o;
			return sigma.actions.equals(this.actions);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.actions.hashCode();
	}

	@Override
	public String toString() {
		return actions.toString();
	}
}
