/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;

import tzuyu.plugin.core.dto.MethodMnemonic;
import tzuyu.plugin.core.dto.TypeMnemonic;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class PackageTreeUtils {
	private PackageTreeUtils() {
	}

	public static void runWithProgress(final IJavaProject javaProject) {
		final MutableBoolean isCancelled = new MutableBoolean(true);
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				SubMonitor parentMonitor = SubMonitor.convert(monitor);
				parentMonitor.beginTask(
						"Searching for classes and methods in selection...", 3);
				try {
					javaProject.getProject()
							.refreshLocal(IResource.DEPTH_INFINITE,
									parentMonitor.newChild(1));
				} catch (CoreException e1) {
				}

				if (parentMonitor.isCanceled()) {
					isCancelled.setValue(true);
					return;
				}
			}
		};
	}

	public static void getStructure(List<IJavaElement> elements,
			final IJavaProject javaProject) {
		final Map<String, List<String>> selectedMethodsByDeclaringTypes = new HashMap<String, List<String>>();
		List<IType> types = new ArrayList<IType>();
		List<IType> selectedTypes = new ArrayList<IType>();

		for (IJavaElement element : elements) {
			List<IType> foundTypes;
			switch (element.getElementType()) {
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			case IJavaElement.PACKAGE_FRAGMENT:
				// till now, only selected class/methods accepted.
				break;
			case IJavaElement.COMPILATION_UNIT:
				foundTypes = ResourcesUtils.getTypes((ICompilationUnit) element, false);
				types.addAll(foundTypes);
				selectedTypes.addAll(foundTypes);
				break;
			case IJavaElement.TYPE:
				types.add((IType) element);
				selectedTypes.add((IType) element);

				selectedMethodsByDeclaringTypes.remove((IType) element);
				break;
			case IJavaElement.METHOD:
				IMethod m = (IMethod) element;
				IType type = m.getDeclaringType();

				if (!selectedTypes.contains(type)) {
					try {
						if (AdaptablePropertyTester.isTestable(m)) {
							List<String> methodMnemonics = selectedMethodsByDeclaringTypes
									.get(type);

							String typeMnemonicString = new TypeMnemonic(type)
									.toString();

							if (methodMnemonics == null) {
								methodMnemonics = new ArrayList<String>();
								selectedMethodsByDeclaringTypes.put(
										typeMnemonicString, methodMnemonics);
							}
							methodMnemonics.add(new MethodMnemonic(m)
									.toString());
							if (!types.contains(type)) {
								types.add(type);
							}
						}
					} catch (JavaModelException e) {
						PluginLogger.logEx(e);
					}
				}
				break;
			}
		}
	}
}
