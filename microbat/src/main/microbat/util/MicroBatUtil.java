package microbat.util;

import microbat.codeanalysis.LocalVariableScope;
import microbat.model.InterestedVariable;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ReferenceValue;

public class MicroBatUtil {
	public static String combineTraceNodeExpression(String className, int lineNumber){
		String exp = className + " line:" + lineNumber;
		return exp;
	}
	
	/**
	 * @param variable1
	 * @param variable2
	 * @return
	 */
	public static boolean isTheSameVariable(InterestedVariable v1, InterestedVariable v2) {
		ExecValue var1 = v1.getVariable();
		ExecValue var2 = v2.getVariable();
		
		if(var1 instanceof ReferenceValue && var2 instanceof ReferenceValue){
			ReferenceValue rv1 = (ReferenceValue)var1;
			ReferenceValue rv2 = (ReferenceValue)var2;
			if(rv1.getReferenceID() == rv2.getReferenceID()){
				return true;
			}
		}
		/**
		 * Otherwise, we compare two variables based on their parents.
		 */
		else if(var1.getVarName().equals(var2.getVarName())){
			ExecValue rootVar1 = v1.getVariable().getFirstRootParent();
			ExecValue rootVar2 = v2.getVariable().getFirstRootParent();
			
			String visitingClassName1 = v1.getVisitingClassName();
			String visitingClassName2 = v2.getVisitingClassName();
			boolean isEqualVisitingClass = visitingClassName1.equals(visitingClassName2);
			
			if(rootVar1.isField() && rootVar2.isField()){
				return isEqualVisitingClass;
			}
			else if(rootVar1.isLocalVariable() && rootVar2.isLocalVariable()){
				boolean isEqualName = rootVar1.getVarName().equals(rootVar2.getVarName());
				
				if(isEqualVisitingClass && isEqualName){
					boolean isEqualRange = isEqualRange(rootVar1, v1, rootVar2, v2);
					return isEqualRange;
				}
			}
		}
		
		return false;
	}


	private static boolean isEqualRange(ExecValue rootVar1, InterestedVariable v1, ExecValue rootVar2, InterestedVariable v2) {
		String varID = rootVar1.getVarId();
		for(LocalVariableScope lvs: Settings.localVariableScopes.getVariableScopes()){
			if(varID.equals(lvs.getVariableName())){
				boolean isRootVar1InScope = lvs.getStartLine() <= v1.getLineNumber() && lvs.getEndLine() >= v1.getLineNumber();
				boolean isRootVar2InScope = lvs.getStartLine() <= v2.getLineNumber() && lvs.getEndLine() >= v2.getLineNumber();
				
				return isRootVar1InScope && isRootVar2InScope;
			}
		}
		
		return false;
	}
}
