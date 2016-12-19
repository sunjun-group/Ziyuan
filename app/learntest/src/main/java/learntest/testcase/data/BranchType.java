package learntest.testcase.data;

public class BranchType {
	public static int FALSE = 0;
	public static int TRUE = 1;
	public static int MORE = 2;
	
	public static String getBranchType(int no){
		if(no == 0){
			return "false";
		}
		else if(no == 1){
			return "true";
		}
		else if(no == 2){
			return "more";
		}
		
		return null;
	}
}
