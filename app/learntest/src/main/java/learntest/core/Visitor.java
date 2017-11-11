package learntest.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
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

import learntest.core.rule.EqualVarRelationShip;
import learntest.core.rule.NotEqualVarRelationShip;
import learntest.core.rule.RelationShip;

public class Visitor extends ASTVisitor {

	int line;
	CompilationUnit cu;
	boolean inTargetMethod = false;
	List<RelationShip> relationShips = new LinkedList<>();
	int index;

	public Visitor(int line, CompilationUnit cu, int index) {
		this.line = line;
		this.cu = cu;
		this.index = index;
	}
	
	public RelationShip getRelationShip(){
		if (relationShips.size() > index) {
			return relationShips.get(index);
		}else {
			return null;
		}
	}

	public boolean checkLineRange(ASTNode node) {
		int lineNumS = cu.getLineNumber(node.getStartPosition()),
				lineNumE = cu.getLineNumber(node.getStartPosition() + node.getLength());
		if (lineNumS <= line && lineNumE >= line) {
			return true;
		}
		return false;
	}

	public boolean visit(MethodDeclaration node) {
		if (checkLineRange(node)) {
			inTargetMethod = true;
			return true;
		}
		return false;
	}

	public boolean visit(InfixExpression node) {
		if (checkLineRange(node)) {
			int lineNumS = cu.getLineNumber(node.getStartPosition()),
					lineNumE = cu.getLineNumber(node.getStartPosition() + node.getLength());
			if (lineNumS == lineNumE) {
				Operator operator = node.getOperator();
				Expression left = node.getLeftOperand(), right = node.getRightOperand();
				if (basicType(left) && basicType(right)) { // base case, no need to continue to visit
					if (operator.toString().equals(Operator.EQUALS.toString())) {
						if (left instanceof QualifiedName && right instanceof QualifiedName) {
							relationShips.add(new EqualVarRelationShip(left.toString(), right.toString()));
						}else if (left instanceof SimpleName && right instanceof SimpleName) {
							relationShips.add(new EqualVarRelationShip(left.toString(), right.toString()));
						}else {
							relationShips.add(null);							
						}
					} else if (operator.toString().equals(Operator.NOT_EQUALS.toString())) {
						if (left instanceof QualifiedName && right instanceof QualifiedName) {
							relationShips.add(new NotEqualVarRelationShip(left.toString(), right.toString()));
						}else if (left instanceof SimpleName && right instanceof SimpleName) {
							relationShips.add(new NotEqualVarRelationShip(left.toString(), right.toString()));
						}else {
							relationShips.add(null);							
						}
					} else {
						relationShips.add(null);
					}
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean basicType(Expression node){
		if (node instanceof NumberLiteral || node instanceof Name) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean visit(PrefixExpression node) {
		if (checkLineRange(node)) {
			int lineNumS = cu.getLineNumber(node.getStartPosition()),
					lineNumE = cu.getLineNumber(node.getStartPosition() + node.getLength());
			if (lineNumS == lineNumE) {
				Expression operand = node.getOperand();
				if (basicType(operand)) { // base case, no need to continue to visit
					relationShips.add(null);
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
