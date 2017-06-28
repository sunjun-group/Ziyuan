package testdata.l2t.test.init.dfp.align;

import testdata.l2t.test.init.dfp.align.Dfp11;
import java.lang.reflect.Method;

public class DfpMain {

    private static void invokeMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName);
            method.invoke(obj);
        } catch (Throwable e) {
            return;
        }
    }

    public static void main(String[] args) {
        Dfp11 obj0 = new Dfp11();
        invokeMethod(obj0, "test1");
    }
}

