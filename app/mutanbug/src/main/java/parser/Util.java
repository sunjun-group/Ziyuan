package parser;

import japa.parser.ASTHelper;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

/**
 * Created by 1001385 on 1/25/2015.
 */
public class Util
{
    /**
     * check if type is a sub type of supperType
     * @param type
     * @param supperType
     * @return true if type is sub type of superType
     */
    public static boolean isSubtypeOf(Type type, Type supperType)
    {
        if (type == null || supperType == null)
        {
            return false;
        }

        if (type.equals(supperType))
        {
            return true;
        }

        if (type instanceof PrimitiveType && supperType instanceof PrimitiveType)
        {
            if (type.equals(ASTHelper.BYTE_TYPE))
            {
                return isSubtypeOf(ASTHelper.SHORT_TYPE, supperType);
            }

            if (type.equals(ASTHelper.CHAR_TYPE))
            {
                return isSubtypeOf(ASTHelper.INT_TYPE, supperType);
            }

            if (type.equals(ASTHelper.SHORT_TYPE))
            {
                return isSubtypeOf(ASTHelper.INT_TYPE, supperType);
            }

            if (type.equals(ASTHelper.INT_TYPE))
            {
                return isSubtypeOf(ASTHelper.LONG_TYPE, supperType) || isSubtypeOf(ASTHelper.FLOAT_TYPE, supperType);
            }

            if (type.equals(ASTHelper.LONG_TYPE))
            {
                return isSubtypeOf(ASTHelper.FLOAT_TYPE, supperType);
            }

            if (type.equals(ASTHelper.FLOAT_TYPE))
            {
                return isSubtypeOf(ASTHelper.DOUBLE_TYPE, supperType);
            }
        }

        return false;
    }
//
//    static void test(float t, boolean cont)
//    {
//        System.out.println(t);
//        if (cont)
//        test(2323423235345234l, false);
//    }
//
//    public static void main(String[] args) {
//        System.out.println(isSubtypeOf(ASTHelper.SHORT_TYPE, ASTHelper.INT_TYPE));
//        test(3242, true);
//    }
}
