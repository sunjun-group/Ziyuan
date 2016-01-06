package microbat.codeanalysis.ast;

import java.util.ArrayList;
import java.util.List;

public class LocalVariableScopes {
	private List<LocalVariableScope> variableScopes = new ArrayList<>();

	public List<LocalVariableScope> getVariableScopes() {
		return variableScopes;
	}

	public void setVariableScopes(List<LocalVariableScope> variableScopes) {
		this.variableScopes = variableScopes;
	}
	
	
}
