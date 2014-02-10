/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.console;

import java.io.PrintStream;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.themes.ITheme;

/**
 * @author LLT
 * 
 */
public class TzConsole extends MessageConsole implements IPropertyChangeListener {
	public static final String NAME = "Tzuyu Console";
	private static final String CONSOLE_FONT = "tzuyu.plugin.consoleFont";
	static TzConsole console;
	
	public TzConsole() {
		super(NAME, null, true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub

	}

	public static void showConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		getConsole().activate();
		ITheme theme = PlatformUI.getWorkbench().getThemeManager()
				.getCurrentTheme();
		theme.addPropertyChangeListener(console);
		manager.showConsoleView(console);
		MessageConsoleStream stream = console.newMessageStream();
		stream.setActivateOnWrite(true);
		PrintStream ps = new PrintStream(stream, true);
		System.setOut(ps);
	    System.setErr(ps);
	}

	private static TzConsole getConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		boolean exists = false;
		if (console != null) {
			IConsole[] existing = manager.getConsoles();
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i]) {
					exists = true;
				}
			}
		} else {
			console = new TzConsole();
		}
		if (!exists) {
			manager.addConsoles(new IConsole[] { console });
		}
		return console;
	}

	public static IOConsoleOutputStream getOutputStream() {
		if (console == null) {
			return null;
		}
		IOConsoleOutputStream out = getConsole().newOutputStream();
		out.setActivateOnWrite(true);
		return out;
	}
}
