package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.Var;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;



public class CharEqualsValueAtom extends Atom {

  private FieldVar variable;

  private char unicodeValue;

  public CharEqualsValueAtom(FieldVar fieldVar, char codepoint) {
    variable = fieldVar;
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

    return atom.variable.equals(variable) && atom.unicodeValue == unicodeValue;
  }

  @Override
  public List<Var> getReferencedVariables() {
    List<Var> referencedVariables = new ArrayList<Var>(1);
    referencedVariables.add(variable);
    return referencedVariables;
  }

  @Override
  public boolean evaluate(Object[] objects) {
    Object object = variable.getValue(objects);

    if (object == null) {
      return false;
    } else {
      return object.toString().equals("" + unicodeValue);
    }
  }

  @Override
  public boolean evaluate(Prestate state) {
    ObjectInfo objectInfo = variable.getObjectInfo(state);
    // ObjectInfoPrimitive charFieldInfo = (ObjectInfoPrimitive) objectInfo;

    char value = TzuYuPrimtiveTypes.getChar(char.class,
        (int) objectInfo.getNumericValue());
    return unicodeValue == value;
  }

}
