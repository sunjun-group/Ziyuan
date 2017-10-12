/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

import sav.common.core.pattern.IDataProvider;

/**
 * @author LLT
 *
 */
public class EmptyGentestManager implements IJavaGentestEventManager, IDataProvider<IJavaModelRuntimeInfo> {

	@Override
	public void setData(IJavaModelRuntimeInfo data) {
		
	}

	@Override
	public IJavaModelRuntimeInfo getData() {
		return null;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void fireOnChangedEvent(JavaGentestEvent javaGentestEvent) {
		
	}

	@Override
	public void addJavaGentestCompleteListener(IJavaGentestCompleteListener listener) {
		
	}

	@Override
	public void fireGentestStartEvent() {
		
	}

	@Override
	public void fireAnnotationChange(AnnotationChangeEvent event) {
		
	}

	@Override
	public void addAnnotationChangedListener(IAnnotationChangeListener listener) {
		
	}

}
