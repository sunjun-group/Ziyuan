/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.view.report.ReportLabelProvider.ImageType;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class ReportTreeViewer extends TreeViewer {
	private ReportLabelProvider textProvider;
	private ReportContentProvider contentProvider;
	private TreeViewerColumn jEleColumn;
	private Map<Object, Boolean> highlightSelected = new HashMap<Object, Boolean>();

	public ReportTreeViewer(Composite parent, ViewSettings settings) {
		super(parent, SWT.MULTI | SWT.BORDER);
		Tree tree = getTree();
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 400;
		tree.setLayoutData(layoutData);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		contentProvider = new ReportContentProvider(settings) {
			public void inputChanged(org.eclipse.jface.viewers.Viewer viewer, Object oldInput, Object newInput) {
				super.inputChanged(viewer, oldInput, newInput);
				textProvider.onInputChanged();
			};
		};
		setContentProvider(contentProvider);
		textProvider = new ReportLabelProvider();

		/* add columns */
		addJEleColumn();
//		addTestableColumn();
		addBranchCoverageColumn();
//		addUncoveredBranchesColumn();
		addLengthColumn();
		
		/* header */
		initColumnHeader();
	}
	
	public void updateHighlightElements(IModelRuntimeInfo runtimeInfo, Object[] elements) {
		boolean fullHighlight = (elements == null);
		highlightSelected.put(runtimeInfo.getJavaElement(), fullHighlight);
		for (Object ele : contentProvider.getChildren(runtimeInfo.getJavaElement())) {
			highlightSelected.put(ele, fullHighlight);
		}
		if (elements != null) {
			for (Object ele : elements) {
				highlightSelected.put(ele, true);
			}
		}
		jEleColumn.getViewer().refresh(runtimeInfo.getJavaElement());
	}

	private void initColumnHeader() {
		final TreeColumn[] columns = getTree().getColumns();
		for (ReportHeader header : ReportHeader.values()) {
			TreeColumn col = columns[header.getColIdx()];
			col.setText(header.getText());
			col.setWidth(header.getWidth());
		}
	}
	
	private TreeViewerColumn addJEleColumn() {
		jEleColumn = new TreeViewerColumn(this, SWT.LEFT);
		final ILabelProvider delegate = new WorkbenchLabelProvider();
		jEleColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText(".......");
					cell.setImage(null);
				} else {
					cell.setText(textProvider.getJEleColumnText(cell.getElement()));
					if (((IJavaElement) cell.getElement()).getAdapter(IModelRuntimeInfo.class) == null) {
						cell.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
					}
					Image image = delegate.getImage(cell.getElement());
					cell.setImage(image);
				}
			}
		});
		return jEleColumn;
	}
	
	private TreeViewerColumn addBranchCoverageColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.CENTER);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				String cvgText = textProvider.getBranchCoverageText(cell.getElement());
				if (ReportLabelProvider.ERROR == cvgText || "0%".equals(cvgText)) {
					cell.setText("0%");
					Image img = textProvider.getImage(ImageType.ERROR);
					cell.setImage(img);
				} else {
					cell.setText(cvgText);
					if (!StringUtils.isEmpty(cvgText)) {
						cell.setImage(textProvider.getImage(ImageType.SUCCESS));
					} else {
						cell.setImage(null);
					}
				}
			}
		});
		return column;
	}

	private TreeViewerColumn addTestableColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.CENTER);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText("");
					cell.setImage(null);
				} else {
					cell.setText("TODO-IMAGE");
				}
			}
		});
		return column;
	}

	private TreeViewerColumn addUncoveredBranchesColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.RIGHT);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(textProvider.getUncoveredBranchesText(cell.getElement()));
			}
		});
		return column;
	}

	private TreeViewerColumn addLengthColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.RIGHT);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText(textProvider.getMethodLengthText(cell.getElement()));
			}
		});
		return column;
	}
	
	private static enum ReportHeader {
		JELE_COLUMN ("", 500),
//		TEST_STATUS_COLUMN ("", 100),
		BRANCH_COVERAGE("Branch coverage", 100),
//		MISSING_BRANCH_COLUMN ("Uncovered branches", 200),
		TARGET_METHOD_LENGHT_COLUMN ("Method length", 200);
		
		private int width;
		private String text;
		private ReportHeader(String text, int width) {
			this.text = text;
			this.width = width;
		}
		
		public String getText() {
			return text;
		}
		
		public int getColIdx() {
			return ordinal();
		}
		
		public int getWidth() {
			return width;
		}
	}

}
