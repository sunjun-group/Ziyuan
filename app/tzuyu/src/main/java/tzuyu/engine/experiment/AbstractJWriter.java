/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.experiment;

import tzuyu.engine.experiment.JWriterFactory.JunitConfig;

/**
 * @author LLT
 *
 */
public abstract class AbstractJWriter {
	protected JunitConfig config;
	protected VariableRenamer renamer;

	public AbstractJWriter(JunitConfig config, VariableRenamer renamer) {
		this.config = config;
		this.renamer = renamer;
	}
	
	public abstract void writeCode(StringBuilder sb);
}
