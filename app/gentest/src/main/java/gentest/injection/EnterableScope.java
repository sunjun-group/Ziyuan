/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import java.util.HashMap;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author LLT
 *
 */
public class EnterableScope implements Scope {

	private HashMap<Key<?>, Object> values;

	public void enter() {
		values = new HashMap<Key<?>, Object>();
	}

	public void exit() {
		values = null;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			@SuppressWarnings("unchecked")
			public T get() {
				// if values is null return null otherwise return the stored
				// value of one exists
				Object object = values != null ? values.get(key) : null;

				if (object == null) {
					object = unscoped.get();

					// if we in scope, store it, otherwise always create a new
					// one
					if (values != null) {
						values.put(key, object);
					}
				}
				return (T) object;
			}
		};
	}

}
