/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.StringUtils;
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
	private Map<String, String> typesMap;
	private GenTestPreferences data;
	
	public GenericParamGroup(Composite parent) {
		super(parent, SWT.NONE);
		typesMap = new HashMap<String, String>();
		setLayout();
		createContent(this);
		registerListener();
	}

	private void registerListener() {
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
		// TODO Auto-generated method stub
	}

	protected void onAdd() {
		GenericSearchScopeDialog dialog = new GenericSearchScopeDialog(
				getShell(), data);
		if (dialog.open() == Window.CANCEL) {
			return;
		}
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
		tableViewer.setInput(typesMap);
		
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
				if (inputElement == typesMap) {
					return typesMap.keySet().toArray();
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
				// TODO Auto-generated method stub
				
			}
		};
	}

	private CellLabelProvider initTypeLabelProvider() {
		return new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	public void setValue(GenTestPreferences data) {
		this.data = data;
	}
}
