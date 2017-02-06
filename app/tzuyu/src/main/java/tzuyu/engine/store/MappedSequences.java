package tzuyu.engine.store;

import java.util.LinkedHashMap;
import java.util.Map;

import tzuyu.engine.model.Sequence;


public class MappedSequences<T> {

  public Map<T, SequenceCollection> map;

  public MappedSequences() {
    this.map = new LinkedHashMap<T, SequenceCollection>();
  }

  public void addSequence(T key, Sequence seq) {
    if (seq == null) {
      throw new IllegalArgumentException("seq is null");
    }
    
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }

    SequenceCollection collection = map.get(key);
    if (collection == null) {
      collection = new SequenceCollection();
      map.put(key, collection);
    }
    collection.add(seq);
  }

  public SequenceCollection find(T key) {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }

    SequenceCollection collection = map.get(key);

    return collection;
  }
}
