/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.vm;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.CollectionBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class VMRunner {
	private static final String cpToken = "-cp";
	private static final String debugToken = "-Xdebug";
	private static final String socketToken = "-Xrunjdwp:transport=dt_socket,address=%d,server=%s,suspend=%s";
	private static final String enableAssertionToken = "-ea";

	public static Process startJVM(VMConfiguration config) throws IcsetlvException {
		List<String> commands = CollectionBuilder.init(new ArrayList<String>())
				.add(config.getPath())
				.add(debugToken)
				.add(String.format(socketToken, config.getPort(), "y", "y"))
				.add(enableAssertionToken)
				.add(cpToken)
				.add(config.getClasspaths())
				.add(config.getClazzName())
				.getResult();
		for (String arg : config.getArgs()) {
			commands.add(arg);
		}
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			IcsetlvException.rethrow(e, "cannot start jvm process");
		}
		return process;
	}
}
