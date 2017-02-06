package tzuyu.engine.store;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import tzuyu.engine.model.QueryTrace;


public class MappedTraces<T> {
  private Map<T, TraceCollection> map;

  public MappedTraces() {
    this.map = new LinkedHashMap<T, TraceCollection>();
  }

  public void addTrace(T key, QueryTrace trace) {
    if (trace == null) {
      throw new IllegalArgumentException("trace is null");
    }

    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }

    TraceCollection collection = map.get(key);

    if (collection == null) {
      collection = new TraceCollection();
      map.put(key, collection);
    }
    collection.add(trace);

  }

  public TraceCollection find(T key) {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }

    TraceCollection collection = map.get(key);
    if (collection == null) {
      return new TraceCollection();
    } else {
      return collection;
    }
  }

  public Set<T> getKeys() {
    return map.keySet();
  }

  public void clear() {
    this.map.clear();
  }
}
