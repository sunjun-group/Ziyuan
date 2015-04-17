package mutation.mutator;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.Map;

/**
 * Created by hoangtung on 4/3/15.
 */
public class MutationVisitor extends VoidVisitorAdapter<Map<String, Object>>
{

    public static void main(String[] args)
    {
        try
        {
            CompilationUnit cu = JavaParser.parse(new File("./src/test/mutator/ExpressionTest0.java"));
            new MutationVisitor().visit(cu, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    @Override
//    public void visit(ArrayAccessExpr node, Map<String, Object> arg)
//    {
//        System.out.println(node);
//         
//    }
//
//    @Override
//    public void visit(ArrayInitializerExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(AssignExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(BinaryExpr node, Map<String, Object> arg)
//    {
//        System.out.println(node);
//         
//    }
//    @Override
//    public void visit(BlockStmt node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(BooleanLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(BreakStmt node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(CastExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(CharLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ClassOrInterfaceDeclaration node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ClassOrInterfaceType node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ConditionalExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ContinueStmt node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(DoubleLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(EnclosedExpr node, Map<String, Object> arg)
//    {
//         
//    }

    @Override
    public void visit(ExpressionStmt node, Map<String, Object> arg)
    {
//        super.visit(node, arg);
        Expression exp = node.getExpression();
        System.out.println(exp);
    }

//    @Override
//    public void visit(FieldAccessExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
    @Override
    public void visit(FieldDeclaration node, Map<String, Object> arg)
    {
        System.out.println(node);
    }

    @Override
    public void visit(ForStmt node, Map<String, Object> arg)
    {
        System.out.println(node);
        node.getBody().accept(this, arg);
    }

    @Override
    public void visit(IfStmt node, Map<String, Object> arg)
    {
//        CompilationUnitMutator.mutate(node, arg);
        node.getThenStmt().accept(this, arg);
        node.getElseStmt().accept(this, arg);
    }

//    @Override
//    public void visit(IntegerLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(LongLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(MethodCallExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(MethodDeclaration node, Map<String, Object> arg)
//    {
//        super.visit(node, arg);
//        System.out.println(node.getName());
//    }
//
//    @Override
//    public void visit(NameExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(QualifiedNameExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ReferenceType node, Map<String, Object> arg)
//    {
//         
//    }

    @Override
    public void visit(ReturnStmt node, Map<String, Object> arg)
    {
        System.out.println(node);
    }

//    @Override
//    public void visit(StringLiteralExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(SuperExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(ThisExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(TypeDeclarationStmt node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(UnaryExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(VariableDeclarationExpr node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(VariableDeclarator node, Map<String, Object> arg)
//    {
//         
//    }
//
//    @Override
//    public void visit(VariableDeclaratorId node, Map<String, Object> arg)
//    {
//         
//    }
//
    @Override
    public void visit(WhileStmt node, Map<String, Object> arg)
    {
        node.getBody().accept(this, arg);
    }

}
