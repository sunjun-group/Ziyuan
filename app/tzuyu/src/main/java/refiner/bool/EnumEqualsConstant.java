package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;

/**
 * We treat enumeration type as categorical type while In Java it is a special
 * reference type.
 * 
 * @author Spencer Xiao
 * 
 */
public class EnumEqualsConstant extends FieldAtom {
	private final Object constantVal;

	public EnumEqualsConstant(FieldVar variable, Object value) {
		super(variable);
		constantVal = value;
	}

	@Override
	public String toString() {
		return variable.toString() + " == " + constantVal.toString();
	}

	@Override
	public int hashCode() {
		return variable.hashCode() * 31 + constantVal.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof EnumEqualsConstant)) {
			return false;
		}

		EnumEqualsConstant obj = (EnumEqualsConstant) o;

		return obj.variable.equals(variable)
				&& obj.constantVal.equals(constantVal);
	}

	public List<Var> getReferencedVariables() {
		List<Var> referencedVars = new ArrayList<Var>(1);
		referencedVars.add(variable);
		return referencedVars;
	}

	public boolean evaluate(Object[] objects) {
		Object object = variable.getValue(objects);
		if (object == null) {
			return false;
		} else {
			return object.equals(constantVal);
		}
	}

	public boolean evaluate(Prestate state) {
		ObjectInfo objectInfo = variable.getObjectInfo(state);
		// ObjectInfoPrimitive enumObjectInfo = (ObjectInfoPrimitive)
		// objectInfo;

		Object value = TzuYuPrimtiveTypes.getEnum(objectInfo.getType()
				.getType(), (int) objectInfo.getNumericValue());

		return constantVal == value;
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

	public Object getConstantVal() {
		return constantVal;
	}
}
