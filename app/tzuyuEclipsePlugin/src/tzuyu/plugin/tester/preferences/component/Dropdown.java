/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences.component;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import tzuyu.engine.utils.Assert;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.tester.ui.SWTFactory;

/**
 * @author LLT
 *
 */
public class Dropdown<T extends Enum<?>> {
	private Combo comb;
	private T[] data;
	
	public Dropdown(Composite parent, T[] data) {
		this(parent);
		setData(data);
	}
	
	public Dropdown(Composite parent) {
		comb = SWTFactory.creatDropdown(parent);
	}

	public void setData(T[] data) {
		Assert.assertNotNull(data, "Values to creat dropdown can not be null!!");
		this.data = data;
		for (T item : data) { 
			comb.add(TzuyuPlugin.getMessages().getMessage(item));
		}		
	}
	
	public T getValue() {
		return data[comb.getSelectionIndex()];
	}
	
	public void setValue(T item) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].equals(item)) { 
				comb.select(i);
				return;
			}
		}
		Assert.assertFail("Dropdonw: error when try to select an item. Item " + item
				+ " is not found in data");
	}
}
