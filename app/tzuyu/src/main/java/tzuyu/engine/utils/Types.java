package tzuyu.engine.utils;

import java.util.ArrayList;

public class Types {

  public static final byte T_NONE = 0;
  public static final byte T_BOOLEAN = 4;
  public static final byte T_BYTE = 8;
  public static final byte T_CHAR = 5;
  public static final byte T_SHORT = 9;
  public static final byte T_INT = 10;
  public static final byte T_LONG = 11;
  public static final byte T_FLOAT = 6;
  public static final byte T_DOUBLE = 7;
  public static final byte T_REFERENCE = 14;
  public static final byte T_VOID = 12;
  public static final byte T_ARRAY = 13;

  public static double intsToDouble(int low, int high) {
    return longToDouble(intsToLong(low, high));
  }

  private static double longToDouble(long l) {
    return Double.longBitsToDouble(l);
  }

  public static int hiDouble(double d) {
    return hiLong(Double.doubleToLongBits(d));
  }

  public static int loDouble(double d) {
    return loLong(Double.doubleToLongBits(d));
  }

  public static float intToFloat(int i) {
    return Float.intBitsToFloat(i);
  }

  public static int floatToInt(float f) {
    return Float.floatToIntBits(f);
  }

  public static int loLong(long l) {
    return (int) (l & 0xFFFFFFFFL);
  }

  public static int hiLong(long l) {

    return (int) (l >> 32);
  }

  public static long intsToLong(int low, int high) {
    return ((long) (high << 32)) | (low & 0xFFFFFFFFL);
  }

  public static String getTypeName(String signature) {
    int len = signature.length();
    char c = signature.charAt(0);
    if (len == 1) {
      switch (c) {
      case 'B':
        return "byte";
      case 'C':
        return "char";
      case 'D':
        return "double";
      case 'F':
        return "float";
      case 'I':
        return "int";
      case 'J':
        return "long";
      case 'S':
        return "short";
      case 'V':
        return "void";
      case 'Z':
        return "boolean";
      }
    }
    if (c == '[') {
      return getTypeName(signature.substring(1)) + "[]";
    }

    int len1 = len - 1;
    if (signature.charAt(len1) == ';') {
      return signature.substring(1, len1).replace('/', '.');
    }
    throw new RuntimeException("invalid type string: " + signature);
  }

  public static byte getTypeCode(String signature) {
    char c = signature.charAt(0);
    switch (c) {
    case 'B':
      return T_BYTE;
    case 'C':
      return T_CHAR;
    case 'D':
      return T_DOUBLE;
    case 'F':
      return T_FLOAT;
    case 'I':
      return T_INT;
    case 'J':
      return T_LONG;
    case 'L':
      return T_REFERENCE;
    case 'S':
      return T_SHORT;
    case 'V':
      return T_VOID;
    case 'Z':
      return T_BOOLEAN;
    case '[':
      return T_ARRAY;
    }
    throw new RuntimeException("unkown type code: " + signature);
  }

  public static String getReturnTypeName(String signature) {
    int i = signature.indexOf(')');
    return getTypeName(signature.substring(i + 1));
  }

  public static String getReturnTypeSignature(String signature) {
    int i = signature.indexOf(')');
    return signature.substring(i + 1);
  }

  public static int getNumberOfArguments(String signature) {
    int i = 1;
    int n = 0;
    int sigLen = signature.length();

    for (; i < sigLen; n++) {
      switch (signature.charAt(i)) {
      case ')':
        return n;
      case 'L':
        do {
          i++;
        } while (signature.charAt(i) != ';');
        break;
      case '[':
        do {
          i++;
        } while (signature.charAt(i) == '[');

        if (signature.charAt(i) == 'L') {
          do {
            i++;
          } while (signature.charAt(i) != ';');
        }
        break;
      default:
      }
      i++;
    }
    return n;
  }

  public static byte getReturnBuiltinType(String signature) {
    int i = signature.indexOf(')');
    return getBuiltinTypeFromSignature(signature.substring(i + 1));
  }

  private static byte getBuiltinTypeFromSignature(String signature) {
    switch (signature.charAt(0)) {
    case 'B':
      return T_BYTE;
    case 'C':
      return T_CHAR;
    case 'D':
      return T_DOUBLE;
    case 'F':
      return T_FLOAT;
    case 'I':
      return T_INT;
    case 'J':
      return T_LONG;
    case 'L':
      return T_REFERENCE;
    case 'S':
      return T_SHORT;
    case 'V':
      return T_VOID;
    case 'Z':
      return T_BOOLEAN;
    case '[':
      return T_ARRAY;
    }
    throw new RuntimeException("invalid type string: " + signature);
  }

  public static String[] getArgumentTypeNames(String signature) {
    int len = signature.length();
    if (len > 1 && signature.charAt(1) == ')') {
      return new String[0]; // no argument shortcut
    }

    ArrayList<String> a = new ArrayList<String>();
    for (int i = 1; signature.charAt(i) != ')';) {
      int end = i + getTypeLength(signature, i);
      String arg = signature.substring(i, end);
      i = end;
      a.add(getTypeName(arg));
    }
    String[] typeNames = new String[a.size()];
    a.toArray(typeNames);
    return typeNames;
  }

  private static int getTypeLength(String signature, int index) {
    switch (signature.charAt(index)) {
    case 'B':
    case 'C':
    case 'D':
    case 'F':
    case 'I':
    case 'J':
    case 'S':
    case 'V':
    case 'Z':
      return 1;
    case '[':
      return 1 + getTypeLength(signature, index + 1);
    case 'L':
      int semicolon = signature.indexOf(';', index);
      if (semicolon == -1) {
        throw new RuntimeException("invalid type signature: " + signature);
      }
      return semicolon - index + 1;
    }
    throw new RuntimeException("invalid type signature");
  }

  public static byte[] getArgumentType(String signature) {
    int i, j;
    int nArgs = 0;
    for (i = 1; signature.charAt(i) != ')'; nArgs++) {
      i += getTypeLength(signature, i);
    }
    byte[] args = new byte[nArgs];

    for (i = 1, j = 0; j < nArgs; j++) {
      int end = i + getTypeLength(signature, i);
      String arg = signature.substring(i, end);
      i = end;
      args[j] = getBuiltinTypeFromSignature(arg);
    }
    return args;
  }

  public static boolean intToBoolean(int i) {
    return i != 0;
  }

  public static boolean isReference(String type) {
    int t = getBuiltinTypeFromSignature(type);
    return (t == T_ARRAY || t == T_REFERENCE);
  }

  public static boolean isReferenceSignature(String signature) {
    return signature.charAt(signature.length() - 1) == ';';

  }

  public static String getArrayElementType(String type) {
    if (type.charAt(0) != '[') {
      throw new RuntimeException("not an array type:" + type);
    }
    return type.substring(1);
  }

  public static int getArgumentsSize(String sig) {
    int n = 0;
    for (int i = 1; sig.charAt(i) != ')'; i++) {
      switch (sig.charAt(i)) {
      case 'L':
        do {
          i++;
        } while (sig.charAt(i) != ';');
        n++;
        break;
      case '[':
        do {
          i++;
        } while (sig.charAt(i) != '[');
        
        if (sig.charAt(i) == 'L') {
          do {
            i++;
          } while (sig.charAt(i) != ';');
        }
        n++;
        break;
      case 'J':
      case 'D':
        n += 2;
        break;
      default:
        n++;
      }
    }
    return n;
  }

}
