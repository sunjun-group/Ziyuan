package mutanbug.mutator;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.UnaryExpr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sav.common.core.utils.CollectionUtils;

/**
 * Created by sutd on 1/4/15.
 */
public class MutationMap
{
	private static final String OPERATOR_MAP_FILE = "/OperatorMap.txt";
    public static final String UNARY_OPERATOR = "UNARY_OPERATOR";
    public static final String IDENTIFIER = "IDENTIFIER";

    private static Map<Object, List<?>> operatorMap;

    static
    {
        loadOperatorMap();
    }

    private static void loadOperatorMap()
    {
        operatorMap = new TreeMap<Object, List<?>>();

        try
        {
//            BufferedReader br = new BufferedReader(new FileReader("OperatorMap.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(
					MutationMap.class.getResourceAsStream(OPERATOR_MAP_FILE)));
            String s = null;

            while ((s = br.readLine()) != null)
            {
                String[] parts = s.split(":");
                if (parts.length != 2)
                {
                    continue;
                }

                parts[0] = parts[0].trim();
                parts[1] = parts[1].trim();
                String[] opStrings = parts[1].split(" ");

                if (parts[0].equals(IDENTIFIER))
                {
                    List<UnaryExpr.Operator> opList = new ArrayList<UnaryExpr.Operator>();

                    for (String op : opStrings)
                    {
                        UnaryExpr.Operator uOp = getUnaryOperator(op);
                        CollectionUtils.addIfNotNull(opList, uOp);
                    }

                    operatorMap.put(IDENTIFIER, opList);
                }
                else if (getBinaryOperator(parts[0]) != null)
                {
                    BinaryExpr.Operator bOp = getBinaryOperator(parts[0]);
                    List<BinaryExpr.Operator> opList = new ArrayList<BinaryExpr.Operator>();

                    for (String op : opStrings)
                    {
                        BinaryExpr.Operator binaryOperator = getBinaryOperator(op);
                        CollectionUtils.addIfNotNull(opList, binaryOperator);
                    }

                    operatorMap.put(bOp.getClass() + bOp.toString(), opList);
                }
                else if (getUnaryOperator(parts[0]) != null)
                {
                    UnaryExpr.Operator uOp = getUnaryOperator(parts[0]);

                    List<UnaryExpr.Operator> opList = new ArrayList<UnaryExpr.Operator>();

                    for (String op : opStrings)
                    {
                        UnaryExpr.Operator unaryOperator = getUnaryOperator(op);
                        CollectionUtils.addIfNotNull(opList, unaryOperator);
                    }

                    operatorMap.put(uOp.getClass() + uOp.toString(), opList);
                }
                else if (getAssignOperator(parts[0]) != null)
                {
                    AssignExpr.Operator aOp = getAssignOperator(parts[0]);

                    List<AssignExpr.Operator> opList = new ArrayList<AssignExpr.Operator>();

                    for (String op : opStrings)
                    {
                        AssignExpr.Operator assignOp = getAssignOperator(op);
                        CollectionUtils.addIfNotNull(opList, assignOp);
                    }

                    operatorMap.put(aOp.getClass() + aOp.toString(), opList);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static List getMutationOp(String originalOp)
    {
        return operatorMap.get(originalOp);
    }

    public static Object getOperator(String opString)
    {
        if (opString.compareToIgnoreCase("true") == 0)
        {
            return new Boolean(true);
        }

        if (opString.compareToIgnoreCase("false") == 0)
        {
            return new Boolean(false);
        }

        BinaryExpr.Operator bOp = getBinaryOperator(opString);
        if (bOp != null)
        {
            return bOp;
        }

        UnaryExpr.Operator uOp = getUnaryOperator(opString);
        if (uOp != null)
        {
            return uOp;
        }

        AssignExpr.Operator aOp = getAssignOperator(opString);
        if (aOp != null)
        {
            return aOp;
        }

        return null;
    }

    public static BinaryExpr.Operator getBinaryOperator(String opString)
    {
        opString = opString.trim();
        
        if (opString.equals("||"))
        {
            return BinaryExpr.Operator.or;
        }
        else if (opString.equals("&&"))
        {
            return BinaryExpr.Operator.and;
        }
        else if (opString.equals("|"))
        {
            return BinaryExpr.Operator.binOr;
        }
        else if (opString.equals("&"))
        {
            return BinaryExpr.Operator.binAnd;
        }
        else if (opString.equals("^"))
        {
            return BinaryExpr.Operator.xor;
        }
        else if (opString.equals("=="))
        {
            return BinaryExpr.Operator.equals;                    
        }
        else if (opString.equals("!="))
        {
            return BinaryExpr.Operator.notEquals;
        }
        else if (opString.equals("<"))
        {
            return BinaryExpr.Operator.less;
        }
        else if (opString.equals(">"))
        {
            return BinaryExpr.Operator.greater;
        }
        else if (opString.equals("<="))
        {
            return BinaryExpr.Operator.lessEquals;
        }
        else if (opString.equals(">="))
        {
            return BinaryExpr.Operator.greaterEquals;
        }
        else if (opString.equals("<<"))
        {
            return BinaryExpr.Operator.lShift;
        }
        else if (opString.equals(">>"))
        {
            return BinaryExpr.Operator.rSignedShift;
        }
        else if (opString.equals(">>>"))
        {
            return BinaryExpr.Operator.rUnsignedShift;
        }
        else if (opString.equals("+"))
        {
            return BinaryExpr.Operator.plus;
        }
        else if (opString.equals("-"))
        {
            return BinaryExpr.Operator.minus;
        }
        else if (opString.equals("*"))
        {
            return BinaryExpr.Operator.times;
        }
        else if (opString.equals("/"))
        {
            return BinaryExpr.Operator.divide;
        }
        else if (opString.equals("%"))
        {
            return BinaryExpr.Operator.remainder;
        }
        
        return null;
    }

    public static AssignExpr.Operator getAssignOperator(String opString)
    {
        opString = opString.trim();
        if (opString.equals("="))
        {
            return AssignExpr.Operator.assign;
        }
        else if (opString.equals("+="))
        {
            return AssignExpr.Operator.plus;
        }
        else if (opString.equals("-="))
        {
            return AssignExpr.Operator.minus;
        }
        else if (opString.equals("*="))
        {
            return AssignExpr.Operator.star;
        }
        else if (opString.equals("/="))
        {
            return AssignExpr.Operator.slash;
        }
        else if (opString.equals("&="))
        {
            return AssignExpr.Operator.and;                    
        }
        else if (opString.equals("|="))
        {
            return AssignExpr.Operator.or;
        }
        else if (opString.equals("^="))
        {
            return AssignExpr.Operator.xor;
        }
        else if (opString.equals("%="))
        {
            return AssignExpr.Operator.rem;
        }
        else if (opString.equals("<<="))
        {
            return AssignExpr.Operator.lShift;
        }
        else if (opString.equals(">>="))
        {
            return AssignExpr.Operator.rSignedShift;
        }
        else if (opString.equals(">>>="))
        {
            return AssignExpr.Operator.rUnsignedShift;
        }
        
        return null;
    }

    public static UnaryExpr.Operator getUnaryOperator(String opString)
    {
        opString = opString.trim();

        if (opString.equals("+x"))
        {
            return UnaryExpr.Operator.positive;
        }
        else if (opString.equals("-x"))
        {
            return UnaryExpr.Operator.negative;
        }
        else if (opString.startsWith("++x"))
        {
            return UnaryExpr.Operator.preIncrement;
        }
        else if (opString.startsWith("--x"))
        {
            return UnaryExpr.Operator.preDecrement;
        }
        else if (opString.equals("!x"))
        {
            return UnaryExpr.Operator.not;
        }
        else if (opString.equals("~x"))
        {
            return UnaryExpr.Operator.inverse;
        }
        else if (opString.endsWith("x++"))
        {
            return UnaryExpr.Operator.posIncrement;
        }
        else if (opString.endsWith("x--"))
        {
            return UnaryExpr.Operator.posDecrement;
        }

        return null;
    }
}
