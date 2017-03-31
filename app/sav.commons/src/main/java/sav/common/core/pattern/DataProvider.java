/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.pattern;


/**
 * @author LLT
 *
 */
public class DataProvider<T> implements IDataProvider<T> {
	private T data;
	
	@Override
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public T getData() {
		return data;
	}

}
