package microbat.util;

import java.util.ArrayList;
import java.util.List;

import microbat.Activator;
import microbat.codeanalysis.ast.LocalVariableScopes;
import microbat.model.InterestedVariable;
import microbat.preference.MicrobatPreference;

public class Settings {
	public static String projectName;
	
	public static String lanuchClass;
	
	public static String buggyClassName;
	public static String buggyLineNumber;
	
	
	static{
		if(Activator.getDefault() != null){
			try{
				projectName = Activator.getDefault().getPreferenceStore().getString(MicrobatPreference.TARGET_PORJECT);
				lanuchClass = Activator.getDefault().getPreferenceStore().getString(MicrobatPreference.LANUCH_CLASS);
				buggyClassName = Activator.getDefault().getPreferenceStore().getString(MicrobatPreference.CLASS_NAME);
				buggyLineNumber = Activator.getDefault().getPreferenceStore().getString(MicrobatPreference.LINE_NUMBER);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static List<InterestedVariable> interestedVariables = new ArrayList<>();
	/**
	 * This variable is to trace whether the variables in different lines are the same
	 * local variable.
	 */
	public static LocalVariableScopes localVariableScopes = new LocalVariableScopes();


}
