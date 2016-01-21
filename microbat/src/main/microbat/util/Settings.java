package microbat.util;

import java.util.ArrayList;
import java.util.List;

import microbat.codeanalysis.ast.LocalVariableScopes;
import microbat.model.InterestedVariable;

public class Settings {
	public static String projectName = "Test";
	public static String buggyClassName;
	public static String buggyLineNumber;
	
	
	public static List<InterestedVariable> interestedVariables = new ArrayList<>();
	/**
	 * This variable is to trace whether the variables in different lines are the same
	 * local variable.
	 */
	public static LocalVariableScopes localVariableScopes = new LocalVariableScopes();


}
