/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg.iface;

import java.util.List;

import learntest.cfg.impl.javasource.core.CfgEdge;
import learntest.cfg.impl.javasource.core.CfgNode;

/**
 * @author LLT
 *
 */
public interface ContextFreeGrammar {

	List<CfgEdge> getEntryOutEdges();

	List<CfgNode> getVertices();

	List<CfgEdge> getOutEdges(CfgNode node);

	CfgNode getEntry();

	CfgNode getExit();

}
