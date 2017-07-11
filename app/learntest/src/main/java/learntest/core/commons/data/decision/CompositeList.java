/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorChain;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CompositeList<E> extends AbstractList<E> {
	private List<List<E>> componentList;
	
	public CompositeList() {
		componentList = new ArrayList<List<E>>();
	}
	
	public CompositeList(int size) {
		componentList = new ArrayList<List<E>>(size);
	}

	@Override
	public E get(int index) {
		int cursor = 0;
		for (List<E> component : componentList) {
			if (index < (cursor + component.size())) {
				return component.get(index - cursor);
			}
			cursor += component.size();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> iterator() {
		if (componentList.isEmpty()) {
			return EmptyIterator.INSTANCE;
		}
		final IteratorChain chain = new IteratorChain();
		for (final Collection<E> item : componentList) {
			chain.addIterator(item.iterator());
		}
		return chain;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (CollectionUtils.isEmpty(c)) {
			return false;
		}
		if (c instanceof List<?>) {
			componentList.add((List<E>) c);
			return true;
		}
		throw new UnsupportedOperationException("CompositeList: only a list is accepted to add");
	}
	
	@Override
	public int size() {
		int size = 0;
		for (List<E> list : componentList) {
			size += list.size();
		}
		return size;
	}

}
