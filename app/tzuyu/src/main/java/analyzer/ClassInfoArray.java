package analyzer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.ArtFieldInfo;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.FieldInfo;
import tzuyu.engine.model.MethodInfo;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.utils.Options;
import tzuyu.engine.utils.ReflectionUtils;


public class ClassInfoArray extends ClassInfo {

  private Class<?> type;
  private ClassInfo baseType;
  private FieldInfo arrayLengthField;

  public ClassInfoArray(Class<?> name, ClassInfo baseType) {
    this.type = name;
    this.baseType = baseType;
    this.arrayLengthField = new FieldInfoArrayLength(this);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ClassInfoArray)) {
      return false;
    }
    
    ClassInfoArray cia = (ClassInfoArray) o;
    
    return this.type.equals(cia.type);
  }
  
  @Override
  public int hashCode() {
    return this.type.hashCode();
  }
  
  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public FieldInfo[] getDeclaredFields() {
    return new FieldInfo[] { arrayLengthField };
  }

  @Override
  public FieldInfo getDeclaredField(String name) {
    return null;
  }

  @Override
  public MethodInfo[] getDeclaredMethods() {
    return null;
  }

  @Override
  public ClassInfo[] getInterfaces() {
    return null;
  }

  @Override
  public ClassInfo getSuperClass() {
    return null;
  }

  @Override
  public ClassInfo[] getInnerClasses() {
    return new ClassInfo[0];
  }

  @Override
  public ConstructorInfo[] getConstructors() {
    return new ConstructorInfo[0];
  }

  @Override
  public ClassInfo getComponentType() {
    return baseType;
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean isInterface() {
    return false;
  }

  @Override
  public ObjectInfo cloneMockup(Object object, int level) {
    if (level > Options.classMaxDepth()) {
      return null;
    }

    if (object == null) {
      // for null array we set the length to be 1 in order to
      // include one of its element object.
      List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
      ;
      ObjectInfo element = baseType.cloneMockup(null, level + 1);
      if (element != null) {
        elements.add(element);
      }

      return new ObjectInfoArray(this, level, elements, true);
    }

    if (!ReflectionUtils.canBeUsedAs(object.getClass(), type)) {
      throw new TzuYuException("try to clone incompatable object");
    } else {
      int length = Array.getLength(object);
      // To eradicate the out of memory problem when the target array object
      // contains too many elements.
      length = Math.min(length, Options.arrayMaxLength());
      List<ObjectInfo> elements = new ArrayList<ObjectInfo>();
      for (int index = 0; index < length; index++) {
        Object elementObject = Array.get(object, index);
        ObjectInfo element = baseType.cloneMockup(elementObject, level + 1);
        if (element != null) {
          elements.add(element);
        }
      }
      return new ObjectInfoArray(this, level, elements, false);
    }
  }

  @Override
  public List<ArtFieldInfo> getFieldsOnLevel(
      ArtFieldInfo parent, FieldInfo field, int level) {

    ArtFieldInfo thisField = new ArtFieldInfo(parent, field, this);

    if (level == 0) {
      List<ArtFieldInfo> fields = new ArrayList<ArtFieldInfo>();
      fields.add(thisField);
      return fields;
    } else if (level == 1) {
      List<ArtFieldInfo> fields = new ArrayList<ArtFieldInfo>();
      fields.add(new ArtFieldInfo(thisField, new FieldInfoArrayLength(this), 
          new ClassInfoPrimitive(int.class)));
      return fields;
    } else {
      return baseType.getFieldsOnLevel(thisField, null, level - 1);
    }
  }

}
