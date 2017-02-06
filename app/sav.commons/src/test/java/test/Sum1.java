package test;

import sav.commons.testdata.calculator.Sum;
import org.junit.Test;
import org.junit.Assert;

public class Sum1 {

    @Test
    public void test1() throws Throwable {
        int i0 = -78;
        Sum sum0 = new Sum(i0);
        int i1 = -8;
        int i2 = -45;
        int i3 = 36;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -14;
        int i5 = -65;
        int i6 = 46;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -15;
        int i8 = -68;
        int i9 = 82;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -48;
        int i11 = -8;
        int i12 = 7;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -46;
        int i14 = -86;
        int i15 = 73;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -91;
        int i17 = 65;
        int i18 = 80;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 38;
        int i20 = sum0.getSum(i13, i19);
        Assert.assertTrue(Sum.validateGetSum(i13, i19, i20));
    }

    @Test
    public void test2() throws Throwable {
        int i0 = -75;
        Sum sum0 = new Sum(i0);
        int i1 = -67;
        int i2 = 88;
        int i3 = 74;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 29;
        int i5 = 39;
        int i6 = 92;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 3;
        int i8 = 76;
        int i9 = -97;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -71;
        int i11 = 60;
        int i12 = -84;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i0, i12);
        Assert.assertTrue(Sum.validateGetSum(i0, i12, i13));
    }

    @Test
    public void test3() throws Throwable {
        int i0 = 47;
        Sum sum0 = new Sum(i0);
        int i1 = 54;
        int i2 = sum0.getSum(i1, i0);
        Assert.assertTrue(Sum.validateGetSum(i1, i0, i2));
    }

    @Test
    public void test4() throws Throwable {
        int i0 = 20;
        Sum sum0 = new Sum(i0);
        int i1 = 5;
        int i2 = 75;
        int i3 = -89;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = sum0.getSum(i1, i2);
        Assert.assertTrue(Sum.validateGetSum(i1, i2, i4));
    }

    @Test
    public void test5() throws Throwable {
        int i0 = -12;
        Sum sum0 = new Sum(i0);
        int i1 = -11;
        int i2 = 4;
        int i3 = -3;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -78;
        int i5 = 69;
        int i6 = -24;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -49;
        int i8 = sum0.getSum(i7, i4);
        Assert.assertTrue(Sum.validateGetSum(i7, i4, i8));
    }

    @Test
    public void test6() throws Throwable {
        int i0 = 7;
        Sum sum0 = new Sum(i0);
        int i1 = 58;
        int i2 = -89;
        int i3 = 15;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 98;
        int i5 = 98;
        int i6 = -80;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 88;
        int i8 = 26;
        int i9 = 81;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -33;
        int i11 = -37;
        int i12 = 45;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 73;
        int i14 = 46;
        int i15 = 39;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -69;
        int i17 = -69;
        int i18 = 76;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 81;
        int i20 = 15;
        int i21 = -45;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -12;
        int i23 = -65;
        int i24 = sum0.getSum(i22, i23);
        Assert.assertTrue(Sum.validateGetSum(i22, i23, i24));
    }

    @Test
    public void test7() throws Throwable {
        int i0 = -41;
        Sum sum0 = new Sum(i0);
        int i1 = 80;
        int i2 = 37;
        int i3 = 34;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -19;
        int i5 = -39;
        int i6 = -89;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -99;
        int i8 = -22;
        int i9 = -33;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 74;
        int i11 = -75;
        int i12 = 32;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 29;
        int i14 = sum0.getSum(i13, i7);
        Assert.assertTrue(Sum.validateGetSum(i13, i7, i14));
    }

    @Test
    public void test8() throws Throwable {
        int i0 = 15;
        Sum sum0 = new Sum(i0);
        int i1 = -6;
        int i2 = 59;
        int i3 = 68;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -10;
        int i5 = sum0.getSum(i1, i4);
        Assert.assertTrue(Sum.validateGetSum(i1, i4, i5));
    }

    @Test
    public void test9() throws Throwable {
        int i0 = 18;
        Sum sum0 = new Sum(i0);
        int i1 = -92;
        int i2 = 81;
        int i3 = 2;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 55;
        int i5 = -8;
        int i6 = -78;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -84;
        int i8 = 12;
        int i9 = -88;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -98;
        int i11 = 34;
        int i12 = 30;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 36;
        int i14 = -29;
        int i15 = -27;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -59;
        int i17 = -21;
        int i18 = -52;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 62;
        int i20 = sum0.getSum(i19, i18);
        Assert.assertTrue(Sum.validateGetSum(i19, i18, i20));
    }

    @Test
    public void test10() throws Throwable {
        int i0 = -69;
        Sum sum0 = new Sum(i0);
        int i1 = 2;
        int i2 = 87;
        int i3 = 67;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -45;
        int i5 = -63;
        int i6 = -49;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -92;
        int i8 = 1;
        int i9 = -33;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -56;
        int i11 = 97;
        int i12 = -25;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i6, i3);
        Assert.assertTrue(Sum.validateGetSum(i6, i3, i13));
    }

    @Test
    public void test11() throws Throwable {
        int i0 = -23;
        Sum sum0 = new Sum(i0);
        int i1 = -71;
        int i2 = -18;
        int i3 = -47;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -47;
        int i5 = -71;
        int i6 = -82;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 10;
        int i8 = 10;
        int i9 = 65;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -58;
        int i11 = 23;
        int i12 = 48;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 12;
        int i14 = -45;
        int i15 = 29;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -88;
        int i17 = -45;
        int i18 = 48;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -20;
        int i20 = 50;
        int i21 = 3;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -71;
        int i23 = -43;
        int i24 = -99;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -50;
        int i26 = -40;
        int i27 = -68;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 68;
        int i29 = 33;
        int i30 = -91;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 87;
        int i32 = sum0.getSum(i31, i24);
        Assert.assertTrue(Sum.validateGetSum(i31, i24, i32));
    }

    @Test
    public void test12() throws Throwable {
        int i0 = 20;
        Sum sum0 = new Sum(i0);
        int i1 = -12;
        int i2 = 90;
        int i3 = -69;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -83;
        int i5 = 90;
        int i6 = -44;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -73;
        int i8 = -10;
        int i9 = -48;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 79;
        int i11 = 79;
        int i12 = 85;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 10;
        int i14 = sum0.getSum(i5, i13);
        Assert.assertTrue(Sum.validateGetSum(i5, i13, i14));
    }

    @Test
    public void test13() throws Throwable {
        int i0 = 5;
        Sum sum0 = new Sum(i0);
        int i1 = 57;
        int i2 = -86;
        int i3 = -54;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 74;
        int i5 = -63;
        int i6 = -99;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -43;
        int i8 = 74;
        int i9 = -71;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 82;
        int i11 = -5;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test14() throws Throwable {
        int i0 = -13;
        Sum sum0 = new Sum(i0);
        int i1 = 80;
        int i2 = sum0.getSum(i0, i1);
        Assert.assertTrue(Sum.validateGetSum(i0, i1, i2));
    }

    @Test
    public void test15() throws Throwable {
        int i0 = 25;
        Sum sum0 = new Sum(i0);
        int i1 = -98;
        int i2 = 71;
        int i3 = -12;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -79;
        int i5 = -82;
        int i6 = 63;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = sum0.getSum(i3, i6);
        Assert.assertTrue(Sum.validateGetSum(i3, i6, i7));
    }

    @Test
    public void test16() throws Throwable {
        int i0 = -34;
        Sum sum0 = new Sum(i0);
        int i1 = 7;
        int i2 = 94;
        int i3 = 11;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 94;
        int i5 = 60;
        int i6 = -79;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -67;
        int i8 = 8;
        int i9 = -17;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 5;
        int i11 = 3;
        int i12 = -76;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 59;
        int i14 = 75;
        int i15 = -43;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -45;
        int i17 = 13;
        int i18 = -75;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 3;
        int i20 = -41;
        int i21 = -50;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -80;
        int i23 = -80;
        int i24 = -68;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -82;
        int i26 = sum0.getSum(i1, i25);
        Assert.assertTrue(Sum.validateGetSum(i1, i25, i26));
    }

    @Test
    public void test17() throws Throwable {
        int i0 = -71;
        Sum sum0 = new Sum(i0);
        int i1 = -5;
        int i2 = 73;
        int i3 = 50;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -98;
        int i5 = -31;
        int i6 = -64;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 95;
        int i8 = 5;
        int i9 = 54;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 63;
        int i11 = sum0.getSum(i0, i10);
        Assert.assertTrue(Sum.validateGetSum(i0, i10, i11));
    }

    @Test
    public void test18() throws Throwable {
        int i0 = -6;
        Sum sum0 = new Sum(i0);
        int i1 = -62;
        int i2 = sum0.getSum(i1, i0);
        Assert.assertTrue(Sum.validateGetSum(i1, i0, i2));
    }

    @Test
    public void test19() throws Throwable {
        int i0 = -67;
        Sum sum0 = new Sum(i0);
        int i1 = -70;
        int i2 = -20;
        int i3 = -91;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -34;
        int i5 = 17;
        int i6 = 55;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 60;
        int i8 = 20;
        int i9 = 86;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -26;
        int i11 = 6;
        int i12 = 71;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i0, i1);
        Assert.assertTrue(Sum.validateGetSum(i0, i1, i13));
    }

    @Test
    public void test20() throws Throwable {
        int i0 = -96;
        Sum sum0 = new Sum(i0);
        int i1 = 6;
        int i2 = 62;
        int i3 = -93;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = sum0.getSum(i3, i3);
        Assert.assertTrue(Sum.validateGetSum(i3, i3, i4));
    }

    @Test
    public void test21() throws Throwable {
        int i0 = -38;
        Sum sum0 = new Sum(i0);
        int i1 = -21;
        int i2 = -94;
        int i3 = 4;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 53;
        int i5 = -48;
        int i6 = -94;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 2;
        int i8 = -56;
        int i9 = -24;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -95;
        int i11 = 91;
        int i12 = -84;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -39;
        int i14 = -99;
        int i15 = 58;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 14;
        int i17 = 43;
        int i18 = 51;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 24;
        int i20 = 33;
        int i21 = -100;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -85;
        int i23 = -26;
        int i24 = -35;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -3;
        int i26 = sum0.getSum(i7, i25);
        Assert.assertTrue(Sum.validateGetSum(i7, i25, i26));
    }

    @Test
    public void test22() throws Throwable {
        int i0 = -10;
        Sum sum0 = new Sum(i0);
        int i1 = 89;
        int i2 = 13;
        int i3 = -68;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 30;
        int i5 = 38;
        int i6 = -99;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 54;
        int i8 = 1;
        int i9 = -10;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -31;
        int i11 = 65;
        int i12 = 44;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -90;
        int i14 = -59;
        int i15 = -33;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -75;
        int i17 = 74;
        int i18 = -85;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -71;
        int i20 = 99;
        int i21 = -53;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -94;
        int i23 = -17;
        int i24 = 88;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -65;
        int i26 = sum0.getSum(i25, i10);
        Assert.assertTrue(Sum.validateGetSum(i25, i10, i26));
    }

    @Test
    public void test23() throws Throwable {
        int i0 = -51;
        Sum sum0 = new Sum(i0);
        int i1 = -85;
        int i2 = -3;
        int i3 = -73;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -91;
        int i5 = 29;
        int i6 = 5;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -83;
        int i8 = -95;
        int i9 = -99;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 69;
        int i11 = -29;
        int i12 = 27;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 47;
        int i14 = 98;
        int i15 = -68;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -7;
        int i17 = -64;
        int i18 = 1;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 70;
        int i20 = 38;
        int i21 = sum0.getSum(i19, i20);
        Assert.assertTrue(Sum.validateGetSum(i19, i20, i21));
    }

    @Test
    public void test24() throws Throwable {
        int i0 = 30;
        Sum sum0 = new Sum(i0);
        int i1 = -17;
        int i2 = -34;
        int i3 = -78;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 23;
        int i5 = sum0.getSum(i4, i0);
        Assert.assertTrue(Sum.validateGetSum(i4, i0, i5));
    }

    @Test
    public void test25() throws Throwable {
        int i0 = -18;
        Sum sum0 = new Sum(i0);
        int i1 = -13;
        int i2 = 99;
        int i3 = -94;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -57;
        int i5 = -37;
        int i6 = -14;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 44;
        int i8 = -16;
        int i9 = 35;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 68;
        int i11 = 19;
        int i12 = -60;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 45;
        int i14 = 65;
        int i15 = -88;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 88;
        int i17 = -5;
        int i18 = -81;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 47;
        int i20 = 79;
        int i21 = 56;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -99;
        int i23 = 69;
        int i24 = 6;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 70;
        int i26 = -52;
        int i27 = -14;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 76;
        int i29 = 72;
        int i30 = -65;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = sum0.getSum(i24, i16);
        Assert.assertTrue(Sum.validateGetSum(i24, i16, i31));
    }

    @Test
    public void test26() throws Throwable {
        int i0 = 27;
        Sum sum0 = new Sum(i0);
        int i1 = 67;
        int i2 = -32;
        int i3 = 92;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -60;
        int i5 = 44;
        int i6 = -48;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -52;
        int i8 = -57;
        int i9 = -98;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 98;
        int i11 = 90;
        int i12 = -59;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 15;
        int i14 = -7;
        int i15 = 25;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = sum0.getSum(i13, i2);
        Assert.assertTrue(Sum.validateGetSum(i13, i2, i16));
    }

    @Test
    public void test27() throws Throwable {
        int i0 = 36;
        Sum sum0 = new Sum(i0);
        int i1 = 42;
        int i2 = -77;
        int i3 = -28;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -76;
        int i5 = -72;
        int i6 = -86;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -27;
        int i8 = 57;
        int i9 = 96;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 33;
        int i11 = 71;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test28() throws Throwable {
        int i0 = -49;
        Sum sum0 = new Sum(i0);
        int i1 = sum0.getSum(i0, i0);
        Assert.assertTrue(Sum.validateGetSum(i0, i0, i1));
    }

    @Test
    public void test29() throws Throwable {
        int i0 = -7;
        Sum sum0 = new Sum(i0);
        int i1 = 93;
        int i2 = -89;
        int i3 = 75;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -99;
        int i5 = 93;
        int i6 = 65;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -68;
        int i8 = -52;
        int i9 = -61;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -42;
        int i11 = -36;
        int i12 = -34;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 98;
        int i14 = 63;
        int i15 = -73;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -44;
        int i17 = -69;
        int i18 = 21;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -60;
        int i20 = -55;
        int i21 = 62;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = sum0.getSum(i2, i19);
        Assert.assertTrue(Sum.validateGetSum(i2, i19, i22));
    }

    @Test
    public void test30() throws Throwable {
        int i0 = 34;
        Sum sum0 = new Sum(i0);
        int i1 = -27;
        int i2 = 65;
        int i3 = -55;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 47;
        int i5 = sum0.getSum(i2, i4);
        Assert.assertTrue(Sum.validateGetSum(i2, i4, i5));
    }

    @Test
    public void test31() throws Throwable {
        int i0 = -49;
        Sum sum0 = new Sum(i0);
        int i1 = -21;
        int i2 = 95;
        int i3 = -43;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 52;
        int i5 = -89;
        int i6 = -70;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -67;
        int i8 = 42;
        int i9 = 51;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 62;
        int i11 = 64;
        int i12 = 19;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -59;
        int i14 = -88;
        int i15 = -22;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -40;
        int i17 = -83;
        int i18 = -39;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -48;
        int i20 = -40;
        int i21 = 31;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -53;
        int i23 = sum0.getSum(i22, i19);
        Assert.assertTrue(Sum.validateGetSum(i22, i19, i23));
    }

    @Test
    public void test32() throws Throwable {
        int i0 = -34;
        Sum sum0 = new Sum(i0);
        int i1 = -38;
        int i2 = -64;
        int i3 = 16;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -25;
        int i5 = sum0.getSum(i2, i4);
        Assert.assertTrue(Sum.validateGetSum(i2, i4, i5));
    }

    @Test
    public void test33() throws Throwable {
        int i0 = -59;
        Sum sum0 = new Sum(i0);
        int i1 = 35;
        int i2 = 17;
        int i3 = -69;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 10;
        int i5 = -30;
        int i6 = -27;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -64;
        int i8 = 11;
        int i9 = 59;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 39;
        int i11 = -74;
        int i12 = -67;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 57;
        int i14 = 11;
        int i15 = 3;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 44;
        int i17 = 9;
        int i18 = 98;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -62;
        int i20 = -29;
        int i21 = -82;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 71;
        int i23 = -4;
        int i24 = -100;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 34;
        int i26 = 86;
        int i27 = 30;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -23;
        int i29 = -19;
        int i30 = 47;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = sum0.getSum(i5, i3);
        Assert.assertTrue(Sum.validateGetSum(i5, i3, i31));
    }

    @Test
    public void test34() throws Throwable {
        int i0 = -72;
        Sum sum0 = new Sum(i0);
        int i1 = -80;
        int i2 = 89;
        int i3 = -16;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 70;
        int i5 = 59;
        int i6 = -64;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -78;
        int i8 = -28;
        int i9 = -24;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 67;
        int i11 = -100;
        int i12 = 100;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i12, i0);
        Assert.assertTrue(Sum.validateGetSum(i12, i0, i13));
    }

    @Test
    public void test35() throws Throwable {
        int i0 = -88;
        Sum sum0 = new Sum(i0);
        int i1 = 66;
        int i2 = sum0.getSum(i0, i1);
        Assert.assertTrue(Sum.validateGetSum(i0, i1, i2));
    }

    @Test
    public void test36() throws Throwable {
        int i0 = -27;
        Sum sum0 = new Sum(i0);
        int i1 = -34;
        int i2 = 77;
        int i3 = -40;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 19;
        int i5 = 35;
        int i6 = 37;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 56;
        int i8 = -88;
        int i9 = 60;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 60;
        int i11 = 4;
        int i12 = 78;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 5;
        int i14 = 92;
        int i15 = -11;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 32;
        int i17 = 91;
        int i18 = -79;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 40;
        int i20 = sum0.getSum(i17, i19);
        Assert.assertTrue(Sum.validateGetSum(i17, i19, i20));
    }

    @Test
    public void test37() throws Throwable {
        int i0 = -5;
        Sum sum0 = new Sum(i0);
        int i1 = 92;
        int i2 = -60;
        int i3 = 53;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -91;
        int i5 = sum0.getSum(i4, i1);
        Assert.assertTrue(Sum.validateGetSum(i4, i1, i5));
    }

    @Test
    public void test38() throws Throwable {
        int i0 = 39;
        Sum sum0 = new Sum(i0);
        int i1 = 45;
        int i2 = -2;
        int i3 = -10;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -11;
        int i5 = -84;
        int i6 = -83;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -8;
        int i8 = 30;
        int i9 = 1;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -89;
        int i11 = -72;
        int i12 = -27;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -38;
        int i14 = 24;
        int i15 = 2;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -16;
        int i17 = -79;
        int i18 = -76;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 75;
        int i20 = 15;
        int i21 = 36;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 6;
        int i23 = 53;
        int i24 = 76;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 93;
        int i26 = -90;
        int i27 = 75;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -27;
        int i29 = 10;
        int i30 = 41;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = -12;
        int i32 = sum0.getSum(i31, i19);
        Assert.assertTrue(Sum.validateGetSum(i31, i19, i32));
    }

    @Test
    public void test39() throws Throwable {
        int i0 = -80;
        Sum sum0 = new Sum(i0);
        int i1 = -11;
        int i2 = -48;
        int i3 = -22;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -35;
        int i5 = -36;
        int i6 = 27;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 35;
        int i8 = 56;
        int i9 = 3;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -17;
        int i11 = -20;
        int i12 = 98;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -29;
        int i14 = 86;
        int i15 = -47;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = sum0.getSum(i10, i15);
        Assert.assertTrue(Sum.validateGetSum(i10, i15, i16));
    }

    @Test
    public void test40() throws Throwable {
        int i0 = -15;
        Sum sum0 = new Sum(i0);
        int i1 = 56;
        int i2 = 10;
        int i3 = -64;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -15;
        int i5 = -48;
        int i6 = -47;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 79;
        int i8 = -49;
        int i9 = -78;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -32;
        int i11 = -15;
        int i12 = 58;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 51;
        int i14 = 53;
        int i15 = 39;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 73;
        int i17 = sum0.getSum(i9, i16);
        Assert.assertTrue(Sum.validateGetSum(i9, i16, i17));
    }

    @Test
    public void test41() throws Throwable {
        int i0 = 44;
        Sum sum0 = new Sum(i0);
        int i1 = -36;
        int i2 = 14;
        int i3 = 47;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 56;
        int i5 = 92;
        int i6 = 46;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 23;
        int i8 = 73;
        int i9 = 11;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = sum0.getSum(i4, i7);
        Assert.assertTrue(Sum.validateGetSum(i4, i7, i10));
    }

    @Test
    public void test42() throws Throwable {
        int i0 = 31;
        Sum sum0 = new Sum(i0);
        int i1 = 72;
        int i2 = -7;
        int i3 = -89;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 19;
        int i5 = -69;
        int i6 = -83;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -50;
        int i8 = -9;
        int i9 = 79;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 80;
        int i11 = -55;
        int i12 = -34;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i10, i0);
        Assert.assertTrue(Sum.validateGetSum(i10, i0, i13));
    }

    @Test
    public void test43() throws Throwable {
        int i0 = -6;
        Sum sum0 = new Sum(i0);
        int i1 = 63;
        int i2 = -53;
        int i3 = -65;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 75;
        int i5 = 39;
        int i6 = 49;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 36;
        int i8 = -95;
        int i9 = 99;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 72;
        int i11 = 59;
        int i12 = 8;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 25;
        int i14 = -67;
        int i15 = 80;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -38;
        int i17 = -44;
        int i18 = -52;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -22;
        int i20 = 33;
        int i21 = -67;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 31;
        int i23 = 29;
        int i24 = 95;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 100;
        int i26 = -2;
        int i27 = 11;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = sum0.getSum(i20, i6);
        Assert.assertTrue(Sum.validateGetSum(i20, i6, i28));
    }

    @Test
    public void test44() throws Throwable {
        int i0 = 25;
        Sum sum0 = new Sum(i0);
        int i1 = -98;
        int i2 = 11;
        int i3 = -81;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 84;
        int i5 = 87;
        int i6 = 78;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 35;
        int i8 = 97;
        int i9 = 99;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -92;
        int i11 = 91;
        int i12 = 52;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -82;
        int i14 = -91;
        int i15 = 47;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 18;
        int i17 = -24;
        int i18 = -79;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -34;
        int i20 = 45;
        int i21 = 59;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -76;
        int i23 = 14;
        int i24 = -99;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = sum0.getSum(i20, i21);
        Assert.assertTrue(Sum.validateGetSum(i20, i21, i25));
    }

    @Test
    public void test45() throws Throwable {
        int i0 = -18;
        Sum sum0 = new Sum(i0);
        int i1 = -54;
        int i2 = -99;
        int i3 = -77;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 72;
        int i5 = -11;
        int i6 = 0;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 3;
        int i8 = -15;
        int i9 = 21;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 84;
        int i11 = 98;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test46() throws Throwable {
        int i0 = -82;
        Sum sum0 = new Sum(i0);
        int i1 = -6;
        int i2 = 51;
        int i3 = -14;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -33;
        int i5 = -83;
        int i6 = 39;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 91;
        int i8 = 55;
        int i9 = 25;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 27;
        int i11 = 84;
        int i12 = -49;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 27;
        int i14 = -30;
        int i15 = 37;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 31;
        int i17 = 63;
        int i18 = -100;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 56;
        int i20 = -63;
        int i21 = 63;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 43;
        int i23 = 58;
        int i24 = -10;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -83;
        int i26 = -80;
        int i27 = 36;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 15;
        int i29 = -19;
        int i30 = -36;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = -47;
        int i32 = sum0.getSum(i28, i31);
        Assert.assertTrue(Sum.validateGetSum(i28, i31, i32));
    }

    @Test
    public void test47() throws Throwable {
        int i0 = -44;
        Sum sum0 = new Sum(i0);
        int i1 = -58;
        int i2 = sum0.getSum(i0, i1);
        Assert.assertTrue(Sum.validateGetSum(i0, i1, i2));
    }

    @Test
    public void test48() throws Throwable {
        int i0 = -54;
        Sum sum0 = new Sum(i0);
        int i1 = 60;
        int i2 = -69;
        int i3 = 95;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 85;
        int i5 = -61;
        int i6 = -12;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -55;
        int i8 = -69;
        int i9 = 67;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 88;
        int i11 = -91;
        int i12 = 93;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 31;
        int i14 = sum0.getSum(i13, i1);
        Assert.assertTrue(Sum.validateGetSum(i13, i1, i14));
    }

    @Test
    public void test49() throws Throwable {
        int i0 = -92;
        Sum sum0 = new Sum(i0);
        int i1 = -93;
        int i2 = 46;
        int i3 = -67;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -100;
        int i5 = -70;
        int i6 = 60;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 17;
        int i8 = -90;
        int i9 = -60;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 37;
        int i11 = -8;
        int i12 = -28;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -68;
        int i14 = 24;
        int i15 = 16;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 77;
        int i17 = -22;
        int i18 = 75;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -42;
        int i20 = -94;
        int i21 = -95;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = sum0.getSum(i0, i17);
        Assert.assertTrue(Sum.validateGetSum(i0, i17, i22));
    }

    @Test
    public void test50() throws Throwable {
        int i0 = -65;
        Sum sum0 = new Sum(i0);
        int i1 = 42;
        int i2 = 55;
        int i3 = -59;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -23;
        int i5 = -97;
        int i6 = -27;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -94;
        int i8 = -40;
        int i9 = 20;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 18;
        int i11 = -47;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test51() throws Throwable {
        int i0 = -59;
        Sum sum0 = new Sum(i0);
        int i1 = -10;
        int i2 = 61;
        int i3 = 1;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = sum0.getSum(i1, i0);
        Assert.assertTrue(Sum.validateGetSum(i1, i0, i4));
    }

    @Test
    public void test52() throws Throwable {
        int i0 = 5;
        Sum sum0 = new Sum(i0);
        int i1 = 77;
        int i2 = -54;
        int i3 = 23;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -30;
        int i5 = 57;
        int i6 = -99;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 80;
        int i8 = 57;
        int i9 = 40;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -34;
        int i11 = 50;
        int i12 = -61;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = sum0.getSum(i1, i10);
        Assert.assertTrue(Sum.validateGetSum(i1, i10, i13));
    }

    @Test
    public void test53() throws Throwable {
        int i0 = -47;
        Sum sum0 = new Sum(i0);
        int i1 = -32;
        int i2 = -34;
        int i3 = 75;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 76;
        int i5 = 5;
        int i6 = 47;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 12;
        int i8 = 62;
        int i9 = 87;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 12;
        int i11 = -57;
        int i12 = 67;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 39;
        int i14 = -67;
        int i15 = 56;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -8;
        int i17 = 90;
        int i18 = -24;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -18;
        int i20 = -26;
        int i21 = 76;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -31;
        int i23 = sum0.getSum(i15, i22);
        Assert.assertTrue(Sum.validateGetSum(i15, i22, i23));
    }

    @Test
    public void test54() throws Throwable {
        int i0 = -10;
        Sum sum0 = new Sum(i0);
        int i1 = -58;
        int i2 = 100;
        int i3 = -17;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -83;
        int i5 = 45;
        int i6 = 12;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -51;
        int i8 = 41;
        int i9 = -98;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 73;
        int i11 = 58;
        int i12 = -54;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -12;
        int i14 = 84;
        int i15 = 89;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -3;
        int i17 = -45;
        int i18 = -62;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -76;
        int i20 = -44;
        int i21 = -98;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -57;
        int i23 = sum0.getSum(i22, i17);
        Assert.assertTrue(Sum.validateGetSum(i22, i17, i23));
    }

    @Test
    public void test55() throws Throwable {
        int i0 = -66;
        Sum sum0 = new Sum(i0);
        int i1 = 66;
        int i2 = -38;
        int i3 = 67;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 42;
        int i5 = -98;
        int i6 = 11;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 65;
        int i8 = 0;
        int i9 = -63;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -72;
        int i11 = -17;
        int i12 = -43;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 50;
        int i14 = 29;
        int i15 = -40;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 21;
        int i17 = 50;
        int i18 = -3;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -96;
        int i20 = 17;
        int i21 = 37;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = sum0.getSum(i10, i15);
        Assert.assertTrue(Sum.validateGetSum(i10, i15, i22));
    }

    @Test
    public void test56() throws Throwable {
        int i0 = -63;
        Sum sum0 = new Sum(i0);
        int i1 = 39;
        int i2 = 94;
        int i3 = -72;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -16;
        int i5 = -79;
        int i6 = -66;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 15;
        int i8 = -94;
        int i9 = 34;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -23;
        int i11 = 60;
        int i12 = -90;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -4;
        int i14 = 4;
        int i15 = -90;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -65;
        int i17 = 7;
        int i18 = 36;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 14;
        int i20 = 19;
        int i21 = -72;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -3;
        int i23 = -63;
        int i24 = -31;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -21;
        int i26 = -99;
        int i27 = sum0.getSum(i25, i26);
        Assert.assertTrue(Sum.validateGetSum(i25, i26, i27));
    }

    @Test
    public void test57() throws Throwable {
        int i0 = 25;
        Sum sum0 = new Sum(i0);
        int i1 = -90;
        int i2 = -74;
        int i3 = 54;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -22;
        int i5 = -70;
        int i6 = -89;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -42;
        int i8 = -9;
        int i9 = -26;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 43;
        int i11 = 96;
        int i12 = 71;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -54;
        int i14 = -51;
        int i15 = -83;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = sum0.getSum(i1, i6);
        Assert.assertTrue(Sum.validateGetSum(i1, i6, i16));
    }

    @Test
    public void test58() throws Throwable {
        int i0 = 5;
        Sum sum0 = new Sum(i0);
        int i1 = 51;
        int i2 = -58;
        int i3 = -57;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 25;
        int i5 = -97;
        int i6 = 34;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 40;
        int i8 = 83;
        int i9 = -71;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 60;
        int i11 = 8;
        int i12 = 50;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 79;
        int i14 = 64;
        int i15 = 67;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -35;
        int i17 = -24;
        int i18 = -72;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 79;
        int i20 = -56;
        int i21 = -12;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 88;
        int i23 = sum0.getSum(i22, i3);
        Assert.assertTrue(Sum.validateGetSum(i22, i3, i23));
    }

    @Test
    public void test59() throws Throwable {
        int i0 = 43;
        Sum sum0 = new Sum(i0);
        int i1 = 18;
        int i2 = 27;
        int i3 = 21;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 100;
        int i5 = sum0.getSum(i3, i4);
        Assert.assertTrue(Sum.validateGetSum(i3, i4, i5));
    }

    @Test
    public void test60() throws Throwable {
        int i0 = 34;
        Sum sum0 = new Sum(i0);
        int i1 = -35;
        int i2 = -79;
        int i3 = 17;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 90;
        int i5 = 98;
        int i6 = 91;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -21;
        int i8 = -97;
        int i9 = 16;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 39;
        int i11 = -54;
        int i12 = -7;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -19;
        int i14 = -65;
        int i15 = 59;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 82;
        int i17 = -90;
        int i18 = 66;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -71;
        int i20 = 21;
        int i21 = -42;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 33;
        int i23 = -19;
        int i24 = 0;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 76;
        int i26 = 66;
        int i27 = -90;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -37;
        int i29 = -49;
        int i30 = 49;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = sum0.getSum(i8, i26);
        Assert.assertTrue(Sum.validateGetSum(i8, i26, i31));
    }

    @Test
    public void test61() throws Throwable {
        int i0 = -29;
        Sum sum0 = new Sum(i0);
        int i1 = -8;
        int i2 = -46;
        int i3 = 77;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -65;
        int i5 = 73;
        int i6 = -91;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 53;
        int i8 = -33;
        int i9 = 87;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = sum0.getSum(i2, i1);
        Assert.assertTrue(Sum.validateGetSum(i2, i1, i10));
    }

    @Test
    public void test62() throws Throwable {
        int i0 = -8;
        Sum sum0 = new Sum(i0);
        int i1 = 100;
        int i2 = 84;
        int i3 = -4;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 4;
        int i5 = 88;
        int i6 = -19;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 99;
        int i8 = 88;
        int i9 = 71;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 73;
        int i11 = -11;
        int i12 = 0;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 78;
        int i14 = -44;
        int i15 = 46;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -89;
        int i17 = -80;
        int i18 = 11;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 34;
        int i20 = 24;
        int i21 = -96;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 54;
        int i23 = sum0.getSum(i22, i5);
        Assert.assertTrue(Sum.validateGetSum(i22, i5, i23));
    }

    @Test
    public void test63() throws Throwable {
        int i0 = -71;
        Sum sum0 = new Sum(i0);
        int i1 = -13;
        int i2 = -3;
        int i3 = 55;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -16;
        int i5 = -47;
        int i6 = -34;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -74;
        int i8 = -59;
        int i9 = 92;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -50;
        int i11 = -82;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test64() throws Throwable {
        int i0 = 37;
        Sum sum0 = new Sum(i0);
        int i1 = 89;
        int i2 = 49;
        int i3 = 30;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -71;
        int i5 = -70;
        int i6 = 71;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 23;
        int i8 = 6;
        int i9 = 22;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 92;
        int i11 = -34;
        int i12 = 72;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 85;
        int i14 = 31;
        int i15 = -19;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 35;
        int i17 = -100;
        int i18 = 44;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 45;
        int i20 = -54;
        int i21 = -40;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = sum0.getSum(i18, i3);
        Assert.assertTrue(Sum.validateGetSum(i18, i3, i22));
    }

    @Test
    public void test65() throws Throwable {
        int i0 = -12;
        Sum sum0 = new Sum(i0);
        int i1 = -47;
        int i2 = 21;
        int i3 = -34;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -61;
        int i5 = sum0.getSum(i4, i0);
        Assert.assertTrue(Sum.validateGetSum(i4, i0, i5));
    }

    @Test
    public void test66() throws Throwable {
        int i0 = -67;
        Sum sum0 = new Sum(i0);
        int i1 = -79;
        int i2 = -56;
        int i3 = -30;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -72;
        int i5 = -85;
        int i6 = -60;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 61;
        int i8 = sum0.getSum(i2, i7);
        Assert.assertTrue(Sum.validateGetSum(i2, i7, i8));
    }

    @Test
    public void test67() throws Throwable {
        int i0 = -32;
        Sum sum0 = new Sum(i0);
        int i1 = -14;
        int i2 = 66;
        int i3 = -92;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 58;
        int i5 = 58;
        int i6 = -52;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 85;
        int i8 = -98;
        int i9 = -98;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 85;
        int i11 = -97;
        int i12 = 50;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 13;
        int i14 = 84;
        int i15 = 21;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -68;
        int i17 = 53;
        int i18 = 10;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 66;
        int i20 = -12;
        int i21 = -81;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -20;
        int i23 = -69;
        int i24 = 53;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -82;
        int i26 = sum0.getSum(i25, i2);
        Assert.assertTrue(Sum.validateGetSum(i25, i2, i26));
    }

    @Test
    public void test68() throws Throwable {
        int i0 = 38;
        Sum sum0 = new Sum(i0);
        int i1 = 78;
        int i2 = -10;
        int i3 = -83;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -20;
        int i5 = 93;
        int i6 = 20;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -79;
        int i8 = 59;
        int i9 = -28;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 63;
        int i11 = sum0.getSum(i5, i10);
        Assert.assertTrue(Sum.validateGetSum(i5, i10, i11));
    }

    @Test
    public void test69() throws Throwable {
        int i0 = -20;
        Sum sum0 = new Sum(i0);
        int i1 = -84;
        int i2 = 6;
        int i3 = -76;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 57;
        int i5 = -65;
        int i6 = -99;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -20;
        int i8 = -3;
        int i9 = -70;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 74;
        int i11 = 38;
        int i12 = -93;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -91;
        int i14 = -34;
        int i15 = -17;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 14;
        int i17 = -26;
        int i18 = 35;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 47;
        int i20 = -24;
        int i21 = -4;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -81;
        int i23 = -69;
        int i24 = -59;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -45;
        int i26 = -4;
        int i27 = 61;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = sum0.getSum(i23, i12);
        Assert.assertTrue(Sum.validateGetSum(i23, i12, i28));
    }

    @Test
    public void test70() throws Throwable {
        int i0 = -9;
        Sum sum0 = new Sum(i0);
        int i1 = 66;
        int i2 = 45;
        int i3 = 28;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -67;
        int i5 = 6;
        int i6 = -48;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -32;
        int i8 = 42;
        int i9 = sum0.getSum(i7, i8);
        Assert.assertTrue(Sum.validateGetSum(i7, i8, i9));
    }

    @Test
    public void test71() throws Throwable {
        int i0 = -80;
        Sum sum0 = new Sum(i0);
        int i1 = 10;
        int i2 = -60;
        int i3 = 90;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -82;
        int i5 = -11;
        int i6 = -29;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 21;
        int i8 = 94;
        int i9 = -85;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = sum0.getSum(i4, i3);
        Assert.assertTrue(Sum.validateGetSum(i4, i3, i10));
    }

    @Test
    public void test72() throws Throwable {
        int i0 = -11;
        Sum sum0 = new Sum(i0);
        int i1 = 35;
        int i2 = -3;
        int i3 = -7;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -28;
        int i5 = 72;
        int i6 = -91;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -95;
        int i8 = 1;
        int i9 = 21;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -80;
        int i11 = -67;
        int i12 = 42;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 17;
        int i14 = 94;
        int i15 = 42;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -43;
        int i17 = 48;
        int i18 = -89;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -10;
        int i20 = 50;
        int i21 = 20;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -49;
        int i23 = -5;
        int i24 = 16;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -84;
        int i26 = -74;
        int i27 = 30;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 66;
        int i29 = -58;
        int i30 = 25;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 100;
        int i32 = sum0.getSum(i31, i21);
        Assert.assertTrue(Sum.validateGetSum(i31, i21, i32));
    }

    @Test
    public void test73() throws Throwable {
        int i0 = 44;
        Sum sum0 = new Sum(i0);
        int i1 = 97;
        int i2 = 10;
        int i3 = 59;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -48;
        int i5 = 15;
        int i6 = 43;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 12;
        int i8 = -38;
        int i9 = 53;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -68;
        int i11 = 98;
        int i12 = -39;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 29;
        int i14 = -96;
        int i15 = 79;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 100;
        int i17 = -75;
        int i18 = 51;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -3;
        int i20 = 94;
        int i21 = -66;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 89;
        int i23 = 59;
        int i24 = 81;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 80;
        int i26 = 28;
        int i27 = 66;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -98;
        int i29 = -4;
        int i30 = -42;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 45;
        int i32 = sum0.getSum(i4, i31);
        Assert.assertTrue(Sum.validateGetSum(i4, i31, i32));
    }

    @Test
    public void test74() throws Throwable {
        int i0 = -32;
        Sum sum0 = new Sum(i0);
        int i1 = -3;
        int i2 = 14;
        int i3 = -18;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -61;
        int i5 = -94;
        int i6 = 88;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 34;
        int i8 = 10;
        int i9 = 13;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 30;
        int i11 = 77;
        int i12 = -72;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 40;
        int i14 = -55;
        int i15 = 0;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 7;
        int i17 = 50;
        int i18 = 85;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 60;
        int i20 = -78;
        int i21 = 33;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 1;
        int i23 = -46;
        int i24 = 5;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 37;
        int i26 = 53;
        int i27 = -51;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -62;
        int i29 = -45;
        int i30 = 60;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 10;
        int i32 = -82;
        int i33 = sum0.getSum(i31, i32);
        Assert.assertTrue(Sum.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test75() throws Throwable {
        int i0 = -23;
        Sum sum0 = new Sum(i0);
        int i1 = 85;
        int i2 = 29;
        int i3 = -20;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -68;
        int i5 = -19;
        int i6 = 96;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = sum0.getSum(i3, i3);
        Assert.assertTrue(Sum.validateGetSum(i3, i3, i7));
    }

    @Test
    public void test76() throws Throwable {
        int i0 = 99;
        Sum sum0 = new Sum(i0);
        int i1 = -16;
        int i2 = sum0.getSum(i0, i1);
        Assert.assertTrue(Sum.validateGetSum(i0, i1, i2));
    }

    @Test
    public void test77() throws Throwable {
        int i0 = 62;
        Sum sum0 = new Sum(i0);
        int i1 = -55;
        int i2 = 100;
        int i3 = 1;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -21;
        int i5 = -29;
        int i6 = 14;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 53;
        int i8 = 7;
        int i9 = -75;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 44;
        int i11 = -27;
        int i12 = -16;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -77;
        int i14 = 65;
        int i15 = -80;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -19;
        int i17 = -13;
        int i18 = -26;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 100;
        int i20 = 50;
        int i21 = -2;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -55;
        int i23 = -38;
        int i24 = -12;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 42;
        int i26 = 88;
        int i27 = -63;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -53;
        int i29 = 43;
        int i30 = -88;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = -49;
        int i32 = -86;
        int i33 = sum0.getSum(i31, i32);
        Assert.assertTrue(Sum.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test78() throws Throwable {
        int i0 = 80;
        Sum sum0 = new Sum(i0);
        int i1 = 78;
        int i2 = -40;
        int i3 = -33;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -60;
        int i5 = 76;
        int i6 = 93;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 7;
        int i8 = -12;
        int i9 = -46;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 69;
        int i11 = -54;
        int i12 = 34;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 80;
        int i14 = -58;
        int i15 = 61;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 26;
        int i17 = 67;
        int i18 = -37;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -25;
        int i20 = -32;
        int i21 = -27;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = sum0.getSum(i21, i20);
        Assert.assertTrue(Sum.validateGetSum(i21, i20, i22));
    }

    @Test
    public void test79() throws Throwable {
        int i0 = 54;
        Sum sum0 = new Sum(i0);
        int i1 = -3;
        int i2 = -8;
        int i3 = 10;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 52;
        int i5 = -47;
        int i6 = -15;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 84;
        int i8 = -74;
        int i9 = -11;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 91;
        int i11 = 32;
        int i12 = -24;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 40;
        int i14 = -85;
        int i15 = 15;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -51;
        int i17 = 28;
        int i18 = 95;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -76;
        int i20 = 50;
        int i21 = 97;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 69;
        int i23 = 32;
        int i24 = 54;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -48;
        int i26 = -22;
        int i27 = 83;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -59;
        int i29 = -37;
        int i30 = -86;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = sum0.getSum(i24, i26);
        Assert.assertTrue(Sum.validateGetSum(i24, i26, i31));
    }

    @Test
    public void test80() throws Throwable {
        int i0 = 78;
        Sum sum0 = new Sum(i0);
        int i1 = 4;
        int i2 = -46;
        int i3 = -56;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -56;
        int i5 = -1;
        int i6 = -98;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 18;
        int i8 = -67;
        int i9 = -60;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -97;
        int i11 = 65;
        int i12 = 8;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -76;
        int i14 = -72;
        int i15 = 46;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -19;
        int i17 = -4;
        int i18 = -5;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -40;
        int i20 = -22;
        int i21 = 85;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -78;
        int i23 = 41;
        int i24 = 28;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -47;
        int i26 = -28;
        int i27 = -4;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 51;
        int i29 = 21;
        int i30 = 23;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = -28;
        int i32 = sum0.getSum(i8, i31);
        Assert.assertTrue(Sum.validateGetSum(i8, i31, i32));
    }

    @Test
    public void test81() throws Throwable {
        int i0 = 62;
        Sum sum0 = new Sum(i0);
        int i1 = -98;
        int i2 = -22;
        int i3 = 100;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -71;
        int i5 = -36;
        int i6 = 33;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -18;
        int i8 = -7;
        int i9 = 9;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 50;
        int i11 = 84;
        int i12 = -19;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -69;
        int i14 = 82;
        int i15 = -39;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 50;
        int i17 = 11;
        int i18 = 49;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -97;
        int i20 = -10;
        int i21 = -21;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 4;
        int i23 = -90;
        int i24 = -38;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 73;
        int i26 = -69;
        int i27 = 9;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = sum0.getSum(i1, i17);
        Assert.assertTrue(Sum.validateGetSum(i1, i17, i28));
    }

    @Test
    public void test82() throws Throwable {
        int i0 = 88;
        Sum sum0 = new Sum(i0);
        int i1 = 27;
        int i2 = -91;
        int i3 = -43;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -3;
        int i5 = 87;
        int i6 = -5;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = sum0.getSum(i5, i0);
        Assert.assertTrue(Sum.validateGetSum(i5, i0, i7));
    }

    @Test
    public void test83() throws Throwable {
        int i0 = 89;
        Sum sum0 = new Sum(i0);
        int i1 = -49;
        int i2 = -7;
        int i3 = -55;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 42;
        int i5 = 45;
        int i6 = 21;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 16;
        int i8 = 0;
        int i9 = 38;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 72;
        int i11 = 77;
        int i12 = -61;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 73;
        int i14 = -62;
        int i15 = 81;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 3;
        int i17 = 66;
        int i18 = 25;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -22;
        int i20 = 49;
        int i21 = -57;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 28;
        int i23 = 7;
        int i24 = 55;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -45;
        int i26 = -1;
        int i27 = -68;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 57;
        int i29 = sum0.getSum(i25, i28);
        Assert.assertTrue(Sum.validateGetSum(i25, i28, i29));
    }

    @Test
    public void test84() throws Throwable {
        int i0 = 86;
        Sum sum0 = new Sum(i0);
        int i1 = -54;
        int i2 = -77;
        int i3 = -17;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 13;
        int i5 = 51;
        int i6 = 39;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -30;
        int i8 = 90;
        int i9 = 91;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 10;
        int i11 = -30;
        int i12 = -1;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -11;
        int i14 = sum0.getSum(i13, i4);
        Assert.assertTrue(Sum.validateGetSum(i13, i4, i14));
    }

    @Test
    public void test85() throws Throwable {
        int i0 = 78;
        Sum sum0 = new Sum(i0);
        int i1 = 83;
        int i2 = -84;
        int i3 = -44;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 32;
        int i5 = -68;
        int i6 = -58;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -78;
        int i8 = -100;
        int i9 = -64;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 75;
        int i11 = -46;
        int i12 = 75;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 86;
        int i14 = 59;
        int i15 = 16;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -63;
        int i17 = sum0.getSum(i16, i10);
        Assert.assertTrue(Sum.validateGetSum(i16, i10, i17));
    }

    @Test
    public void test86() throws Throwable {
        int i0 = 62;
        Sum sum0 = new Sum(i0);
        int i1 = 40;
        int i2 = 46;
        int i3 = -89;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 47;
        int i5 = -96;
        int i6 = 100;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -14;
        int i8 = -86;
        int i9 = 92;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 26;
        int i11 = -76;
        int i12 = 99;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 61;
        int i14 = -26;
        int i15 = -20;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 81;
        int i17 = -71;
        int i18 = 47;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 19;
        int i20 = 49;
        int i21 = 17;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -24;
        int i23 = -79;
        int i24 = 90;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -87;
        int i26 = 51;
        int i27 = 34;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 29;
        int i29 = 80;
        int i30 = 72;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 91;
        int i32 = -59;
        int i33 = sum0.getSum(i31, i32);
        Assert.assertTrue(Sum.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test87() throws Throwable {
        int i0 = 53;
        Sum sum0 = new Sum(i0);
        int i1 = -61;
        int i2 = 17;
        int i3 = -35;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -88;
        int i5 = -88;
        int i6 = sum0.getSum(i4, i5);
        Assert.assertTrue(Sum.validateGetSum(i4, i5, i6));
    }

    @Test
    public void test88() throws Throwable {
        int i0 = 90;
        Sum sum0 = new Sum(i0);
        int i1 = -61;
        int i2 = 33;
        int i3 = -99;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -62;
        int i5 = 29;
        int i6 = 49;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -38;
        int i8 = 29;
        int i9 = 29;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 30;
        int i11 = -28;
        int i12 = 59;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -56;
        int i14 = 5;
        int i15 = 58;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 42;
        int i17 = -30;
        int i18 = -13;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -84;
        int i20 = -97;
        int i21 = -68;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 58;
        int i23 = 43;
        int i24 = sum0.getSum(i22, i23);
        Assert.assertTrue(Sum.validateGetSum(i22, i23, i24));
    }

    @Test
    public void test89() throws Throwable {
        int i0 = 83;
        Sum sum0 = new Sum(i0);
        int i1 = -58;
        int i2 = -38;
        int i3 = -91;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -99;
        int i5 = 100;
        int i6 = 85;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 91;
        int i8 = 87;
        int i9 = -72;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -35;
        int i11 = -25;
        int i12 = 70;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 16;
        int i14 = 77;
        int i15 = -44;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -47;
        int i17 = 81;
        int i18 = -21;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -34;
        int i20 = -54;
        int i21 = -96;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 10;
        int i23 = -25;
        int i24 = -7;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -42;
        int i26 = -70;
        int i27 = 7;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = 34;
        int i29 = -64;
        int i30 = -16;
        sum0.validateGetSum(i28, i29, i30);
        int i31 = 42;
        int i32 = 16;
        int i33 = sum0.getSum(i31, i32);
        Assert.assertTrue(Sum.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test90() throws Throwable {
        int i0 = 86;
        Sum sum0 = new Sum(i0);
        int i1 = -46;
        int i2 = -6;
        int i3 = -44;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -57;
        int i5 = 7;
        int i6 = -90;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -83;
        int i8 = 97;
        int i9 = -76;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -54;
        int i11 = sum0.getSum(i10, i8);
        Assert.assertTrue(Sum.validateGetSum(i10, i8, i11));
    }

    @Test
    public void test91() throws Throwable {
        int i0 = 96;
        Sum sum0 = new Sum(i0);
        int i1 = -100;
        int i2 = -94;
        int i3 = -36;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -29;
        int i5 = 55;
        int i6 = 83;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -84;
        int i8 = -17;
        int i9 = 9;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 58;
        int i11 = 39;
        int i12 = 27;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 38;
        int i14 = -1;
        int i15 = -54;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -77;
        int i17 = 11;
        int i18 = 50;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -36;
        int i20 = -100;
        int i21 = 79;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -69;
        int i23 = 66;
        int i24 = sum0.getSum(i22, i23);
        Assert.assertTrue(Sum.validateGetSum(i22, i23, i24));
    }

    @Test
    public void test92() throws Throwable {
        int i0 = 73;
        Sum sum0 = new Sum(i0);
        int i1 = -30;
        int i2 = -12;
        int i3 = 39;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -6;
        int i5 = sum0.getSum(i2, i4);
        Assert.assertTrue(Sum.validateGetSum(i2, i4, i5));
    }

    @Test
    public void test93() throws Throwable {
        int i0 = 54;
        Sum sum0 = new Sum(i0);
        int i1 = 68;
        int i2 = -70;
        int i3 = -63;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 68;
        int i5 = -94;
        int i6 = 98;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 17;
        int i8 = -85;
        int i9 = -54;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 42;
        int i11 = 93;
        int i12 = -73;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -8;
        int i14 = 15;
        int i15 = -66;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 26;
        int i17 = -19;
        int i18 = 72;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 14;
        int i20 = -89;
        int i21 = -75;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 58;
        int i23 = -38;
        int i24 = -72;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -1;
        int i26 = 0;
        int i27 = 42;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = sum0.getSum(i0, i24);
        Assert.assertTrue(Sum.validateGetSum(i0, i24, i28));
    }

    @Test
    public void test94() throws Throwable {
        int i0 = 64;
        Sum sum0 = new Sum(i0);
        int i1 = -33;
        int i2 = -71;
        int i3 = 31;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -40;
        int i5 = -91;
        int i6 = -45;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -83;
        int i8 = -77;
        int i9 = -30;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -34;
        int i11 = -65;
        int i12 = 30;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 74;
        int i14 = -1;
        int i15 = -29;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 89;
        int i17 = -19;
        int i18 = -37;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 65;
        int i20 = 90;
        int i21 = -6;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 64;
        int i23 = 69;
        int i24 = 29;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = 21;
        int i26 = 43;
        int i27 = 21;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = -81;
        int i29 = sum0.getSum(i18, i28);
        Assert.assertTrue(Sum.validateGetSum(i18, i28, i29));
    }

    @Test
    public void test95() throws Throwable {
        int i0 = 80;
        Sum sum0 = new Sum(i0);
        int i1 = -34;
        int i2 = -86;
        int i3 = -34;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -6;
        int i5 = 45;
        int i6 = 29;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -30;
        int i8 = -15;
        int i9 = -25;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -30;
        int i11 = -13;
        int i12 = -28;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -43;
        int i14 = 26;
        int i15 = -9;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 69;
        int i17 = -55;
        int i18 = -19;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 49;
        int i20 = -23;
        int i21 = 52;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = -50;
        int i23 = 72;
        int i24 = 6;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = -19;
        int i26 = -42;
        int i27 = 32;
        sum0.validateGetSum(i25, i26, i27);
        int i28 = sum0.getSum(i2, i25);
        Assert.assertTrue(Sum.validateGetSum(i2, i25, i28));
    }

    @Test
    public void test96() throws Throwable {
        int i0 = 76;
        Sum sum0 = new Sum(i0);
        int i1 = 10;
        int i2 = -12;
        int i3 = 26;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -45;
        int i5 = -97;
        int i6 = 29;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 36;
        int i8 = 88;
        int i9 = 26;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -52;
        int i11 = 96;
        int i12 = -97;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = 25;
        int i14 = 42;
        int i15 = -55;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = 36;
        int i17 = -31;
        int i18 = -20;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = -50;
        int i20 = sum0.getSum(i19, i7);
        Assert.assertTrue(Sum.validateGetSum(i19, i7, i20));
    }

    @Test
    public void test97() throws Throwable {
        int i0 = 94;
        Sum sum0 = new Sum(i0);
        int i1 = 21;
        int i2 = 51;
        int i3 = 58;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -45;
        int i5 = sum0.getSum(i4, i1);
        Assert.assertTrue(Sum.validateGetSum(i4, i1, i5));
    }

    @Test
    public void test98() throws Throwable {
        int i0 = 88;
        Sum sum0 = new Sum(i0);
        int i1 = -65;
        int i2 = -28;
        int i3 = -62;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = -78;
        int i5 = 21;
        int i6 = 20;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -63;
        int i8 = -66;
        int i9 = sum0.getSum(i7, i8);
        Assert.assertTrue(Sum.validateGetSum(i7, i8, i9));
    }

    @Test
    public void test99() throws Throwable {
        int i0 = 64;
        Sum sum0 = new Sum(i0);
        int i1 = 100;
        int i2 = 83;
        int i3 = 71;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 19;
        int i5 = 0;
        int i6 = -60;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = -97;
        int i8 = -96;
        int i9 = 35;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = 1;
        int i11 = 44;
        int i12 = sum0.getSum(i10, i11);
        Assert.assertTrue(Sum.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test100() throws Throwable {
        int i0 = 83;
        Sum sum0 = new Sum(i0);
        int i1 = -68;
        int i2 = -36;
        int i3 = -24;
        sum0.validateGetSum(i1, i2, i3);
        int i4 = 21;
        int i5 = 97;
        int i6 = 39;
        sum0.validateGetSum(i4, i5, i6);
        int i7 = 94;
        int i8 = 2;
        int i9 = 5;
        sum0.validateGetSum(i7, i8, i9);
        int i10 = -56;
        int i11 = 8;
        int i12 = -33;
        sum0.validateGetSum(i10, i11, i12);
        int i13 = -73;
        int i14 = 39;
        int i15 = 94;
        sum0.validateGetSum(i13, i14, i15);
        int i16 = -40;
        int i17 = 12;
        int i18 = -34;
        sum0.validateGetSum(i16, i17, i18);
        int i19 = 22;
        int i20 = -47;
        int i21 = 13;
        sum0.validateGetSum(i19, i20, i21);
        int i22 = 49;
        int i23 = 35;
        int i24 = 61;
        sum0.validateGetSum(i22, i23, i24);
        int i25 = sum0.getSum(i5, i5);
        Assert.assertTrue(Sum.validateGetSum(i5, i5, i25));
    }
}

