/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.LearntestPlugin;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;
import sav.common.core.pattern.IDataProvider;

/**
 * @author LLT
 *
 */
public class JavaElementRuntimeInfoAdapterFactory implements IAdapterFactory {
	private static Logger log = LoggerFactory.getLogger(JavaElementRuntimeInfoAdapterFactory.class);

	private IDataProvider<IJavaModelRuntimeInfo> dataProvider;

	public JavaElementRuntimeInfoAdapterFactory() {
		this.dataProvider = LearntestPlugin.getJavaModelRuntimeInfoProvider();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		IJavaModelRuntimeInfo javaModelRuntimeInfo = dataProvider.getData();
		if (javaModelRuntimeInfo == null) {
			log.warn("javaModelRuntimeInfo is empty!");
			return null;
		}
		if (adaptableObject instanceof IJavaElement) {
			return (T) javaModelRuntimeInfo.getCorrespondingRuntimeInfo((IJavaElement) adaptableObject);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IModelRuntimeInfo.class };
	}
}
