package tzuyu.engine.utils;

public class LogicUtils {

	public static boolean iff(boolean a, boolean b) {
		return a == b;
	}

	public static boolean implies(boolean a, boolean b) {
		return !a || b;
	}
}
