/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import learntest.cfg.iface.ContextFreeGrammar;

/**
 * @author LLT
 *
 */
public abstract class CfgAdapter implements ContextFreeGrammar {
	
	public enum CfgAproach {
		SOURCE_CODE_LEVEL,
		BYTE_CODE_LEVEL
	}
}
