package datastructure;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
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
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.comments.BlockComment;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.comments.LineComment;
import japa.parser.ast.expr.AnnotationExpr;
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
import japa.parser.ast.expr.Expression;
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
import japa.parser.ast.stmt.Statement;
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
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class PrintStructureVisitor<A> extends VoidVisitorAdapter<A> {
	
	@Override public void visit(final AnnotationDeclaration n, final A arg) {
		System.out.println("AnnotationDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final AnnotationMemberDeclaration n, final A arg) {
		System.out.println("AnnotationMemberDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ArrayAccessExpr n, final A arg) {
		System.out.println("ArrayAccessExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ArrayCreationExpr n, final A arg) {
		System.out.println("ArrayCreationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ArrayInitializerExpr n, final A arg) {
		System.out.println("ArrayInitializerExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final AssertStmt n, final A arg) {
		System.out.println("AssertStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final AssignExpr n, final A arg) {
		System.out.println("AssignExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final BinaryExpr n, final A arg) {
		System.out.println("BinaryExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final BlockComment n, final A arg) {
		System.out.println("BlockComment");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final BlockStmt n, final A arg) {
		System.out.println("BlockStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final BooleanLiteralExpr n, final A arg) {
		System.out.println("BooleanLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final BreakStmt n, final A arg) {
		System.out.println("BreakStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final CastExpr n, final A arg) {
		System.out.println("CastExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final CatchClause n, final A arg) {
		System.out.println("CatchClause");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final CharLiteralExpr n, final A arg) {
		System.out.println("CharLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ClassExpr n, final A arg) {
		System.out.println("ClassExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ClassOrInterfaceDeclaration n, final A arg) {
		System.out.println("ClassOrInterfaceDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ClassOrInterfaceType n, final A arg) {
		System.out.println("ClassOrInterfaceType");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final CompilationUnit n, final A arg) {
		System.out.println("CompilationUnit");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ConditionalExpr n, final A arg) {
		System.out.println("ConditionalExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ConstructorDeclaration n, final A arg) {
		System.out.println("ConstructorDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ContinueStmt n, final A arg) {
		System.out.println("ContinueStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final DoStmt n, final A arg) {
		System.out.println("DoStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final DoubleLiteralExpr n, final A arg) {
		System.out.println("DoubleLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EmptyMemberDeclaration n, final A arg) {
		System.out.println("EmptyMemberDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EmptyStmt n, final A arg) {
		System.out.println("EmptyStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EmptyTypeDeclaration n, final A arg) {
		System.out.println("EmptyTypeDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EnclosedExpr n, final A arg) {
		System.out.println("EnclosedExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EnumConstantDeclaration n, final A arg) {
		System.out.println("EnumConstantDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final EnumDeclaration n, final A arg) {
		System.out.println("EnumDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ExplicitConstructorInvocationStmt n, final A arg) {
		System.out.println("ExplicitConstructorInvocationStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ExpressionStmt n, final A arg) {
		System.out.println("ExpressionStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final FieldAccessExpr n, final A arg) {
		System.out.println("FieldAccessExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final FieldDeclaration n, final A arg) {
		System.out.println("FieldDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ForeachStmt n, final A arg) {
		System.out.println("ForeachStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ForStmt n, final A arg) {
		System.out.println("ForStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final IfStmt n, final A arg) {
		System.out.println("IfStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ImportDeclaration n, final A arg) {
		System.out.println("ImportDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final InitializerDeclaration n, final A arg) {
		System.out.println("InitializerDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final InstanceOfExpr n, final A arg) {
		System.out.println("InstanceOfExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final IntegerLiteralExpr n, final A arg) {
		System.out.println("IntegerLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final IntegerLiteralMinValueExpr n, final A arg) {
		System.out.println("IntegerLiteralMinValueExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final JavadocComment n, final A arg) {
		System.out.println("JavadocComment");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final LabeledStmt n, final A arg) {
		System.out.println("LabeledStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final LineComment n, final A arg) {
		System.out.println("LineComment");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final LongLiteralExpr n, final A arg) {
		System.out.println("LongLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final LongLiteralMinValueExpr n, final A arg) {
		System.out.println("LongLiteralMinValueExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final MarkerAnnotationExpr n, final A arg) {
		System.out.println("MarkerAnnotationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final MemberValuePair n, final A arg) {
		System.out.println("MemberValuePair");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final MethodCallExpr n, final A arg) {
		System.out.println("MethodCallExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final MethodDeclaration n, final A arg) {
		System.out.println("MethodDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final NameExpr n, final A arg) {
		System.out.println("NameExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final NormalAnnotationExpr n, final A arg) {
		System.out.println("NormalAnnotationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final NullLiteralExpr n, final A arg) {
		System.out.println("NullLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ObjectCreationExpr n, final A arg) {
		System.out.println("ObjectCreationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final PackageDeclaration n, final A arg) {
		System.out.println("PackageDeclaration");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final Parameter n, final A arg) {
		System.out.println("Parameter");
		System.out.println(n);
		
		super.visit(n, arg);
	}
	
	@Override public void visit(final MultiTypeParameter n, final A arg) {
		System.out.println("MultiTypeParameter");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final PrimitiveType n, final A arg) {
		System.out.println("PrimitiveType");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final QualifiedNameExpr n, final A arg) {
		System.out.println("QualifiedNameExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ReferenceType n, final A arg) {
		System.out.println("ReferenceType");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ReturnStmt n, final A arg) {
		System.out.println("ReturnStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final SingleMemberAnnotationExpr n, final A arg) {
		System.out.println("SingleMemberAnnotationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final StringLiteralExpr n, final A arg) {
		System.out.println("StringLiteralExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final SuperExpr n, final A arg) {
		System.out.println("SuperExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final SwitchEntryStmt n, final A arg) {
		System.out.println("SwitchEntryStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final SwitchStmt n, final A arg) {
		System.out.println("SwitchStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final SynchronizedStmt n, final A arg) {
		System.out.println("SynchronizedStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ThisExpr n, final A arg) {
		System.out.println("ThisExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final ThrowStmt n, final A arg) {
		System.out.println("ThrowStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final TryStmt n, final A arg) {
		System.out.println("TryStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final TypeDeclarationStmt n, final A arg) {
		System.out.println("TypeDeclarationStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final TypeParameter n, final A arg) {
		System.out.println("TypeParameter");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final UnaryExpr n, final A arg) {
		System.out.println("UnaryExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final VariableDeclarationExpr n, final A arg) {
		System.out.println("VariableDeclarationExpr");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final VariableDeclarator n, final A arg) {
		System.out.println("VariableDeclarator");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final VariableDeclaratorId n, final A arg) {
		System.out.println("VariableDeclaratorId");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final VoidType n, final A arg) {
		System.out.println("VoidType");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final WhileStmt n, final A arg) {
		System.out.println("WhileStmt");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	@Override public void visit(final WildcardType n, final A arg) {
		System.out.println("WildcardType");
		System.out.println(n);
		
		super.visit(n, arg);
	}

	private void visitComment(final Comment n, final A arg) {
		if (n != null) {
			n.accept(this, arg);
		}
	}

}
