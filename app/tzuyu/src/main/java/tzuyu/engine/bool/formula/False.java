package tzuyu.engine.bool.formula;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

public class False extends Atom {

	private final List<Var> variables = new ArrayList<Var>();

	private static final False instance = new False();

	private False() {
	}

	public static False getInstance() {
		return instance;
	}

	public List<Var> getReferencedVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return "false";
	}

	public boolean evaluate(Object[] objects) {
		return false;
	}

	@Override
	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		return this;
	}

	@Override
	public int hashCode() {
		return 3;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		return o instanceof False;
	}

	public boolean evaluate(Prestate state) {
		return false;
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

}
