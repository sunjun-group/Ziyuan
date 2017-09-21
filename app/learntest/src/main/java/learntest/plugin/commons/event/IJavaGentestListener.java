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
public interface IJavaGentestListener extends ILearntestListener<JavaGentestEvent>{

	public void onChanged(JavaGentestEvent event);
	
}
