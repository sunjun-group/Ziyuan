/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import sav.common.core.Logger;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class VMRunner {
	private static final int NO_TIME_OUT = -1;
	private static Logger<?> log = Logger.getDefaultLogger();
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
	private Redirect redirect;
	/* timeout in millisecond */
	private long timeout = NO_TIME_OUT;
	
	public Process startVm(VMConfiguration config) throws SavException {
		Assert.assertTrue(config.getPort() != VMConfiguration.INVALID_PORT,
				"Cannot find free port to start jvm!");
		List<String> commands = buildCommandsFromConfiguration(config);
		return startVm(commands, false);
	}

	private List<String> buildCommandsFromConfiguration(VMConfiguration config)
			throws SavException {
		if (config.getPort() == -1) {
			throw new SavException(ModuleEnum.JVM, "Cannot find free port to start jvm!");
		}
		
		CollectionBuilder<String, Collection<String>> builder = CollectionBuilder
						.init(new ArrayList<String>())
						.add(VmRunnerUtils.buildJavaExecArg(config));
		buildVmOption(builder, config);
		buildProgramArgs(config, builder);
		List<String> commands = builder.getResult();
		return commands;
	}
	
	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		builder.add(cpToken)
				.add(config.getClasspathStr())
				.add(config.getLaunchClass());
		for (String arg : config.getProgramArgs()) {
			builder.add(arg);
		}
	}

	public Process startAndWaitUntilStop(VMConfiguration config)
			throws SavException {
		List<String> commands = buildCommandsFromConfiguration(config);
		return startAndWaitUntilStop(commands);
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
		/* Note: The input stream must be read if available to be closed, 
		 * otherwise, the process will never end. So, keep doing this even if 
		 * the printStream is not set */
		while (in.ready() && (inputLine = in.readLine()) != null) {
//			if(log.isDebug()) {
//				log.debug(inputLine);
//			}
		}
		in.close();
	}

	protected void buildVmOption(CollectionBuilder<String, ?> builder, VMConfiguration config) {
		builder.addIf(String.format(debugToken, config.getPort()), config.isDebug())
				.addIf(enableAssertionToken, config.isEnableAssertion());
	}

	public Process startVm(List<String> commands, boolean waitUntilStop)
			throws SavException {
		if (log.isDebug()) {
			log.debug("start cmd..");
			log.debug(StringUtils.join(commands, " "));
			for (String cmd : commands) {
				log.debug(cmd);
			}
		}
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);
		if (redirect != null) {
			processBuilder.redirectOutput(redirect);
		}
		try {
			final Process process = processBuilder.start();
			Timer t = new Timer();
			if (timeout != NO_TIME_OUT) {
			    t.schedule(new TimerTask() {

			        @Override
			        public void run() {
			            process.destroy();
			        }
			    }, timeout); 
			}
			if (waitUntilStop) {
				waitUntilStop(process);
				t.cancel();
			}
			return process;
		} catch (IOException e) {
			log.logEx(e, "");
			throw new SavException(ModuleEnum.JVM, e, "cannot start jvm process");
		}
	}
	
	public void waitUntilStop(Process process) throws SavException {
		try {
			printStream(process.getInputStream());
			printStream(process.getErrorStream());
			process.waitFor();
		} catch (IOException e) {
			log.logEx(e, "");
			throw new SavException(ModuleEnum.JVM, e);
		} catch (InterruptedException e) {
			log.logEx(e, "");
			throw new SavException(ModuleEnum.JVM, e);
		}
	}

//	public void waitUntilStop(Process process)
//			throws SavException {
//		while (true) {
//			try {
//				printStream(process.getInputStream());
//				printStream(process.getErrorStream());
//				process.exitValue();
//				break;
//			} catch (IOException e) {
//				log.logEx(e, "");
//				throw new SavException(ModuleEnum.JVM, e);
//			} catch (IllegalThreadStateException ex) {
//				// means: not yet terminated
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					log.logEx(e, "");
//					throw new SavException(ModuleEnum.JVM, e);
//				}
//			} 
//		}
//	}
	
	public void setRedirect(Redirect redirect) {
		this.redirect = redirect;
	}

	public void setTimeout(int timeout, TimeUnit unit) {
		this.timeout = unit.toMillis(timeout);
	}
	
	public static Process start(VMConfiguration config) throws SavException {
		VMRunner vmRunner = new VMRunner();
		return vmRunner.startVm(config);
	}
	
	public Process startAndWaitUntilStop(List<String> commands)
			throws SavException {
		return startVm(commands, true);
	}
	
	public static VMRunner getDefault() {
		return new VMRunner();
	}
}
