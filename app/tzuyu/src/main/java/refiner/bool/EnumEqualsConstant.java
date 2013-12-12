package refiner.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.bool.Atom;
import tzuyu.engine.bool.Var;
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
public class EnumEqualsConstant extends Atom {
  private final FieldVar enumVar;
  private final Object constantVal;

  public EnumEqualsConstant(FieldVar variable, Object value) {
    enumVar = variable;
    constantVal = value;
  }

  @Override
  public String toString() {
    return enumVar.toString() + " == " + constantVal.toString();
  }

  @Override
  public int hashCode() {
    return enumVar.hashCode() * 31 + constantVal.hashCode();
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

    return obj.enumVar.equals(enumVar) && obj.constantVal.equals(constantVal);
  }

  @Override
  public List<Var> getReferencedVariables() {
    List<Var> referencedVars = new ArrayList<Var>(1);
    referencedVars.add(enumVar);
    return referencedVars;
  }

  @Override
  public boolean evaluate(Object[] objects) {
    Object object = enumVar.getValue(objects);
    if (object == null) {
      return false;
    } else {
      return object.equals(constantVal);
    }
  }

  @Override
  public boolean evaluate(Prestate state) {
    ObjectInfo objectInfo = enumVar.getObjectInfo(state);
    // ObjectInfoPrimitive enumObjectInfo = (ObjectInfoPrimitive) objectInfo;

    Object value = TzuYuPrimtiveTypes.getEnum(objectInfo.getType().getType(),
        (int) objectInfo.getNumericValue());

    return constantVal == value;
  }

}
