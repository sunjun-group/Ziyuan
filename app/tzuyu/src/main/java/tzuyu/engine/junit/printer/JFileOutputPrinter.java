/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit.printer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import tzuyu.engine.utils.Log;

/**
 * @author LLT
 * 
 */
public class JFileOutputPrinter extends JOutputPrinter {
	private PrintStream out;

	public JFileOutputPrinter(File file) {
		try {
			out = new PrintStream(file);
		} catch (IOException e) {
			Log.out.println("Exception thrown while creating file:"
					+ file.getName());
			e.printStackTrace();
			System.exit(1);
			throw new Error("This can't happen");
		}
	}

	public JFileOutputPrinter append(String str) {
		out.print(str);
		return this;
	}
	
	@Override
	public void close() {
		out.flush();
		out.close();
	}
}
