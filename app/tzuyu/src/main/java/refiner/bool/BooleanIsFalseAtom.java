package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.Var;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;



/**
 * The default predicate for the boolean type field.
 * 
 * @author Spencer Xiao
 * 
 */
public class BooleanIsFalseAtom extends Atom {

  private FieldVar variable;

  public BooleanIsFalseAtom(FieldVar var) {
    this.variable = var;
  }

  @Override
  public String toString() {
    return variable.toString() + " == " + "false";
  }

  @Override
  public boolean evaluate(Object[] objects) {
    Object obj = variable.getValue(objects);
    if (obj == null) {
      return false;
    } else {
      boolean val = (Boolean) obj;
      return !val;
    }
  }

  @Override
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

    if (!(o instanceof BooleanIsFalseAtom)) {
      return false;
    }

    BooleanIsFalseAtom obj = (BooleanIsFalseAtom) o;

    return obj.variable.equals(variable);
  }

  @Override
  public boolean evaluate(Prestate state) {
    ObjectInfo objectInfo = variable.getObjectInfo(state);
    // ObjectInfoPrimitive booleanInfo = (ObjectInfoPrimitive) objectInfo;

    return !TzuYuPrimtiveTypes.isBooleanTrue(boolean.class,
        (int) objectInfo.getNumericValue());
  }

}
