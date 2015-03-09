/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.dto;

/**
 * @author LLT
 *
 */
public interface IDataProvider<T> {
	
	public void setData(T data);
	
	public T getData();
}
