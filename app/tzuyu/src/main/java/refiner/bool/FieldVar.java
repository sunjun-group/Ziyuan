package refiner.bool;

import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.ArtFieldInfo;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.StatementKind;

/**
 * This is the real variable referenced in the TzuYu's boolean formula. which
 * represents a field defined in the target class or the parameter types.
 * 
 * @author Spencer Xiao
 * 
 */
public class FieldVar implements Var {

	/**
	 * The statement on which the parameter variable is defined.
	 */
	private StatementKind statement;
	/**
	 * The index of the parameter in the above <code>statement</code>.
	 */
	private int argIndex;

	/**
	 * The actual field defined in the parameter
	 */
	private ArtFieldInfo field;

	private FieldVar(StatementKind stmt, int argIdx, ArtFieldInfo fi) {
		this.statement = stmt;
		this.argIndex = argIdx;
		this.field = fi;
	}

	public static FieldVar getVar(StatementKind stmt, int argIdx,
			ArtFieldInfo fi) {
		return new FieldVar(stmt, argIdx, fi);
	}

	public Object getValue(Object[] objs) {
		// Check whether the type matches
		List<Class<?>> inputTypes = statement.getInputTypes();
		if (inputTypes.size() != objs.length) {
			return null;
		}

		return field.getObject(objs[argIndex]);
	}

	public ObjectInfo getObjectInfo(Prestate state) {
		ObjectInfo parameterInfo = state.getParameter(argIndex);
		return field.getObjectInfo(parameterInfo);
	}

	@Override
	public String toString() {
		String type = statement.getInputTypes().get(argIndex).getSimpleName();
		return type + "." + field.getFullName() + "("
				+ Integer.toString(argIndex) + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof FieldVar)) {
			return false;
		}

		FieldVar obj = (FieldVar) o;

		return statement.equals(obj.statement) && argIndex == obj.argIndex
				&& field.equals(obj.field);
	}

	@Override
	public int hashCode() {
		return statement.hashCode() * 31 + field.hashCode() * 17 + argIndex;
	}

	public ArtFieldInfo getReferencedFieldInfo() {
		return field;
	}

	public Class<?> getType() {
		return field.getClassInfo().getType();
	}

	public StatementKind getStatement() {
		return statement;
	}

	public boolean isReceiver() {
		if (argIndex >= 1) {
			return false;
		} else {
			return statement.hasReceiverParameter();
		}
	}

	public int getArgIndex() {
		return argIndex;
	}

	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

	public ArtFieldInfo getField() {
		return field;
	}

	public String getName() {
		return field.getFullName();
	}
}
