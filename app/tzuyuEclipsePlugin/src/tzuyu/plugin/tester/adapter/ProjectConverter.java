/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.adapter;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import sav.common.core.utils.CollectionUtils;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzMethod;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.dto.WorkObject.WorkItem;
import tzuyu.plugin.commons.exception.ErrorType;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.ClassLoaderUtils;
import tzuyu.plugin.commons.utils.SignatureParser;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 *
 */
public class ProjectConverter {
	private WorkObject workObject;
	private URLClassLoader classLoader;
	
	private ProjectConverter(WorkObject workObject) throws PluginException {
		this.workObject = workObject;
		IJavaProject project = workObject.getProject();
		classLoader = ClassLoaderUtils.getClassLoader(project);
	}
	
	public static TzClass from(WorkObject workObject, GenTestPreferences config) throws PluginException {
		ProjectConverter converter = new ProjectConverter(workObject);
		TzClass tzProject = converter.getTzClass();
		tzProject.setConfiguration(config.getTzConfig(true));
		return tzProject;
	}
	
	private TzClass getTzClass() throws PluginException {
		Map<Class<?>, List<TzMethod>> classMethodsMap = new HashMap<Class<?>, List<TzMethod>>();
		if (workObject.isEmtpy()) {
			throw new PluginException(
					ErrorType.SELECTION_MORE_THAN_ONE_PROJ_SELECTED);
		}
		// for each case
		for (WorkItem item : workObject.getWorkItems()) {
			IJavaElement ele = item.getCorrespondingJavaElement();
			switch (ele.getElementType()) {
			case IJavaElement.CLASS_FILE:
				addClazzFromClassFile(classMethodsMap, (IClassFile) ele);
				break;
			case IJavaElement.METHOD:
				addFromMethod(classMethodsMap, (IMethod) ele);
				break;
			case IJavaElement.TYPE:
				addClazzFromType(classMethodsMap, (IType) ele);
				break;
			case IJavaElement.COMPILATION_UNIT:
				addClazzFromCU(classMethodsMap, (ICompilationUnit) ele);
				break;
			default:
				break;
			}
		}
		Class<?> firstKey = classMethodsMap.keySet().iterator().next();
		TzClass tzProject = new TzClass(firstKey, classMethodsMap.get(firstKey));
		// just support selecting on 1 selected class only right now.
		return tzProject;
	}

	private void addClazzFromCU(Map<Class<?>, List<TzMethod>> classMethodsMap,
			ICompilationUnit ele) throws PluginException {
		try {
			for (IType type : ele.getAllTypes()) {
				addClazzFromType(classMethodsMap, type);
			}
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			throw new PluginException();
		}
	}

	private Class<?> addClazzFromType(Map<Class<?>, List<TzMethod>> classMethodsMap,
			IType ele) throws PluginException {
		return addClazzToMap(classMethodsMap, ele.getFullyQualifiedName());
	}

	private void addClazzFromClassFile(Map<Class<?>, List<TzMethod>> classMethodsMap,
			IClassFile ele) throws PluginException {
		addClazzFromType(classMethodsMap, ele.getType());
	}

	private void addFromMethod(Map<Class<?>, List<TzMethod>> classMethodsMap,
			IMethod method) throws PluginException {
		Class<?> clazz = addClazzFromType(classMethodsMap, method.getDeclaringType());
		classMethodsMap.get(clazz).add(toTzMethod(method));
	}

	private Class<?> addClazzToMap(
			Map<Class<?>, List<TzMethod>> classMethodsMap,
			String fullyQualifiedName) throws PluginException {
		Class<?> clazz;
		try {
			clazz = classLoader.loadClass(fullyQualifiedName);
		} catch (ClassNotFoundException e) {
			PluginLogger.getLogger().logEx(e);
			throw new PluginException();
		}
		CollectionUtils.getListInitIfEmpty(classMethodsMap, clazz);
		return clazz;
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
