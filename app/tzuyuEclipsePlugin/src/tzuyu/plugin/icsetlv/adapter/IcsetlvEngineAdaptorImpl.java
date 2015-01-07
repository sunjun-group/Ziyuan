/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.adapter;

import icsetlv.IcsetlvEngine;
import icsetlv.IcsetlvInput;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.SignatureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.SocketUtil;
import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.dto.WorkObject.WorkItem;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.IProjectUtils;
import tzuyu.plugin.commons.utils.ResourcesUtils;
import tzuyu.plugin.commons.utils.SignatureParser;
import tzuyu.plugin.icsetlv.command.AnalysisPreferences;
import tzuyu.plugin.tester.reporter.PluginLogger;


/**
 * @author LLT
 * 
 */
public class IcsetlvEngineAdaptorImpl implements IcsetlvEngineAdaptor {
	
	@Override
	public List<BreakPoint> analyse(WorkObject workObj, AnalysisPreferences prefs)
			throws PluginException, SavException, IcsetlvException {
		IcsetlvEngine icsetlv = new IcsetlvEngine();
		IcsetlvInput input = initIcsetlvInput(workObj, prefs);

		List<BreakPoint> result = icsetlv.run(input);
		return result;
	}

	private IcsetlvInput initIcsetlvInput(WorkObject workObject,
			AnalysisPreferences prefs) throws IcsetlvException, PluginException {
		IcsetlvInput input = new IcsetlvInput();
		IJavaProject project = workObject.getProject();
		/* JVM settings */
		IVMInstall vmInstall;
		try {
			vmInstall = JavaRuntime.getVMInstall(project);
			input.setJavaHome(vmInstall.getInstallLocation().getAbsolutePath());
		} catch (CoreException e) {
			IcsetlvException.rethrow(e);
			PluginLogger.getLogger().logEx(e);
		}
		input.setJvmPort(SocketUtil.findFreePort());
		input.setRunJunit(true);
		/* project detail */
		input.setPrjClasspath(getPrjClasspaths(project));
		try {
			input.setAppOutput(IProjectUtils.relativeToAbsolute(
					project.getOutputLocation()).toOSString());
		} catch (JavaModelException e) {
			IcsetlvException.rethrow(e);
			PluginLogger.getLogger().logEx(e);
		}
		Map<ICompilationUnit, List<IMethod>> assertionSources = getAssertionSources(workObject, prefs);
		List<BreakPoint> bkps = AssertionDetector.scan(assertionSources);
		input.setAssertionSourcePaths(toOsstring(assertionSources));
		input.setBreakpoints(bkps);
		List<String> sourceNames = getName(assertionSources.keySet());
		List<IType> passTcs = extractSourceByNames(sourceNames, prefs.getPassPkg());
		List<IType> failTcs = extractSourceByNames(sourceNames, prefs.getFailPkg());
		input.setPassTestcases(toFullyQualifiedName(passTcs));
		input.setFailTestcases(toFullyQualifiedName(failTcs));
//		input.setTestMethods(extractAllTestMethods(failTcs));
		// add source folders
		try {
			for (IPackageFragmentRoot root : project.getAllPackageFragmentRoots()) {
				if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
					input.addSrcFolder(ResourcesUtils.relativeToAbsolute(root.getPath()).toOSString());
				}
			}
		} catch (JavaModelException e) {
			PluginException.wrapEx(e);
			PluginLogger.getLogger().logEx(e);
		}
		return input;
	}
	
	private Map<String, List<String>> toOsstring(
			Map<ICompilationUnit, List<IMethod>> assertionSources) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (ICompilationUnit cu : assertionSources.keySet()) {
			List<IMethod> methods = assertionSources.get(cu);
			String path = ResourcesUtils.relativeToAbsolute(cu.getPath()).toOSString();
			if (methods == null) {
				result.put(path, null);
			} else {
				List<String> methodSigns = new ArrayList<String>();
				for (IMethod method : methods) {
					try {
						methodSigns.add(SignatureUtils.createMethodNameSign(
								method.getElementName(), method.getSignature()));
					} catch (JavaModelException e) {
						PluginLogger.getLogger().logEx(e);
					}
				}
				result.put(path, methodSigns);
			}
		}
		return result;
	}

	private List<String[]> extractAllTestMethods(List<IType> tcs) {
		List<String[]> result = new ArrayList<String[]>();
		for (IType type : tcs) {
			try {
				for (IMethod method : type.getMethods()) {
					if (method.getAnnotation(Test.class.getSimpleName()) != null) {
						result.add(new String[]{type.getKey().replace(";", ""),
								SignatureUtils.createMethodNameSign(method.getElementName(), 
										new SignatureParser(method.getDeclaringType()).toMethodJVMSignature(
												method.getParameterTypes(), method.getReturnType()))});
					}
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e,
								"cannot find methods for type: " + type.getElementName());
			} catch (PluginException e) {
				PluginLogger.getLogger().logEx(e,
						"cannot find methods for type: " + type.getElementName());
			}
		}

		return result;
	}
	
	private List<String> toFullyQualifiedName(List<IType> types) {
		List<String> result = new ArrayList<String>(types.size());
		for (IType type : types) {
			result.add(type.getFullyQualifiedName());
		}
		return result;
	}

	private List<String> getName(Set<ICompilationUnit> set) throws PluginException {
		List<String> names =  new ArrayList<String>();
		for (ICompilationUnit cu : set) {
			try {
				for (IType type : cu.getTypes()) {
					names.add(type.getElementName());
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
			}
		}
		return names;
	}

	private List<IType> extractSourceByNames(List<String> sourceNames,
			IPackageFragment pkg) throws PluginException {
		List<IType> sourceFiles = new ArrayList<IType>();
		try {
			for (ICompilationUnit cu : pkg.getCompilationUnits()) {
				String name = cu.getElementName();
				for (String source : sourceNames) {
					if (name.startsWith(source)) {
						for (IType type : cu.getTypes()) {
							sourceFiles.add(type);
						}
						break;
					}
				}
			}
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			PluginException.wrapEx(e);
		}
		return sourceFiles;
	}

	private Map<ICompilationUnit, List<IMethod>> getAssertionSources(WorkObject workObject,
			AnalysisPreferences prefs) throws PluginException {
		Map<ICompilationUnit, List<IMethod>> typeMethods = new HashMap<ICompilationUnit, List<IMethod>>();
		if (workObject.isEmtpy()) {
			return typeMethods;
		}
		// for each case
		for (WorkItem item : workObject.getWorkItems()) {
			IJavaElement ele = item.getCorrespondingJavaElement();
			switch (ele.getElementType()) {
			case IJavaElement.METHOD:
				IMethod method = (IMethod) ele;
				List<IMethod> methods = CollectionUtils.getListInitIfEmpty(
						typeMethods, method.getDeclaringType().getCompilationUnit());
				methods.add(method);
				break;
			case IJavaElement.TYPE:
				IType type = (IType) ele;
				addCompilationUnit(typeMethods, type.getCompilationUnit());
				break;
			case IJavaElement.COMPILATION_UNIT:
				addCompilationUnit(typeMethods, (ICompilationUnit) ele);
				break;
			default:
				break;
			}
		}
		return typeMethods;
	}

	private void addCompilationUnit(Map<ICompilationUnit, List<IMethod>> typeMethods, ICompilationUnit cu) {
		if (!typeMethods.containsKey(cu)) {
			typeMethods.put(cu, null);
		}
	}
	
	private static List<String> getPrjClasspaths(IJavaProject project)
			throws IcsetlvException {
		List<String> paths = new ArrayList<String>();
		try {
			IRuntimeClasspathEntry[] entries = JavaRuntime
					.computeUnresolvedRuntimeClasspath(project);
			for (IRuntimeClasspathEntry entry : entries) {
				if (entry.getType() != IRuntimeClasspathEntry.PROJECT) {
					IRuntimeClasspathEntry[] resolvedEntries = JavaRuntime
							.resolveRuntimeClasspathEntry(entry, project);
					for (IRuntimeClasspathEntry resolvedEntry : resolvedEntries) {
						paths.add(resolvedEntry.getLocation());
					}
				}
			}
		} catch (CoreException e) {
			IcsetlvException.rethrow(e);
			PluginLogger.getLogger().logEx(e);
		}
		return paths;
	}
}

