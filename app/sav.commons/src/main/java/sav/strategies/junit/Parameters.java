/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author LLT
 *
 */
public class Parameters {
	
	protected static CommandLine parse (Options opts, String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(opts, args);
		if (cmd.getOptions().length == 0) {
			throw new ParseException("No specified option");
		}
		return cmd;
	}
	
	protected static boolean getSingleOption(CommandLine cmd, String optName) {
		if (cmd.hasOption(optName)) {
			return true;
		}
		return false;
	}
	
	protected static boolean getBooleanOption(CommandLine cmd, String optName) {
		if (cmd.hasOption(optName)) {
			return Boolean.valueOf(cmd.getOptionValue(optName));
		}
		return false;
	}
	
	protected static String getOption(CommandLine cmd, String optName) {
		if (cmd.hasOption(optName)) {
			return cmd.getOptionValue(optName);
		}
		return null;
	}
	
	protected static List<String> getListStringOption(CommandLine cmd, String optName) {
		if (cmd.hasOption(optName)) {
			return Arrays.asList(cmd.getOptionValues(optName));
		}
		return null;
	}
}
