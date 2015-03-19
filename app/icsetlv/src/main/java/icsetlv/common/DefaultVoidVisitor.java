/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.MultiTypeParameter;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.comments.BlockComment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.comments.LineComment;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitor;

/**
 * @author LLT
 *
 */
public class DefaultVoidVisitor<A> implements VoidVisitor<A> {

	public void visit(CompilationUnit n, A arg) {
		// do nothing by default
	}

	public void visit(PackageDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(ImportDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(TypeParameter n, A arg) {
		// do nothing by default
		
	}

	public void visit(LineComment n, A arg) {
		// do nothing by default
		
	}

	public void visit(BlockComment n, A arg) {
		// do nothing by default
		
	}

	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(EnumDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(EmptyTypeDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(EnumConstantDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(AnnotationDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(AnnotationMemberDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(FieldDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(VariableDeclarator n, A arg) {
		// do nothing by default
		
	}

	public void visit(VariableDeclaratorId n, A arg) {
		// do nothing by default
		
	}

	public void visit(ConstructorDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(MethodDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(Parameter n, A arg) {
		// do nothing by default
		
	}

	public void visit(EmptyMemberDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(InitializerDeclaration n, A arg) {
		// do nothing by default
		
	}

	public void visit(JavadocComment n, A arg) {
		// do nothing by default
		
	}

	public void visit(ClassOrInterfaceType n, A arg) {
		// do nothing by default
		
	}

	public void visit(PrimitiveType n, A arg) {
		// do nothing by default
		
	}

	public void visit(ReferenceType n, A arg) {
		// do nothing by default
		
	}

	public void visit(VoidType n, A arg) {
		// do nothing by default
		
	}

	public void visit(WildcardType n, A arg) {
		// do nothing by default
		
	}

	public void visit(ArrayAccessExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ArrayCreationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ArrayInitializerExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(AssignExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(BinaryExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(CastExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ClassExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ConditionalExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(EnclosedExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(FieldAccessExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(InstanceOfExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(StringLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(IntegerLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(LongLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(IntegerLiteralMinValueExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(LongLiteralMinValueExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(CharLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(DoubleLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(BooleanLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(NullLiteralExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(MethodCallExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(NameExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ObjectCreationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(QualifiedNameExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(ThisExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(SuperExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(UnaryExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(VariableDeclarationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(MarkerAnnotationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(SingleMemberAnnotationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(NormalAnnotationExpr n, A arg) {
		// do nothing by default
		
	}

	public void visit(MemberValuePair n, A arg) {
		// do nothing by default
		
	}

	public void visit(ExplicitConstructorInvocationStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(TypeDeclarationStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(AssertStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(BlockStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(LabeledStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(EmptyStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ExpressionStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(SwitchStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(SwitchEntryStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(BreakStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ReturnStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(IfStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(WhileStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ContinueStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(DoStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ForeachStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ForStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(ThrowStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(SynchronizedStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(TryStmt n, A arg) {
		// do nothing by default
		
	}

	public void visit(CatchClause n, A arg) {
		// do nothing by default
		
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitor#visit(japa.parser.ast.body.MultiTypeParameter, java.lang.Object)
	 */
	public void visit(MultiTypeParameter n, A arg) {
		// TODO Auto-generated method stub
		
	}
	
}
