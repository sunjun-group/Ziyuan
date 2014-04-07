/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.proxy;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import tzuyu.engine.TzClass;
import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.dto.WorkObject.WorkItem;
import tzuyu.plugin.core.exception.ErrorType;
import tzuyu.plugin.core.exception.PluginException;
import tzuyu.plugin.core.utils.ClassLoaderUtils;
import tzuyu.plugin.core.utils.ResourcesUtils;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 *
 */
public class ProjectConverter {
	private ProjectConverter(){}
	
	public static TzClass from(WorkObject workObject, GenTestPreferences config) throws PluginException {
		TzClass tzProject = toTzProject(workObject);
		tzProject.setConfiguration(config.getTzConfig(true));
		return tzProject;
	}

	private static TzClass toTzProject(WorkObject workObject)
			throws PluginException {
		Map<Class<?>, List<String>> classMethodsMap = new HashMap<Class<?>, List<String>>();
		if (workObject.isEmtpy()) {
			throw new PluginException(ErrorType.SELECTION_MORE_THAN_ONE_PROJ_SELECTED);
		}
		IJavaProject project = workObject.getProject();
		List<IPath> sourcePaths = ResourcesUtils.getSourcePaths(project);
		URLClassLoader classLoader = ClassLoaderUtils.getClassLoader(project);
		String fullyQualifiedName = null;
		for (WorkItem item : workObject.getWorkItems()) {
			String methodName = null;
			IJavaElement ele = item.getCorrespondingJavaElement();
			//TODO LLT: just temporary, it's messy.
			switch (ele.getElementType()) {
			case IJavaElement.CLASS_FILE:
				fullyQualifiedName = StringUtils.dotJoin(ele.getParent()
						.getElementName(), ((IClassFile)ele).getType().getElementName());
				break;			
			case IJavaElement.METHOD:
				methodName = ele
						.getElementName();
				// no break, get class of the selected method just like the
				// selected class.
			case IJavaElement.COMPILATION_UNIT:
			case IJavaElement.TYPE:
				ele.getElementName();
				IPath filePath = item.getPath();
				IPath scr = ResourcesUtils.getCorrespondingSource(filePath,
						sourcePaths);
				fullyQualifiedName = ResourcesUtils
						.getFullQualifiedName(filePath, scr);
				break;
			default:
				break;
			}
			Class<?> clazz;
			List<String> methods;
			try {
				clazz = classLoader.loadClass(fullyQualifiedName);
				if ((methods = classMethodsMap.get(clazz)) == null) {
					methods = new ArrayList<String>();
					classMethodsMap.put(clazz, methods);
				}
			} catch (ClassNotFoundException e) {
				PluginLogger.getLogger().logEx(e);
				throw new PluginException();
			}
			if (methodName != null) {
				methods.add(methodName);
			}
		}
		TzClass tzProject = null;
		for (Entry<Class<?>, List<String>> entry : classMethodsMap.entrySet()) {
			tzProject = new TzClass(entry.getKey(), entry.getValue());
		}
		// just support selecting on 1 selected class only right now.
		return tzProject;
	}
	
	
}
