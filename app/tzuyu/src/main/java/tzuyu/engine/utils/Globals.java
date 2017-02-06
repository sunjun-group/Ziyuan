package tzuyu.engine.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import sav.common.core.NullPrintStream;
import sav.common.core.SavPrintStream;
import sav.common.core.iface.IPrintStream;

/**
 * Various general global settings
 */
public final class Globals {
	private static final String DUMMY_TC_EX_LOG_FILE = "D:/_1_Projects/Tzuyu/workspace/tzuyuTcRtEx.log";

	public static final String TZUYU_VERSION = "1.0";

	public static final String lineSep = System.getProperty("line.separator");

	public static final String pathSep = System.getProperty("path.separator");

	public static final String fileSep = System.getProperty("file.separator");

	public static final String userDir = System.getProperty("user.dir");
	
	public static boolean DEV_MODE = true;
	public static boolean DEBUG = false;

	// Setting the Constant to any number greater than zero will cause models
	// to have a maximal depth MAX_MODEL_DEPTH+1
	public static final int MAX_MODEL_DEPTH = 100;
	private static IPrintStream tcExPrintStream;
	public static PrintStream tcExStream;

	private static PrintStream oldStdErr;

	static {
		if (DEV_MODE) {
			try {
				tcExStream = new PrintStream(DUMMY_TC_EX_LOG_FILE);
				tcExPrintStream = new SavPrintStream(tcExStream);
			} catch (FileNotFoundException e) {
				tcExPrintStream = NullPrintStream.instance();
			}
		} else {
			tcExPrintStream = NullPrintStream.instance();
		}
	}

	public static class ErrorStreamAssigner {
		public ErrorStreamAssigner(String destination) {
			if (destination.equals("stderr")) {
				System.setErr(oldStdErr);
			} else {
				try {
					System.setErr(new PrintStream(new PrintStream(destination),
							true));
				} catch (FileNotFoundException e) {
					System.out.println(Globals.lineSep
							+ "Could not create a stream for file "
							+ destination);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static String getTzuYuVersion() {
		return TZUYU_VERSION;
	}

	public static String getClassPath() {
		return System.getProperty("java.class.path");
	}

	public static IPrintStream getTcExecutionOutStream() {
		return tcExPrintStream;
	}
}
