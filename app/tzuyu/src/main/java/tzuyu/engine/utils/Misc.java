package tzuyu.engine.utils;

public class Misc {

  public static boolean compare(Object[] a1, Object[] a2) {
    if (a1 == null && a2 == null) {
      return true;
    }

    if (a1 == null && a2 != null) {
      for (int i = 0; i < a2.length; i++) {
        if (a2[i] != null) {
          return false;
        }
      }
    } else if (a2 == null) {
      for (int i = 0; i < a1.length; i++) {
        if (a1[i] == null) {
          return false;
        }
      }
    } else {
      if (a1.length != a2.length) {
        return false;
      }

      for (int i = 0; i < a1.length; i++) {
        Object o1 = a1[i];
        Object o2 = a2[i];
        if (o1 != null && !o1.equals(o2)) {
          return false;
        } else if (o2 != null) {
          return false;
        }
      }
    }
    return true;
  }
}
