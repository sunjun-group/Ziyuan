package parser;

import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.type.Type;

import java.util.*;

/**
 * Created by sutd on 1/2/15.
 */
public class ClassDescriptor implements Comparable<ClassDescriptor>
{
    private String packageName;
    private String className;
    private String quantifiedName;
    private Map<String, Variable> fields;
    private Map<String, Method> functions;

    public ClassDescriptor(PackageDeclaration packageName, String className)
    {
        if (packageName != null)
        {
            this.packageName = packageName.getName().getName();
        }
        else
        {
            this.packageName = "";
        }
        this.className = className;
        this.quantifiedName = this.packageName + "." + this.className;
        this.fields = new TreeMap<String, Variable>();
        this.functions = new TreeMap<String, Method>();
    }

    public Variable addField(Variable field)
    {
        field.clazz = this;
        return fields.put(field.name, field);
    }

    public void addFields(List<Variable> fieldList)
    {
        for (Variable var : fieldList)
        {
            var.clazz = this;
            fields.put(var.name, var);
        }
    }

    public Method addFunction(Method function)
    {
        function.clazz = this;
        return functions.put(function.getSignature(), function);
    }

    public void addFunctions(List<Method> functionsList)
    {
        for (Method func : functionsList)
        {
            func.clazz = this;
            functions.put(func.getSignature(), func);
        }
    }

    public Method getMethodFromSig(String name, List<Type> paramTypes)
    {
        Collection<Method> methods = functions.values();
        for (Method method : methods)
        {
            if (method.match(name, paramTypes))
            {
                return method;
            }
        }

        return null;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getClassName()
    {
        return className;
    }

    public String getQuantifiedName()
    {
        return quantifiedName;
    }

    public String toString()
    {
        return this.quantifiedName;
    }

    @Override
    public int compareTo(ClassDescriptor o)
    {
        return this.quantifiedName.compareTo(o.quantifiedName);
    }

    public Variable getField(String field)
    {
        return fields.get(field);
    }

    public List<Method> getMethodsWithType(Type type)
    {
        List<Method> methodWT = new ArrayList<Method>();
        Collection<Method> methods = functions.values();
        for (Method method : methods)
        {
            if (method.type.equals(type))
            {
                methodWT.add(method);
            }
        }

        return methodWT;
    }

    public List<Variable> getFieldWithType(Type type)
    {
        List<Variable> fWT = new ArrayList<Variable>();
        Collection<Variable> vars = fields.values();
        for (Variable v : vars)
        {
            if (v.type.equals(type))
            {
                fWT.add(v);
            }
        }

        return fWT;
    }
}
