/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import tzuyu.engine.utils.CollectionUtils;
import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.core.utils.ResourcesUtils;
import tzuyu.plugin.preferences.component.EditDialog;
import tzuyu.plugin.preferences.component.RadioBtnGroup;
import tzuyu.plugin.proxy.PluginReferencesAnalyzer;
import tzuyu.plugin.ui.SWTFactory;
import tzuyu.plugin.ui.ValueChangedEvent;
import tzuyu.plugin.ui.ValueChangedListener;

/**
 * @author LLT
 *
 */
public class GenericSearchScopeDialog extends EditDialog<TypeScope> {
	private Label typeTx;
	private Button selectTypeBtn;
	private RadioBtnGroup<SearchScope> scopeGroup;
	private TableViewer classesViewer;
	
	private IType selectedType;
	private List<IType> selectedClasses;
	private Button addImplBtn;
	private IJavaProject project;
	
	public GenericSearchScopeDialog(Shell parentShell, IJavaProject project, TypeScope data) {
		super(parentShell, data);
		this.project = project;
		selectedClasses = new ArrayList<IType>();
	}
	
	@Override
	protected TypeScope initData() {
		return new TypeScope();
	}

	protected void createContent(Composite parent) {
		int contentCol = 3;
		Composite content = SWTFactory.createGridPanel(parent, contentCol);
		/* type */
		Label typeLb = SWTFactory.createLabel(content, msg.genericSearchScopeDialog_type());
		GridData gridData = new GridData();
		gridData.widthHint = 70;
		typeLb.setLayoutData(gridData);
		typeTx = new Label(content, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 300;
		
		typeTx.setLayoutData(gridData);
		selectTypeBtn = SWTFactory.createBtnAlignFill(content,
				msg.common_select());
		
		/* scope */
		scopeGroup = new RadioBtnGroup<SearchScope>(parent, 
				msg.genericSearchScopeDialog_scope(), contentCol);
		scopeGroup.add(SearchScope.SOURCE);
		scopeGroup.add(SearchScope.SOURCE_JARS);
		Button userDefinedRd = SWTFactory.createRadioBtn(scopeGroup.getWidget(), msg.getMessage(SearchScope.USER_DEFINED));
		userDefinedRd.setData(SearchScope.USER_DEFINED);
		scopeGroup.add(userDefinedRd, 1);
		createUserDefinedScopePanel(scopeGroup.getWidget());
	}
	
	@Override
	protected void refresh(TypeScope data) {
		selectedType = data.getType();
		updateTypeTx();
		scopeGroup.setValue(data.getScope());
		onSelectScope(data.getScope());
		addImplClassToResult(data.getImplTypes().toArray());
	}
	
	@Override
	protected void updateData(TypeScope data) {
		data.setType(selectedType);
		data.setScope(scopeGroup.getValue());
		data.setImplTypes(selectedClasses);
	}
	
	private void createUserDefinedScopePanel(Composite parent) {
		Composite content = SWTFactory.createGridPanel(parent, 2);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 20;
		data.horizontalIndent = 20;
		content.setLayoutData(data);
		classesViewer = new TableViewer(content, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		classesViewer.getTable().setLayoutData(data);
		classesViewer.setLabelProvider(initImplClassesLabelProvider());
		
		/* button group on the right (add, remove) */
		Composite btnGroup = new Composite(content, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		btnGroup.setLayout(layout);
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		btnGroup.setLayoutData(data);
		addImplBtn = SWTFactory.createBtnAlignFill(btnGroup, msg.common_addButton());
		addImplBtn.setText(msg.common_addButton());
	}

	private IBaseLabelProvider initImplClassesLabelProvider() {
		return new ImplClassItemLabelProvider();
	}

	@SuppressWarnings({ "restriction"})
	protected void onAddImplTypes() throws JavaModelException {
		int elementKinds = IJavaSearchConstants.TYPE;
		JavaSearchScope scope;
		if (Enum.class.getName().equals(selectedType.getFullyQualifiedName())) {
			scope = (JavaSearchScope) BasicSearchEngine.createJavaSearchScope(
					new IJavaElement[] { project }, false);
			elementKinds = IJavaSearchConstants.ENUM;
		} else {
			scope = new JavaSearchScope();
			for (IType ele : PluginReferencesAnalyzer
					.getAllSubtypes(project, selectedType)) {
				if (ResourcesUtils.isPublicNotInterfaceOrAbstract(ele)) {
					scope.add(ele);
				}
			}
		}
		
		FilteredItemsSelectionDialog dialog = new OpenTypeSelectionDialog(getShell(), true, 
				PlatformUI.getWorkbench().getProgressService(), 
				scope,
				elementKinds);
		
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		if (dialog.open() != OK) {
			return;
		}
		Object[] types = dialog.getResult();
		addImplClassToResult(types);
	}

	private void addImplClassToResult(Object[] types) {
		if (CollectionUtils.isEmpty(types)) {
			return;
		}
		for (Object type : types) {
			if (!selectedClasses.contains(type)) {
				selectedClasses.add((IType) type);
				classesViewer.add(type);
			}
		}
	}

	@Override
	protected void registerListener() {
		selectTypeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSelectType();
			}
		});
		
		scopeGroup.addValueChangedListener(new ValueChangedListener<SearchScope>(
				scopeGroup) {
			@Override
			public void onValueChanged(ValueChangedEvent<SearchScope> event) {
				onSelectScope(event.getNewVal());
			}
		}, false);
		
		addImplBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					onAddImplTypes();
				} catch (JavaModelException e1) {
					// ignore
				}
			}
		});
	}
	
	/**
	 * change behavior of the classesViewer whenever a scope is selected.
	 */
	protected void onSelectScope(SearchScope scope) {
		boolean enabled = (scope == SearchScope.USER_DEFINED); 
		addImplBtn.setEnabled(enabled && selectedType != null);
		classesViewer.getTable().setEnabled(enabled);
	}

	protected void onSelectType() {
		JavaSearchScope scope = getSearchScopeForType();
		SelectionDialog dialog = new OpenTypeSelectionDialog(getShell(), false, 
				PlatformUI.getWorkbench().getProgressService(), 
				scope,
				IJavaSearchConstants.CLASS_AND_INTERFACE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		if (dialog.open() != OK) {
			return;
		}
		Object[] types = dialog.getResult();
		if (CollectionUtils.isEmpty(types) || types.length != 1) {
			return;
		}
		IType newType = (IType) types[0];
		if (!newType.equals(selectedType)) {
			selectedType = newType;
			updateTypeTx();
			selectedClasses.clear();
			classesViewer.remove(selectedClasses
					.toArray(new IType[selectedClasses.size()]));
			classesViewer.refresh();
			// update add button
			onSelectScope(scopeGroup.getValue());
		}
	}

	private void updateTypeTx() {
		String text = selectedType == null ? StringUtils.EMPTY : selectedType
				.getFullyQualifiedName();
		typeTx.setText(text);
	}

	@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
	private JavaSearchScope getSearchScopeForType() {
		JavaSearchScope scope = (JavaSearchScope) BasicSearchEngine.createJavaSearchScope(
				new IJavaElement[]{project}, false);
		scope = new JavaSearchScope() {
			@Override
			public boolean encloses(IJavaElement element) {
				if (element.getElementType() == IJavaElement.TYPE) {
					try {
						int flags = ((IType)element).getFlags();
						if (Flags.isAbstract(flags) ||
								Flags.isInterface(flags)) {
							return super.encloses(element);
						}
					} catch (JavaModelException e) {
						// ignore
					}
				}
				return false;
			}
		};
		HashSet projs = new HashSet<IJavaProject>();
		projs.add(project);
		try {
			scope.add((JavaProject) project, IJavaSearchScope.SOURCES
					| IJavaSearchScope.APPLICATION_LIBRARIES
					| IJavaSearchScope.SYSTEM_LIBRARIES, projs);
		} catch (JavaModelException e) {
			// ignore
		}
		return scope;
	}

	@Override
	protected String getTitleSuffix(OperationMode mode) {
		return msg.genericSearchScopeDialog_title();
	}
	
	private static class ImplClassItemLabelProvider extends LabelProvider {
		public ImplClassItemLabelProvider() {
			
		}
		
		@Override
		public Image getImage(Object element) {
			return JavaPlugin.getImageDescriptorRegistry().get(
					new JavaElementImageDescriptor(
							JavaPluginImages.DESC_OBJS_CLASS, 0,
							JavaElementImageProvider.BIG_SIZE));
		}
		
		@Override
		public String getText(Object element) {
			return TypeScope.getDisplayString((IType) element);
		}
	}
}
