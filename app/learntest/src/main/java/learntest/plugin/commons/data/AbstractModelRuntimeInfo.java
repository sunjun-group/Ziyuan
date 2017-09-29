/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;

import learntest.core.RunTimeInfo;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractModelRuntimeInfo<T extends IModelRuntimeInfo> implements IModelRuntimeInfo {
	protected IJavaElement javaElement;
	private List<T> children;
	
	public AbstractModelRuntimeInfo(IJavaElement element) {
		this(element, -1);
	}
	
	public AbstractModelRuntimeInfo(IJavaElement element, int size) {
		this.javaElement = element;
		if (size < 0) {
			this.children = new ArrayList<T>();
		} else {
			this.children = new ArrayList<T>(size);
		}
	}
	
	public AbstractModelRuntimeInfo(IJavaElement element, List<T> children) {
		this.javaElement = element;
		this.children = children;
	}

	public void add(T child) {
		children.add(child);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void transferToMap(Map<IJavaElement, IModelRuntimeInfo> jEleRuntimeInfoMap) {
		internalTransferToMap(jEleRuntimeInfoMap);
		/* transfer its parent as well */
		IJavaElement parentElement = getParentElement();
		IModelRuntimeInfo info = this;
		while (parentElement != null) {
			IModelRuntimeInfo parentEleRuntimeInfo = jEleRuntimeInfoMap.get(parentElement);
			if (parentEleRuntimeInfo != null) {
				((AbstractModelRuntimeInfo<IModelRuntimeInfo>) parentEleRuntimeInfo).add(info);
				break;
			} else {
				parentEleRuntimeInfo = info.createOrAddToParentInfo();
				jEleRuntimeInfoMap.put(parentElement, parentEleRuntimeInfo);
				info = parentEleRuntimeInfo;
				parentElement = ((AbstractModelRuntimeInfo<?>) parentEleRuntimeInfo).getParentElement();
			}
		}
	}

	protected abstract IJavaElement getParentElement();

	private void internalTransferToMap(Map<IJavaElement, IModelRuntimeInfo> jEleRuntimeInfoMap) {
		jEleRuntimeInfoMap.put(javaElement, this);
		for (IModelRuntimeInfo child : CollectionUtils.nullToEmpty(children)) {
			((AbstractModelRuntimeInfo<?>)child).internalTransferToMap(jEleRuntimeInfoMap);
		}
	}
	
	protected void appendMethodRuntimeInfo(Map<IMethod, RunTimeInfo> map) {
		for (T child : children) {
			((AbstractModelRuntimeInfo<?>) child).appendMethodRuntimeInfo(map);
		}
	}
	
	@Override
	public Map<IMethod, RunTimeInfo> getRuntimeInfo() {
		Map<IMethod, RunTimeInfo> map = new HashMap<IMethod, RunTimeInfo>();
		appendMethodRuntimeInfo(map);
		return map;
	}
	
	@Override
	public IJavaElement getJavaElement() {
		return javaElement;
	}
}
