/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tzuyu.engine.utils.Assert;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.constants.PluginConstants;
import tzuyu.plugin.commons.constants.Messages;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.preferences.component.MessageDialogs;
import tzuyu.plugin.tester.preferences.component.TableViewerEditablePanel;

/**
 * @author LLT
 *
 */
public class TypeScopesTablePanel extends TableViewerEditablePanel<TypeScope> {
	private static Messages msg = TzuyuPlugin.getMessages();
	private GenTestPreferences data;
	private Map<String, TypeScope> scopeMap;
	
	public TypeScopesTablePanel(Composite parent) {
		super(parent);
	}
	
	@Override
	protected Composite createContentPanel(Composite parent) {
		Composite content = super.createContentPanel(parent);
		((GridData) content.getLayoutData()).widthHint = 400;
		return content;
	}
	
	@Override
	protected TableViewer createTableViewer(Composite parent) {
		Composite tableContainer = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 150;
		tableContainer.setLayoutData(data);
		TableViewer tableViewer = super.createTableViewer(tableContainer);
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
		TableViewerColumn typeCol = new TableViewerColumn(tableViewer, SWT.NONE);
		// table columns
		typeCol.setLabelProvider(initTypeLabelProvider());
		typeCol.getColumn().setText(msg.gentest_prefs_param_generic_table_name_col());

		TableViewerColumn scopeCol = new TableViewerColumn(tableViewer,SWT.NONE);
		scopeCol.setLabelProvider(initScopeLabelProvider());
		scopeCol.getColumn().setText(msg.gentest_prefs_param_generic_table_value_col());
		
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(initTableContentProvider());
		
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableLayout.setColumnData(typeCol.getColumn(), new ColumnWeightData(150));
		tableLayout.setColumnData(scopeCol.getColumn(), new ColumnWeightData(250));
		tableContainer.setLayout(tableLayout);
		return tableViewer;
	}
	
	public void setValue(GenTestPreferences data) {
		this.data = data;
		this.scopeMap = new HashMap<String, TypeScope>(data.getSearchScopeMap());
		tableViewer.setInput(scopeMap);
		setToInitState();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onSelectTableRow(StructuredSelection selection) {
		int size = selection.size();
		// only enable remove button if there is a selected typeScope can be removed.
		// By default, we have List, Object, Class that can not be removed by user.
		boolean editable = false;
		if (size == 1) {
			TypeScope typeScope = (TypeScope) selection.getFirstElement();
			if (typeScope.getType() != null) {
				editable = true;
			}
		}
		getButton(EDIT_BTN).setEnabled(editable);
		List<TypeScope> scopes = selection.toList();
		for (TypeScope scope : scopes) {
			if (PluginConstants.CLASS_CLASS_NAME.equals(scope.getFullyQualifiedName()) ||
					PluginConstants.OBJECT_CLASS_NAME.equals(scope.getFullyQualifiedName())) {
				size --;
			}
		}
		getButton(REMOVE_BTN).setEnabled(size > 0);
	}
	
	@Override
	protected void onRemove(List<TypeScope> elements) {
		List<TypeScope> scopes = new ArrayList<TypeScope>(elements);
		scopes.remove(scopeMap.get(PluginConstants.CLASS_CLASS_NAME));
		scopes.remove(scopeMap.get(PluginConstants.OBJECT_CLASS_NAME));
		for (TypeScope scope : elements) {
			scopeMap.remove(scope.getFullyQualifiedName());
		}
		super.onRemove(scopes);
	}
	
	@Override
	protected boolean onEdit(TypeScope firstElement) {
		TypeScopeDialog dialog = new TypeScopeDialog(getShell(), 
				data.getProject(), firstElement);
		return dialog.open() == Window.OK;
	}

	protected void onAdd() {
		TypeScopeDialog dialog = new TypeScopeDialog(getShell(), 
											data.getProject(), null);
		if (dialog.open() != Window.CANCEL) {
			TypeScope typeScope = dialog.getData();
			if (isDuplicateScope(typeScope)) {
				if (MessageDialogs.confirm(getShell(),
								msg.gentest_prefs_param_generic_table_duplicate_confirm("\"" +
										typeScope.getDisplayType() + "\""))) {
					addScopeToList(typeScope);
				}
				return;
			}
			addScopeToList(typeScope);
		}
	}

	private void addScopeToList(TypeScope typeScope) {
		TypeScope oldScope = scopeMap.get(typeScope.getFullyQualifiedName());
		scopeMap.put(typeScope.getFullyQualifiedName(), typeScope);
		tableViewer.add(typeScope);
		if (oldScope != null) {
			tableViewer.remove(oldScope);
		}
	}

	private boolean isDuplicateScope(TypeScope typeScope) {
		for (String type : scopeMap.keySet()) {
			if (type.equals(typeScope.getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	private IContentProvider initTableContentProvider() {
		return new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			@Override
			public void dispose() {
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				Assert.assertTrue(inputElement == scopeMap, "ScopeMap is not set!");
				return scopeMap.values().toArray();
			}
		};
	}

	private CellLabelProvider initScopeLabelProvider() {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				TypeScope typeScope = (TypeScope)cell.getElement();
				cell.setText(typeScope.getDisplayScope());
			}
		};
	}

	private CellLabelProvider initTypeLabelProvider() {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				TypeScope typeScope = (TypeScope) cell.getElement();
				cell.setText(typeScope.getDisplayType());
				if (typeScope.hasError()) {
					cell.setImage(TypeScopeUtils.getTypeErrorImg());
				} else {
					cell.setImage(null);
				}
			}
		};
	}

	public void updateData(GenTestPreferences prefs) {
		prefs.setSearchScopeMap(scopeMap);
	}
}
