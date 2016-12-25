package tzuyu.engine.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * TzuYu Primitive types includes Java primitive types as well as String,
 * enumeration and reference types in Java. This class is responsible for
 * mapping a categorical value to a numerical value and back.
 * 
 * <p>
 * For categorical types(char, String, boolean, enum, Reference), we map their
 * discrete values into a numerical value because SVM only supports double
 * values.
 * 
 * @author Spencer Xiao
 * 
 */
public class TzuYuPrimtiveTypes {
  private static List<Character> charsSeen = new ArrayList<Character>();
  private static List<String> stringsSeen = new ArrayList<String>();

  private static final List<Class<?>> categoricalClasses = 
      new ArrayList<Class<?>>();
  static {
    categoricalClasses.add(boolean.class);
    categoricalClasses.add(Boolean.class);
    categoricalClasses.add(char.class);
    categoricalClasses.add(Character.class);
    categoricalClasses.add(String.class);
  }

  private static final List<Class<?>> numericalClasses = 
      new ArrayList<Class<?>>();

  static {
    numericalClasses.add(double.class);
    numericalClasses.add(Double.class);
    numericalClasses.add(float.class);
    numericalClasses.add(Float.class);
    numericalClasses.add(byte.class);
    numericalClasses.add(Byte.class);
    numericalClasses.add(short.class);
    numericalClasses.add(Short.class);
    numericalClasses.add(long.class);
    numericalClasses.add(Long.class);
    numericalClasses.add(int.class);
    numericalClasses.add(Integer.class);
  }

  private TzuYuPrimtiveTypes() {
  }

  public static boolean isNumerical(Class<?> type) {
    return numericalClasses.indexOf(type) != -1;
  }

  /**
   * The normal Java reference type except the boxed primitives and enumeration
   * types
   * 
   * @param type
   * @return
   */
  public static boolean isReference(Class<?> type) {
    return !(isCategorical(type) || isNumerical(type));
  }

  /**
   * Categorical types include boolean, String, char, enumeration.
   * 
   * @param type
   * @return
   */
  public static boolean isCategorical(Class<?> type) {
    if (type.isEnum()) {
      return true;
    } else {
      return categoricalClasses.indexOf(type) != -1;
    }
  }

