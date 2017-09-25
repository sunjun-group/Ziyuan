/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

import learntest.plugin.commons.PluginException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class IProjectUtils {
	public IProjectUtils(){}
	
	public static IJavaProject getJavaProject(IProject project) {
		return JavaCore.create(project);
	}
	
	public static IJavaProject getJavaProject(String projectName) {
		IProject project = getProject(projectName);
		if (project == null) {
			return null;
		}
		return JavaCore.create(project);
	}

	/**
	 * Returns the IProject by the specified name in the workspace. To convert
	 * the project to a Java project use JavaCore.create(project)
	 */
	public static IProject getProject(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);

		if (status.isOK()) {
			IProject project = workspace.getRoot().getProject(projectName);

			if (!project.exists())
				return null;

			return project;
		}
		return null;
	}
	
	public static String getJavaHome(IJavaProject project) throws CoreException {
		IVMInstall vmInstall = JavaRuntime.getVMInstall(project);
		return vmInstall.getInstallLocation().getAbsolutePath();
	}
	
	public static List<String> getPrjectClasspath(IJavaProject project) {
		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			return Arrays.asList(classPathEntries);
		} catch (CoreException e) {
			throw new SavRtException(e);
		}
	}
	
	public static List<IPackageFragmentRoot> getSourcePkgRoots(IJavaProject project) {
		List<IPackageFragmentRoot> roots = new ArrayList<>();
		try {
			for (IPackageFragmentRoot packageFragmentRoot : project.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE
						&& !startWith(packageFragmentRoot.getResource().getProjectRelativePath().toString(), "src/test",
								"test")) {
					roots.add(packageFragmentRoot);
				}
			}

		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

		return roots;
	}

	private static boolean startWith(String name, String... prefixes) {
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getSourceFolder(IJavaProject javaProject) throws PluginException {
		List<IPath> sourcePaths = getSourcePaths(javaProject);
		for (IPath path : sourcePaths) {
			// FIXME LLT: just workaround, not a complete solution
			if (CollectionUtils.existIn("test", path.segments())) {
				continue;
			}
			return IResourceUtils.relativeToAbsolute(path).toOSString();
		}
		return null;
	}
	
	public static String getTestSourceFolder(IJavaProject javaProject) throws PluginException {
		List<IPath> sourcePaths = getSourcePaths(javaProject);
		for (IPath path : sourcePaths) {
			// FIXME LLT: just workaround, not a complete solution
			if (CollectionUtils.existIn("test", path.segments())) {
				return IResourceUtils.relativeToAbsolute(path).toOSString();
			}
		}
		return null;
	}
	
	public static List<IPath> getSourcePaths(IJavaProject project)
			throws PluginException {
		List<IPath> sourcePaths = new ArrayList<IPath>();
		try {
			for (IClasspathEntry entry : project.getResolvedClasspath(true)) {
				IPath path = entry.getPath();
				if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
					sourcePaths.add(IResourceUtils.relativeToAbsolute(path));
				}
			}
		} catch (JavaModelException e) {
			PluginException.wrapEx(e);
		}
		return sourcePaths;
	}
	
	public static String getTargetFolder(IJavaProject project) {
		String outputFolder = "";
		try {
			for(String seg: project.getOutputLocation().segments()){
				if(!seg.equals(project.getProject().getName())){
					outputFolder += seg + File.separator;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		String outputPath = project.getProject().getLocation().toOSString() + File.separator + outputFolder;
		return outputPath;
	}

	public static String createSourceFolder(IJavaProject javaProject, String name)
			throws CoreException {
		IFolder folder = javaProject.getProject().getFolder(name);
		if (!folder.exists()) {
			folder.create(false, true, null);
		}
		/* add new source folder to classpath */
		addNewSourceFolderIntoClasspath(javaProject, folder);
		return folder.getLocation().toOSString();
	}

	private static void addNewSourceFolderIntoClasspath(IJavaProject javaProject, IFolder folder)
			throws JavaModelException {
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		boolean existInClasspath = false;
		for (IClasspathEntry entry : oldEntries) {
			if (entry.getPath().equals(folder.getFullPath())) {
				existInClasspath = true;
			}
		}
		if (!existInClasspath) {
			IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
			System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
			newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
			javaProject.setRawClasspath(newEntries, null);
		}
	}

	public static ClassLoader getPrjClassLoader(IJavaProject javaProject) {
		try {
			List<URL> urlList = new ArrayList<URL>();
			List<String> classPathEntries = getPrjectClasspath(javaProject);
			for (String cpEntry : classPathEntries) {
				IPath path = new Path(cpEntry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}
			ClassLoader parentClassLoader = javaProject.getClass().getClassLoader();
			URL[] urls = (URL[]) urlList.toArray(new URL[urlList.size()]);
			URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);
			return classLoader;
		} catch (MalformedURLException e) {
			throw new SavRtException(e);
		}
	}
	
	public static final String DEFAULT_PACKAGE_NAME = "default";
	public static String getFullName(IJavaElement element) {
		LinkedList<String> fragments = new LinkedList<String>();
		IJavaElement jEle = element;
		while (true) {
			String elementName = null;
			switch (jEle.getElementType()) {
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT:
				// default package
				if (StringUtils.isEmpty(jEle.getElementName())) {
					elementName = DEFAULT_PACKAGE_NAME;
					break;
				}
			case IJavaElement.COMPILATION_UNIT:
			case IJavaElement.TYPE:
			case IJavaElement.METHOD:
				elementName = jEle.getElementName();
				break;
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				IPackageFragmentRoot root = (IPackageFragmentRoot) jEle;
				elementName = StringUtils.dotJoin((Object[])root.getResource().getProjectRelativePath().segments());
				break;
			}
			if (elementName != null) {
				fragments.addFirst(elementName);
			}
			if (jEle.getElementType() == IJavaElement.JAVA_PROJECT) {
				break;
			}
			jEle = jEle.getParent();
		}
		return StringUtils.dotJoin(fragments);
	}
}
