package mutator;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import parser.ClassDescriptor;
import parser.FolderParser;
import parser.Method;
import parser.Variable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by sutd on 1/3/15.
 */
public class Mutator
{
    static Map<String, ClassDescriptor> classDescriptors;
    static List<Map<String, Variable>> variables;
    static Set<Method> functions;
    static Map<String, ClassDescriptor> classMap;
    static ClassOrInterfaceType curClassType;
    static ClassOrInterfaceType curPackage;
    static List<ImportDeclaration> importDeclarations;
    static long NUMERIC_RANGE = Integer.MAX_VALUE;

    public static void loadClassDescriptor(File sourceFolder)
    {
        FolderParser folderParser;
        try
        {
            folderParser = new FolderParser(sourceFolder);
            classDescriptors = folderParser.getClassDescriptors();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    static void openScope()
    {
        variables.add(0, new TreeMap<String, Variable>());
    }

    static void closeScope()
    {
        variables.remove(0);
    }


    public static void addVariables(Variable v)
    {
        variables.get(0).put(v.name, v);
    }

    static void addVariables(List<? extends Variable> varList)
    {
        Map<String, Variable> currentScope = variables.get(0);
        for (Variable var : varList)
        {
            currentScope.put(var.name, var);
        }
    }

    public static List<Expression> mutateVarDecExpr(VariableDeclarationExpr varDecExpr)
    {
        Type type = varDecExpr.getType();
        List<Expression> muExps = new ArrayList<Expression>();
        List<VariableDeclarator> varDecls = varDecExpr.getVars();
        List<List<VariableDeclarator>> muVarDeclLists = new ArrayList<List<VariableDeclarator>>();

        int idx = 0;
        for (VariableDeclarator varDecl : varDecls)
        {
            Expression init = varDecl.getInit();
            VariableDeclaratorId id = varDecl.getId();
            List<Expression> muInits = mutateExpression(init);

            for (Expression muInit : muInits)
            {
                List<VariableDeclarator> muVarDecls = new ArrayList<VariableDeclarator>(varDecls.size());
                muVarDecls.addAll(varDecls);
//                Collections.copy(muVarDecls, varDecls);
                VariableDeclarator muVarDecl = new VariableDeclarator(id, muInit);
                muVarDecls.remove(idx);
                muVarDecls.add(idx, muVarDecl);
                muVarDeclLists.add(muVarDecls);
            }

            ++idx;
        }

        for (List<VariableDeclarator> decl : muVarDeclLists)
        {
            VariableDeclarationExpr muVarDeclExp = new VariableDeclarationExpr(type, decl);
            muExps.add(muVarDeclExp);
        }

        return muExps;
    }

    public static List<AssignExpr.Operator> mutateOperator(AssignExpr.Operator operator)
    {
        return null;
    }

    public static List<Expression> mutateNameExpr(NameExpr identifier)
    {
        List<Expression> ret = new ArrayList<Expression>();

        Variable variable = getVariableFromName(identifier.getName());

        if (variable.type != ASTHelper.BOOLEAN_TYPE && variable.type != ASTHelper.VOID_TYPE)
        {
            List<?> operatorList = MutationMap.getMutationOp(MutationMap.IDENTIFIER);
            if (operatorList != null && operatorList.size() > 0)
            {
                for (Object operator : operatorList)
                {
                    UnaryExpr expr = new UnaryExpr(identifier, (UnaryExpr.Operator) operator);
                    ret.add(expr);
                    System.out.println(expr);
                }
            }
        }

        return ret;
    }

    public static List<Expression> mutateUnaryExpr(UnaryExpr unaryExpr)
    {
        UnaryExpr.Operator operator = unaryExpr.getOperator();
        Expression exp = unaryExpr.getExpr();
        List<UnaryExpr.Operator> muOp = (List<UnaryExpr.Operator>)MutationMap.getMutationOp(operator.getClass() + operator.toString());
        List<Expression> muExps = new ArrayList<Expression>();

        if (muOp != null && muOp.size() > 0)
        {
            for (UnaryExpr.Operator op : muOp)
            {
                UnaryExpr muExp = new UnaryExpr(exp, op);
                muExps.add(muExp);
            }
        }

        return muExps;
    }

    public static List<Expression> mutateCastExpr(CastExpr castExpr)
    {
        Expression exp = castExpr.getExpr();
        List<Expression> muInner = mutateExpression(exp);
        List<Expression> muExps = new ArrayList<Expression>();
        Type type = castExpr.getType();

        for (Expression inner : muInner)
        {
            Expression muExp = new CastExpr(type, inner);
            muExps.add(muExp);
        }

        return muExps;
    }

    public static List<Expression> mutateAssignExpr(AssignExpr assignExpr)
    {
        List<Expression> muExps = new ArrayList<Expression>();

        AssignExpr.Operator operator = assignExpr.getOperator();
        Expression target = assignExpr.getTarget();
        Expression value = assignExpr.getValue();
        List<AssignExpr.Operator> muOps = MutationMap.getMutationOp(operator.getClass() + operator.toString());
        if (muOps != null && muOps.size() > 0)
        {
            for (AssignExpr.Operator muOp : muOps)
            {
                AssignExpr muExp = new AssignExpr(target, value, muOp);
                muExps.add(muExp);
            }
        }

        List<Expression> muVals = mutateExpression(value);
        for (Expression muVal : muVals)
        {
            AssignExpr muExp = new AssignExpr(target, muVal, operator);
            muExps.add(muExp);
        }

        return muExps;
    }

    public static List<Expression> mutateFieldAccessExpr(FieldAccessExpr fieldAccessExpr)
    {
        List<Expression> muExps = new ArrayList<Expression>();
        Variable field = getFieldFromExpr(fieldAccessExpr);
        List<Variable> friends = field.clazz.getFieldWithType(field.type);
        Expression scope = fieldAccessExpr.getScope();
        String fieldName = fieldAccessExpr.getField();

        for (Variable f : friends)
        {
            if (!f.name.equals(fieldName))
            {
                System.out.println("FieldF: " + f.name);
                Expression muExp = new FieldAccessExpr(scope, f.name);
                muExps.add(muExp);
            }
        }

        return muExps;
    }

    public static List<Expression> mutateMethodCallExpr(MethodCallExpr methodCallExpr)
    {
        List<Expression> muExps = new ArrayList<Expression>();

        Method m = getMethodFromExpr(methodCallExpr);
        if (m != null)
        {
            ClassDescriptor cl = m.clazz;
            Expression scope = methodCallExpr.getScope();
            List<Method> friends = cl.getMethodsWithType(m.type);
            String signature = m.getSignature();
            for (Method f : friends)
            {
                if (!f.getSignature().equals(signature))
                {
                    System.out.println("Func: " + f.getSignature());
                    List<Expression> args = new ArrayList<Expression>();
                    for (Variable v : f.params)
                    {
                        if (v.type instanceof PrimitiveType)
                        {
                            Expression arg = genRandomVal(v.type);
                            args.add(arg);
                        }
                        else
                        {
                            args.add(new NullLiteralExpr());
                        }
                    }

                    MethodCallExpr muCall = new MethodCallExpr(scope, f.name, args);
                    muExps.add(muCall);
                }
            }
        }

        return muExps;
    }

    public static List<Expression> mutateBinaryExpr(BinaryExpr binaryExpr)
    {
        BinaryExpr.Operator op = binaryExpr.getOperator();
        Expression left = binaryExpr.getLeft();
        Expression right = binaryExpr.getRight();
        List<Object> muOps = MutationMap.getMutationOp(op.getClass() + op.toString());
        List<Expression> muExps = new ArrayList<Expression>();
        if (muOps != null && muOps.size() > 0)
        {
            for (Object muOp : muOps)
            {
                BinaryExpr muExp = new BinaryExpr(left, right, (BinaryExpr.Operator)muOp);
                muExps.add(muExp);
            }

            List<Expression> muRights = mutateExpression(right);
            for (Expression muRight : muRights)
            {
                BinaryExpr muExp = new BinaryExpr(left, muRight, op);
                muExps.add(muExp);
            }

            List<Expression> muLefts = mutateExpression(left);
            for (Expression muLeft : muLefts)
            {
                BinaryExpr muExp = new BinaryExpr(muLeft, right, op);
                muExps.add(muExp);
            }
        }
//
//        for (Expression exp : muExps)
//        {
//            System.out.println(exp);
//        }

        return muExps;
    }

    public static List<Expression> mutateExpression(Expression expression)
    {
        if (expression instanceof VariableDeclarationExpr)
        {
            System.out.println("VariableDeclarationExpr" + " " + expression);
            VariableDeclarationExpr varDeclExpr = (VariableDeclarationExpr)expression;
            List<VariableDeclarator> vList = varDeclExpr.getVars();

            for (VariableDeclarator vd : vList)
            {
                Variable v = new Variable(varDeclExpr.getType(), vd.getId().getName(), null);
                addVariables(v);
            }

            return mutateVarDecExpr(varDeclExpr);
        }
        else if (expression instanceof UnaryExpr)
        {
            System.out.println("UnaryExpr" + " " + expression);
            UnaryExpr unaryExpr = (UnaryExpr)expression;

            return mutateUnaryExpr(unaryExpr);
        }
        else if (expression instanceof NameExpr)
        {
            System.out.println("NameExpr" + " " + expression);
            NameExpr nameExpr = (NameExpr)expression;

            return mutateNameExpr(nameExpr);
        }
        else if (expression instanceof AssignExpr)
        {
            System.out.println("AssignExpr" + " " + expression);
            AssignExpr assignExpr = (AssignExpr)expression;

            return mutateAssignExpr(assignExpr);
        }
        else if (expression instanceof MethodCallExpr)
        {
            System.out.println("MethodCallExpr" + " " + expression);
            MethodCallExpr methodCallExpr = (MethodCallExpr)expression;

            return mutateMethodCallExpr(methodCallExpr);
        }
        else if (expression instanceof BinaryExpr)
        {
            System.out.println("BinaryExpr" + " " + expression);
            BinaryExpr binaryExpr = (BinaryExpr)expression;

            return mutateBinaryExpr(binaryExpr);
        }
        else if (expression instanceof EnclosedExpr)
        {
            System.out.println("EnclosedExpr" + " " + expression);
            Expression inner = ((EnclosedExpr)expression).getInner();
            List<Expression> muInners = mutateExpression(inner);
            List<Expression> muExps = new ArrayList<Expression>();
            for (Expression muInner : muInners)
            {
                Expression muExp = new EnclosedExpr(muInner);
                muExps.add(muExp);
            }

            return muExps;
        }
        else if (expression instanceof FieldAccessExpr)
        {
            System.out.println("FieldAccessExpr" + " " + expression);
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr)expression;

            return mutateFieldAccessExpr(fieldAccessExpr);
        }
        else if (expression instanceof CastExpr)
        {
            System.out.print("CastExpr" + " " + expression);
            CastExpr castExpr = (CastExpr)expression;

            return mutateCastExpr(castExpr);
        }

        // if this expression cannot be mutated
        List<Expression> identity = new ArrayList<Expression>();
        return identity;
    }

    public static List<Statement> mutateStatement(Statement stmt)
    {
        if (stmt != null)
        {
            int beginCol = stmt.getBeginColumn();
            int beginLine = stmt.getBeginLine();
            int endLine = stmt.getEndLine();
            int endCol = stmt.getEndColumn();

            if (stmt instanceof ExpressionStmt)
            {
                Expression exp = ((ExpressionStmt) stmt).getExpression();
                List<Statement> muStmts = new ArrayList<Statement>();

                List<Expression> muExps = mutateExpression(exp);
                if (muExps != null && muExps.size() > 0)
                {
                    for (Expression muExp : muExps)
                    {
                        ExpressionStmt muStmt = new ExpressionStmt(muExp);
                        copyBoudary(muStmt, stmt);
                        muStmts.add(muStmt);
                    }
                }

                return muStmts;
            }
            else if (stmt instanceof BreakStmt)
            {
                List<Statement> muStmts = new ArrayList<Statement>();
                muStmts.add(new ContinueStmt(beginLine, beginCol, endLine, endCol, null));
                return muStmts;
            }
            else if (stmt instanceof ContinueStmt)
            {
                List<Statement> muStmts = new ArrayList<Statement>();
                muStmts.add(new BreakStmt(beginLine, beginCol, endLine, endCol, null));
                return muStmts;
            }
            else if (stmt instanceof IfStmt)
            {
                List<Statement> muStmts = new ArrayList<Statement>();
                IfStmt ifStmt = (IfStmt) stmt;
                Expression cond = ifStmt.getCondition();
                List<Expression> muConds = mutateExpression(cond);
                Statement thenStmt = ifStmt.getThenStmt();
                Statement elseStmt = ifStmt.getElseStmt();

                for (Expression muCond : muConds)
                {
                    IfStmt muIf = new IfStmt(muCond, thenStmt, elseStmt);
                    copyBoudary(muIf, stmt);
                    muStmts.add(muIf);
                }

                List<Statement> muThens = mutateStatement(thenStmt);
                for (Statement muThen : muThens)
                {
                    IfStmt muIf = new IfStmt(cond, muThen, elseStmt);
                    copyBoudary(muIf, muThen);
                    muStmts.add(muIf);
                }

                List<Statement> muElses = mutateStatement(elseStmt);
                for (Statement muElse : muElses)
                {
                    IfStmt muIf = new IfStmt(cond, thenStmt, muElse);
                    copyBoudary(muIf, muElse);
                    muStmts.add(muIf);
                }

                return muStmts;
            }
            else if (stmt instanceof ForStmt)
            {
                System.out.println("ForStmt");
                List<Statement> muStmts = new ArrayList<Statement>();

                ForStmt forStmt = (ForStmt) stmt;
                List<Expression> initList = forStmt.getInit();
                List<Expression> updateList = forStmt.getUpdate();
                Expression compareExp = forStmt.getCompare();
                Statement body = forStmt.getBody();

                // mutate init expressions
                int i = 0;
                for (Expression initExp : initList)
                {
                    List<Expression> muInitExps = mutateExpression(initExp);
                    for (Expression muInitExp : muInitExps)
                    {
                        List<Expression> muInitList = new ArrayList<Expression>(initList);
                        muInitList.set(i, muInitExp);
                        ForStmt muForStmt = new ForStmt(muInitList, compareExp, updateList, body);
                        copyBoudary(muForStmt, initExp);
                        muStmts.add(muForStmt);
                    }

                    ++i;
                }

                // mutate compare expression
                List<Expression> muCompareExps = mutateExpression(compareExp);
                for (Expression muCompareExp : muCompareExps)
                {
                    ForStmt muForStmt = new ForStmt(initList, muCompareExp, updateList, body);
                    copyBoudary(muForStmt, compareExp);
                    muStmts.add(muForStmt);
                }

                // mutate update expressions
                i = 0;
                for (Expression updateExp : updateList)
                {
                    List<Expression> muUpdateExps = mutateExpression(updateExp);
                    for (Expression muUpdateExp : muUpdateExps)
                    {
                        List<Expression> muUpdateList = new ArrayList<Expression>(updateList);
                        muUpdateList.set(i, muUpdateExp);
                        ForStmt muForStmt = new ForStmt(initList, compareExp, muUpdateList, body);
                        copyBoudary(muForStmt, updateExp);
                        muStmts.add(muForStmt);
                    }
                }

                // mutate body block
                List<Statement> muBodies = mutateStatement(body);
                for (Statement muBody : muBodies)
                {
                    ForStmt muForStmt = new ForStmt(initList, compareExp, updateList, muBody);
                    copyBoudary(muForStmt, muBody);
                    muStmts.add(muForStmt);
                }

                return muStmts;
            }
            else if (stmt instanceof WhileStmt)
            {
                WhileStmt whileStmt = (WhileStmt)stmt;
                Expression cond = whileStmt.getCondition();
                Statement body = whileStmt.getBody();
                List<Statement> muStmts = new ArrayList<Statement>();

                List<Expression> muConds = mutateExpression(cond);
                for (Expression muCond : muConds)
                {
                    WhileStmt muWhile = new WhileStmt(muCond, body);
                    copyBoudary(muWhile, cond);
                    muStmts.add(muWhile);
                }

                List<Statement> muBodies = mutateStatement(body);
                for (Statement muBody : muBodies)
                {
                    WhileStmt muWhile = new WhileStmt(cond, muBody);
                    copyBoudary(muWhile, muBody);
                    muStmts.add(muWhile);
                }

                return muStmts;
            }
            else if (stmt instanceof SwitchStmt)
            {
                //throw new NotImplementedException();
            }
            else if (stmt instanceof BlockStmt)
            {
                List<Statement> muStmts = new ArrayList<Statement>();
                // recursive call to this function
                openScope();

                BlockStmt blockStmt = (BlockStmt) stmt;
                List<Statement> stmtList = blockStmt.getStmts();
                int nbStmt = stmtList.size();

                for (int i = 0; i < nbStmt; ++i)
                {
                    Statement si = stmtList.get(i);
                    List<Statement> muSis = mutateStatement(si);
                    for (Statement muSi : muSis)
                    {
                        List<Statement> muStmtList = new ArrayList<Statement>(stmtList);
                        muStmtList.set(i, muSi);
                        BlockStmt muBlock = new BlockStmt(muStmtList);
                        copyBoudary(muBlock, muSi);
                        muStmts.add(muBlock);
                    }
                }

                closeScope();

                return muStmts;
            }
            else if (stmt instanceof ReturnStmt)
            {
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                Expression expression = returnStmt.getExpr();
                List<Expression> muExps = mutateExpression(expression);
                List<Statement> muStmts = new ArrayList<Statement>();

                for (Expression muExp : muExps)
                {
                    ReturnStmt muRet = new ReturnStmt(muExp);
                    copyBoudary(muRet, stmt);
                    muStmts.add(muRet);
                }

                return muStmts;
            }
        }

        List<Statement> identity = new ArrayList<Statement>();
        return identity;
    }


    public static List<MethodDeclaration> mutateMethod(MethodDeclaration funcDecl)
    {
        openScope();

        List<Parameter> params = funcDecl.getParameters();
        if (params != null)
        {
            for (Parameter param : params) {
                Variable v = new Variable(param.getType(), param.getId().getName(), null);
                addVariables(v);
            }
        }

        BlockStmt body = funcDecl.getBody();
        List<Statement> stmts = body.getStmts();
        List<MethodDeclaration> muMethods = new ArrayList<MethodDeclaration>();

        List<Statement> muBodies = mutateStatement(body);
        for (Statement muBody : muBodies)
        {
            MethodDeclaration muFunc = cloneMethod(funcDecl);
            muFunc.setBody((BlockStmt)muBody);
            copyBoudary(muFunc, muBody);
            muMethods.add(muFunc);
//            System.out.println("Mutate: " + muBody.getBeginLine() + " " + muBody.getBeginColumn());
//            System.out.println(muFunc);
        }

        closeScope();

        return muMethods;
    }

    public static List<ClassOrInterfaceDeclaration> mutateClass(ClassOrInterfaceDeclaration classDecl)
    {
        List<ClassOrInterfaceDeclaration> muClasses = new ArrayList<ClassOrInterfaceDeclaration>();
        List<BodyDeclaration> members = classDecl.getMembers();

        variables = new LinkedList<Map<String, Variable>>();
        variables.add(new TreeMap<String, Variable>());
        for (BodyDeclaration member : members)
        {
            // currently, no mutation on field declaration
            // we just add all of the fields to the first scope
            if (member instanceof FieldDeclaration)
            {
                List<Variable> field = parseField((FieldDeclaration)member);
                addVariables(field);
            }
        }

        int memSize = members.size();
        for (int i = 0; i < memSize; ++i)
        {
            BodyDeclaration member = members.get(i);
            if (member instanceof MethodDeclaration)
            {
                List<MethodDeclaration> muMethods = mutateMethod((MethodDeclaration) member);
                for (MethodDeclaration muMethod : muMethods)
                {
                    ClassOrInterfaceDeclaration muClassDecl = cloneClassDecl(classDecl);
                    List<BodyDeclaration> muMembers = new ArrayList<BodyDeclaration>(members);
                    muMembers.set(i, muMethod);
                    muClassDecl.setMembers(muMembers);
                    copyBoudary(muClassDecl, muMethod);
                    muClasses.add(muClassDecl);
//                    System.out.println("Class " + muClassDecl.getName() + " " + muClassDecl.getBeginLine() + " " + muClassDecl.getBeginColumn());
                }
            }
        }

        for (ClassOrInterfaceDeclaration muClass : muClasses)
        {
            System.out.println("Class " + muClass.getName() + " " + muClass.getBeginLine() + " " + muClass.getBeginColumn());
        }

        return muClasses;
    }

    public static void mutateFile(File javaFile, File outputFolder)
    {
        try
        {
            CompilationUnit cu = JavaParser.parse(javaFile);

            importDeclarations = cu.getImports();

            PackageDeclaration pkgName = cu.getPackage();
            if (pkgName != null)
            {
                curPackage = new ClassOrInterfaceType(pkgName.getName().toString());
            }
            else
            {
                curPackage = new ClassOrInterfaceType("");
            }

            List<TypeDeclaration> types = cu.getTypes();
            System.out.println(cu.getPackage().toString());

            int typeSize = types.size();
            for (int i = 0; i < typeSize; ++i)
            {
                TypeDeclaration type = types.get(i);
                System.out.println(type.getName());
                if (type instanceof ClassOrInterfaceDeclaration)
                {
                    curClassType = new ClassOrInterfaceType(curPackage, type.getName());
                    List<ClassOrInterfaceDeclaration> muClasses = mutateClass((ClassOrInterfaceDeclaration)type);

                    int version = 0;
                    int line = 0;
                    int col = 0;
                    for (ClassOrInterfaceDeclaration muClass : muClasses)
                    {
                        cu.getTypes().set(i, muClass);
                        copyBoudary(cu, muClass);

                        if (cu.getBeginLine() != line || cu.getBeginColumn() != col)
                        {
                            line = cu.getBeginLine();
                            col = cu.getBeginColumn();
                            version = 0;
                        }
                        else
                        {
                            ++version;
                        }

                        String folderName = cu.getBeginLine() + "_" + cu.getBeginColumn();
                        File lineFolder = new File(outputFolder, folderName);
                        if (!lineFolder.exists())
                        {
                            lineFolder.mkdir();
                        }
                        File verFolder = new File(lineFolder, version + "");
                        verFolder.mkdir();
                        File muFile = new File(verFolder, javaFile.getName());

                        BufferedWriter bw = new BufferedWriter(new FileWriter(muFile));
                        bw.write(cu.toString());
                        bw.close();

                        cu.getTypes().set(i,  type);
//                        System.out.println("File " + cu.getBeginLine() + " " + cu.getBeginColumn());
//                        System.out.println("Class " + muClass.getName() + " " + muClass.getBeginLine() + " " + muClass.getBeginColumn());
                    }
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the class descriptor given its type. This only works for classes in the source folder
     * @param type the type object
     * @return the class corresponding the type
     */
    public static ClassDescriptor getClassDescriptor(Type type)
    {
        if (type != null)
        {
            String name = type.toString();
            ClassDescriptor cd = classDescriptors.get(name);

            // if the type name is not a fully quantified name it may come from the current package or an imported package
            if (cd == null)
            {
                String quantifiedName = createQuantifiedName(curPackage.toString(), name);
                cd = classDescriptors.get(quantifiedName);

                if (cd == null)
                {
                    for (ImportDeclaration id : importDeclarations)
                    {
                        String packageName = id.getName().toString();
                        quantifiedName = createQuantifiedName(packageName, name);
                        cd = classDescriptors.get(quantifiedName);
                        if (cd != null)
                        {
                            break;
                        }
                    }
                }
            }

            return cd;
        }

        return null;
    }

    private static String createQuantifiedName(String packageName, String className)
    {
        if (packageName.endsWith("."))
        {
            return packageName + className;
        }

        return packageName + "." + className;
    }

    public static Expression genRandomVal(Type type)
    {
        if (type.equals(ASTHelper.BOOLEAN_TYPE))
        {
            return new BooleanLiteralExpr(Math.random() > 0.5);
        }
        else if (type.equals(ASTHelper.DOUBLE_TYPE))
        {
            return new DoubleLiteralExpr("" + ((Math.random() - 0.5) * 2 * NUMERIC_RANGE));
        }
        else if (type.equals(ASTHelper.FLOAT_TYPE))
        {
            return new DoubleLiteralExpr(((Math.random() - 0.5) * 2 * NUMERIC_RANGE) + "f");
        }
        else if (type.equals(ASTHelper.INT_TYPE) || type.equals(ASTHelper.LONG_TYPE))
        {
            return new IntegerLiteralExpr((int)((Math.random() - 0.5) * 2 * NUMERIC_RANGE) + "");
        }
        else if (type.equals(ASTHelper.SHORT_TYPE))
        {
            return new IntegerLiteralExpr((int)((Math.random() - 0.5) * 2 * NUMERIC_RANGE) % Short.MAX_VALUE + "");
        }
        else if (type.equals(ASTHelper.CHAR_TYPE))
        {
            return new CharLiteralExpr((char)('A' + (int)(Math.random() * 26)) + "");
        }

        return null;
    }

    public static Type getExpressionType(Expression expression)
    {
        if (expression == null || expression instanceof ThisExpr)
        {
            return curClassType;
        }
        else if (expression instanceof SuperExpr)
        {
            return curClassType;
        }
        else if (expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr)expression;
            Variable variable = getVariableFromName(nameExpr.getName());
            if (variable != null)
                return variable.type;
        }
        else if (expression instanceof LiteralExpr)
        {
            if (expression instanceof IntegerLiteralExpr)
            {
                return ASTHelper.INT_TYPE;
            }
            else if (expression instanceof LongLiteralExpr)
            {
                return ASTHelper.LONG_TYPE;
            }
            else if (expression instanceof DoubleLiteralExpr)
            {
                return ASTHelper.DOUBLE_TYPE;
            }
            else if (expression instanceof CharLiteralExpr)
            {
                return ASTHelper.CHAR_TYPE;
            }
            else if (expression instanceof BooleanLiteralExpr)
            {
                return ASTHelper.BOOLEAN_TYPE;
            }
            else if (expression instanceof StringLiteralExpr)
            {
                return new ReferenceType(new ClassOrInterfaceType("String")); // string
            }
        }
        else if (expression instanceof BinaryExpr)
        {
            BinaryExpr binaryExpr = (BinaryExpr)expression;
            Expression left = binaryExpr.getLeft();
            Expression right = binaryExpr.getRight();
            Type leftType = getExpressionType(left);
            Type rightType = getExpressionType(right);

            return getMaxType(leftType, rightType);
        }
        else if (expression instanceof EnclosedExpr)
        {
            EnclosedExpr enclosedExpr = (EnclosedExpr) expression;
            return getExpressionType(enclosedExpr.getInner());
        }
        else if (expression instanceof CastExpr)
        {
            return ((CastExpr)expression).getType();
        }
        else if (expression instanceof MethodCallExpr)
        {
            Method method = getMethodFromExpr((MethodCallExpr) expression);
            return method.getType();
        }
        else if (expression instanceof FieldAccessExpr)
        {
            Variable v = getVariableFromExpr(expression);
            if (v != null)
            {
                return v.type;
            }
        }

        return null;
    }

    static Variable getVariableFromName(String variableName)
    {
        Iterator<Map<String, Variable>> varIt = variables.iterator();

        while (varIt.hasNext())
        {
            Map<String, Variable> varSet = varIt.next();

            Variable var = varSet.get(variableName);
            if (var != null)
            {
                return var;
            }
        }

        return null;
    }

    public static Variable getVariableFromExpr(Expression expression)
    {
        if (expression instanceof NameExpr)
        {
            return getVariableFromName(((NameExpr)expression).getName());
        }
        else if (expression instanceof FieldAccessExpr)
        {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr)expression;
            Expression scope = fieldAccessExpr.getScope();
            ClassDescriptor descriptor = getClassDescriptor(getExpressionType(scope));
            if (descriptor != null)
                return descriptor.getField(fieldAccessExpr.getField());
        }

        return null;
    }

    public static Variable getFieldFromExpr(FieldAccessExpr fieldAccessExpr)
    {
        Expression scope = fieldAccessExpr.getScope();
        Type type = getExpressionType(scope);

        if (type != null)
        {
            ClassDescriptor cl = getClassDescriptor(type);

            if (cl != null)
            {
                return cl.getField(fieldAccessExpr.getField());
            }
        }

        return null;
    }

    public static Method getMethodFromExpr(MethodCallExpr methodCallExpr)
    {
        Expression scope = methodCallExpr.getScope();
        Type type = getExpressionType(scope);
        if (type != null)
        {
            ClassDescriptor descriptor = getClassDescriptor(type);
            if (descriptor != null)
            {
                List<Expression> expList = methodCallExpr.getArgs();
                List<Type> params = new ArrayList<Type>();

                if (expList != null)
                {
                    for (Expression exp : expList)
                    {
                        params.add(getExpressionType(exp));
                    }
                }

                return descriptor.getMethodFromSig(methodCallExpr.getName(), params);
            }
        }

        return null;
    }

    private static List<Variable> parseField(FieldDeclaration varDecl)
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

    private static void setBoundary(Node node, int beginLine, int endLine, int beginColumn, int endColumn)
    {
        node.setBeginLine(beginLine);
        node.setEndColumn(endColumn);
        node.setEndLine(endLine);
        node.setBeginColumn(beginColumn);
    }

    private static void copyBoudary(Node dest, Node source)
    {
        if (dest != null && source != null)
            setBoundary(dest, source.getBeginLine(), source.getEndLine(), source.getBeginColumn(), source.getEndColumn());
    }

    private static MethodDeclaration cloneMethod(MethodDeclaration decl)
    {
//        MethodDeclaration clone = new MethodDeclaration(decl.getJavaDoc(), decl.getModifiers(), decl.getAnnotations(), decl.getTypeParameters(), decl.getType(), decl.getName(), decl.getParameters(), decl.getArrayCount(), decl.getThrows(), decl.getBody());
    	MethodDeclaration clone = new MethodDeclaration(decl.getModifiers(), decl.getAnnotations(), decl.getTypeParameters(), decl.getType(), decl.getName(), decl.getParameters(), decl.getArrayCount(), decl.getThrows(), decl.getBody());
        copyBoudary(clone, decl);
        return clone;
    }

    private static ClassOrInterfaceDeclaration cloneClassDecl(ClassOrInterfaceDeclaration source)
    {
        ClassOrInterfaceDeclaration clone = new ClassOrInterfaceDeclaration(source.getModifiers(), source.getAnnotations(), source.isInterface(), source.getName(), source.getTypeParameters(), source.getExtends(), source.getImplements(), source.getMembers());
        copyBoudary(clone, source);
        return clone;
    }

    private static Type getMaxType(Type leftType, Type rightType)
    {
        if (leftType instanceof PrimitiveType && rightType instanceof PrimitiveType)
        {
            if (leftType == ASTHelper.DOUBLE_TYPE || rightType == ASTHelper.DOUBLE_TYPE)
            {
                return ASTHelper.DOUBLE_TYPE;
            }

            if (leftType == ASTHelper.FLOAT_TYPE || rightType == ASTHelper.FLOAT_TYPE)
            {
                return ASTHelper.FLOAT_TYPE;
            }

            if (leftType == ASTHelper.LONG_TYPE || rightType == ASTHelper.LONG_TYPE)
            {
                return ASTHelper.LONG_TYPE;
            }

            if (leftType == ASTHelper.INT_TYPE || rightType == ASTHelper.INT_TYPE)
            {
                return ASTHelper.INT_TYPE;
            }

            if (leftType == ASTHelper.SHORT_TYPE || rightType == ASTHelper.SHORT_TYPE)
            {
                return ASTHelper.SHORT_TYPE;
            }

            if (leftType == ASTHelper.BYTE_TYPE || rightType == ASTHelper.BYTE_TYPE)
            {
                return ASTHelper.BYTE_TYPE;
            }

            if (leftType == ASTHelper.CHAR_TYPE || rightType == ASTHelper.CHAR_TYPE)
            {
                return ASTHelper.CHAR_TYPE;
            }

            if (leftType == ASTHelper.BOOLEAN_TYPE || rightType == ASTHelper.BOOLEAN_TYPE)
            {
                return ASTHelper.BOOLEAN_TYPE;
            }
        }

        if (leftType == ASTHelper.VOID_TYPE || rightType == ASTHelper.VOID_TYPE)
        {
            return ASTHelper.VOID_TYPE;
        }

        return leftType;
    }

    public static void main(String[] args)
    {
//        double a = 2, b = 2.4;
//        int c = ~2;
//        boolean d = !true;
//
//        BinaryExpr.Operator op = BinaryExpr.Operator.plus;
//        BinaryExpr left = new BinaryExpr(new IntegerLiteralExpr("3"), new IntegerLiteralExpr("10"), BinaryExpr.Operator.times);
//        BinaryExpr right = new BinaryExpr(new DoubleLiteralExpr("2.0"), new IntegerLiteralExpr("2"), BinaryExpr.Operator.divide);
//        BinaryExpr binaryExpr = new BinaryExpr(left, right, op);
//
//        System.out.println(binaryExpr);
//        System.out.println(getExpressionType(left));
//        System.out.println(getExpressionType(right));
//        System.out.println(getExpressionType(binaryExpr));
//        int a = 2;
//        int b = - -(a++);

//        String srcFolder = "/Users/sutd/Dropbox/MutantBug/src/";
        String srcFolder = "C:\\Users\\1001385\\Dropbox\\MutantBug\\src\\";
        loadClassDescriptor(new File(srcFolder));
        File javaFile = new File(srcFolder + "test/TestForStmt.java");
        mutateFile(javaFile, new File("C:\\Users\\1001385\\Dropbox\\MutantBug\\mutatedSource"));
    }
}
