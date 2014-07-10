/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.proxy;

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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzMethod;
import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.dto.WorkObject.WorkItem;
import tzuyu.plugin.commons.exception.ErrorType;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.ClassLoaderUtils;
import tzuyu.plugin.commons.utils.ResourcesUtils;
import tzuyu.plugin.commons.utils.SignatureParser;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.reporter.PluginLogger;

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
		Map<Class<?>, List<TzMethod>> classMethodsMap = new HashMap<Class<?>, List<TzMethod>>();
		if (workObject.isEmtpy()) {
			throw new PluginException(ErrorType.SELECTION_MORE_THAN_ONE_PROJ_SELECTED);
		}
		IJavaProject project = workObject.getProject();
		List<IPath> sourcePaths = ResourcesUtils.getSourcePaths(project);
		URLClassLoader classLoader = ClassLoaderUtils.getClassLoader(project);
		String fullyQualifiedName = null;
		for (WorkItem item : workObject.getWorkItems()) {
			TzMethod method = null;
			IJavaElement ele = item.getCorrespondingJavaElement();
			//TODO LLT: just temporary, this is messy.
			// 	TO REFACTOR
			switch (ele.getElementType()) {
			case IJavaElement.CLASS_FILE:
				fullyQualifiedName = StringUtils.dotJoin(ele.getParent()
						.getElementName(), ((IClassFile)ele).getType().getElementName());
				break;			
			case IJavaElement.METHOD:
				method = toTzMethod((IMethod) ele);
				// no break, get class of the selected method just like the
				// selected class.
				if (((IMethod)ele).isBinary()) {
					IType type = (IType) ele.getParent();
					fullyQualifiedName = type.getFullyQualifiedName();
					break;
				}
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
			List<TzMethod> methods;
			try {
				clazz = classLoader.loadClass(fullyQualifiedName);
				if ((methods = classMethodsMap.get(clazz)) == null) {
					methods = new ArrayList<TzMethod>();
					classMethodsMap.put(clazz, methods);
				}
			} catch (ClassNotFoundException e) {
				PluginLogger.getLogger().logEx(e);
				throw new PluginException();
			}
			if (method != null) {
				methods.add(method);
			}
		}
		TzClass tzProject = null;
		for (Entry<Class<?>, List<TzMethod>> entry : classMethodsMap.entrySet()) {
			tzProject = new TzClass(entry.getKey(), entry.getValue());
		}
		// just support selecting on 1 selected class only right now.
		return tzProject;
	}

	private static TzMethod toTzMethod(IMethod jMethod) throws PluginException {
		SignatureParser parser = new SignatureParser((IType)jMethod.getParent());
		try {
			String methodJVMSignature = parser.toMethodJVMSignature(
					jMethod.getParameterTypes(), jMethod.getReturnType());
			PluginLogger.getLogger().info(methodJVMSignature);
			return new TzMethod(jMethod.getElementName(), methodJVMSignature);
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			throw new PluginException();
		}
	}
	
}
