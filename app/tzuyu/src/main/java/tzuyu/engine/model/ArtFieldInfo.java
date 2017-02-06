package tzuyu.engine.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.ObjectInfo;



/**
 * An artificial field which represents a real field or superficial field such
 * as the array element field and the artificial field which can be used to get
 * an instance of a top level class.
 * 
 * @author Spencer Xiao
 * 
 */
public class ArtFieldInfo {

  /**
   * This field represents the java field whose type class directly contains the
   * field represented by this artificial field. If the artificial field which
   * represents an instance of the top level class has no parent.
   */
  private ArtFieldInfo parent;

  /**
   * This field payload may be null for array elements and reference object of
   * an top level class
   */
  private FieldInfo fieldPayload;
  private ClassInfo classPayload;

  public ArtFieldInfo(ArtFieldInfo father, FieldInfo fieldData,
      ClassInfo classData) {
    this.parent = father;
    this.fieldPayload = fieldData;
    this.classPayload = classData;
  }

  private ArtFieldInfo getParent() {
    return parent;
  }

  /**
   * Whether this artificial field represents an object of a top level class,
   * because the object of the top level class does not belongs to any class. So
   * an object is top level class instance if and only if its parent is null and
   * the field is null.
   * 
   * @return
   */
  public boolean isTopLevelObject() {
    return parent == null && fieldPayload == null;
  }

  /**
   * Whether this artificial field represents an array element field. The
   * artificial field represents an array element if and only if the its parent
   * node is not null and the field payload is null.
   * 
   * @return
   */
  public boolean isArrayElement() {
    return parent != null && fieldPayload == null;
  }

  public final FieldInfo getField() {
    return fieldPayload;
  }

  public final ClassInfo getClassInfo() {
    return classPayload;
  }

  public String getFullName() {
    List<ArtFieldInfo> reversePath = new ArrayList<ArtFieldInfo>();
    ArtFieldInfo current = this;
    while (current != null) {
      reversePath.add(current);
      current = current.getParent();
    }

    StringBuilder sb = new StringBuilder();
    for (int index = reversePath.size() - 2; index >= 1; index--) {
      String name = reversePath.get(index).getSimpleName();
      sb.append(name);
      sb.append(".");
    }
    sb.append(getSimpleName());

    return sb.toString();
  }

  public String getSimpleName() {
    if (isArrayElement()) {
      return "[index]";
    } else if (isTopLevelObject()) {
      return "";
    } else {
      return fieldPayload.getName();
    }
  }

  public ObjectInfo getObjectInfo(ObjectInfo topLevelObjectInfo) {
    List<ArtFieldInfo> reversePath = new ArrayList<ArtFieldInfo>();
    ArtFieldInfo current = this;
    while (current != null) {
      reversePath.add(current);
      current = current.getParent();
    }

    ObjectInfo currentObjectInfo = topLevelObjectInfo;
    int currentLevel = 0;
    for (int index = reversePath.size() - 2; index >= 0; index--) {
      List<ObjectInfo> children = currentObjectInfo.getValues(currentLevel + 1);
      List<ArtFieldInfo> fields = currentObjectInfo.getType().getFieldsOnLevel(
          1);
      for (int i = 0; i < fields.size(); i++) {
        if (fields.get(i).equals(reversePath.get(index))) {
          currentObjectInfo = children.get(i);
          currentLevel++;
          break;
        }
      }
    }
    return currentObjectInfo;
  }

  public Object getObject(Object topLevelObject) {
    List<ArtFieldInfo> reversePath = new ArrayList<ArtFieldInfo>();
    ArtFieldInfo current = this;
    while (current != null) {
      reversePath.add(current);
      current = current.getParent();
    }

    Object currentObj = topLevelObject;
    for (int index = reversePath.size() - 2; index >= 0; index--) {
      try {
        currentObj = reversePath.get(index).get(currentObj);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }

    return currentObj;
  }

  private Object get(Object parent) {
    if (parent == null) {
      return null;
    }
    
    if (isArrayElement()) {
      return Array.get(parent, 0);
    } else if (isTopLevelObject()) {
      return parent;
    } else {
      return this.fieldPayload.getValue(parent);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof ArtFieldInfo)) {
      return false;
    }

    ArtFieldInfo fieldObject = (ArtFieldInfo) object;
    return fieldObject.fieldPayload == fieldPayload
        && fieldObject.classPayload.equals(classPayload);
  }

  @Override
  public int hashCode() {
    int hashCode = (fieldPayload == null) ? 0 : fieldPayload.hashCode();
    return hashCode * 31 + this.classPayload.hashCode();
  }
}