  /**
   * Get the numerical value for a TzuYu categorical type variable.
   * 
   * @param obj
   *          the type of the object must be a TzuYu categorical type.
   * @return the corresponding numerical value for the categorical value.
   */
  public static double getNumericalValue(Object obj) {
    // The object could be null only if the type of the object is
    // actually a type in the filter and its value is null we return 0;
    if (obj == null) {
      return 0;
    }

    Class<?> type = obj.getClass();

    String key = obj.toString();

    if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      if (key.equals("false")) {
        return 0;
      } else {
        return 1;
      }
    } else if (type.equals(String.class)) {
      int index = stringsSeen.indexOf(key);
      if (index == -1) {
        stringsSeen.add(key);
        return stringsSeen.size() - 1;
      } else {
        return index;
      }
    } else if (type.equals(char.class) || type.equals(Character.class)) {
      int index = charsSeen.indexOf(obj);
      char value = (Character) obj;
      if (index == -1) {
        charsSeen.add(value);
        return charsSeen.size() - 1;
      } else {
        return index;
      }
    } else if (type.isEnum()) {
      Object[] constants = type.getEnumConstants();
      for (int index = 0; index < constants.length; index++) {
        if (constants[index].equals(obj)) {
          return index;
        }
      }

      throw new IllegalArgumentException("invalid enueration value:" + obj);

    } else if (type.equals(double.class) || type.equals(Double.class)) {
      return Double.valueOf(obj.toString()).doubleValue();
    } else if (type.equals(float.class) || type.equals(Float.class)) {
      return (double) Float.valueOf(obj.toString()).floatValue();
    } else if (type.equals(byte.class) || type.equals(Byte.class)) {
      return (double) Byte.valueOf(obj.toString()).byteValue();
    } else if (type.equals(int.class) || type.equals(Integer.class)) {
      return (double) Integer.valueOf(obj.toString()).intValue();
    } else if (type.equals(short.class) || type.equals(Short.class)) {
      return (double) Short.valueOf(obj.toString()).shortValue();
    } else if (type.equals(long.class) || type.equals(Long.class)) {
      return (double) Long.valueOf(obj.toString()).longValue();
    } else {
      // The instance of a reference type which is treated as primitive by the
      // type filter.
      return (obj == null) ? 0 : 1;
    }
  }

  public static String getString(Class<?> type, int index) {
    if (!type.equals(String.class)) {
      throw new IllegalArgumentException("type must be String");
    }

    return stringsSeen.get(index);
  }

  public static char getChar(Class<?> type, int index) {
    if (!(type.equals(char.class) || type.equals(Character.class))) {
      throw new IllegalArgumentException("type must be char");
    }

    return charsSeen.get(index);
  }

  /**
   * Get the enumeration value for the enumeration type by index.
   * 
   * @param type
   * @param index
   * @return
   */
  public static Object getEnum(Class<?> type, int index) {
    if (!type.isEnum()) {
      throw new IllegalArgumentException("the input type is not enumeration");
    }

    Object[] constValues = type.getEnumConstants();
    if (index < 0 || index > constValues.length - 1) {
      throw new IllegalArgumentException("index out of range");
    }
    return constValues[index];
  }

  public static Object getCategoricalValue(double doubleKey, Class<?> type) {

    if (type.equals(double.class) || type.equals(Double.class)) {
      return doubleKey;
    } else if (type.equals(float.class) || type.equals(Float.class)) {
      return (float) doubleKey;
    } else if (type.equals(byte.class) || type.equals(Byte.class)) {
      return (byte) doubleKey;
    } else if (type.equals(int.class) || type.equals(Integer.class)) {
      return (int) doubleKey;
    } else if (type.equals(short.class) || type.equals(Short.class)) {
      return (short) doubleKey;
    } else if (type.equals(long.class) || type.equals(Long.class)) {
      return (long) doubleKey;
    } else {
      // Must be categorical variables;
      if (!isCategorical(type)) {
        throw new IllegalArgumentException("type is not categorical:" + type);
      }

      if (type.equals(char.class) || type.equals(Boolean.class)) {
        int index = (int) doubleKey;
        if (index == 0) {
          return Boolean.FALSE;
        } else {
          return Boolean.TRUE;
        }
      } else if (type.equals(String.class)) {
        int index = (int) doubleKey;
        return stringsSeen.get(index);
      } else if (type.equals(char.class) || type.equals(Character.class)) {
        int index = (int) doubleKey;
        return charsSeen.get(index);
      } else if (type.isEnum()) {
        int index = (int) doubleKey;
        return type.getEnumConstants()[index];
      } else {
        // Must be reference type;
        int index = (int) doubleKey;
        if (index == 0) {
          return null;
        } else {
          return null;
        }
      }
    }
  }

  public static void clear() {
    charsSeen.clear();
    stringsSeen.clear();
  }

  /**
   * Get the size of the value of a categorical type
   * 
   * @param type
   *          must be a categorical or reference type
   * @return
   */
  public static int getDomainSize(Class<?> type) {
    if (isNumerical(type)) {
      throw new IllegalArgumentException("the input type is not "
          + "categorical:" + type);
    }

    if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return 2;
    } else if (type.equals(char.class) || type.equals(Character.class)) {
      return charsSeen.size();
    } else if (type.equals(String.class)) {
      return stringsSeen.size();
    } else if (type.isEnum()) {
      return type.getEnumConstants().length;
    } else {// must be reference type
      return 2;
    }
  }

  /**
   * Get the numerical value for the <code>index</code> categorical value of the
   * give type.
   * 
   * @param type
   *          must be a categorical type
   * @param index
   *          the index of the categorical values
   * @return
   */
  public static double getNumeicalValue(Class<?> type, int index) {
    if (type.equals(Boolean.class) || type.equals(Boolean.class)) {
      if (index == 0) {
        return 0;
      } else {
        return 1;
      }
    } else if (type.equals(char.class) || type.equals(Character.class)) {
      return index;
    } else if (type.equals(String.class)) {
      return index;
    } else if (type.isEnum()) {
      if (index > type.getEnumConstants().length - 1) {
        throw new IllegalArgumentException("index out of range");
      }
      return index;
    } else {// Must be reference type
      if (index == 0) {
        return 0;
      } else {
        return 1;
      }
    }
  }

  public static boolean isNullReference(Class<?> type, int index) {
    if (!isReference(type)) {
      throw new IllegalArgumentException("non-reference type passed in:" + type);
    }
    
    if (index == 0) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isBooleanTrue(Class<?> type, int index) {
    if (!(type.equals(boolean.class) || type.equals(Boolean.class))) {
      throw new IllegalArgumentException("boolean type expected:" + type);
    }
    return index == 1;
  }
}
