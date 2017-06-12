package learntest.util;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodFinder extends ASTVisitor {
	private MethodDeclaration result;
	private int lineNumber;
	private CompilationUnit cu;
	private String methodName;

	public MethodFinder(CompilationUnit cu, String methodName, int lineNumber) {
		super();
		this.lineNumber = lineNumber;
		this.cu = cu;
		this.methodName = methodName;
	}

	public boolean visit(MethodDeclaration md) {
		if (this.result != null) {
			return false;
		}

		if (md.getName().getFullyQualifiedName().equals(methodName)) {
			if (lineNumber <= 0) {
				result = md;
			} else {
				int startLine = cu.getLineNumber(md.getStartPosition());
				int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());

				if (startLine <= lineNumber && lineNumber <= endLine) {
					result = md;
				}
			}
		}

		return false; // no need to visit its children.
	}
	
	public MethodDeclaration getResult() {
		return result;
	}
}
