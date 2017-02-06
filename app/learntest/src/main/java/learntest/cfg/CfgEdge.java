/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import learntest.cfg.graph.Edge;

/**
 * @author LLT
 *
 */
public class CfgEdge extends Edge<CfgNode>{
	
	public CfgEdge(CfgNode source, CfgNode dest) {
		super(source, dest);
	}

	public Type getType() {
		return Type.BLANK;
	}

	@Override
	public CfgEdge clone(CfgNode newDest) {
		CfgEdge newEdge = new CfgEdge(getSource(), newDest);
		newEdge.setProperties(getProperties());
		return newEdge;
	}
	
	public static enum Type {
		BLANK,
		TRUE,
		FALSE,
		BRANCH
	}
	
}
