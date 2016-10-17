package learntest.main;

public class LearnTestConfig {
	
	public static final String MODULE = "learntest";

	public static String typeName = "Triangle";
	public static String methodName = "triangleType";
	public static String filePath = "F:/git_space/Ziyuan_master/Ziyuan/app/learntest/src/test/java/testdata/example/" + typeName + ".java";
	public static String className = "testdata.example." + typeName;
	
	public static String pkg = "testdata.test.example." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
	public static String testPath = pkg + "." + typeName + "1";
	
	public static String resPkg = "testdata.result." + typeName.toLowerCase() + "." + methodName.toLowerCase();
	
}
