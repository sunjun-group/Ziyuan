package tzuyu.engine.instrument;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.utils.Types;

public class MethodInfo extends InfoObject implements Cloneable {

  static final int INIT_MTH_SIZE = 4096;
  protected static final List<MethodInfo> mthTable = new ArrayList<MethodInfo>(
      INIT_MTH_SIZE);

  private static final int IS_INIT = 0;
  private static final int FIREWALL = 0;
  private static final int IS_CLINIT = 0;

  /** A unique integer assigned to this method */
  protected int globalId = -1;

  /**
   * This is a lazy evaluated mangled name consisting of the name and argument
   * type signature
   */
  protected String uniqueName;
  /** Name of the method */
  protected String name;

  /** Signature of the method */
  protected String signature;

  protected String genericSignature;

  protected ClassInfo ci;

  protected int modifiers;

  protected int attributes;

  protected byte[] argTypes = null;

  /** Number of arguments inclusive of this */
  protected int argSize = -1;

  /** Number of arguments exclusive of this */
  protected int nArgs = -1;

  protected byte returnType = -1;

  /** Number of stack slots for return value */
  protected int retSize = -1;

  public MethodInfo(ClassInfo ci, String name, String signature, int modifiers) {
    this.ci = ci;
    this.name = name;
    this.signature = signature;
    this.uniqueName = generateUniqueName(name, signature);
    this.genericSignature = "";
    this.modifiers = modifiers;

    if (ci != null) {
      if (name.equals("<init>")) {
        attributes |= IS_INIT;
      } else if (name.equals("<clinit>")) {
        this.modifiers |= Modifier.SYNCHRONIZED;
        attributes |= (IS_CLINIT | FIREWALL);
      }

      if (ci.isInterface()) {
        this.modifiers |= Modifier.PUBLIC;
      }

      globalId = mthTable.size();
      mthTable.add(this);
    }
  }

  private String generateUniqueName(String nm, String sig) {
    return nm + sig;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  @Override
  public ClassInfo getClassInfo() {
    return ci;
  }

  public String getReturnTypeName() {
    return Types.getReturnTypeName(signature);
  }

  public boolean isCtor() {
    return name.equals("<init>");
  }

  public int getModifiers() {
    return this.modifiers;
  }

  public boolean isStaic() {
    return (modifiers & Modifier.STATIC) != 0;
  }

  public boolean isPublic() {
    return (modifiers & Modifier.PUBLIC) != 0;
  }

  public boolean isProtected() {
    return (modifiers & Modifier.PROTECTED) != 0;
  }

  public boolean isPrivate() {
    return (modifiers & Modifier.PRIVATE) != 0;
  }

  public boolean isNative() {
    return (modifiers & Modifier.NATIVE) != 0;
  }

  public boolean isAbstract() {
    return (modifiers & Modifier.ABSTRACT) != 0;
  }

  public int getReturnTypeCode() {
    if (returnType < 0) {
      returnType = Types.getReturnBuiltinType(signature);
    }
    return returnType;
  }

  public boolean isReferenceReturnType() {
    int r = getReturnTypeCode();
    return (r == Types.T_REFERENCE || r == Types.T_ARRAY);
  }

  public int getNumberOfArguments() {
    if (nArgs < 0) {
      nArgs = Types.getNumberOfArguments(signature);
    }
    return nArgs;
  }

  public int getNumberOfStackArguments() {
    int n = getNumberOfArguments();
    return isStaic() ? n : (n + 1);
  }

  public String[] getArguementTypeNames() {
    return Types.getArgumentTypeNames(signature);
  }

  public byte[] getArgumentTypes() {
    if (argTypes == null) {
      argTypes = Types.getArgumentType(signature);
      nArgs = argTypes.length;
    }

    return argTypes;
  }

}
