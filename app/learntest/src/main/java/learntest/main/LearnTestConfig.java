package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "FastMath";
	public static String methodName = "tanh";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/org/apache/commons/math4/util/" + typeName + ".java";
	public static String className = "org.apache.commons.math4.util." + typeName;
	
	public static String pkg = "testdata.test.math." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
