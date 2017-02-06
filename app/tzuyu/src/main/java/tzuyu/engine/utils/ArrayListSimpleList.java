package tzuyu.engine.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayListSimpleList<T> extends SimpleList<T> implements
    Serializable {

  private static final long serialVersionUID = 9155161101212598259L;

  public final ArrayList<T> theList;

  public ArrayListSimpleList(ArrayList<T> list) {
    theList = new ArrayList<T>(list);
  }

  public ArrayListSimpleList() {
    theList = new ArrayList<T>();
  }

  public ArrayListSimpleList(int capacity) {
    theList = new ArrayList<T>(capacity);
  }

  @Override
  public int size() {
    return theList.size();
  }

  @Override
  public T get(int index) {
    return theList.get(index);
  }

  public boolean add(T element) {
    return theList.add(element);
  }

  @Override
  public List<T> toJDKList() {
    return new ArrayList<T>(theList);
  }

  @Override
  public String toString() {
    return toJDKList().toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((theList == null) ? 0 : theList.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof ArrayListSimpleList)) {
      return false;
    }
    ArrayListSimpleList<?> other = (ArrayListSimpleList<?>) obj;
    if (theList == null) {
      if (other.theList != null) {
        return false;
      }
    } else if (!theList.equals(other.theList)) {
      return false;
    }
    return true;
  }

}
