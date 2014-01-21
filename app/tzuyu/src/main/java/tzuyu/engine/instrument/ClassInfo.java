package tzuyu.engine.instrument;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClassInfo extends InfoObject implements Iterable<MethodInfo> {

  static ClassInfo objectClassInfo;
  static ClassInfo classClassInfo;
  static ClassInfo stringClassInfo;
  static ClassInfo refClassInfo;
  static ClassInfo enumClassInfo;

  protected String name;

  protected String signature;

  protected String genericSignature;

  protected boolean isClass = true;
  protected boolean isWeakReference = false;
  protected boolean isArray = false;
  protected boolean isEnum = false;
  protected boolean isReferenceArray = false;
  protected boolean isAbstract = false;
  protected boolean isBuiltin = false;

  int modifiers;
  protected MethodInfo finalizer = null;

  protected int elementInfoAttrs = 0;

  protected Map<String, MethodInfo> methods;

  /**
   * Instance fields
   */
  protected FieldInfo[] iFields;
  protected int instanceDataSize;
  protected int nInstanceFields;
  protected int instanceDataOffset;

  /**
   * Static fields
   */
  protected FieldInfo[] sFields;

  protected int staticDataSize;

  protected ClassInfo superClass;

  protected String enclosingClassName;
  protected String[] innerClassNames;

  protected Set<String> interfaceNames;

  protected Set<String> allInterfaces;

  protected String packageName;
  protected int uniqueId;

  protected Object attr;

  private ClassInfo() {

  }

  public static ClassInfo getResolvedClassInfo(String type) {
    return new ClassInfo();
  }

  public String getName() {
    return name;
  }

  public String getType() {
    // TODO Auto-generated method stub
    return null;
  }

  public static boolean isStringClassInfo(ClassInfo ci) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean hasRefField(int objRef, Fields fields) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isInterface() {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isArray() {
    return isArray;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public Iterator<MethodInfo> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ClassInfo getClassInfo() {
    return this;
  }

}
