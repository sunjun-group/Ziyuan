package tzuyu.engine.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<T1, T2> implements IMultiMap<T1, T2> {

	private final Map<T1, Set<T2>> mappings;

	public MultiMap() {
		this.mappings = new LinkedHashMap<T1, Set<T2>>();
	}

	public void put(T1 key, Collection<? extends T2> values) {
		if (containsKey(key)) {
			this.remove(key);
		}
		this.mappings.put(key, new LinkedHashSet<T2>(values));
	}

	public boolean containsKey(T1 key) {
		return this.mappings.containsKey(key);
	}

	public void remove(T1 key) {
		Set<T2> values = this.mappings.get(key);
		if (values == null)
			throw new IllegalArgumentException("");
		this.mappings.remove(key);
	}

	public void addAll(T1 key, Collection<? extends T2> values) {
		for (T2 value : values) {
			this.add(key, value);
		}
	}

	public void AddAll(Map<? extends T1, ? extends T2> map) {
		for (T1 key : map.keySet()) {
			this.add(key, map.get(key));
		}
	}

	public void add(T1 key, T2 value) {
		Set<T2> values = this.mappings.get(key);
		if (values == null) {
			values = new LinkedHashSet<T2>(1);
			this.mappings.put(key, values);
		}

		values.add(value);
	}

	public void remove(T1 key, T2 value) {
		Set<T2> values = this.mappings.get(key);
		if (values == null)
			throw new IllegalArgumentException("");
		values.remove(value);
	}

	public Set<T2> getValues(T1 key) {
		Set<T2> values = this.mappings.get(key);
		if (values == null)
			return Collections.emptySet();
		return values;
	}

	public Set<T1> keySet() {
		return this.mappings.keySet();
	}

	public int size() {
		return this.mappings.size();
	}

	public void clear() {
		this.mappings.clear();
	}

	@Override
	public String toString() {
		return this.mappings.toString();
	}

}
