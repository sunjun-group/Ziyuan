/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.view.dfa;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import tzuyu.engine.utils.dfa.DfaGraph;

/**
 * @author LLT
 *
 */
public class DfaLayoutAlgorithm extends VerticalLayoutAlgorithm {
	public static final int Y_OFFSET_PER_CONN = 20;
	public static final int DEPTH_PER_CONN = 30;
	public static final int DEPTH_PER_RECUR_CONN = 30;
	private static final int ROW_PADDING = 100;
	
	private DfaGraph dfaGraph;
	
	public DfaLayoutAlgorithm(DfaGraph dfaGraph) {
		super(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		this.dfaGraph = dfaGraph;
		setRowPadding(ROW_PADDING);
	}

	@Override
	protected int[] calculateNumberOfRowsAndCols(int numChildren,
			double boundX, double boundY, double boundWidth, double boundHeight) {
		int cols = 1;
		int rows = numChildren;
		int[] result = { cols, rows };
		return result;
	}
	
	@Override
	protected synchronized void applyLayoutInternal(
			InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider, double boundsX,
			double boundsY, double boundsWidth, double boundsHeight) {
		double offsetX = boundsWidth / 2;
		
		for (int i = 0; i < entitiesToLayout.length; i++) {
			int connNum = dfaGraph.getConnNum(i);
			int rowHeight = rowPadding + connNum * Y_OFFSET_PER_CONN;
			double xmove = boundsX + offsetX;
			double ymove = boundsY + i * rowHeight;
			InternalNode sn = entitiesToLayout[i];
			sn.setInternalLocation(xmove, ymove);
			fireProgressEvent(2 + i, getTotalNumberOfLayoutSteps());
			updateLayoutLocations(entitiesToLayout);
		}
		fireProgressEvent(getTotalNumberOfLayoutSteps(), getTotalNumberOfLayoutSteps());
	}
}
