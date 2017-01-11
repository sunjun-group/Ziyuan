/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public class ResolverTypeMap<K, V> implements Map<K, V> {
	private Map<K, V> orgMap;
	
	private ResolverTypeMap(Map<K, V> orgMap) {
		this.orgMap = orgMap;
	}
	
	public static<K, V> Map<K, V> of(Map<K, V> orgMap) {
		return new ResolverTypeMap<K, V>(orgMap);
	}

	@Override
	public int size() {
		return orgMap.size();
	}

	@Override
	public boolean isEmpty() {
		return orgMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return orgMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return orgMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return orgMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		throw new SavRtException("Operation denied!!");
	}

	@Override
	public V remove(Object key) {
		throw new SavRtException("Operation denied!!");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new SavRtException("Operation denied!!");
	}

	@Override
	public void clear() {
		throw new SavRtException("Operation denied!!");
	}

	@Override
	public Set<K> keySet() {
		return orgMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return orgMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return orgMap.entrySet();
	}
	
	

}
