package microbat.codeanalysis.ast;

import java.util.ArrayList;
import java.util.List;

import microbat.util.JavaUtil;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class VariableScopeParser {

	private List<LocalVariableScope> variableScopeList = new ArrayList<>();
	
	public void parseLocalVariableScopes(List<String> interestingClasses){
		System.currentTimeMillis();
		for(String qualifiedName: interestingClasses){
			ICompilationUnit iunit = JavaUtil.findICompilationUnitInProject(qualifiedName);
			if(iunit == null){
				System.err.println("The class " + qualifiedName + 
						" does not have its ICompilationUnit in workspace");
			}
			else{
				CompilationUnit cu = JavaUtil.convertICompilationUnitToASTNode(iunit);
				parseLocalVariables(cu);
			}
		}
	}

	private void parseLocalVariables(CompilationUnit cu) {
		cu.accept(new ASTVisitor() {
			public boolean visit(VariableDeclarationFragment fragment){
				SimpleName name = fragment.getName();
				ASTNode scope = findLeastContainingBlock(name);
				if(scope != null){
					LocalVariableScope lvs = new LocalVariableScope(name.getIdentifier(), scope);
					variableScopeList.add(lvs);
				}
				return false;
			}
			
			public boolean visit(SingleVariableDeclaration svd){
				SimpleName name = svd.getName();
				ASTNode scope = findLeastContainingBlock(name);
				if(scope != null){
					LocalVariableScope lvs = new LocalVariableScope(name.getIdentifier(), scope);
					variableScopeList.add(lvs);					
				}
				
				return false;
			}
			
			private ASTNode findLeastContainingBlock(ASTNode node){
				ASTNode parentNode = node.getParent();
				while(!(parentNode instanceof Block) && 
						!(parentNode instanceof MethodDeclaration)){
					parentNode = parentNode.getParent();
					if(parentNode == null){
						break;
					}
				}
				
				return parentNode;
			}
		});
		
	}

	public List<LocalVariableScope> getVariableScopeList() {
		return variableScopeList;
	}
	
	
}
