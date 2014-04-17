package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;

/**
 * The default predicate for the non-primitive reference type.
 * 
 * @author Spencer Xiao
 * 
 */
public class ObjectIsNullAtom extends FieldAtom {

	public ObjectIsNullAtom(FieldVar var) {
		super(var);
	}

	@Override
	public String toString() {
		return variable.toString() + " == " + "null";
	}

	public boolean evaluate(Object[] objects) {
		Object obj = variable.getValue(objects);
		return obj == null;
	}

	public List<Var> getReferencedVariables() {
		List<Var> vars = new ArrayList<Var>();
		vars.add(this.variable);
		return vars;
	}

	@Override
	public int hashCode() {
		return this.variable.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof ObjectIsNullAtom)) {
			return false;
		}

		ObjectIsNullAtom obj = (ObjectIsNullAtom) o;

		return this.variable.equals(obj.variable);
	}

	public boolean evaluate(Prestate state) {
		ObjectInfo objectInfo = variable.getObjectInfo(state);
		return objectInfo.isValueNull();
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}
}
