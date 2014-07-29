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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

import sav.common.core.utils.CollectionUtils;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.dto.WorkObject.WorkItem;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.IProjectUtils;
import tzuyu.plugin.commons.utils.ResourcesUtils;
import tzuyu.plugin.icsetlv.command.AnalysisPreferences;
import tzuyu.plugin.tester.reporter.PluginLogger;

import com.ibm.wala.util.collections.Pair;


/**
 * @author LLT
 * 
 */
public class IcsetlvEngineAdaptorImpl implements IcsetlvEngineAdaptor {
	
	@Override
	public List<BreakPoint> analyse(WorkObject workObj, AnalysisPreferences prefs)
			throws IcsetlvException, PluginException {
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
		Map<IPath, List<String>> assertionSources = getAssertionSources(workObject, prefs);
		input.setAssertionSourcePaths(toOsstring(assertionSources));
		List<String> sourceNames = getName(assertionSources.keySet());
		List<IType> passTcs = extractSourceByNames(sourceNames, prefs.getPassPkg());
		List<IType> failTcs = extractSourceByNames(sourceNames, prefs.getFailPkg());
		input.setPassTestcases(toFullyQualifiedName(passTcs));
		input.setFailTestcases(toFullyQualifiedName(failTcs));
		input.setTestMethods(extractAllTestMethods(failTcs));
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
			Map<IPath, List<String>> assertionSources) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (IPath path : assertionSources.keySet()) {
			result.put(path.toOSString(), assertionSources.get(path));
		}
		return result;
	}

	private List<Pair<String, String>> extractAllTestMethods(List<IType> tcs) {
		List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
		for (IType type : tcs) {
			try {
				for (IMethod method : type.getMethods()) {
					if (method.getAnnotation(Test.class.getSimpleName()) != null) {
						result.add(Pair.make(type.getKey().replace(";", ""),
								method.getElementName()));
					}
				}
			} catch (JavaModelException e) {
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

	private List<String> getName(Set<IPath> paths) throws PluginException {
		List<String> names =  new ArrayList<String>();
		for (IPath path : paths) {
			String name = path.removeFileExtension().segment(
					path.segmentCount() - 1);
			names.add(name);
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

	private Map<IPath, List<String>> getAssertionSources(WorkObject workObject,
			AnalysisPreferences prefs) throws PluginException {
		Map<IPath, List<String>> typeMethods = new HashMap<IPath, List<String>>();
		if (workObject.isEmtpy()) {
			return typeMethods;
		}
		// for each case
		for (WorkItem item : workObject.getWorkItems()) {
			IJavaElement ele = item.getCorrespondingJavaElement();
			switch (ele.getElementType()) {
			case IJavaElement.METHOD:
				List<String> methods = CollectionUtils.getListInitIfEmpty(typeMethods, IProjectUtils.relativeToAbsolute(ele
						.getPath()));
				methods.add(ele.getElementName());
			case IJavaElement.TYPE:
			case IJavaElement.COMPILATION_UNIT:
				CollectionUtils.getListInitIfEmpty(typeMethods, IProjectUtils.relativeToAbsolute(ele
						.getPath()));
				break;
			default:
				break;
			}
		}
		return typeMethods;
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

