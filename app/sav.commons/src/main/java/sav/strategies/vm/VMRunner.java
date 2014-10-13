/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sav.common.core.Constants;
import sav.common.core.ModuleEnum;
import sav.common.core.NullPrintStream;
import sav.common.core.SavException;
import sav.common.core.iface.IPrintStream;
import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class VMRunner {
	public static boolean debug = true;
	protected static final String cpToken = "-cp";
	/*
	 * from jdk 1.5, we can use new JVM option: -agentlib 
	 * Benefits of using the new -agentlib args is, it doesn't contain any whitespace, so
	 * you don't need to worry if you need to quote it or not. But if you do
	 * want to use the old flags, be careful about when to quote the value and
	 * when to not quote.
	 */
	protected static final String debugToken = "-agentlib:jdwp=transport=dt_socket,suspend=y,address=%s";
	protected static final String enableAssertionToken = "-ea";
	protected IPrintStream out;
	
	public static Process startJVM(VMConfiguration config) throws SavException {
		VMRunner vmRunner = new VMRunner();
		return vmRunner.start(config);
	}
	
	@SuppressWarnings("unchecked")
	public Process start(VMConfiguration config) throws SavException {
		if (config.getPort() == -1) {
			throw new SavException(ModuleEnum.JVM, "Cannot find free port to start jvm!");
		}
		
		CollectionBuilder<String, Collection<String>> builder = CollectionBuilder
						.init(new ArrayList<String>())
						.add(buildJavaExecArg(config));
		buildVmOption(builder, config);
		buildProgramArgs(config, builder);
		List<String> commands = (List<String>)builder.getResult();
		if (debug) {
			System.out.println(StringUtils.join(commands, " "));
			for (String cmd : commands) {
				System.out.println(cmd);
			}
		}
		return startVm(commands);
	}
	
	public void startAndWaitUntilStop(VMConfiguration config)
			throws SavException, IOException, InterruptedException {
		Process process = start(config);
		while (true) {
			try {
				process.exitValue();
				break;
			} catch (IllegalThreadStateException ex) {
				printStream(process.getInputStream());
				printStream(process.getErrorStream());
				// means: not yet terminated
				Thread.currentThread();
				Thread.sleep(100);
			}
		}
	}
	
	private void printStream(InputStream stream) throws IOException {
		try {
			stream.available();
		} catch (IOException ex) {
			// stream closed already!
			return;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;
		IPrintStream out = getOut();
		/* Note: The input stream must be read if available to be closed, 
		 * otherwise, the process will never end. So, keep doing this even if 
		 * the printStream is not set */
		while ((inputLine = in.readLine()) != null) {
			out.println(inputLine);
		}
		in.close();
	}

	private IPrintStream getOut() {
		if (out == null) {
			out = NullPrintStream.instance();
		}
		return out;
	}

	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		builder.add(cpToken)
				.add(toClasspathStr(config.getClasspaths()))
				.add(config.getLaunchClass());
		for (String arg : config.getProgramArgs()) {
			builder.add(arg);
		}
	}
	
	protected void buildVmOption(CollectionBuilder<String, ?> builder, VMConfiguration config) {
		builder.addIf(String.format(debugToken, config.getPort()), config.isDebug())
				.addIf(enableAssertionToken, config.isEnableAssertion());
	}

	protected Process startVm(List<String> commands)
			throws SavException {
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);
		Process process = null;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			new SavException(ModuleEnum.JVM, e, "cannot start jvm process");
		}
		return process;
	}
	
	private String toClasspathStr(List<String> classpaths) {
		return StringUtils.join(classpaths, File.pathSeparator);
	}

	private String buildJavaExecArg(VMConfiguration config) {
		return StringUtils.join(Constants.FILE_SEPARATOR, config.getJavaHome(), "bin", "java");
	}
	
	public void setOut(IPrintStream out) {
		this.out = out;
	}
}
