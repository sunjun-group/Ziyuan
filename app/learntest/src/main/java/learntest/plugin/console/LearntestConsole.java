/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.console;

import java.io.PrintStream;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
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
public class LearntestConsole extends MessageConsole implements IPropertyChangeListener {
	public static final String NAME = "Learntest Console";
	private static final String CONSOLE_FONT = "learntest.consoleFont";
	private static LearntestConsole console;
	
	public LearntestConsole() {
		super(NAME, null, true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (CONSOLE_FONT.equals(event.getProperty())) {
			setConsoleFont();
		}
	}

	private void setConsoleFont() {
		if (Display.getCurrent() == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					setConsoleFont();
				}
			});
		} else {
			ITheme theme = PlatformUI.getWorkbench().getThemeManager()
					.getCurrentTheme();
			Font font = theme.getFontRegistry().get(CONSOLE_FONT);
			console.setFont(font);
		}
	}

	public static LearntestConsole showConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		getConsole().activate();
		ITheme theme = PlatformUI.getWorkbench().getThemeManager()
				.getCurrentTheme();
		theme.addPropertyChangeListener(console);
		manager.showConsoleView(console);
		MessageConsoleStream stream = console.newMessageStream();
		stream.setActivateOnWrite(true);
	    return console;
	}
	
	public static PrintStream getPrintStream() {
		MessageConsoleStream stream = getConsole().newMessageStream();
		stream.setActivateOnWrite(true);
		return new PrintStream(stream, true);
	}

	public static LearntestConsole getConsole() {
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
			console = new LearntestConsole();
		}
		if (!exists) {
			manager.addConsoles(new IConsole[] { console });
		}
		return console;
	}

	public static IOConsoleOutputStream getOutputStream() {
		IOConsoleOutputStream out = getConsole().newOutputStream();
		out.setActivateOnWrite(true);
		return out;
	}

	/**
	 * delegate system stream to plugin console.
	 * */
	public static void delegateConsole() {
		System.setOut(getPrintStream());
		System.setErr(getPrintStream());
	}
}
