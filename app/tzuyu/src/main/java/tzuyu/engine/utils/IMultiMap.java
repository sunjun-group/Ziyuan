package tzuyu.engine.utils;

import java.util.Set;

public interface IMultiMap<T1, T2> {

  /**
   * The pair key->value does not exist
   * 
   * @param key
   * @param value
   */
  void add(T1 key, T2 value);

  /**
   * The pair key->value is in the map
   * 
   * @param key
   * @param value
   */
  void remove(T1 key, T2 value);

  /**
   * Returns the set of values associated with the key
   * 
   * @param key
   * @return
   */
  Set<T2> getValues(T1 key);

  /**
   * Returns the set of keys in the map, the domain
   * 
   * @return
   */
  Set<T1> keySet();

  /**
   * The size of this map, the number of mappings which also equals to the
   * number of keys.
   * 
   * @return
   */
  int size();

  /**
   * Clear the content of the mappings
   */
  void clear();

  /**
   * Returns a string representation of this map
   * 
   * @return
   */
  String toString();
}
