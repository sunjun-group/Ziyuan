package microbat.codeanalysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class LocalVariableScope {
	private String variableName;
	private ASTNode scope;

	public LocalVariableScope(String variableName, ASTNode scope) {
		super();
		this.variableName = variableName;
		this.scope = scope;
	}
	
	public String toString(){
		CompilationUnit cu = getCompilationUnit();
		int startPosition = this.scope.getStartPosition();
		int startLine = cu.getLineNumber(startPosition);
		int endLine = cu.getLineNumber(startPosition + this.scope.getLength());
		
		String className = getContainingClass();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("variable ");
		buffer.append(this.variableName);
		buffer.append(" in "  + className);
		buffer.append(" from line " + startLine + " to line " + endLine);
		
		return buffer.toString();
	}
	
	public int getStartLine(){
		CompilationUnit cu = getCompilationUnit();
		int startPosition = this.scope.getStartPosition();
		int startLine = cu.getLineNumber(startPosition);
		return startLine;
	}
	
	public int getEndLine(){
		CompilationUnit cu = getCompilationUnit();
		int startPosition = this.scope.getStartPosition();
		int endLine = cu.getLineNumber(startPosition + this.scope.getLength());
		return endLine;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public ASTNode getScope() {
		return scope;
	}
	
	public CompilationUnit getCompilationUnit(){
		return (CompilationUnit)scope.getRoot();
	}
	
	public String getContainingClass(){
		CompilationUnit cu = getCompilationUnit();
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		return type.getName().getIdentifier();
	}

	public void setScope(Block scope) {
		this.scope = scope;
	}

}
