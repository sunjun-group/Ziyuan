package learntest.util;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodFinder extends ASTVisitor{
	MethodDeclaration requiredMD;
	
	int lineNumber;
	CompilationUnit cu;
	String methodName;

	public MethodFinder(int lineNumber, CompilationUnit cu, String methodName) {
		super();
		this.lineNumber = lineNumber;
		this.cu = cu;
		this.methodName = methodName;
	}

	public boolean visit(MethodDeclaration md){
		
		if(md.getName().getFullyQualifiedName().equals(methodName)){
			if(lineNumber == 0){
				requiredMD = md;
			}
			else{
				int startLine = cu.getLineNumber(md.getStartPosition());
				int endLine = cu.getLineNumber(md.getStartPosition()+md.getLength());
				
				if(startLine<=lineNumber && lineNumber<=endLine){
					requiredMD = md;
				}
			}
		}
		
		return false;
	}
}
