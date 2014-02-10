/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.console;

import org.eclipse.ui.console.IConsoleFactory;

/**
 * @author LLT
 * 
 */
public class TzConsoleFactory implements IConsoleFactory {
	
	@Override
	public void openConsole() {
		TzConsole.showConsole();
	}

}
