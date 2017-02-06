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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import tzuyu.engine.utils.Pair;
import tzuyu.plugin.tester.preferences.component.EditDialog;
import tzuyu.plugin.tester.preferences.component.RadioBtnGroup;
import tzuyu.plugin.tester.preferences.component.TableViewerEditablePanel;
import tzuyu.plugin.tester.ui.SWTFactory;
import tzuyu.plugin.tester.ui.ValueChangedEvent;
import tzuyu.plugin.tester.ui.ValueChangedListener;

/**
 * @author LLT
 *
 */
public class TypeScopeDialog extends EditDialog<TypeScope> {
	private Label typeTx;
	private Button selectTypeBtn;
	private RadioBtnGroup<SearchScope> scopeGroup;
	
	private IType selectedType;
	private List<Pair<String, IType>> selectedClasses;
	private IJavaProject project;
	private TableViewerEditablePanel<IType> tablePanel;
	private TableViewer classesViewer;
	
	public TypeScopeDialog(Shell parentShell, IJavaProject project, TypeScope data) {
		super(parentShell, data);
		this.project = project;
		selectedClasses = new ArrayList<Pair<String,IType>>();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage(msg.typeScopeDialog_desc());
		return super.createDialogArea(parent);
	}
	
	@Override
	protected TypeScope initData() {
		return new TypeScope();
	}

	protected void createContent(Composite parent) {
		int contentCol = 3;
		Composite content = SWTFactory.createGridPanel(parent, contentCol);
		/* type */
		Label typeLb = SWTFactory.createLabel(content, msg.typeScopeDialog_type());
		GridData gridData = new GridData();
		gridData.widthHint = 70;
		typeLb.setLayoutData(gridData);
		typeTx = new Label(content, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 300;
		
		typeTx.setLayoutData(gridData);
		selectTypeBtn = SWTFactory.createBtnAlignFill(content,
				msg.common_select());
		selectTypeBtn.setEnabled(mode == OperationMode.NEW);
		/* scope */
		scopeGroup = new RadioBtnGroup<SearchScope>(parent, 
				msg.typeScopeDialog_scope(), contentCol);
		scopeGroup.add(SearchScope.SOURCE);
		scopeGroup.add(SearchScope.SOURCE_JARS);
		Button userDefinedRd = SWTFactory.createRadioBtn(
				scopeGroup.getWidget(),
				msg.getMessage(SearchScope.USER_DEFINED));
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
		addImplClassToResult(data.getImplTypes());
	}
	
	@Override
	protected void updateData(TypeScope data) {
		data.setType(selectedType);
		data.setScope(scopeGroup.getValue());
		if (data.getScope() == SearchScope.USER_DEFINED) {
			data.setImplTypes(selectedClasses);
		} else {
			data.setImplTypes(new ArrayList<Pair<String,IType>>());
		}
	}
	
	private void createUserDefinedScopePanel(Composite parent) {
		tablePanel = new TableViewerEditablePanel<IType>(parent) {
			@Override
			protected void onAdd() {
				try {
					onAddImplTypes();
				} catch (JavaModelException e) {
					// ignore
				}
			}
			
			@Override
			protected void onRemove(List<IType> elements) {
				super.onRemove(elements);
				selectedClasses.removeAll(elements);
			}
		};
		tablePanel.hide(TableViewerEditablePanel.EDIT_BTN);
		tablePanel.getTableViewer().setLabelProvider(
				initImplClassesLabelProvider());
		classesViewer = tablePanel.getTableViewer();
	}
	
	@Override
	protected String validate() {
		if (selectedType == null) {
			return msg.error_not_defined(msg.typeScopeDialog_type());
		}
		if (scopeGroup.getValue() == SearchScope.USER_DEFINED
				&& selectedClasses.isEmpty()) {
			return msg.error_not_defined(msg
					.typeScopeDialog_impl_type());
		}
		return null;
	}

	private IBaseLabelProvider initImplClassesLabelProvider() {
		return new ImplClassItemLabelProvider();
	}

	protected void onAddImplTypes() throws JavaModelException {
		SelectionDialog dialog = TypeScopeUtils.getImplTypeDialog(selectedType,
				project, getShell());
		if (dialog.open() != OK) {
			return;
		}
		Object[] types = dialog.getResult();
		List<Pair<String, IType>> result = new ArrayList<Pair<String,IType>>(types.length);
		for (Object typeObj : types) {
			IType type = (IType) typeObj;
			result.add(Pair.of(type.getFullyQualifiedName(), type));
		}
		addImplClassToResult(result);
	}

	/**
	 * @param types: Pair<String, IType>
	 */
	private void addImplClassToResult(List<Pair<String, IType>> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (Pair<String, IType> type : list) {
			if (!selectedClasses.contains(type)) {
				selectedClasses.add(type);
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
	}
	
	/**
	 * change behavior of the classesViewer whenever a scope is selected.
	 */
	protected void onSelectScope(SearchScope scope) {
		boolean enabled = (scope == SearchScope.USER_DEFINED); 
		tablePanel.getAddBtn().setEnabled(
				enabled && selectedType != null);
		classesViewer.getTable().setEnabled(enabled);
		tablePanel.getRemoveBtn().setEnabled(false);
	}

	protected void onSelectType() {
		SelectionDialog dialog = TypeScopeUtils
				.getAbstractTypeDialog(project, getParentShell());
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

	protected String getShellTitleSuffix(OperationMode mode) {
		return msg.typeScopeDialog_title();
	}
	
	private static class ImplClassItemLabelProvider extends LabelProvider {
		public ImplClassItemLabelProvider() {
			
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Image getImage(Object element) {
			Pair<String, IType> typePair = (Pair<String, IType>) element;
			return TypeScopeUtils.getImplTypeImg(typePair.b == null);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public String getText(Object element) {
			Pair<String, IType> typePair = (Pair<String, IType>) element;
			return TypeScope.getDisplayString(typePair.b, typePair.a);
		}
	}
}
