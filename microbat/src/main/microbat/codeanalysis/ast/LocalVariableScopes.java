package microbat.codeanalysis.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class LocalVariableScopes {
	private List<LocalVariableScope> variableScopes = new ArrayList<>();

	public List<LocalVariableScope> getVariableScopes() {
		return variableScopes;
	}

	public void setVariableScopes(List<LocalVariableScope> variableScopes) {
		this.variableScopes = variableScopes;
	}
	
	public LocalVariableScope findScope(String variableName, int appearedLineNum, String fullQualifiedTypeName){
		for(LocalVariableScope scope: variableScopes){
			TypeDeclaration td = (TypeDeclaration) scope.getCompilationUnit().types().get(0);
			String typeName = td.resolveBinding().getQualifiedName();
			
			if(typeName.equals(fullQualifiedTypeName) && scope.getVariableName().equals(variableName) &&
					appearedLineNum >= scope.getStartLine() && appearedLineNum <= scope.getEndLine()){
				return scope;
			}
		}
		
		return null;
	}
}
