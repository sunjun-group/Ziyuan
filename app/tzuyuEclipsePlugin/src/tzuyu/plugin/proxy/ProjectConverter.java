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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import tzuyu.engine.TzProject;
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
	
	public static TzProject from(WorkObject workObject, GenTestPreferences config) throws PluginException {
		TzProject tzProject = toTzProject(workObject);
		tzProject.setConfiguration(config.getTzConfig());
		return tzProject;
	}

	private static TzProject toTzProject(WorkObject workObject)
			throws PluginException {
		Map<Class<?>, List<String>> classMethodsMap = new HashMap<Class<?>, List<String>>();
		if (workObject.size() != 1) {
			throw new PluginException(ErrorType.SELECTION_MORE_THAN_ONE_PROJ_SELECTED);
		}

		for (IJavaProject project : workObject.getProjects()) {
			List<IPath> sourcePaths = ResourcesUtils.getSourcePaths(project);
			URLClassLoader classLoader = ClassLoaderUtils.getClassLoader(project);
			for (WorkItem item : workObject.getWorkItems(project)) {
				String methodName = null; 
				switch (item.getCorrespondingJavaElement().getElementType()) {
				case IJavaElement.METHOD:
					methodName = item.getCorrespondingJavaElement().getElementName();
					// no break, get class of the selected method just like the selected class.
				case IJavaElement.COMPILATION_UNIT:
				case IJavaElement.TYPE:
					IPath filePath = item.getPath();
					IPath scr = ResourcesUtils.getCorrespondingSource(filePath,
							sourcePaths);
					String fullyQualifiedName = ResourcesUtils.getFullQualifiedName(filePath, scr);
					Class<?> clazz;
					List<String> methods;
					try {
						clazz = classLoader.loadClass(fullyQualifiedName);
						if ((methods = classMethodsMap.get(clazz)) == null) {
							methods = new ArrayList<String>();
							classMethodsMap.put(clazz, methods);
						}
					} catch (ClassNotFoundException e) {
						PluginLogger.logEx(e);
						throw new PluginException();
					}
					if (methodName != null) {
						methods.add(methodName);
					}
				default:
					break;
				}
			}
		}
		TzProject tzProject = null;
		for (Entry<Class<?>, List<String>> entry : classMethodsMap.entrySet()) {
			tzProject = new TzProject(entry.getKey(), entry.getValue());
		}
		// just support selecting on 1 selected class only right now.
		return tzProject;
	}
	
	
}
