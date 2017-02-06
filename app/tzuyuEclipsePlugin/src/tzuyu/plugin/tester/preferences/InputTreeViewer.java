/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.dto.WorkObject.WorkItem;
import tzuyu.plugin.tester.reporter.PluginLogger;
import tzuyu.plugin.tester.ui.AppEvent;
import tzuyu.plugin.tester.ui.AppEventManager;
import tzuyu.plugin.tester.ui.ValueChangedEvent;

/**
 * @author LLT
 * 
 */
public class InputTreeViewer extends CheckboxTreeViewer {
	private ContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private AppEventManager eventManager;

	public InputTreeViewer(Composite parent, AppEventManager eventManager) {
		super(parent, SWT.BORDER);
		this.eventManager = eventManager;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		getControl().setLayoutData(gd);
		contentProvider = new ContentProvider();
		labelProvider = new LabelProvider();
		setLabelProvider(labelProvider);
		setContentProvider(contentProvider);
		addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object[] oldValue = getCheckedElements();
				// select all its children if selected
				Object[] children;
				Object parent;
				if (event.getChecked()
						&& (children = contentProvider.getChildren(event
								.getElement())) != ContentProvider.EMPTY) {
					setCheckedElements(children);
				}
				// unselect its parent if unselected
				if (!event.getChecked()) {
					parent  = event.getElement();
					while (parent != null) {
						setChecked(parent, false);
						parent = contentProvider.getParent(parent);
					} 
				}
				
				fireEvent(new ValueChangedEvent<Object[]>(InputTreeViewer.this, oldValue, 
						getCheckedElements()));
			}
		});
	}

	protected void fireEvent(AppEvent event) {
		if (eventManager != null) {
			eventManager.fireEvent(event);
		}
	}

	public void setData(WorkObject workObject) {
		setInput(workObject);
		List<Object> selectedItems = new ArrayList<Object>();
		for (WorkItem item : workObject.getWorkItems()) {
			selectedItems.add(item.getCorrespondingJavaElement());
		}
		expandAll();
		refresh();
		setCheckedElements(selectedItems.toArray());
	}
	
	public void updateData(WorkObject workObject) {
		List<WorkItem> items = new ArrayList<WorkObject.WorkItem>();
		for (Object ele : getCheckedElements()) {
			if (!getChecked(contentProvider.getParent(ele))) {
				items.add(new WorkItem(null, (IJavaElement) ele));
			}
		}
		workObject.update(items);
	}
	
	@Override
	public void setCheckedElements(Object[] elements) {
		// check the node and all its children nodes.
		for (Object ele : elements) {
			setCheckedElements(contentProvider.getChildren(ele));
			super.setChecked(ele, true);
		}
	}
	
	private static class ContentProvider implements ITreeContentProvider {
		protected static final Object[] EMPTY = new Object[0];

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			// if class
			try {
				if (parentElement instanceof WorkObject) {
					return getAllCompilationUnit(((WorkObject)parentElement));
				} else if (parentElement instanceof ICompilationUnit) {
					ICompilationUnit compEle = (ICompilationUnit) parentElement;
					return compEle.getTypes();
				} else if (parentElement instanceof IType) {
					return ((IType) parentElement).getMethods();
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
			}
			return EMPTY;
		}

		private Object[] getAllCompilationUnit(WorkObject workObject) {
			List<Object> result = new ArrayList<Object>();
			for (WorkItem item : workObject.getWorkItems()) {
				IJavaElement javaEle = item.getCorrespondingJavaElement();
				Object cUnit = null;
				switch (javaEle.getElementType()) {
				case IJavaElement.METHOD:
					cUnit = javaEle.getParent();
					break;
				case IJavaElement.COMPILATION_UNIT:
					cUnit =  javaEle;
					break;
				case IJavaElement.TYPE:
					cUnit = javaEle;
				default:
					break;
				}
				if (cUnit != null && !result.contains(cUnit)) {
					result.add(cUnit);
				}
			}
			return result.toArray();
			
		}

		public Object getParent(Object element) {
			if (element instanceof IType) {
				return ((IType) element).getParent();
			} else if (element instanceof IMember) {
				return ((IMember) element).getParent();
			} 
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

	}

	private static class LabelProvider extends JavaElementLabelProvider {

	}
}
