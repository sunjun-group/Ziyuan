package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "MathOps";
	public static String methodName = "divide";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/experiment/" + typeName + ".java";
	public static String className = "testdata.experiment." + typeName;
	
	public static String pkg = "testdata.test.experiment." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
