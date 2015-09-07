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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import sav.common.core.Logger;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
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
//	private Redirect redirect;
	/* timeout in millisecond */
	private long timeout = NO_TIME_OUT;
	private boolean isLog = true;
	
	private Process process;
	private String processError;
	
	public boolean startVm(VMConfiguration config) throws SavException {
		this.isLog = config.isVmLogEnable();
		List<String> commands = buildCommandsFromConfiguration(config);
		return startVm(commands, false);
	}

	private List<String> buildCommandsFromConfiguration(VMConfiguration config)
			throws SavException {
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

	public boolean startAndWaitUntilStop(VMConfiguration config)
			throws SavException {
		List<String> commands = buildCommandsFromConfiguration(config);
		return startAndWaitUntilStop(commands);
	}
	
	public String getProccessError() throws SavRtException {
		if (processError == null) {
			if (process != null) {
				try {
					processError = getText(process.getErrorStream());
				} catch (IOException e) {
					throw new SavRtException(e);
				}
			}
			processError = StringUtils.EMPTY;
		}
		return processError;
	}
	
	private String getText(InputStream stream) throws IOException {
		try {
			stream.available();
		} catch (IOException ex) {
			// stream closed already!
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;
		/* Note: The input stream must be read if available to be closed, 
		 * otherwise, the process will never end. So, keep doing this even if 
		 * the printStream is not set */
		while (in.ready() && (inputLine = in.readLine()) != null) {
			sb.append(inputLine)
				.append("\n");
		}
		in.close();
		return sb.toString();
	}

	protected void buildVmOption(CollectionBuilder<String, ?> builder, VMConfiguration config) {
		builder.addIf(String.format(debugToken, config.getPort()), config.isDebug())
				.addIf(enableAssertionToken, config.isEnableAssertion());
	}

	public boolean startVm(List<String> commands, boolean waitUntilStop)
			throws SavException {
		if (isLog && log.isDebug()) {
			log.debug("start cmd..");
			log.debug(StringUtils.join(commands, " "));
			for (String cmd : commands) {
				log.debug(cmd);
			}
		}
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
//		if (redirect != null) {
//			processBuilder.redirectOutput(redirect);
//		}
		try {
			process = processBuilder.start();
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
				boolean success = waitUntilStop(process);
				t.cancel();
				return success;
			}
			return true;
		} catch (IOException e) {
			log.logEx(e, "");
			throw new SavException(ModuleEnum.JVM, e, "cannot start jvm process");
		}
	}
	
	public boolean waitUntilStop(Process process) throws SavException {
		try {
			getText(process.getInputStream());
			processError = getText(process.getErrorStream());
			process.waitFor();
			String error = getText((process.getErrorStream()));
			if (!StringUtils.isEmpty(error)) {
				log.debug(error);
				return false;
			}
			return true;
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
	
//	public void setRedirect(Redirect redirect) {
//		this.redirect = redirect;
//	}

	public void setTimeout(int timeout, TimeUnit unit) {
		this.timeout = unit.toMillis(timeout);
	}
	
	public static boolean start(VMConfiguration config) throws SavException {
		VMRunner vmRunner = new VMRunner();
		return vmRunner.startVm(config);
	}
	
	public boolean startAndWaitUntilStop(List<String> commands)
			throws SavException {
		return startVm(commands, true);
	}
	
	public static VMRunner getDefault() {
		return new VMRunner();
	}
	
	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}
	
	public Process getProcess() {
		return process;
	}
}
