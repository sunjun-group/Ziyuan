package mutation.parser;

import japa.parser.ast.expr.NameExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by hoangtung on 3/31/15.
 */
public class MethodDescriptor
{
    private ClassDescriptor classDescriptor;
    private int modifier;
    private String name;
    private Object returnType;
    private List<VariableDescriptor> parameters;
    private List<Tuple<Integer, Integer, Map<String, VariableDescriptor>>> localVars;

    public int getOpenScopeIdx() {
        return openScopeIdx;
    }

    public void setOpenScopeIdx(int openScopeIdx) {
        this.openScopeIdx = openScopeIdx;
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    public void setClassDescriptor(ClassDescriptor classDescriptor) {
        this.classDescriptor = classDescriptor;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getReturnType() {
        return returnType;
    }

    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }

    public List<VariableDescriptor> getParameters() {
        return parameters;
    }

    public void setParameters(List<VariableDescriptor> parameters) {
        this.parameters = parameters;
    }

    public List<Tuple<Integer, Integer, Map<String, VariableDescriptor>>> getLocalVars() {
        return localVars;
    }

    public void setLocalVars(List<Tuple<Integer, Integer, Map<String, VariableDescriptor>>> localVars) {
        this.localVars = localVars;
    }

    private int openScopeIdx;

    public MethodDescriptor()
    {
        parameters = new ArrayList<>();
        localVars = new ArrayList<>();
    }

    public void openScope(int beginLine)
    {
        localVars.add(new Tuple<Integer, Integer, Map<String, VariableDescriptor>>(beginLine, -1, new HashMap<String, VariableDescriptor>()));
        openScopeIdx = localVars.size() - 1;
    }

    public void closeScope(int endLine)
    {
//        stupid way of closing scope
        localVars.get(openScopeIdx).second = endLine;
        ListIterator<Tuple<Integer, Integer, Map<String, VariableDescriptor>>> it = localVars.listIterator(openScopeIdx);

        while (it.hasPrevious())
        {
            Tuple<Integer, Integer, Map<String, VariableDescriptor>> scope = it.previous();
            --openScopeIdx;
            if (scope.second < 0)
            {
                break;
            }
        }
    }

    public VariableDescriptor getVarFromName(String name, int beginLine, int endLine)
    {
        throw new NotImplementedException();
//        return null;
    }

    public VariableDescriptor getVarFromName(NameExpr name)
    {
        throw new NotImplementedException();
    }

    public void addLocalVar(VariableDescriptor variableDescriptor)
    {
        localVars.get(openScopeIdx).third.put(variableDescriptor.getName(), variableDescriptor);
    }

    public void addLocalVars(List<VariableDescriptor> vList)
    {
        for (VariableDescriptor var : vList)
            localVars.get(openScopeIdx).third.put(var.getName(), var);
    }

    public void addParamter(VariableDescriptor vp) {
        this.parameters.add(vp);
    }
}
