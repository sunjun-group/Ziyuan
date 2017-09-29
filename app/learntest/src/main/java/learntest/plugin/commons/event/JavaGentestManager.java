/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

import org.eclipse.core.commands.common.EventManager;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.view.report.ReportView;
import sav.common.core.pattern.IDataProvider;

/**
 * @author LLT
 *
 */
public class JavaGentestManager extends EventManager implements IJavaGentestEventManager, IDataProvider<IJavaModelRuntimeInfo> {
	private IJavaModelRuntimeInfo javaModelRuntimeInfo;
	private IJavaGentestStartListener defaultListener;
	
	public JavaGentestManager() {
		defaultListener = new IJavaGentestStartListener() {
			@Override
			public void onStart() {
				LearntestPlugin.displayView(ReportView.ID);
			}
		};
	}

	public void start() {
		addListenerObject(defaultListener);
	}
	
	public void stop() {
		super.clearListeners();
	}

	public void fireOnChangedEvent(JavaGentestEvent event) {
		setData(event.getRuntimeInfo());
		for (Object listener : getListeners()) {
			if (listener instanceof IJavaGentestCompleteListener) {
				((IJavaGentestCompleteListener) listener).onChanged(event);
			}
		}
	}

	@Override
	public void fireGentestStartEvent() {
		for (Object listener : getListeners()) {
			if (listener instanceof IJavaGentestStartListener) {
				((IJavaGentestStartListener) listener).onStart();
			}
		}
	}
	
	@Override
	public void fireAnnotationChange(AnnotationChangeEvent event) {
		for (Object listener : getListeners()) {
			if (listener instanceof IAnnotationChangeListener) {
				((IAnnotationChangeListener) listener).onChange(event);
			}
		}
	}
	
	@Override
	public void addAnnotationChangedListener(IAnnotationChangeListener listener) {
		addListenerObject(listener);
	}
	
	@Override
	public void addJavaGentestCompleteListener(IJavaGentestCompleteListener listener) {
		addListenerObject(listener);
	}

	@Override
	public void setData(IJavaModelRuntimeInfo data) {
		this.javaModelRuntimeInfo = data;
	}

	@Override
	public IJavaModelRuntimeInfo getData() {
		return javaModelRuntimeInfo;
	}
}
