package parser;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by sutd on 1/2/15.
 */
public class FileParser
{
    private CompilationUnit cu;
    private Map<String, ClassDescriptor> classDescriptorMap;

    public FileParser(File javaFile) throws IOException, ParseException
    {
        cu = JavaParser.parse(javaFile);
        classDescriptorMap = new TreeMap<String, ClassDescriptor>();

        parse();
    }

    public FileParser(CompilationUnit cu)
    {
        this.cu = cu;
        classDescriptorMap = new TreeMap<String, ClassDescriptor>();

        parse();
    }

    public Map<String, ClassDescriptor> getClasses()
    {
        return classDescriptorMap;
    }

    private void parse()
    {
        List<TypeDeclaration> types = cu.getTypes();

        for (TypeDeclaration type : types)
        {
            if (type instanceof ClassOrInterfaceDeclaration)
            {
                ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration)type;
                ClassDescriptor c = parseClass(clazz);
                classDescriptorMap.put(c.getQuantifiedName(), c);
            }
        }
    }

    private ClassDescriptor parseClass(ClassOrInterfaceDeclaration clssDecl)
    {
        ClassDescriptor cl = new ClassDescriptor(cu.getPackage(), clssDecl.getName());
        List<BodyDeclaration> members = clssDecl.getMembers();

        for (BodyDeclaration member : members)
        {
            if (member instanceof FieldDeclaration)
            {
                List<Variable> fields = parseField((FieldDeclaration)member);
                cl.addFields(fields);
            }
            else if (member instanceof MethodDeclaration)
            {
                Method func = parseFunction((MethodDeclaration)member);
                cl.addFunction(func);
            }
        }

        return cl;
    }

    private Method parseFunction(MethodDeclaration funcDecl)
    {
        Method function = new Method(funcDecl.getType(), funcDecl.getName());

        List<Parameter> params = funcDecl.getParameters();

        if (params != null)
        {
            for (Parameter p : params)
            {
                Variable v = new Variable(p.getType(), p.getId().getName(), null);
                function.addParam(v);
            }
        }

        BlockStmt body = funcDecl.getBody();
        List<Statement> stmts = body.getStmts();
        for (Statement stmt : stmts)
        {
            if (stmt instanceof ExpressionStmt)
            {
                ExpressionStmt expStmt = (ExpressionStmt) stmt;
                Expression exp = expStmt.getExpression();
                if (exp instanceof VariableDeclarationExpr)
                {
                    VariableDeclarationExpr varDeclExp = (VariableDeclarationExpr)exp;
                    List<VariableDeclarator> vList = varDeclExp.getVars();

                    for (VariableDeclarator vd : vList)
                    {
                        Variable v = new Variable(varDeclExp.getType(), vd.getId().getName(), null);
                        function.addLocalVar(v);
                    }
                }
            }
        }

        return function;
    }

    private List<Variable> parseField(FieldDeclaration varDecl)
    {
        List<Variable> variables = new ArrayList<Variable>();

        List<VariableDeclarator> vList = varDecl.getVariables();

        for (VariableDeclarator vd : vList)
        {
            Variable v = new Variable(varDecl.getType(), vd.getId().getName(), null);
            variables.add(v);
        }

        return variables;
    }

    public static void main(String[] args)
    {
        FileParser parser = null;
        try
        {
            parser = new FileParser(new File("C:\\Users\\1001385\\Dropbox\\MutantBug\\src\\hello/Test0.java"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        System.out.println("Done " + parser.classDescriptorMap.size());
    }

}
