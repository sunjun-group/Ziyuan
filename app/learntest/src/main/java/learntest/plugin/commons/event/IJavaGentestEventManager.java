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
public interface IJavaGentestEventManager {

	void start();

	void stop();

	void fireOnChangedEvent(JavaGentestEvent javaGentestEvent);

	void addJavaGentestCompleteListener(IJavaGentestCompleteListener listener);

	void fireGentestStartEvent();

	void fireAnnotationChange(AnnotationChangeEvent event);

	void addAnnotationChangedListener(IAnnotationChangeListener listener);

}
