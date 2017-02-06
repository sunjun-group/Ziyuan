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

import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;

/**
 * @author LLT
 * 
 */
public class JFileOutputPrinter extends JOutputPrinter {
	private PrintStream out;

	public JFileOutputPrinter(File file) throws TzException {
		try {
			out = new PrintStream(file);
		} catch (IOException e) {
			throw new TzException(TzExceptionType.JUNIT_FAIL_WRITE_FILE,
					file.getAbsolutePath());
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
