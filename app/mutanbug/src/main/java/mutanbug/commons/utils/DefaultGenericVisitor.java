/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.commons.utils;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
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
import japa.parser.ast.visitor.GenericVisitor;

/**
 * @author LLT
 *
 */
public class DefaultGenericVisitor<R, A> implements GenericVisitor<R, A> {

	public R visitNode(Node node, A arg) {
		if (accept(node)) {
			return node.accept(this, arg);
		}
		return getDefaultReturnValue();
	}
	
	protected R getDefaultReturnValue() {
		return null;
	}

	protected boolean accept(Node node) {
		return true;
	}
	
	@Override
	public R visit(CompilationUnit n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.PackageDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(PackageDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.ImportDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(ImportDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.TypeParameter, java.lang.Object)
	 */
	@Override
	public R visit(TypeParameter n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.comments.LineComment, java.lang.Object)
	 */
	@Override
	public R visit(LineComment n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.comments.BlockComment, java.lang.Object)
	 */
	@Override
	public R visit(BlockComment n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.ClassOrInterfaceDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(ClassOrInterfaceDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.EnumDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(EnumDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.EmptyTypeDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(EmptyTypeDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.EnumConstantDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(EnumConstantDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.AnnotationDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(AnnotationDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.AnnotationMemberDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(AnnotationMemberDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.FieldDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(FieldDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.VariableDeclarator, java.lang.Object)
	 */
	@Override
	public R visit(VariableDeclarator n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.VariableDeclaratorId, java.lang.Object)
	 */
	@Override
	public R visit(VariableDeclaratorId n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.ConstructorDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(ConstructorDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.MethodDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(MethodDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.Parameter, java.lang.Object)
	 */
	@Override
	public R visit(Parameter n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.MultiTypeParameter, java.lang.Object)
	 */
	@Override
	public R visit(MultiTypeParameter n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.EmptyMemberDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(EmptyMemberDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.body.InitializerDeclaration, java.lang.Object)
	 */
	@Override
	public R visit(InitializerDeclaration n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.comments.JavadocComment, java.lang.Object)
	 */
	@Override
	public R visit(JavadocComment n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.type.ClassOrInterfaceType, java.lang.Object)
	 */
	@Override
	public R visit(ClassOrInterfaceType n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.type.PrimitiveType, java.lang.Object)
	 */
	@Override
	public R visit(PrimitiveType n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.type.ReferenceType, java.lang.Object)
	 */
	@Override
	public R visit(ReferenceType n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.type.VoidType, java.lang.Object)
	 */
	@Override
	public R visit(VoidType n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.type.WildcardType, java.lang.Object)
	 */
	@Override
	public R visit(WildcardType n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ArrayAccessExpr, java.lang.Object)
	 */
	@Override
	public R visit(ArrayAccessExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ArrayCreationExpr, java.lang.Object)
	 */
	@Override
	public R visit(ArrayCreationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ArrayInitializerExpr, java.lang.Object)
	 */
	@Override
	public R visit(ArrayInitializerExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.AssignExpr, java.lang.Object)
	 */
	@Override
	public R visit(AssignExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.BinaryExpr, java.lang.Object)
	 */
	@Override
	public R visit(BinaryExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.CastExpr, java.lang.Object)
	 */
	@Override
	public R visit(CastExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ClassExpr, java.lang.Object)
	 */
	@Override
	public R visit(ClassExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ConditionalExpr, java.lang.Object)
	 */
	@Override
	public R visit(ConditionalExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.EnclosedExpr, java.lang.Object)
	 */
	@Override
	public R visit(EnclosedExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.FieldAccessExpr, java.lang.Object)
	 */
	@Override
	public R visit(FieldAccessExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.InstanceOfExpr, java.lang.Object)
	 */
	@Override
	public R visit(InstanceOfExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.StringLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(StringLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.IntegerLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(IntegerLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.LongLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(LongLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.IntegerLiteralMinValueExpr, java.lang.Object)
	 */
	@Override
	public R visit(IntegerLiteralMinValueExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.LongLiteralMinValueExpr, java.lang.Object)
	 */
	@Override
	public R visit(LongLiteralMinValueExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.CharLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(CharLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.DoubleLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(DoubleLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.BooleanLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(BooleanLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.NullLiteralExpr, java.lang.Object)
	 */
	@Override
	public R visit(NullLiteralExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.MethodCallExpr, java.lang.Object)
	 */
	@Override
	public R visit(MethodCallExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.NameExpr, java.lang.Object)
	 */
	@Override
	public R visit(NameExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ObjectCreationExpr, java.lang.Object)
	 */
	@Override
	public R visit(ObjectCreationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.QualifiedNameExpr, java.lang.Object)
	 */
	@Override
	public R visit(QualifiedNameExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.ThisExpr, java.lang.Object)
	 */
	@Override
	public R visit(ThisExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.SuperExpr, java.lang.Object)
	 */
	@Override
	public R visit(SuperExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.UnaryExpr, java.lang.Object)
	 */
	@Override
	public R visit(UnaryExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.VariableDeclarationExpr, java.lang.Object)
	 */
	@Override
	public R visit(VariableDeclarationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.MarkerAnnotationExpr, java.lang.Object)
	 */
	@Override
	public R visit(MarkerAnnotationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.SingleMemberAnnotationExpr, java.lang.Object)
	 */
	@Override
	public R visit(SingleMemberAnnotationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.NormalAnnotationExpr, java.lang.Object)
	 */
	@Override
	public R visit(NormalAnnotationExpr n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.expr.MemberValuePair, java.lang.Object)
	 */
	@Override
	public R visit(MemberValuePair n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ExplicitConstructorInvocationStmt, java.lang.Object)
	 */
	@Override
	public R visit(ExplicitConstructorInvocationStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.TypeDeclarationStmt, java.lang.Object)
	 */
	@Override
	public R visit(TypeDeclarationStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.AssertStmt, java.lang.Object)
	 */
	@Override
	public R visit(AssertStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.BlockStmt, java.lang.Object)
	 */
	@Override
	public R visit(BlockStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.LabeledStmt, java.lang.Object)
	 */
	@Override
	public R visit(LabeledStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.EmptyStmt, java.lang.Object)
	 */
	@Override
	public R visit(EmptyStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ExpressionStmt, java.lang.Object)
	 */
	@Override
	public R visit(ExpressionStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.SwitchStmt, java.lang.Object)
	 */
	@Override
	public R visit(SwitchStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.SwitchEntryStmt, java.lang.Object)
	 */
	@Override
	public R visit(SwitchEntryStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.BreakStmt, java.lang.Object)
	 */
	@Override
	public R visit(BreakStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ReturnStmt, java.lang.Object)
	 */
	@Override
	public R visit(ReturnStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.IfStmt, java.lang.Object)
	 */
	@Override
	public R visit(IfStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.WhileStmt, java.lang.Object)
	 */
	@Override
	public R visit(WhileStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ContinueStmt, java.lang.Object)
	 */
	@Override
	public R visit(ContinueStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.DoStmt, java.lang.Object)
	 */
	@Override
	public R visit(DoStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ForeachStmt, java.lang.Object)
	 */
	@Override
	public R visit(ForeachStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ForStmt, java.lang.Object)
	 */
	@Override
	public R visit(ForStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.ThrowStmt, java.lang.Object)
	 */
	@Override
	public R visit(ThrowStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.SynchronizedStmt, java.lang.Object)
	 */
	@Override
	public R visit(SynchronizedStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.TryStmt, java.lang.Object)
	 */
	@Override
	public R visit(TryStmt n, A arg) {
		return getDefaultReturnValue() ;
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.GenericVisitor#visit(japa.parser.ast.stmt.CatchClause, java.lang.Object)
	 */
	@Override
	public R visit(CatchClause n, A arg) {
		return getDefaultReturnValue() ;
	}

}
