package analyzer;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ObjectInfo;


public class ObjectInfoReference implements ObjectInfo {

  private ClassInfo type;
  private boolean isValueNull;
  private int level;
  private List<ObjectInfo> attributes;

  public ObjectInfoReference(ClassInfo typeInfo, int level,
      List<ObjectInfo> values, boolean nullValue) {
    this.type = typeInfo;
    this.level = level;
    this.attributes = values;
    this.isValueNull = nullValue;
  }

  public double getNumericValue() {
    return isValueNull ? 0 : 1;
  }

  public List<ObjectInfo> getValues(int level) {
    if (level < this.level) {
      return new ArrayList<ObjectInfo>();
    } else if (level == this.level) {
      List<ObjectInfo> objs = new ArrayList<ObjectInfo>();
      objs.add(this);
      return objs;
    } else {
      List<ObjectInfo> objs = new ArrayList<ObjectInfo>();
      for (ObjectInfo obj : attributes) {
        objs.addAll(obj.getValues(level));
      }
      return objs;
    }
  }

  public boolean isValueNull() {
    return isValueNull;
  }

  public ClassInfo getType() {
    return type;
  }

}
