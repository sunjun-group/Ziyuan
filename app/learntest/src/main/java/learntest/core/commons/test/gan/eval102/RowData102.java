/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan.eval102;

import learntest.core.commons.test.gan.AbstractRowData;

/**
 * @author LLT
 *
 */
public class RowData102 extends AbstractRowData {
	private String id;
	private NodeExportData102 nodeData;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public NodeExportData102 getNodeData() {
		return nodeData;
	}

	public void setNodeData(NodeExportData102 nodeData) {
		this.nodeData = nodeData;
	}

}
