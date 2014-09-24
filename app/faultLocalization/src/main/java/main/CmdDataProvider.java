/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * @author LLT
 *
 */
public class CmdDataProvider extends AbstractDataProvider {
	
	public CmdDataProvider(String[] args) {
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
	protected String getJavaHome() {
		return null;
	}

	@Override
	protected String getTracerJarPath() {
		return null;
	}

	@Override
	protected List<String> getProjectClasspath() {
		// TODO Auto-generated method stub
		return null;
	}
}
