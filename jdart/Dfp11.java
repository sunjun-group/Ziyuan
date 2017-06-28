package testdata.l2t.test.init.dfp.align;

import org.apache.commons.math.dfp.DfpField;
import org.apache.commons.math.dfp.Dfp;
import org.junit.Test;

public class Dfp11 {

    @Test
    public void test1() throws Throwable {
        int i0 = -137;
        DfpField dfpfield0 = new DfpField(i0);
        int i1 = -9;
        dfpfield0.setIEEEFlags(i1);
        dfpfield0.newDfp();
        double d0 = -97.45465487706765;
        Dfp dfp0 = new Dfp(dfpfield0, d0);
        int i2 = dfp0.align(i1);
    }
}

