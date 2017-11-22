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
	private UpdateType updateType;

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
	
	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	public static enum UpdateType {
		COVERAGE,
		ALL
	}
}
