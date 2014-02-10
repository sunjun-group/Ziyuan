/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.ui.console.IOConsoleOutputStream;

import tzuyu.engine.iface.ILogger;
import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.exception.ErrorType;

/**
 * @author LLT
 * for logging in plugin.
 */
public class PluginLogger implements ILogger<PluginLogger>{
	private PrintWriter pw;
	private IOConsoleOutputStream stream;
	
	public PluginLogger(IOConsoleOutputStream out) {
		if (out != null) {
			pw = new PrintWriter(out);
			this.stream = out;
		}
	}
	
	@Override
	public PluginLogger info(Object... msgs) {
		if (consoleOpen()) {
			String msg = StringUtils.spaceJoin(msgs);
			try {
				stream.write(msg);
				stream.write("\n");
			} catch (IOException e) {
				logEx(e);
			}
//			pw.println(msg);
		}
		return this;
	}

	private boolean consoleOpen() {
		return pw != null;
	}
	
	public void close() {
		if (consoleOpen()) {
			pw.flush();
			pw.close();
		}
	}

	@Override
	public PluginLogger error(Object... msgs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void logEx(Exception e) {
		System.out.println(e.getMessage());
	}

	public static void logEx(Exception e, String msg) {
		System.out.println(msg);
	}

	public static void logEx(Exception e, ErrorType errorType) {
		System.out.println(TzuyuPlugin.getMessages().getMessage(errorType));
	}
}
