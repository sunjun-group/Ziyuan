package sav.commons.testdata.assertion;

import org.junit.Test;
import sav.commons.testdata.assertion.PrimitiveAssertion;

public class PrimitiveAssertionTest {

	@Test
    public void test1() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 1;
        int i1 = 2;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test2() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -1;
        int i1 = -2;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test3() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 0;
        int i1 = 0;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test4() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 2;
        int i1 = 1;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test5() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -2;
        int i1 = -1;
        double d0 = primitiveassertion0.foo(i0, i1);
    }
	
	/*
    @Test
    public void test1() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -7;
        int i1 = 20;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test2() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -7;
        int i1 = 20;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test3() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 20;
        int i1 = -7;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test4() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -71;
        int i1 = 48;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test5() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -19;
        int i1 = -19;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test6() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 20;
        int i1 = 43;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test7() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 41;
        int i1 = 30;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test8() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 48;
        int i1 = 95;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test9() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 41;
        int i1 = -71;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test10() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 30;
        int i1 = 48;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test11() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 95;
        int i1 = 41;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test12() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -19;
        int i1 = 30;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test13() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 41;
        int i1 = -13;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test14() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -13;
        int i1 = -66;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test15() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -19;
        int i1 = -71;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test16() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -46;
        int i1 = 41;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test17() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 40;
        int i1 = 40;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test18() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -46;
        int i1 = -54;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test19() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 20;
        int i1 = 89;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test20() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 5;
        int i1 = 6;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test21() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 30;
        int i1 = -73;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test22() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -77;
        int i1 = 89;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test23() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 89;
        int i1 = -46;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test24() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 41;
        int i1 = -46;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test25() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -89;
        int i1 = 11;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test26() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -54;
        int i1 = 57;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test27() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 43;
        int i1 = -73;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test28() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 43;
        int i1 = -45;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test29() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -71;
        int i1 = 7;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test30() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 9;
        int i1 = 30;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test31() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -73;
        int i1 = -7;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test32() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 30;
        int i1 = -19;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test33() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 7;
        int i1 = 96;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test34() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 96;
        int i1 = 20;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test35() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -19;
        int i1 = -77;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test36() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 96;
        int i1 = -7;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test37() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 48;
        int i1 = 20;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test38() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 41;
        int i1 = 5;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test39() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -19;
        int i1 = 11;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test40() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 40;
        int i1 = 5;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test41() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 9;
        int i1 = 11;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test42() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -89;
        int i1 = -7;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test43() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 11;
        int i1 = -77;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test44() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 6;
        int i1 = -54;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test45() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 30;
        int i1 = 9;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test46() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 9;
        int i1 = 43;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test47() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -73;
        int i1 = -45;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test48() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -98;
        int i1 = 20;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test49() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = 6;
        int i1 = 83;
        double d0 = primitiveassertion0.foo(i0, i1);
    }

    @Test
    public void test50() throws Throwable {
        PrimitiveAssertion primitiveassertion0 = new PrimitiveAssertion();
        int i0 = -13;
        int i1 = 34;
        double d0 = primitiveassertion0.foo(i0, i1);
    }
    */
}

