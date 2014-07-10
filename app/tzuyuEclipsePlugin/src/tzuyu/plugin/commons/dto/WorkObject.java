/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;

import tzuyu.plugin.commons.utils.ResourcesUtils;

/**
 * @author LLT
 */
public class WorkObject {
	private IJavaProject project;
	private List<WorkItem> workItems = new ArrayList<WorkObject.WorkItem>();
	
	public WorkObject() {

	}
	
	public void update(List<WorkItem> items) {
		this.workItems.clear();
		this.workItems.addAll(items);
	}
	
	public WorkObject(IJavaProject project, List<WorkItem> items) {
		this.project = project;
		this.workItems = items;
	}

	public IJavaProject getProject() {
		return project;
	}
	
	public boolean isEmtpy() {
		return project == null || workItems.isEmpty();
	}
	
	public List<WorkItem> getWorkItems() {
		return workItems;
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
			projectItems.add(new WorkItem(null, element));
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
		getProjectItems(project).add(new WorkItem(resource, null));
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
		if (this.project == null) {
			this.project = project;
		}
		if (this.project.equals(project)) {
			return workItems;
		} 
		return new ArrayList<WorkObject.WorkItem>();
	}

	/**
	 * @author LLT
	 */
	public static class WorkItem {
		IResource resource;
		IJavaElement jEle;

		public WorkItem(IResource resource,
				IJavaElement jele) {
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
