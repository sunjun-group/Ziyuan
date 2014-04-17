package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

public class True extends Atom {

	private final List<Var> variables = new ArrayList<Var>();

	private static final True instance = new True();

	private True() {
	}

	public static True getInstance() {
		return instance;
	}

	public List<Var> getReferencedVariables() {
		return variables;
	}

	@Override
	public String toString() {
		return "true";
	}

	public boolean evaluate(Object[] objects) {
		return true;
	}

	@Override
	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		return this;
	}

	@Override
	public int hashCode() {
		return 11;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		return o instanceof True;
	}

	public boolean evaluate(Prestate state) {
		return true;
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

}
