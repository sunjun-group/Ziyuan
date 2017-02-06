package tzuyu.engine.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class OneMoreElementList<T> extends SimpleList<T> implements
    Serializable {

  private static final long serialVersionUID = 1332963552183905833L;

  public final T lastElement;
  public final SimpleList<T> list;
  public final int size;

  public OneMoreElementList(SimpleList<T> list, T extraElement) {
    this.list = list;
    this.lastElement = extraElement;
    this.size = list.size() + 1;
  }

  @Override
  public int size() {
    return size; // XXX this is bogus: what if the list changes size?
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((lastElement == null) ? 0 : lastElement.hashCode());
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    result = prime * result + size;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof OneMoreElementList)) {
      return false;
    }
    OneMoreElementList<?> other = (OneMoreElementList<?>) obj;
    if (lastElement == null) {
      if (other.lastElement != null) {
        return false;
      }
    } else if (!lastElement.equals(other.lastElement)) {
      return false;
    }
    if (list == null) {
      if (other.list != null) {
        return false;
      }
    } else if (!list.equals(other.list)) {
      return false;
    }
    if (size != other.size) {
      return false;
    }
    return true;
  }

  @Override
  public T get(int index) {
    if (index < list.size())
      return list.get(index);
    if (index == list.size())
      return lastElement;
    throw new IndexOutOfBoundsException("No such element:" + index);
  }

  @Override
  public List<T> toJDKList() {
    List<T> result = new ArrayList<T>();
    result.addAll(list.toJDKList());
    result.add(lastElement);
    return result;
  }

  @Override
  public String toString() {
    return toJDKList().toString();
  }

}
