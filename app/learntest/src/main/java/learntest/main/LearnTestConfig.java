package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "FastMath";
	public static String methodName = "tanh";
	public static String filePath = "D:/git/Ziyuan/app/learntest/src/test/java/testdata/finaltest/" + typeName + ".java";
	public static String className = "testdata.finaltest." + typeName;
	
	public static String pkg = "testdata.test.finaltest." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
