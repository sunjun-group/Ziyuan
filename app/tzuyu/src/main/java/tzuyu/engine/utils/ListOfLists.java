package tzuyu.engine.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Given a list of lists, defines methods that can access all the elements as if
 * they were part of a single list, without actually merging the lists.
 * 
 * This class is used for performance reasons, we want the ability to select
 * elements to a collected across several lists, but we observed that creating a
 * brand new list (i.e. via a sequence of List.addAll(...) operations can be
 * very expensive, because it happened in a hot spot.
 * 
 * @param <T>
 */
public class ListOfLists<T> extends SimpleList<T> implements Serializable {

  private static final long serialVersionUID = 7395580091746452340L;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(accumulatedSize);
    result = prime * result + ((lists == null) ? 0 : lists.hashCode());
    result = prime * result + totalElements;
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
    if (!(obj instanceof ListOfLists)) {
      return false;
    }
    ListOfLists<?> other = (ListOfLists<?>) obj;
    if (!Arrays.equals(accumulatedSize, other.accumulatedSize)) {
      return false;
    }
    if (lists == null) {
      if (other.lists != null) {
        return false;
      }
    } else if (!lists.equals(other.lists)) {
      return false;
    }
    if (totalElements != other.totalElements) {
      return false;
    }
    return true;
  }

  public final List<SimpleList<T>> lists;

  private int[] accumulatedSize;

  private int totalElements;

  public ListOfLists(List<SimpleList<T>> lsts) {
    if (lsts == null) {
      throw new IllegalArgumentException("parameters is null");
    }
    this.lists = lsts;

    this.accumulatedSize = new int[lsts.size()];
    this.totalElements = 0;
    for (int i = 0; i < lsts.size(); i++) {
      SimpleList<T> l = lsts.get(i);
      if (l == null) {
        throw new IllegalArgumentException("All lists should be non-null");
      } else {
        this.totalElements += l.size();
        this.accumulatedSize[i] = this.totalElements;
      }
    }
  }

  public ListOfLists(SimpleList<T> first, SimpleList<T> second) {
    if (first == null || second == null) {
      throw new IllegalArgumentException("parameters should not be null");
    }

    this.lists = new ArrayList<SimpleList<T>>(2);
    lists.add(first);
    lists.add(second);

    this.accumulatedSize = new int[2];
    this.totalElements = 0;
    for (int i = 0; i < 2; i++) {
      SimpleList<T> l = lists.get(i);
      this.totalElements += l.size();
      this.accumulatedSize[i] = this.totalElements;
    }
  }

  @Override
  public int size() {
    return this.totalElements;
  }

  @Override
  public T get(int index) {
    if (index < 0 || index > this.totalElements - 1) {
      throw new IllegalArgumentException("index must between 0 and size() -1");
    }
    int previousListSize = 0;
    for (int i = 0; i < this.accumulatedSize.length; i++) {
      if (index < this.accumulatedSize[i]) {
        return this.lists.get(i).get(index - previousListSize);
      }
      previousListSize = this.accumulatedSize[i];
    }
    throw new RuntimeException("this point should not be reached");
  }

  @Override
  public List<T> toJDKList() {
    List<T> result = new ArrayList<T>();
    for (SimpleList<T> l : lists) {
      result.addAll(l.toJDKList());
    }
    return result;
  }

  @Override
  public String toString() {
    return toJDKList().toString();
  }

}
