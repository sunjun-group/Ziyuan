package microbat.util;

import java.util.ArrayList;
import java.util.List;

import microbat.codeanalysis.LocalVariableScopes;
import microbat.model.InterestedVariable;

public class Settings {
	public static String projectName = "Test";
	
	public static List<InterestedVariable> interestedVariables = new ArrayList<>();
	/**
	 * This variable is to trace whether the variables in different lines are the same
	 * local variable.
	 */
	public static LocalVariableScopes localVariableScopes = new LocalVariableScopes();
}
