package tzuyu.engine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tzuyu.engine.utils.ReflectionUtils;

public class Analytics {

  private static Map<Class<?>, ClassInfo> typeMap;
  private static Class<?> target;

  private Analytics() {
  }

//  public static void initialize(Class<?> targetClass, Map<Class<?>, 
//      ClassInfo> map) {
//    typeMap = map;
//    target = targetClass;
//  }

//  public static final ClassInfo getClassInfo(Class<?> type) {
//    return typeMap.get(type);
//  }
//
//  public static final ClassInfo getTargetClassInfo() {
//    return typeMap.get(target);
//  }
//
//  public static final Class<?> getTarget() {
//    return target;
//  }
//
//  /**
//   * Get a list of accessible subclasses(exclusive interfaces) of the specified
//   * class or interface from the referenced class list.
//   * 
//   * @param superClass
//   * @return
//   */
//  public static List<ClassInfo> getAccessibleClasses(Class<?> superClass) {
//    List<ClassInfo> subClasses = new ArrayList<ClassInfo>();
//    Set<Class<?>> classes = typeMap.keySet();
//
//    for (Class<?> subClass : classes) {
//      if (ReflectionUtils.canBeUsedAs(subClass, superClass)
//          && ReflectionUtils.isVisible(subClass) && !subClass.isInterface()) {
//        subClasses.add(typeMap.get(subClass));
//      }
//    }
//
//    return subClasses;
//  }
}
