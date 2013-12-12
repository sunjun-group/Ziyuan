package analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.utils.PrimitiveTypes;

public class ClassAnalyzer {

  public static void main(String[] args) throws ClassNotFoundException {
    Class<?> targetClassName = Class.forName("java.security.Signature");
    List<String> methods = new ArrayList<String>();
    methods.add("initVerify");
    methods.add("initSign");
    methods.add("update");
    methods.add("verify");
    methods.add("sign");
    ClassAnalyzer ca = new ClassAnalyzer(targetClassName, methods);
    ca.analysis();
  }

  private Class<?> target;

  List<String> methods;

  Set<Class<?>> referencedTypes;

  /**
   * We only analysis the classes referenced in by the target class and the
   * types referenced in the specified methods. If the methodNames is empty, 
   * all public methods defined in the target class will be analyzed.
   * 
   * @param targetClassName
   * @param methodNames
   * @throws ClassNotFoundException
   */
  public ClassAnalyzer(Class<?> targetClass, List<String> methodNames) {
    this.target = targetClass;
    this.methods = methodNames;
    referencedTypes = new HashSet<Class<?>>();
  }

  public Map<Class<?>, ClassInfo> analysis() {
    ClassVisitor cv = new ClassVisitor();
    if (methods.size() != 0) {
      Filter.setMethodFileter(target, methods);
    }
    cv.visitClass(target);
    Map<Class<?>, ClassInfo> allClasses = cv.getReferencedClasses();
    return allClasses;
  }

  /**
   * Find the closure of all the referenced class. The referenced classes
   * include the super classes, the interfaces, the inner classes, field types,
   * the return and parameter types of the public methods defined in the target
   * class.
   * 
   */
  public void closeTargetClass() {
    List<Class<?>> workingSet = new LinkedList<Class<?>>();
    Set<Class<?>> doneSet = new HashSet<Class<?>>();

    workingSet.add(target);

    while (!workingSet.isEmpty()) {
      Class<?> current = workingSet.remove(0);

      if (!doneSet.contains(current)) {
        doneSet.add(current);
        // The class reader cannot handle primitive type, and we don't go 
        // deep into class that is in the ignore list
        if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(current)
            || Filter.filterClass(current)) {
          continue;
        }

        // The class reader cannot handle array type
        if (current.isArray()) {
          current = current.getComponentType();
          workingSet.add(0, current);
          continue;
        }

        Set<Class<?>> classes = getDirectlyReferencedTypes(current);

        workingSet.addAll(0, classes);
      }
    }

    // save the found classes
    referencedTypes = doneSet;
  }

  private Set<Class<?>> getDirectlyReferencedTypes(Class<?> type) {
    Set<Class<?>> resultSet = new HashSet<Class<?>>();
    if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(type)) {
      // primitive don't have reference to other types
    } else if (type.isArray()) {
      resultSet.add(type.getComponentType());
    } else {// reference types
      resultSet.add(type.getSuperclass());

      Class<?>[] interfaces = type.getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        resultSet.add(interfaces[i]);
      }

      Field[] fields = type.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        resultSet.add(fields[i].getType());
      }

      Method[] methods = type.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];
        if (Filter.filterMethod(method)) {
          Class<?> returnType = method.getReturnType();
          resultSet.add(returnType);
          Class<?>[] parameterTypes = method.getParameterTypes();
          for (int j = 0; j < parameterTypes.length; j++) {
            resultSet.add(parameterTypes[j]);
          }
        }
      }
    }

    return resultSet;
  }
}
