package microbat.model;

import microbat.model.trace.TraceNode;
import microbat.util.JavaUtil;

import org.eclipse.jdt.core.dom.CompilationUnit;

public class Scope {
	private CompilationUnit cu;
	private int startLine;
	private int endLine;
	public CompilationUnit getCu() {
		return cu;
	}
	public void setCompilationUnit(CompilationUnit cu) {
		this.cu = cu;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public boolean containsNodeScope(TraceNode node) {
		String nodeClassName = node.getClassName();
		String scopeClassName = JavaUtil.getFullNameOfCompilationUnit(cu);
		
		if(nodeClassName.equals(scopeClassName)){
			int line = node.getLineNumber();
			if(line >= startLine && line <= endLine){
				return true;
			}
		}
		
		return false;
	}
	
	
}
