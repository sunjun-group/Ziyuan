package parser;

import japa.parser.ast.type.Type;

/**
 * Created by sutd on 1/2/15.
 */
public class Variable implements Comparable<Variable>
{
    public ClassDescriptor clazz;
    public Method function;
    public Type type;
    public String name;
    public Object value;

    public Variable(Type type, String name, Object value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(Variable o)
    {
        return name.compareTo(o.name);
    }
}
