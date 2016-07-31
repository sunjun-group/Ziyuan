package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "Abs";
	public static String methodName = "getAbs";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/benchmark/" + typeName + ".java";
	public static String className = "testdata.benchmark." + typeName;
	
	public static String pkg = "testdata.test.benchmark." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
