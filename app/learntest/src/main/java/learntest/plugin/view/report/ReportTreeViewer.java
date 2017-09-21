/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author LLT
 *
 */
public class ReportTreeViewer extends TreeViewer {
	private ReportLabelProvider textProvider;

	public ReportTreeViewer(Composite parent) {
		super(parent);
		Tree tree = new Tree(parent, SWT.MULTI);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		textProvider = new ReportLabelProvider();
		/* add columns */
		addJEleColumn();
		addTestableColumn();
		addGeneratedTestColumn();
		addStartLineColumn();
		addLengthColumn();
	}

	private TreeViewerColumn addJEleColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.LEFT);
		column.setLabelProvider(new CellLabelProvider() {
			private final ILabelProvider delegate = new WorkbenchLabelProvider();
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText(".......");
					cell.setImage(null);
				} else {
					cell.setText(textProvider.getJEleColumnText(cell.getElement()));
					cell.setImage(delegate.getImage(cell.getElement()));
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

	private TreeViewerColumn addGeneratedTestColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.RIGHT);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText("");
					cell.setImage(null);
				} else {
					cell.setText("TODO-TESTCASES");
				}
			}
		});
		return column;
	}

	private TreeViewerColumn addStartLineColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.RIGHT);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText("");
					cell.setImage(null);
				} else {
					cell.setText("TODO-STARTLINE");
				}
			}
		});
		return column;
	}

	private TreeViewerColumn addLengthColumn() {
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.RIGHT);
		column.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == ReportContentProvider.LOADING) {
					cell.setText("");
					cell.setImage(null);
				} else {
					cell.setText("TODO-LENGTH");
				}
			}
		});
		return column;
	}
}
