package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.Var;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;



/**
 * The predicate for enumerative String variable which is in the form
 * "str == var".
 * 
 * @author Spencer Xiao
 * 
 */
public class StringEqualsValueAtom extends Atom {

  private FieldVar variable;
  private String strValue;

  public StringEqualsValueAtom(FieldVar var, String value) {
    this.variable = var;
    this.strValue = value;
  }

  @Override
  public String toString() {
    return variable.toString() + " == " + strValue;
  }

  @Override
  public boolean evaluate(Object[] objects) {
    Object obj = variable.getValue(objects);

    if (obj == null) {
      return false;
    } else {
      return (obj.toString().equals(strValue));
    }
  }

  @Override
  public List<Var> getReferencedVariables() {
    List<Var> vars = new ArrayList<Var>(1);
    vars.add(variable);
    return vars;
  }

  @Override
  public int hashCode() {
    return variable.hashCode() * 31 + strValue.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof StringEqualsValueAtom)) {
      return false;
    }

    StringEqualsValueAtom obj = (StringEqualsValueAtom) o;

    return obj.variable.equals(variable) && obj.strValue.equals(strValue);
  }

  @Override
  public boolean evaluate(Prestate state) {
    ObjectInfo objectInfo = variable.getObjectInfo(state);

    // ObjectInfoPrimitive stringInfo = (ObjectInfoPrimitive) objectInfo;

    String value = TzuYuPrimtiveTypes.getString(String.class,
        (int) objectInfo.getNumericValue());

    return strValue.equals(value);
  }
}
