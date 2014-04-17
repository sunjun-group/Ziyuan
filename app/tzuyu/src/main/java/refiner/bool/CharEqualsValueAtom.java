package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;

public class CharEqualsValueAtom extends FieldAtom {

	private char unicodeValue;

	public CharEqualsValueAtom(FieldVar fieldVar, char codepoint) {
		super(fieldVar);
		unicodeValue = codepoint;
	}

	@Override
	public String toString() {
		// Show the character representation of the unicode code-point
		return variable.toString() + " == " + unicodeValue;
	}

	@Override
	public int hashCode() {
		return variable.hashCode() * 31 + unicodeValue;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof CharEqualsValueAtom)) {
			return false;
		}

		CharEqualsValueAtom atom = (CharEqualsValueAtom) o;

		return atom.variable.equals(variable)
				&& atom.unicodeValue == unicodeValue;
	}

	public List<Var> getReferencedVariables() {
		List<Var> referencedVariables = new ArrayList<Var>(1);
		referencedVariables.add(variable);
		return referencedVariables;
	}

	public boolean evaluate(Object[] objects) {
		Object object = variable.getValue(objects);

		if (object == null) {
			return false;
		} else {
			return object.toString().equals("" + unicodeValue);
		}
	}

	public boolean evaluate(Prestate state) {
		ObjectInfo objectInfo = variable.getObjectInfo(state);
		// ObjectInfoPrimitive charFieldInfo = (ObjectInfoPrimitive) objectInfo;

		char value = TzuYuPrimtiveTypes.getChar(char.class,
				(int) objectInfo.getNumericValue());
		return unicodeValue == value;
	}

	@Override
	public void accept(BoolVisitor visitor) {
		visitor.visit(this);
	}

	public char getValue() {
		return unicodeValue;
	}
}
