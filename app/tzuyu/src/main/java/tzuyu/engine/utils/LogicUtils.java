package tzuyu.engine.utils;

public class LogicUtils {

  public static boolean equalsWithNull(Object obj1, Object obj2) {
    if (obj1 == null)
      return obj2 == null;
    if (obj2 == null)
      return false;
    return obj1.equals(obj2);
  }

  public static boolean iff(boolean a, boolean b) {
    return a == b;
  }
  
  public static boolean implies(boolean a, boolean b) {
    return !a || b;
  }
}
