package mutanbug.parser;

import japa.parser.ASTHelper;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.util.*;

/**
 * Created by sutd on 1/2/15.
 */
public class Method implements Comparable<Method>
{
    public ClassDescriptor clazz;
    public String name;
    public Type type;
    public List<Variable> params;
    public Set<Variable> localVars;

    public Method(Type type, String name)
    {
        this.type = type;
        this.name = name;
        this.params = new ArrayList<Variable>();
        this.localVars = new TreeSet<Variable>();
    }

    public Method(Type type, String name, List<Variable> params, Set<Variable> localVars)
    {
        this.type = type;
        this.name = name;
        this.params = params;
        this.localVars = localVars;
    }

    public boolean addParam(Variable variable)
    {
        variable.function = this;
        return params.add(variable);
    }

    public boolean addLocalVar(Variable variable)
    {
        variable.function = this;
        return localVars.add(variable);
    }

    public List<Variable> getParams()
    {
        return params;
    }

    public Set<Variable> getLocalVars()
    {
        return localVars;
    }

    public Type getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getSignature()
    {
        String paramSigs = "";
        for (Variable param : params)
        {
            paramSigs += param.type;
        }

        return name + paramSigs;
    }

    @Override
    public int compareTo(Method o)
    {
        return getSignature().compareTo(o.getSignature());
//        int nameComp = name.compareTo(o.name);
//
//        if (nameComp != 0)
//        {
//            return nameComp;
//        }
//
//        if (params.size() != o.params.size())
//        {
//            return params.size() - o.params.size();
//        }
//
//        int pSize = params.size();
//        Iterator<Variable> it1 = params.iterator();
//        Iterator<Variable> it2 = o.params.iterator();
//        for (int i = 0; i < pSize; ++i)
//        {
//            Variable v1 = it1.next();
//            Variable v2 = it2.next();
//
//            if (v1.type != v2.type)
//            {
//                return v1.compareTo(v2);
//            }
//        }
//
//        return 0;
    }

    public boolean match(String name, List<Type> paramTypes)
    {
        if (name.equals(this.name) && paramTypes.size() == params.size())
        {
            Iterator<Variable> vIt = params.iterator();
            for (Type pType : paramTypes)
            {
                Variable v = vIt.next();
                System.out.println(pType + " " + v.type);
                if (!Util.isSubtypeOf(pType, v.type))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
//
//    public static void main(String[] args) {
//        Method method = new Method(ASTHelper.BOOLEAN_TYPE, "test");
//        Variable v1 = new Variable(ASTHelper.INT_TYPE, "a", null);
//        Variable v2 = new Variable(ASTHelper.INT_TYPE, "b", null);
//        Variable v3 = new Variable(new ClassOrInterfaceType("String"), "c", null);
//
//        method.addParam(v1);
//        method.addParam(v2);
//        method.addParam(v3);
//
//        List<Type> params = new ArrayList<Type>();
//        params.add(v1.type);
//        params.add(ASTHelper.SHORT_TYPE);
//        params.add(new ClassOrInterfaceType("String"));
//        System.out.println(method.match("test", params));
//    }
}
