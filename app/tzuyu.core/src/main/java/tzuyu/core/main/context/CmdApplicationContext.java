/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.context;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import sav.common.core.iface.IPrintStream;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 *
 */
public class CmdApplicationContext extends AbstractApplicationContext {
	
	public CmdApplicationContext(String[] args) {
		Options opts = createOptions();
	}

	@SuppressWarnings("static-access")
	private Options createOptions() {
		Options options = new Options();
		Option option = OptionBuilder
				.withArgName("")
				.hasArg(true)
				.withDescription(
						"thread id to select for slicing (default: main thread)")
				.withLongOpt("threadid").create('t');
		options.addOption(option);
		return null;
	}
	
	@Override
	protected String getTracerJarPath() {
		return "TODO-missing tracerJarPath";
	}

	@Override
	protected List<String> getProjectClasspath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getJavahome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpectrumAlgorithm getSuspiciousnessCalculationAlgorithm() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tzuyu.core.main.context.AbstractApplicationContext#getVmRunnerPrintStream()
	 */
	@Override
	public IPrintStream getVmRunnerPrintStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tzuyu.core.main.context.AbstractApplicationContext#getAssembly(java.lang.String)
	 */
	@Override
	protected String getAssembly(String tzuyuJacocoAssembly) {
		// TODO Auto-generated method stub
		return null;
	}
}
