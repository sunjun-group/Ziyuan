package microbat.util;

public class MicroBatUtil {
	public static String combineTraceNodeExpression(String className, int lineNumber){
		String exp = className + " line:" + lineNumber;
		return exp;
	}
}
