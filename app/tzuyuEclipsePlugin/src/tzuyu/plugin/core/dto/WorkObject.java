/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;

import tzuyu.plugin.core.utils.ResourcesUtils;

/**
 * @author LLT
 */
public class WorkObject {
	private Map<IJavaProject, List<WorkItem>> projectMap = new HashMap<IJavaProject, List<WorkItem>>();
	
	public IJavaProject getFirstProject() {
		return projectMap.keySet().iterator().next();
	}
	
	public boolean isEmtpy() {
		return projectMap == null || projectMap.isEmpty();
	}
	
	public int size() {
		if (isEmtpy()) {
			return -1;
		}
		return projectMap.size();
	}
	
	public Map<IJavaProject, List<WorkItem>> getProjectMap() {
		return projectMap;
	}
	
	public Set<IJavaProject> getProjects() {
		return projectMap.keySet();
	}
	
	public List<WorkItem> getWorkItems(IJavaProject project) {
		return projectMap.get(project);
	}
	
	public List<WorkItem> getFirstItem() {
		Assert.isLegal(size() == 1);
		return projectMap.entrySet().iterator().next().getValue();
	}

	/**
	 * Collects and combines the selection which may contain sources from
	 * different projects and / or multiple sources from same project.
	 * <p>
	 * If selection contains hierarchical data (like file and it's parent
	 * directory), the only topmost element is returned (same for directories
	 * from projects).
	 * <p>
	 * The children from selected parents are not resolved, so that the return
	 * value contains the 'highest' possible hierarchical elements without
	 * children.
	 */
	public static WorkObject getResourcesPerProject(
			IStructuredSelection structuredSelection) {
		WorkObject workObject = new WorkObject();

		for (Iterator<?> iter = structuredSelection.iterator(); iter.hasNext();) {
			Object element = iter.next();
			workObject.extendFrom(element);
		}
		return workObject;
	}

	public WorkObject extendFrom(Object element) {
		if (element instanceof IResource) {
			return extendFromIResource((IResource) element);
		}

		if (element instanceof IJavaElement) {
			return extendFromIJavaElement((IJavaElement) element);
		}

		if (element instanceof IAdaptable) {
			return extendFromIAdaptable((IAdaptable) element);
		}
		return this;
	}

	private WorkObject extendFromIAdaptable(IAdaptable element) {
		Object adapter = element.getAdapter(IResource.class);
		if (adapter instanceof IResource) {
			return extendFromIResource((IResource) adapter);
		}
		adapter = ((IAdaptable) element).getAdapter(IPackageFragment.class);
		if (adapter instanceof IPackageFragment) {
			return extendFromIJavaElement((IPackageFragment) adapter);
		}
		adapter = ((IAdaptable) element).getAdapter(IType.class);
		if (adapter instanceof IType) {
			return extendFromIJavaElement((IType) adapter);
		}
		return this;
	}

	private WorkObject extendFromIJavaElement(IJavaElement element) {
		List<WorkItem> projectItems = getProjectItems(element.getJavaProject());
		if (!isAlreadyIncluded(element, projectItems)) {
			projectItems.add(new WorkItem(element.getJavaProject(), null,
					element));
		}
		return this;
	}

	private WorkObject extendFromIResource(IResource resource) {
		if (resource.getType() == IResource.FILE
				&& !ResourcesUtils.isJavaArtifact(resource)
				|| !resource.isAccessible()) {
			// Ignore non java files or deleted/closed files/projects
			return this;
		}
		if (!ResourcesUtils.isJavaProject(resource.getProject())) {
			return this;
		}
		IJavaProject project = JavaCore.create(resource.getProject());
		getProjectItems(project).add(new WorkItem(project, resource, null));
		return this;
	}

	private boolean isAlreadyIncluded(IJavaElement element, List<WorkItem> items) {
		IPath location = element.getPath();
		if (location == null) {
			return false;
		}
		for (WorkItem item : items) {
			if (!item.isDirectory()) {
				continue;
			}
			IPath parentLoc = item.getPath();
			if (parentLoc != null && parentLoc.isPrefixOf(location)) {
				return true;
			}
		}
		return false;
	}

	private List<WorkItem> getProjectItems(IJavaProject project) {
		List<WorkItem> items = projectMap.get(project);
		if (items == null) {
			items = new ArrayList<WorkItem>();
			projectMap.put(project, items);
		}
		return items;
	}

	/**
	 * @author LLT
	 */
	public static class WorkItem {
		IJavaProject project;
		IResource resource;
		IJavaElement jEle;

		public WorkItem(IJavaProject project, IResource resource,
				IJavaElement jele) {
			this.project = project;
			this.resource = resource;
			this.jEle = jele;
		}

		public boolean isDirectory() {
			IResource corespondingResource = getCorrespondingResource();
			if (corespondingResource != null) {
				return corespondingResource.getType() == IResource.FOLDER
						|| corespondingResource.getType() == IResource.PROJECT;
			}
			return false;
		}

		public IPath getPath() {
			IResource corespondingResource = getCorrespondingResource();
			if (corespondingResource != null) {
				return corespondingResource.getLocation();
			}
			if (jEle != null) {
				return jEle.getPath();
			}
			return null;
		}

		public IResource getCorrespondingResource() {
			if (resource != null) {
				return resource;
			}
			try {
				IResource resource1 = jEle.getCorrespondingResource();
				if (resource1 != null) {
					return resource1;
				}
				IJavaElement ancestor = jEle
						.getAncestor(IJavaElement.COMPILATION_UNIT);
				if (ancestor != null) {
					return ancestor.getCorrespondingResource();
				}
			} catch (JavaModelException e) {
				// ignore, just return nothing
			}
			return null;
		}
		
		public IJavaElement getCorrespondingJavaElement() {
			if (jEle != null) {
				return jEle;
			}
			return JavaCore.create(resource);
		}
	}

}
