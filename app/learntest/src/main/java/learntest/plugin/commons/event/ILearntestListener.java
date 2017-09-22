/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

/**
 * @author LLT
 *
 */
public interface ILearntestListener<T extends ILearntestEvent> {

	public void onChanged(T event);
	
	public Class<T> getEventType();
}
