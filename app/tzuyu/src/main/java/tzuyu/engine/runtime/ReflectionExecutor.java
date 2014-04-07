package tzuyu.engine.runtime;

import tzuyu.engine.iface.IPrintStream;

public final class ReflectionExecutor {

	private static long normalExecAccum = 0;
	private static int normalExecCount = 0;
	private static long excepExecAccum = 0;
	private static int excepExecCount = 0;

	public static Throwable executeReflectionCode(ReflectionCode code,
			IPrintStream out) {
		Throwable ret = null;

		long start = System.nanoTime();

		ret = executeReflectionCodeUnThreaded(code, out);

		long duration = System.nanoTime() - start;

		if (ret == null) {
			normalExecAccum += duration;
			assert normalExecAccum > 0;
			normalExecCount++;
		} else {
			excepExecAccum += duration;
			assert excepExecAccum > 0;
			excepExecCount++;
		}
		return ret;
	}

	public static int normalExecs() {
		return normalExecCount;
	}

	public static int excepExecs() {
		return excepExecCount;
	}

	public static double normalExecAvgMillis() {
		return ((normalExecAccum / (double) normalExecCount) / Math.pow(10, 6));
	}

	public static double excepExecAvgMillis() {
		return ((excepExecAccum / (double) excepExecCount) / Math.pow(10, 6));
	}

	private static Throwable executeReflectionCodeUnThreaded(
			ReflectionCode code, IPrintStream out) {
		try {
			code.runReflectionCode();
			return null;
		} catch (ThreadDeath e) { // can't stop these guys
			throw e;
		} catch (ReflectionCode.NotCaughtIllegalStateException e) {
			throw e;
		} catch (Throwable e) {
			Throwable orig_e = null;
			if (e instanceof java.lang.reflect.InvocationTargetException) {
				orig_e = e;
				e = e.getCause();
			}

			if (out != null) {
				printExceptionDetails(e, out);
				if (orig_e != null) {
					out.println("Original exception: " + orig_e);
				}
			}
			return e;
		}
	}

	private static void printExceptionDetails(Throwable e, IPrintStream out) {
		out.println("Exception thrown:" + e.toString());
		out.println("Message: " + e.getMessage());
		out.println("Stack trace: ");
		out.println(e.getStackTrace());
	}

}
