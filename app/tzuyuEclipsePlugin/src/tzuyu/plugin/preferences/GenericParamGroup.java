/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.ui.SWTFactory;

/**
 * @author LLT
 *
 */
public class GenericParamGroup extends Composite {
	private static Messages msg = TzuyuPlugin.getMessages();
	private Button addBtn;
	private Button editBtn;
	private Button removeBtn;
	private TableViewer tableViewer;
	
	private GenTestPreferences data;
	private Map<String, TypeScope> scopeMap;
	
	public GenericParamGroup(Composite parent) {
		super(parent, SWT.NONE);
		setLayout();
		createContent(this);
		registerListener();
	}

	private void registerListener() {
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection)event.getSelection();
				int size = selection.size();
				editBtn.setEnabled(size == 1);
				removeBtn.setEnabled(size > 0);
			}
		});
		addBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAdd();
			}
		});
		
		editBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onEdit();
			}
		});
		
		removeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onRemove();
			}
		});
	}

	protected void onRemove() {
		// TODO Auto-generated method stub
	}

	protected void onEdit() {
		TypeScope typeScope = (TypeScope) ((StructuredSelection) tableViewer
				.getSelection()).getFirstElement();
		GenericSearchScopeDialog dialog = new GenericSearchScopeDialog(getShell(), 
				data.getProject(), typeScope);
		if (dialog.open() != Window.CANCEL) {
			tableViewer.refresh(dialog.getData());
		}
	}

	protected void onAdd() {
		GenericSearchScopeDialog dialog = new GenericSearchScopeDialog(
				getShell(), data.getProject(), null);
		if (dialog.open() != Window.CANCEL) {
			TypeScope typeScope = dialog.getData();
			if (isDuplicateScope(typeScope)) {
				 
			}
			addScopeToList(typeScope);
			return;
		}
	}

	private void addScopeToList(TypeScope typeScope) {
		scopeMap.put(typeScope.getFullyQualifiedName(), typeScope);
		tableViewer.add(typeScope);
	}

	private boolean isDuplicateScope(TypeScope typeScope) {
		// TODO Auto-generated method stub
		return false;
	}

	private void setLayout() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 400;
		setLayoutData(data);
	}

	private void createContent(Composite contentPanel) {
		// table on the left
		Composite tableContainer = new Composite(contentPanel, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 150;
		tableContainer.setLayoutData(data);
		
		Table table = new Table(tableContainer, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setToolTipText(null);
	
		tableViewer = new TableViewer(table);
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
		TableViewerColumn typeCol = new TableViewerColumn(tableViewer, SWT.NONE);
		// table columns
		typeCol.setLabelProvider(initTypeLabelProvider());
		typeCol.getColumn().setText(msg.gentest_prefs_param_generic_table_name_col());
		typeCol.getColumn().setWidth(100);

		TableViewerColumn scopeCol = new TableViewerColumn(tableViewer,SWT.NONE);
		scopeCol.setLabelProvider(initScopeLabelProvider());
		scopeCol.getColumn().setText(msg.gentest_prefs_param_generic_table_value_col());
		scopeCol.getColumn().setWidth(400);
		
		table.setHeaderVisible(true);
		tableViewer.setContentProvider(initTableContentProvider());
		
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableLayout.setColumnData(typeCol.getColumn(), new ColumnWeightData(100));
		tableLayout.setColumnData(scopeCol.getColumn(), new ColumnWeightData(300));
		tableContainer.setLayout(tableLayout);
		// buttons on the right.
		createButtonGroup(contentPanel);
	}

	private IContentProvider initTableContentProvider() {
		return new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// DO NOTHING
			}
			
			@Override
			public void dispose() {
				// DO NOTHING
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement == scopeMap) {
					return scopeMap.keySet().toArray();
				}
				return null;
			}
		};
	}

	private void createButtonGroup(Composite contentPanel) {
		Composite btnGroup = new Composite(contentPanel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		btnGroup.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		btnGroup.setLayoutData(data);
		addBtn = SWTFactory.createBtnAlignFill(btnGroup, msg.common_addButton());
		editBtn = SWTFactory.createBtnAlignFill(btnGroup, msg.common_editButton());
		removeBtn = SWTFactory.createBtnAlignFill(btnGroup, msg.common_removeButton());
	}

	private CellLabelProvider initScopeLabelProvider() {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((TypeScope)cell.getElement()).getDisplayImplTypes());
			}
		};
	}

	private CellLabelProvider initTypeLabelProvider() {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((TypeScope) cell.getElement()).getDisplayType());
			}
		};
	}

	public void setValue(GenTestPreferences data) {
		this.data = data;
		this.scopeMap = data.getSearchScopeMap();
		editBtn.setEnabled(false);
		removeBtn.setEnabled(false);
	}
}
