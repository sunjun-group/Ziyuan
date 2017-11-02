package learntest.core;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class Visitor extends ASTVisitor {

	int line;
	CompilationUnit cu;
	boolean inTargetMethod = false;
	
	public Visitor(int line, CompilationUnit cu) {
		this.line = line;
		this.cu = cu;
	}
	
	public boolean checkLineRange(ASTNode node){
        int lineNumS = cu.getLineNumber(node.getStartPosition()),
        		lineNumE = cu.getLineNumber(node.getStartPosition() + node.getLength());
        
        System.out.println(node.getClass() + "," + lineNumS + "," + lineNumE);
        if (lineNumS <= line && lineNumE >= line) {
			return true;
		}
		return false;
	}
	
	public boolean visit(MethodDeclaration node) {
        
        System.out.println(node.getName());
        if (checkLineRange(node)) {
        	inTargetMethod = true;
			return true;
		}
        return false;
	}

	public boolean visit(ConditionalExpression node) {
		if (checkLineRange(node)) {
			System.out.println(node);
		}
		return false;
	}

	public boolean visit(FieldAccess node) {

		return super.visit(node);
	}

	public boolean visit(SuperFieldAccess node) {

		return super.visit(node);
	}

	public boolean visit(InstanceofExpression node) {
		return super.visit(node);
	}

	public boolean visit(StringLiteral node) {
		return super.visit(node);
	}

	public boolean visit(ThisExpression node) {
		return super.visit(node);
	}

	public boolean visit(TypeLiteral node) {
		return super.visit(node);
	}

	public boolean visit(IfStatement node) {

		return checkLineRange(node);

	}

	public boolean visit(WhileStatement node) {

		return super.visit(node);
	}

	public boolean visit(InfixExpression node) {
		if (checkLineRange(node)) {
			Operator operator = node.getOperator();
			Expression left = node.getLeftOperand(),
					right = node.getRightOperand();
			if (operator.toString().equals(Operator.EQUALS.toString())) {
				System.out.println("equals");
				ASTNode parent = left.getRoot();
				parent.getNodeType();
				ITypeBinding bind = left.resolveTypeBinding();
				bind.getJavaElement();
			}else if (operator.toString().equals(Operator.NOT_EQUALS.toString())) {
				System.out.println("not equals");
			}
			System.currentTimeMillis();
		}
		return super.visit(node);
	}

	public boolean visit(DoStatement node) {

		return super.visit(node);
	}

	public boolean visit(SynchronizedStatement node) {
		return super.visit(node);
	}

	public boolean visit(AssertStatement node) {
		return super.visit(node);
	}

	public boolean visit(LabeledStatement node) {
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		return super.visit(node);
	}

	public boolean visit(SwitchStatement node) {

		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {

		return super.visit(node);
	}

	public boolean visit(TypeDeclarationStatement node) {
		return super.visit(node);
	}

	public boolean visit(CatchClause node) {

		return super.visit(node);
	}

	public boolean visit(TryStatement node) {

		return super.visit(node);
	}

	public boolean visit(ThrowStatement node) {

		return super.visit(node);
	}

	public boolean visit(ContinueStatement node) {

		return super.visit(node);
	}

	public boolean visit(BreakStatement node) {

		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		return super.visit(node);
	}

	public boolean visit(AnonymousClassDeclaration node) {

		return super.visit(node);

	}

	public void endVisit(AnonymousClassDeclaration node) {

		super.endVisit(node);
	}

	public void endVisit(TypeDeclaration node) {

		super.endVisit(node);
	}

	public void endVisit(SuperMethodInvocation node) {
		super.endVisit(node);
	}

	public void endVisit(MethodDeclaration node) {

		super.endVisit(node);
	}

	public void endVisit(MethodInvocation node) {
		super.endVisit(node);
	}

	public void endVisit(Assignment node) {
		super.endVisit(node);
	}

	public void endVisit(InfixExpression node) {
		super.endVisit(node);
	}

	public void endVisit(PrefixExpression node) {
		super.endVisit(node);
	}

	public void endVisit(PostfixExpression node) {
		super.endVisit(node);
	}

	public void endVisit(ParenthesizedExpression node) {
		super.endVisit(node);
	}

	public void endVisit(NumberLiteral node) {
		super.endVisit(node);
	}

	public void endVisit(SimpleName node) {
		super.endVisit(node);
	}

	public void endVisit(ArrayAccess node) {
		super.endVisit(node);
	}

	public void endVisit(ArrayCreation node) {
		super.endVisit(node);
	}

	public void endVisit(BooleanLiteral node) {
		super.endVisit(node);
	}

	public void endVisit(CastExpression node) {
		super.endVisit(node);
	}

	public void endVisit(CharacterLiteral node) {
		super.endVisit(node);
	}

	public void endVisit(ClassInstanceCreation node) {
		super.endVisit(node);
	}

	public void endVisit(ConditionalExpression node) {
		super.endVisit(node);
	}

	public void endVisit(FieldAccess node) {

		super.endVisit(node);
	}

	public void endVisit(SuperFieldAccess node) {
		super.endVisit(node);
	}

	public void endVisit(InstanceofExpression node) {
		super.endVisit(node);
	}

	public void endVisit(StringLiteral node) {
		super.endVisit(node);
	}

	public void endVisit(ThisExpression node) {
		super.endVisit(node);
	}

	public void endVisit(TypeLiteral node) {
		super.endVisit(node);
	}

	public void endVisit(IfStatement node) {

		super.endVisit(node);
	}

	public void endVisit(WhileStatement node) {

		super.endVisit(node);
	}

	public void endVisit(ForStatement node) {

		super.endVisit(node);
	}

	public void endVisit(EnhancedForStatement node) {

		super.endVisit(node);
	}

	public void endVisit(DoStatement node) {

		super.endVisit(node);
	}

	public void endVisit(SynchronizedStatement node) {
		super.endVisit(node);
	}

	public void endVisit(AssertStatement node) {
		super.endVisit(node);
	}

	public void endVisit(LabeledStatement node) {
		super.endVisit(node);
	}

	public void endVisit(ConstructorInvocation node) {
		super.endVisit(node);
	}

	public void endVisit(VariableDeclarationStatement node) {
		super.endVisit(node);
	}

	public void endVisit(SwitchStatement node) {

		super.endVisit(node);
	}

	public void endVisit(SwitchCase node) {

		super.endVisit(node);
	}

	public void endVisit(TypeDeclarationStatement node) {
		super.endVisit(node);
	}

	public void endVisit(TryStatement node) {

		super.endVisit(node);
	}

	public void endVisit(ThrowStatement node) {

		super.endVisit(node);
	}

	public void endVisit(ContinueStatement node) {
		super.endVisit(node);
	}

	public void endVisit(BreakStatement node) {
		super.endVisit(node);
	}

	public void endVisit(ReturnStatement node) {
		super.endVisit(node);
	}

	public void endVisit(CatchClause node) {

		super.endVisit(node);
	}
	
	public void endVisit(ImportDeclaration node){
		super.endVisit(node);
	}
	
	public void endVisit(PackageDeclaration node){
		super.endVisit(node);
	}
}
