package test;

import org.junit.Test;
import sav.commons.testdata.calculator.ClassA;
import org.junit.Assert;

public class ClassA1 {

    @Test
    public void test1() throws Throwable {
        int i0 = 2;
        ClassA classa0 = new ClassA(i0);
        int i1 = 50;
        int i2 = 24;
        int i3 = 88;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 73;
        int i5 = 51;
        int i6 = 86;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 77;
        int i8 = 64;
        int i9 = 16;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 39;
        int i11 = 19;
        int i12 = 62;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 80;
        int i14 = 7;
        int i15 = 38;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 45;
        int i17 = 5;
        int i18 = 77;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 29;
        int i20 = 88;
        int i21 = 30;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 39;
        int i23 = 59;
        int i24 = classa0.getSum(i22, i23);
        Assert.assertTrue(ClassA.validateGetSum(i22, i23, i24));
    }

    @Test
    public void test2() throws Throwable {
        int i0 = 0;
        ClassA classa0 = new ClassA(i0);
        int i1 = 22;
        int i2 = 61;
        int i3 = 52;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 62;
        int i5 = 69;
        int i6 = 75;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 27;
        int i8 = 5;
        int i9 = 87;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 97;
        int i11 = 71;
        int i12 = 29;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 76;
        int i14 = 1;
        int i15 = 74;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 39;
        int i17 = 65;
        int i18 = 81;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = classa0.getSum(i9, i4);
        Assert.assertTrue(ClassA.validateGetSum(i9, i4, i19));
    }

    @Test
    public void test3() throws Throwable {
        int i0 = 28;
        ClassA classa0 = new ClassA(i0);
        int i1 = 9;
        int i2 = 95;
        int i3 = 69;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 31;
        int i5 = 6;
        int i6 = 67;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 91;
        int i8 = 32;
        int i9 = 98;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 26;
        int i11 = 34;
        int i12 = 80;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 52;
        int i14 = 30;
        int i15 = 23;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 2;
        int i17 = 51;
        int i18 = 50;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 26;
        int i20 = 33;
        int i21 = 26;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 98;
        int i23 = classa0.getSum(i22, i14);
        Assert.assertTrue(ClassA.validateGetSum(i22, i14, i23));
    }

    @Test
    public void test4() throws Throwable {
        int i0 = 97;
        ClassA classa0 = new ClassA(i0);
        int i1 = 87;
        int i2 = 70;
        int i3 = 50;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 24;
        int i5 = 66;
        int i6 = 78;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 71;
        int i8 = 41;
        int i9 = 76;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 36;
        int i11 = 85;
        int i12 = 93;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 82;
        int i14 = 79;
        int i15 = 57;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 38;
        int i17 = 67;
        int i18 = classa0.getSum(i16, i17);
        Assert.assertTrue(ClassA.validateGetSum(i16, i17, i18));
    }

    @Test
    public void test5() throws Throwable {
        int i0 = 42;
        ClassA classa0 = new ClassA(i0);
        int i1 = 1;
        int i2 = 7;
        int i3 = 51;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 8;
        int i5 = 58;
        int i6 = 19;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 22;
        int i8 = 16;
        int i9 = 97;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 66;
        int i11 = 100;
        int i12 = 72;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 100;
        int i14 = 83;
        int i15 = 81;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 64;
        int i17 = 73;
        int i18 = 6;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 3;
        int i20 = classa0.getSum(i19, i4);
        Assert.assertTrue(ClassA.validateGetSum(i19, i4, i20));
    }

    @Test
    public void test6() throws Throwable {
        int i0 = 40;
        ClassA classa0 = new ClassA(i0);
        int i1 = 14;
        int i2 = 77;
        int i3 = 25;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 87;
        int i5 = 92;
        int i6 = 82;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 81;
        int i8 = 81;
        int i9 = 87;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 89;
        int i11 = 71;
        int i12 = 51;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 15;
        int i14 = 73;
        int i15 = 85;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 86;
        int i17 = 94;
        int i18 = 11;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 27;
        int i20 = 19;
        int i21 = 34;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 32;
        int i23 = 37;
        int i24 = 96;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 15;
        int i26 = 50;
        int i27 = 38;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 33;
        int i29 = 62;
        int i30 = 98;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 58;
        int i32 = 13;
        int i33 = classa0.getSum(i31, i32);
        Assert.assertTrue(ClassA.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test7() throws Throwable {
        int i0 = 21;
        ClassA classa0 = new ClassA(i0);
        int i1 = 47;
        int i2 = 94;
        int i3 = 0;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 99;
        int i5 = classa0.getSum(i1, i4);
        Assert.assertTrue(ClassA.validateGetSum(i1, i4, i5));
    }

    @Test
    public void test8() throws Throwable {
        int i0 = 87;
        ClassA classa0 = new ClassA(i0);
        int i1 = 83;
        int i2 = 75;
        int i3 = 52;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 12;
        int i5 = 59;
        int i6 = 34;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 29;
        int i8 = 42;
        int i9 = 83;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 16;
        int i11 = 55;
        int i12 = 95;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 1;
        int i14 = 63;
        int i15 = 56;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 85;
        int i17 = 5;
        int i18 = 44;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 40;
        int i20 = 5;
        int i21 = 62;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 83;
        int i23 = 62;
        int i24 = 18;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 65;
        int i26 = 59;
        int i27 = 22;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = classa0.getSum(i7, i22);
        Assert.assertTrue(ClassA.validateGetSum(i7, i22, i28));
    }

    @Test
    public void test9() throws Throwable {
        int i0 = 76;
        ClassA classa0 = new ClassA(i0);
        int i1 = 88;
        int i2 = 75;
        int i3 = 37;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 3;
        int i5 = 36;
        int i6 = 59;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 84;
        int i8 = 48;
        int i9 = 53;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 60;
        int i11 = classa0.getSum(i10, i7);
        Assert.assertTrue(ClassA.validateGetSum(i10, i7, i11));
    }

    @Test
    public void test10() throws Throwable {
        int i0 = 57;
        ClassA classa0 = new ClassA(i0);
        int i1 = 95;
        int i2 = 77;
        int i3 = 5;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 62;
        int i5 = 80;
        int i6 = 37;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 52;
        int i8 = classa0.getSum(i1, i7);
        Assert.assertTrue(ClassA.validateGetSum(i1, i7, i8));
    }

    @Test
    public void test11() throws Throwable {
        int i0 = 37;
        ClassA classa0 = new ClassA(i0);
        int i1 = 6;
        int i2 = 20;
        int i3 = 52;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 100;
        int i5 = 81;
        int i6 = 19;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 62;
        int i8 = 59;
        int i9 = 0;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 56;
        int i11 = 89;
        int i12 = 46;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 10;
        int i14 = 96;
        int i15 = 41;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 45;
        int i17 = 73;
        int i18 = 42;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 84;
        int i20 = 64;
        int i21 = 24;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 49;
        int i23 = classa0.getSum(i9, i22);
        Assert.assertTrue(ClassA.validateGetSum(i9, i22, i23));
    }

    @Test
    public void test12() throws Throwable {
        int i0 = 96;
        ClassA classa0 = new ClassA(i0);
        int i1 = 65;
        int i2 = 95;
        int i3 = 35;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 13;
        int i5 = 57;
        int i6 = 95;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 54;
        int i8 = 81;
        int i9 = 35;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 97;
        int i11 = 17;
        int i12 = 32;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 68;
        int i14 = 29;
        int i15 = 92;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 47;
        int i17 = 98;
        int i18 = 14;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 5;
        int i20 = 42;
        int i21 = 4;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 33;
        int i23 = 22;
        int i24 = 92;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 1;
        int i26 = 37;
        int i27 = classa0.getSum(i25, i26);
        Assert.assertTrue(ClassA.validateGetSum(i25, i26, i27));
    }

    @Test
    public void test13() throws Throwable {
        int i0 = 71;
        ClassA classa0 = new ClassA(i0);
        int i1 = 57;
        int i2 = 11;
        int i3 = 24;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 9;
        int i5 = 75;
        int i6 = 62;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 90;
        int i8 = 14;
        int i9 = 75;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 18;
        int i11 = classa0.getSum(i0, i10);
        Assert.assertTrue(ClassA.validateGetSum(i0, i10, i11));
    }

    @Test
    public void test14() throws Throwable {
        int i0 = 37;
        ClassA classa0 = new ClassA(i0);
        int i1 = 35;
        int i2 = 68;
        int i3 = 85;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 3;
        int i5 = classa0.getSum(i0, i4);
        Assert.assertTrue(ClassA.validateGetSum(i0, i4, i5));
    }

    @Test
    public void test15() throws Throwable {
        int i0 = 12;
        ClassA classa0 = new ClassA(i0);
        int i1 = 52;
        int i2 = 32;
        int i3 = 73;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 26;
        int i5 = 16;
        int i6 = 66;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 50;
        int i8 = 7;
        int i9 = 100;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 12;
        int i11 = 22;
        int i12 = 26;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 7;
        int i14 = 31;
        int i15 = 77;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 32;
        int i17 = classa0.getSum(i3, i16);
        Assert.assertTrue(ClassA.validateGetSum(i3, i16, i17));
    }

    @Test
    public void test16() throws Throwable {
        int i0 = 17;
        ClassA classa0 = new ClassA(i0);
        int i1 = 63;
        int i2 = 10;
        int i3 = 96;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 73;
        int i5 = 90;
        int i6 = 42;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 39;
        int i8 = 1;
        int i9 = 55;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 26;
        int i11 = 19;
        int i12 = 87;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 49;
        int i14 = classa0.getSum(i11, i13);
        Assert.assertTrue(ClassA.validateGetSum(i11, i13, i14));
    }

    @Test
    public void test17() throws Throwable {
        int i0 = 52;
        ClassA classa0 = new ClassA(i0);
        int i1 = 9;
        int i2 = 67;
        int i3 = 57;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 27;
        int i5 = 61;
        int i6 = 47;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 63;
        int i8 = 58;
        int i9 = 87;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 99;
        int i11 = 74;
        int i12 = 30;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 100;
        int i14 = 28;
        int i15 = 14;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 28;
        int i17 = 78;
        int i18 = 44;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 49;
        int i20 = 8;
        int i21 = 90;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 73;
        int i23 = 4;
        int i24 = 50;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = classa0.getSum(i15, i15);
        Assert.assertTrue(ClassA.validateGetSum(i15, i15, i25));
    }

    @Test
    public void test18() throws Throwable {
        int i0 = 32;
        ClassA classa0 = new ClassA(i0);
        int i1 = 68;
        int i2 = classa0.getSum(i1, i0);
        Assert.assertTrue(ClassA.validateGetSum(i1, i0, i2));
    }

    @Test
    public void test19() throws Throwable {
        int i0 = 40;
        ClassA classa0 = new ClassA(i0);
        int i1 = 14;
        int i2 = 74;
        int i3 = 17;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = classa0.getSum(i0, i2);
        Assert.assertTrue(ClassA.validateGetSum(i0, i2, i4));
    }

    @Test
    public void test20() throws Throwable {
        int i0 = 58;
        ClassA classa0 = new ClassA(i0);
        int i1 = 31;
        int i2 = 86;
        int i3 = 37;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 46;
        int i5 = 58;
        int i6 = 1;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 13;
        int i8 = 31;
        int i9 = 17;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 30;
        int i11 = 1;
        int i12 = 9;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 1;
        int i14 = 64;
        int i15 = 0;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 23;
        int i17 = 100;
        int i18 = 89;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 84;
        int i20 = 17;
        int i21 = 72;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 20;
        int i23 = 30;
        int i24 = 44;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = classa0.getSum(i3, i11);
        Assert.assertTrue(ClassA.validateGetSum(i3, i11, i25));
    }

    @Test
    public void test21() throws Throwable {
        int i0 = 73;
        ClassA classa0 = new ClassA(i0);
        int i1 = 96;
        int i2 = 49;
        int i3 = 72;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 38;
        int i5 = 66;
        int i6 = 26;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 65;
        int i8 = 45;
        int i9 = 65;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 40;
        int i11 = 81;
        int i12 = 76;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 34;
        int i14 = 33;
        int i15 = 83;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 14;
        int i17 = 81;
        int i18 = 75;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 63;
        int i20 = 84;
        int i21 = 85;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 37;
        int i23 = 9;
        int i24 = 64;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 73;
        int i26 = 9;
        int i27 = 34;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 49;
        int i29 = 66;
        int i30 = 13;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 68;
        int i32 = classa0.getSum(i8, i31);
        Assert.assertTrue(ClassA.validateGetSum(i8, i31, i32));
    }

    @Test
    public void test22() throws Throwable {
        int i0 = 55;
        ClassA classa0 = new ClassA(i0);
        int i1 = 73;
        int i2 = classa0.getSum(i0, i1);
        Assert.assertTrue(ClassA.validateGetSum(i0, i1, i2));
    }

    @Test
    public void test23() throws Throwable {
        int i0 = 16;
        ClassA classa0 = new ClassA(i0);
        int i1 = 60;
        int i2 = 52;
        int i3 = 92;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 21;
        int i5 = 13;
        int i6 = 62;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 22;
        int i8 = 23;
        int i9 = 64;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 91;
        int i11 = 3;
        int i12 = 47;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 40;
        int i14 = 23;
        int i15 = 79;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 91;
        int i17 = 6;
        int i18 = 100;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 95;
        int i20 = 79;
        int i21 = 65;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 77;
        int i23 = 33;
        int i24 = 54;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 73;
        int i26 = classa0.getSum(i25, i19);
        Assert.assertTrue(ClassA.validateGetSum(i25, i19, i26));
    }

    @Test
    public void test24() throws Throwable {
        int i0 = 83;
        ClassA classa0 = new ClassA(i0);
        int i1 = 85;
        int i2 = 17;
        int i3 = 34;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 64;
        int i5 = 2;
        int i6 = 48;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 90;
        int i8 = 55;
        int i9 = 27;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 84;
        int i11 = 92;
        int i12 = 80;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 57;
        int i14 = 7;
        int i15 = 9;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 70;
        int i17 = 80;
        int i18 = 33;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 54;
        int i20 = 34;
        int i21 = 58;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 84;
        int i23 = 69;
        int i24 = 26;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 54;
        int i26 = classa0.getSum(i5, i25);
        Assert.assertTrue(ClassA.validateGetSum(i5, i25, i26));
    }

    @Test
    public void test25() throws Throwable {
        int i0 = 73;
        ClassA classa0 = new ClassA(i0);
        int i1 = 37;
        int i2 = 33;
        int i3 = 29;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 31;
        int i5 = 33;
        int i6 = 46;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 10;
        int i8 = 92;
        int i9 = 24;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 5;
        int i11 = 58;
        int i12 = 26;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 13;
        int i14 = 99;
        int i15 = 96;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 96;
        int i17 = 76;
        int i18 = 10;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 74;
        int i20 = 46;
        int i21 = 3;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 16;
        int i23 = 88;
        int i24 = 62;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 56;
        int i26 = 38;
        int i27 = 8;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 56;
        int i29 = 20;
        int i30 = 3;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = classa0.getSum(i2, i16);
        Assert.assertTrue(ClassA.validateGetSum(i2, i16, i31));
    }

    @Test
    public void test26() throws Throwable {
        int i0 = 32;
        ClassA classa0 = new ClassA(i0);
        int i1 = 90;
        int i2 = 60;
        int i3 = 69;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 15;
        int i5 = 51;
        int i6 = 11;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 42;
        int i8 = 84;
        int i9 = 52;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 9;
        int i11 = 58;
        int i12 = 68;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 30;
        int i14 = 32;
        int i15 = 0;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 54;
        int i17 = 97;
        int i18 = 53;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 19;
        int i20 = 4;
        int i21 = 13;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 82;
        int i23 = 35;
        int i24 = 9;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 98;
        int i26 = 76;
        int i27 = 91;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 6;
        int i29 = classa0.getSum(i28, i13);
        Assert.assertTrue(ClassA.validateGetSum(i28, i13, i29));
    }

    @Test
    public void test27() throws Throwable {
        int i0 = 64;
        ClassA classa0 = new ClassA(i0);
        int i1 = 60;
        int i2 = 23;
        int i3 = 72;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 59;
        int i5 = 96;
        int i6 = 79;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 2;
        int i8 = classa0.getSum(i4, i7);
        Assert.assertTrue(ClassA.validateGetSum(i4, i7, i8));
    }

    @Test
    public void test28() throws Throwable {
        int i0 = 88;
        ClassA classa0 = new ClassA(i0);
        int i1 = 23;
        int i2 = 47;
        int i3 = 17;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 63;
        int i5 = 88;
        int i6 = 20;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 3;
        int i8 = 84;
        int i9 = 10;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 12;
        int i11 = 98;
        int i12 = 68;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 99;
        int i14 = 34;
        int i15 = 61;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 43;
        int i17 = 77;
        int i18 = 63;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 53;
        int i20 = 82;
        int i21 = 43;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 67;
        int i23 = 11;
        int i24 = 29;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 6;
        int i26 = 19;
        int i27 = 19;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = classa0.getSum(i4, i1);
        Assert.assertTrue(ClassA.validateGetSum(i4, i1, i28));
    }

    @Test
    public void test29() throws Throwable {
        int i0 = 77;
        ClassA classa0 = new ClassA(i0);
        int i1 = 63;
        int i2 = 81;
        int i3 = 62;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 52;
        int i5 = 81;
        int i6 = 53;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 45;
        int i8 = 27;
        int i9 = 25;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 51;
        int i11 = 40;
        int i12 = 64;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 27;
        int i14 = 11;
        int i15 = 81;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 16;
        int i17 = 31;
        int i18 = 74;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 61;
        int i20 = 53;
        int i21 = classa0.getSum(i19, i20);
        Assert.assertTrue(ClassA.validateGetSum(i19, i20, i21));
    }

    @Test
    public void test30() throws Throwable {
        int i0 = 37;
        ClassA classa0 = new ClassA(i0);
        int i1 = 53;
        int i2 = 67;
        int i3 = 44;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 20;
        int i5 = 42;
        int i6 = 90;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 70;
        int i8 = 97;
        int i9 = 67;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 75;
        int i11 = 62;
        int i12 = 7;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 92;
        int i14 = 12;
        int i15 = 69;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 73;
        int i17 = 74;
        int i18 = 39;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = classa0.getSum(i12, i8);
        Assert.assertTrue(ClassA.validateGetSum(i12, i8, i19));
    }

    @Test
    public void test31() throws Throwable {
        int i0 = 82;
        ClassA classa0 = new ClassA(i0);
        int i1 = 21;
        int i2 = 62;
        int i3 = 18;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 14;
        int i5 = 22;
        int i6 = 51;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 55;
        int i8 = 45;
        int i9 = 31;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 20;
        int i11 = 2;
        int i12 = 42;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 89;
        int i14 = 27;
        int i15 = 58;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 100;
        int i17 = 58;
        int i18 = 41;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 7;
        int i20 = 77;
        int i21 = 9;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 31;
        int i23 = 36;
        int i24 = 25;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = classa0.getSum(i2, i18);
        Assert.assertTrue(ClassA.validateGetSum(i2, i18, i25));
    }

    @Test
    public void test32() throws Throwable {
        int i0 = 4;
        ClassA classa0 = new ClassA(i0);
        int i1 = 41;
        int i2 = 48;
        int i3 = 39;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 3;
        int i5 = 94;
        int i6 = 55;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 48;
        int i8 = 28;
        int i9 = 11;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = classa0.getSum(i6, i1);
        Assert.assertTrue(ClassA.validateGetSum(i6, i1, i10));
    }

    @Test
    public void test33() throws Throwable {
        int i0 = 59;
        ClassA classa0 = new ClassA(i0);
        int i1 = 44;
        int i2 = 11;
        int i3 = 84;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 89;
        int i5 = 74;
        int i6 = 94;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 35;
        int i8 = 22;
        int i9 = 7;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 66;
        int i11 = 78;
        int i12 = 95;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 48;
        int i14 = 8;
        int i15 = 25;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 33;
        int i17 = 85;
        int i18 = 64;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 48;
        int i20 = classa0.getSum(i19, i11);
        Assert.assertTrue(ClassA.validateGetSum(i19, i11, i20));
    }

    @Test
    public void test34() throws Throwable {
        int i0 = 26;
        ClassA classa0 = new ClassA(i0);
        int i1 = 44;
        int i2 = 74;
        int i3 = 95;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 63;
        int i5 = 19;
        int i6 = 90;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = classa0.getSum(i1, i4);
        Assert.assertTrue(ClassA.validateGetSum(i1, i4, i7));
    }

    @Test
    public void test35() throws Throwable {
        int i0 = 59;
        ClassA classa0 = new ClassA(i0);
        int i1 = 48;
        int i2 = 100;
        int i3 = 84;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 49;
        int i5 = 46;
        int i6 = classa0.getSum(i4, i5);
        Assert.assertTrue(ClassA.validateGetSum(i4, i5, i6));
    }

    @Test
    public void test36() throws Throwable {
        int i0 = 66;
        ClassA classa0 = new ClassA(i0);
        int i1 = 29;
        int i2 = 70;
        int i3 = 78;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = classa0.getSum(i2, i1);
        Assert.assertTrue(ClassA.validateGetSum(i2, i1, i4));
    }

    @Test
    public void test37() throws Throwable {
        int i0 = 34;
        ClassA classa0 = new ClassA(i0);
        int i1 = 30;
        int i2 = 40;
        int i3 = 83;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 89;
        int i5 = 30;
        int i6 = 97;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 31;
        int i8 = 95;
        int i9 = 43;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 11;
        int i11 = 22;
        int i12 = 50;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 40;
        int i14 = 40;
        int i15 = 95;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 36;
        int i17 = 49;
        int i18 = 20;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 10;
        int i20 = 60;
        int i21 = 62;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 15;
        int i23 = classa0.getSum(i22, i8);
        Assert.assertTrue(ClassA.validateGetSum(i22, i8, i23));
    }

    @Test
    public void test38() throws Throwable {
        int i0 = 4;
        ClassA classa0 = new ClassA(i0);
        int i1 = 85;
        int i2 = 40;
        int i3 = 32;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 96;
        int i5 = 55;
        int i6 = 23;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 73;
        int i8 = 14;
        int i9 = 0;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 86;
        int i11 = 63;
        int i12 = 22;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 34;
        int i14 = 88;
        int i15 = 28;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 5;
        int i17 = 18;
        int i18 = 12;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 61;
        int i20 = classa0.getSum(i19, i9);
        Assert.assertTrue(ClassA.validateGetSum(i19, i9, i20));
    }

    @Test
    public void test39() throws Throwable {
        int i0 = 55;
        ClassA classa0 = new ClassA(i0);
        int i1 = 89;
        int i2 = 59;
        int i3 = 82;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 68;
        int i5 = 1;
        int i6 = 93;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 87;
        int i8 = 18;
        int i9 = classa0.getSum(i7, i8);
        Assert.assertTrue(ClassA.validateGetSum(i7, i8, i9));
    }

    @Test
    public void test40() throws Throwable {
        int i0 = 79;
        ClassA classa0 = new ClassA(i0);
        int i1 = 76;
        int i2 = 4;
        int i3 = 44;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 32;
        int i5 = 58;
        int i6 = 97;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 91;
        int i8 = 0;
        int i9 = 95;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 28;
        int i11 = 90;
        int i12 = 70;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 39;
        int i14 = 15;
        int i15 = 42;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 35;
        int i17 = 58;
        int i18 = 85;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 10;
        int i20 = classa0.getSum(i3, i19);
        Assert.assertTrue(ClassA.validateGetSum(i3, i19, i20));
    }

    @Test
    public void test41() throws Throwable {
        int i0 = 36;
        ClassA classa0 = new ClassA(i0);
        int i1 = 43;
        int i2 = 12;
        int i3 = 65;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 24;
        int i5 = 2;
        int i6 = 99;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 94;
        int i8 = 89;
        int i9 = 47;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 91;
        int i11 = 36;
        int i12 = 74;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 59;
        int i14 = 92;
        int i15 = 67;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 16;
        int i17 = 17;
        int i18 = 56;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 52;
        int i20 = 54;
        int i21 = 93;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 90;
        int i23 = 27;
        int i24 = 68;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 3;
        int i26 = 63;
        int i27 = 29;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 63;
        int i29 = classa0.getSum(i19, i28);
        Assert.assertTrue(ClassA.validateGetSum(i19, i28, i29));
    }

    @Test
    public void test42() throws Throwable {
        int i0 = 32;
        ClassA classa0 = new ClassA(i0);
        int i1 = 84;
        int i2 = 73;
        int i3 = 20;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 85;
        int i5 = 70;
        int i6 = 12;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = classa0.getSum(i6, i6);
        Assert.assertTrue(ClassA.validateGetSum(i6, i6, i7));
    }

    @Test
    public void test43() throws Throwable {
        int i0 = 80;
        ClassA classa0 = new ClassA(i0);
        int i1 = 7;
        int i2 = 1;
        int i3 = 67;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 60;
        int i5 = 99;
        int i6 = 86;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 86;
        int i8 = 45;
        int i9 = 25;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 74;
        int i11 = 66;
        int i12 = 40;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 13;
        int i14 = 59;
        int i15 = 35;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 98;
        int i17 = 13;
        int i18 = 34;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 43;
        int i20 = 76;
        int i21 = classa0.getSum(i19, i20);
        Assert.assertTrue(ClassA.validateGetSum(i19, i20, i21));
    }

    @Test
    public void test44() throws Throwable {
        int i0 = 21;
        ClassA classa0 = new ClassA(i0);
        int i1 = 57;
        int i2 = 2;
        int i3 = 50;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 4;
        int i5 = 91;
        int i6 = 4;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 66;
        int i8 = 49;
        int i9 = 77;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 24;
        int i11 = 71;
        int i12 = 37;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 53;
        int i14 = 52;
        int i15 = 83;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 50;
        int i17 = 6;
        int i18 = 43;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 60;
        int i20 = 3;
        int i21 = 82;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 61;
        int i23 = 40;
        int i24 = 46;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 50;
        int i26 = 6;
        int i27 = 77;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 64;
        int i29 = classa0.getSum(i28, i13);
        Assert.assertTrue(ClassA.validateGetSum(i28, i13, i29));
    }

    @Test
    public void test45() throws Throwable {
        int i0 = 28;
        ClassA classa0 = new ClassA(i0);
        int i1 = 58;
        int i2 = 23;
        int i3 = 77;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 19;
        int i5 = 100;
        int i6 = 17;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 96;
        int i8 = 90;
        int i9 = 4;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 10;
        int i11 = 35;
        int i12 = 64;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = classa0.getSum(i4, i3);
        Assert.assertTrue(ClassA.validateGetSum(i4, i3, i13));
    }

    @Test
    public void test46() throws Throwable {
        int i0 = 100;
        ClassA classa0 = new ClassA(i0);
        int i1 = 10;
        int i2 = 82;
        int i3 = 30;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 78;
        int i5 = 85;
        int i6 = 0;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 85;
        int i8 = 14;
        int i9 = 32;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 8;
        int i11 = 20;
        int i12 = 49;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 81;
        int i14 = 78;
        int i15 = 52;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 2;
        int i17 = 84;
        int i18 = 90;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 44;
        int i20 = 6;
        int i21 = 26;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 86;
        int i23 = classa0.getSum(i5, i22);
        Assert.assertTrue(ClassA.validateGetSum(i5, i22, i23));
    }

    @Test
    public void test47() throws Throwable {
        int i0 = 58;
        ClassA classa0 = new ClassA(i0);
        int i1 = 46;
        int i2 = 9;
        int i3 = 94;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 20;
        int i5 = 33;
        int i6 = 25;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 78;
        int i8 = 39;
        int i9 = 78;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 59;
        int i11 = 37;
        int i12 = 51;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 65;
        int i14 = 100;
        int i15 = 7;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 56;
        int i17 = 11;
        int i18 = 19;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 6;
        int i20 = classa0.getSum(i19, i9);
        Assert.assertTrue(ClassA.validateGetSum(i19, i9, i20));
    }

    @Test
    public void test48() throws Throwable {
        int i0 = 13;
        ClassA classa0 = new ClassA(i0);
        int i1 = 41;
        int i2 = 94;
        int i3 = 94;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = classa0.getSum(i2, i0);
        Assert.assertTrue(ClassA.validateGetSum(i2, i0, i4));
    }

    @Test
    public void test49() throws Throwable {
        int i0 = 90;
        ClassA classa0 = new ClassA(i0);
        int i1 = 63;
        int i2 = 54;
        int i3 = 32;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 14;
        int i5 = 88;
        int i6 = 60;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 70;
        int i8 = 78;
        int i9 = 94;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 89;
        int i11 = 30;
        int i12 = 76;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 45;
        int i14 = 32;
        int i15 = 39;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 95;
        int i17 = 8;
        int i18 = 53;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 20;
        int i20 = 4;
        int i21 = 99;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 85;
        int i23 = 36;
        int i24 = 40;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 71;
        int i26 = 87;
        int i27 = 69;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 58;
        int i29 = 54;
        int i30 = 12;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = classa0.getSum(i11, i9);
        Assert.assertTrue(ClassA.validateGetSum(i11, i9, i31));
    }

    @Test
    public void test50() throws Throwable {
        int i0 = 20;
        ClassA classa0 = new ClassA(i0);
        int i1 = 36;
        int i2 = 15;
        int i3 = 47;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 10;
        int i5 = 62;
        int i6 = 7;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 21;
        int i8 = 88;
        int i9 = 100;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 57;
        int i11 = 69;
        int i12 = 17;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 5;
        int i14 = 31;
        int i15 = 11;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 30;
        int i17 = 100;
        int i18 = 68;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 94;
        int i20 = 86;
        int i21 = 22;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 36;
        int i23 = classa0.getSum(i22, i20);
        Assert.assertTrue(ClassA.validateGetSum(i22, i20, i23));
    }

    @Test
    public void test51() throws Throwable {
        int i0 = 83;
        ClassA classa0 = new ClassA(i0);
        int i1 = 7;
        int i2 = 55;
        int i3 = 7;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 71;
        int i5 = 51;
        int i6 = 1;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 25;
        int i8 = 30;
        int i9 = 69;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 61;
        int i11 = 96;
        int i12 = 4;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 35;
        int i14 = 68;
        int i15 = 39;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 28;
        int i17 = 99;
        int i18 = 54;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 96;
        int i20 = 72;
        int i21 = 51;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 86;
        int i23 = 96;
        int i24 = 12;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 59;
        int i26 = 25;
        int i27 = 14;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 83;
        int i29 = 74;
        int i30 = 43;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = classa0.getSum(i5, i21);
        Assert.assertTrue(ClassA.validateGetSum(i5, i21, i31));
    }

    @Test
    public void test52() throws Throwable {
        int i0 = 88;
        ClassA classa0 = new ClassA(i0);
        int i1 = 63;
        int i2 = 49;
        int i3 = 79;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 17;
        int i5 = 35;
        int i6 = 72;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 43;
        int i8 = 66;
        int i9 = 90;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 78;
        int i11 = 21;
        int i12 = 51;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 7;
        int i14 = 67;
        int i15 = 45;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 8;
        int i17 = 76;
        int i18 = 64;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 36;
        int i20 = 16;
        int i21 = 89;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 65;
        int i23 = 92;
        int i24 = 11;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 67;
        int i26 = 22;
        int i27 = 60;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 73;
        int i29 = classa0.getSum(i17, i28);
        Assert.assertTrue(ClassA.validateGetSum(i17, i28, i29));
    }

    @Test
    public void test53() throws Throwable {
        int i0 = 33;
        ClassA classa0 = new ClassA(i0);
        int i1 = 73;
        int i2 = 18;
        int i3 = 38;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 48;
        int i5 = 5;
        int i6 = 99;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 85;
        int i8 = 17;
        int i9 = 60;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = classa0.getSum(i2, i1);
        Assert.assertTrue(ClassA.validateGetSum(i2, i1, i10));
    }

    @Test
    public void test54() throws Throwable {
        int i0 = 99;
        ClassA classa0 = new ClassA(i0);
        int i1 = 34;
        int i2 = 25;
        int i3 = 59;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 12;
        int i5 = 63;
        int i6 = 61;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 90;
        int i8 = 80;
        int i9 = 16;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 1;
        int i11 = 58;
        int i12 = 73;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 42;
        int i14 = 66;
        int i15 = 30;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 69;
        int i17 = 69;
        int i18 = 8;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 26;
        int i20 = 89;
        int i21 = 47;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 49;
        int i23 = 94;
        int i24 = 76;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = classa0.getSum(i2, i11);
        Assert.assertTrue(ClassA.validateGetSum(i2, i11, i25));
    }

    @Test
    public void test55() throws Throwable {
        int i0 = 98;
        ClassA classa0 = new ClassA(i0);
        int i1 = 28;
        int i2 = 99;
        int i3 = 3;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 47;
        int i5 = 27;
        int i6 = 99;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 2;
        int i8 = 94;
        int i9 = 89;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 86;
        int i11 = classa0.getSum(i9, i10);
        Assert.assertTrue(ClassA.validateGetSum(i9, i10, i11));
    }

    @Test
    public void test56() throws Throwable {
        int i0 = 13;
        ClassA classa0 = new ClassA(i0);
        int i1 = 10;
        int i2 = 100;
        int i3 = 33;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 30;
        int i5 = 83;
        int i6 = 91;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 96;
        int i8 = 76;
        int i9 = 91;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 22;
        int i11 = 91;
        int i12 = 83;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = classa0.getSum(i10, i1);
        Assert.assertTrue(ClassA.validateGetSum(i10, i1, i13));
    }

    @Test
    public void test57() throws Throwable {
        int i0 = 34;
        ClassA classa0 = new ClassA(i0);
        int i1 = 74;
        int i2 = 98;
        int i3 = 58;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 21;
        int i5 = 28;
        int i6 = 37;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 75;
        int i8 = 23;
        int i9 = 9;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 94;
        int i11 = 79;
        int i12 = 28;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 34;
        int i14 = 25;
        int i15 = 26;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 25;
        int i17 = 42;
        int i18 = classa0.getSum(i16, i17);
        Assert.assertTrue(ClassA.validateGetSum(i16, i17, i18));
    }

    @Test
    public void test58() throws Throwable {
        int i0 = 39;
        ClassA classa0 = new ClassA(i0);
        int i1 = 41;
        int i2 = 35;
        int i3 = 88;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 63;
        int i5 = 25;
        int i6 = 60;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 13;
        int i8 = 55;
        int i9 = 91;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 92;
        int i11 = 28;
        int i12 = 78;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 86;
        int i14 = classa0.getSum(i7, i13);
        Assert.assertTrue(ClassA.validateGetSum(i7, i13, i14));
    }

    @Test
    public void test59() throws Throwable {
        int i0 = 66;
        ClassA classa0 = new ClassA(i0);
        int i1 = 30;
        int i2 = 80;
        int i3 = 6;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 76;
        int i5 = 59;
        int i6 = 24;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 1;
        int i8 = 9;
        int i9 = 29;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 100;
        int i11 = 72;
        int i12 = 91;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 80;
        int i14 = 55;
        int i15 = 22;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = classa0.getSum(i9, i1);
        Assert.assertTrue(ClassA.validateGetSum(i9, i1, i16));
    }

    @Test
    public void test60() throws Throwable {
        int i0 = 54;
        ClassA classa0 = new ClassA(i0);
        int i1 = 19;
        int i2 = 85;
        int i3 = 63;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 29;
        int i5 = 38;
        int i6 = 52;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 14;
        int i8 = 77;
        int i9 = 59;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 15;
        int i11 = 80;
        int i12 = 53;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 22;
        int i14 = 86;
        int i15 = 78;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 11;
        int i17 = 87;
        int i18 = classa0.getSum(i16, i17);
        Assert.assertTrue(ClassA.validateGetSum(i16, i17, i18));
    }

    @Test
    public void test61() throws Throwable {
        int i0 = 95;
        ClassA classa0 = new ClassA(i0);
        int i1 = 49;
        int i2 = 50;
        int i3 = 64;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 37;
        int i5 = 12;
        int i6 = 71;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 15;
        int i8 = classa0.getSum(i5, i7);
        Assert.assertTrue(ClassA.validateGetSum(i5, i7, i8));
    }

    @Test
    public void test62() throws Throwable {
        int i0 = 51;
        ClassA classa0 = new ClassA(i0);
        int i1 = 22;
        int i2 = 37;
        int i3 = 8;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 76;
        int i5 = 0;
        int i6 = 91;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 2;
        int i8 = 35;
        int i9 = 98;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 86;
        int i11 = 30;
        int i12 = 52;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 68;
        int i14 = 40;
        int i15 = 46;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 53;
        int i17 = 65;
        int i18 = 85;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 41;
        int i20 = classa0.getSum(i0, i19);
        Assert.assertTrue(ClassA.validateGetSum(i0, i19, i20));
    }

    @Test
    public void test63() throws Throwable {
        int i0 = 89;
        ClassA classa0 = new ClassA(i0);
        int i1 = 85;
        int i2 = 57;
        int i3 = 16;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 88;
        int i5 = 38;
        int i6 = 77;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 68;
        int i8 = 50;
        int i9 = 95;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 92;
        int i11 = 51;
        int i12 = classa0.getSum(i10, i11);
        Assert.assertTrue(ClassA.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test64() throws Throwable {
        int i0 = 21;
        ClassA classa0 = new ClassA(i0);
        int i1 = 42;
        int i2 = 54;
        int i3 = 21;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 97;
        int i5 = 88;
        int i6 = 48;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = classa0.getSum(i2, i1);
        Assert.assertTrue(ClassA.validateGetSum(i2, i1, i7));
    }

    @Test
    public void test65() throws Throwable {
        int i0 = 89;
        ClassA classa0 = new ClassA(i0);
        int i1 = 0;
        int i2 = 85;
        int i3 = 10;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 5;
        int i5 = 13;
        int i6 = 52;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 73;
        int i8 = 71;
        int i9 = 11;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 19;
        int i11 = 17;
        int i12 = 13;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 60;
        int i14 = 58;
        int i15 = 0;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 16;
        int i17 = 17;
        int i18 = 62;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 85;
        int i20 = 7;
        int i21 = 85;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 82;
        int i23 = 40;
        int i24 = 93;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 45;
        int i26 = 59;
        int i27 = 68;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 67;
        int i29 = 92;
        int i30 = 86;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 8;
        int i32 = classa0.getSum(i31, i26);
        Assert.assertTrue(ClassA.validateGetSum(i31, i26, i32));
    }

    @Test
    public void test66() throws Throwable {
        int i0 = 23;
        ClassA classa0 = new ClassA(i0);
        int i1 = 58;
        int i2 = 56;
        int i3 = 95;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 46;
        int i5 = 56;
        int i6 = 33;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 36;
        int i8 = 8;
        int i9 = 79;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 63;
        int i11 = 19;
        int i12 = 40;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 24;
        int i14 = 71;
        int i15 = 59;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 26;
        int i17 = 17;
        int i18 = 22;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 55;
        int i20 = 20;
        int i21 = 46;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 57;
        int i23 = 75;
        int i24 = 1;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 78;
        int i26 = 27;
        int i27 = 92;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 87;
        int i29 = 57;
        int i30 = 26;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 38;
        int i32 = 96;
        int i33 = classa0.getSum(i31, i32);
        Assert.assertTrue(ClassA.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test67() throws Throwable {
        int i0 = 74;
        ClassA classa0 = new ClassA(i0);
        int i1 = 84;
        int i2 = 10;
        int i3 = 12;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 27;
        int i5 = 74;
        int i6 = 39;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 63;
        int i8 = 24;
        int i9 = 25;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = classa0.getSum(i2, i1);
        Assert.assertTrue(ClassA.validateGetSum(i2, i1, i10));
    }

    @Test
    public void test68() throws Throwable {
        int i0 = 38;
        ClassA classa0 = new ClassA(i0);
        int i1 = 9;
        int i2 = 93;
        int i3 = 49;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 39;
        int i5 = 68;
        int i6 = 78;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 67;
        int i8 = 22;
        int i9 = 30;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 11;
        int i11 = 66;
        int i12 = 50;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 90;
        int i14 = 88;
        int i15 = 63;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 9;
        int i17 = classa0.getSum(i16, i4);
        Assert.assertTrue(ClassA.validateGetSum(i16, i4, i17));
    }

    @Test
    public void test69() throws Throwable {
        int i0 = 96;
        ClassA classa0 = new ClassA(i0);
        int i1 = 7;
        int i2 = 46;
        int i3 = 39;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 90;
        int i5 = 79;
        int i6 = 52;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 80;
        int i8 = 87;
        int i9 = 82;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 80;
        int i11 = 17;
        int i12 = 26;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 13;
        int i14 = 81;
        int i15 = 2;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = classa0.getSum(i13, i13);
        Assert.assertTrue(ClassA.validateGetSum(i13, i13, i16));
    }

    @Test
    public void test70() throws Throwable {
        int i0 = 22;
        ClassA classa0 = new ClassA(i0);
        int i1 = 28;
        int i2 = 36;
        int i3 = 92;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 70;
        int i5 = classa0.getSum(i3, i4);
        Assert.assertTrue(ClassA.validateGetSum(i3, i4, i5));
    }

    @Test
    public void test71() throws Throwable {
        int i0 = 15;
        ClassA classa0 = new ClassA(i0);
        int i1 = 50;
        int i2 = 70;
        int i3 = 10;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 35;
        int i5 = classa0.getSum(i2, i4);
        Assert.assertTrue(ClassA.validateGetSum(i2, i4, i5));
    }

    @Test
    public void test72() throws Throwable {
        int i0 = 6;
        ClassA classa0 = new ClassA(i0);
        int i1 = classa0.getSum(i0, i0);
        Assert.assertTrue(ClassA.validateGetSum(i0, i0, i1));
    }

    @Test
    public void test73() throws Throwable {
        int i0 = 49;
        ClassA classa0 = new ClassA(i0);
        int i1 = 31;
        int i2 = 58;
        int i3 = 6;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 85;
        int i5 = 79;
        int i6 = 44;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = classa0.getSum(i3, i6);
        Assert.assertTrue(ClassA.validateGetSum(i3, i6, i7));
    }

    @Test
    public void test74() throws Throwable {
        int i0 = 68;
        ClassA classa0 = new ClassA(i0);
        int i1 = 24;
        int i2 = 82;
        int i3 = 9;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 94;
        int i5 = 85;
        int i6 = 96;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 19;
        int i8 = 48;
        int i9 = 38;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 77;
        int i11 = 66;
        int i12 = 26;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 93;
        int i14 = 85;
        int i15 = 87;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 12;
        int i17 = 67;
        int i18 = 80;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 75;
        int i20 = 45;
        int i21 = 88;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 7;
        int i23 = 54;
        int i24 = 31;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 98;
        int i26 = 62;
        int i27 = 80;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 28;
        int i29 = 50;
        int i30 = 21;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 89;
        int i32 = 93;
        int i33 = classa0.getSum(i31, i32);
        Assert.assertTrue(ClassA.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test75() throws Throwable {
        int i0 = 71;
        ClassA classa0 = new ClassA(i0);
        int i1 = 99;
        int i2 = 53;
        int i3 = 42;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 77;
        int i5 = 46;
        int i6 = 90;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 50;
        int i8 = 94;
        int i9 = 72;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 12;
        int i11 = 13;
        int i12 = 44;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 39;
        int i14 = 23;
        int i15 = 29;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 7;
        int i17 = 28;
        int i18 = 42;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 51;
        int i20 = 73;
        int i21 = classa0.getSum(i19, i20);
        Assert.assertTrue(ClassA.validateGetSum(i19, i20, i21));
    }

    @Test
    public void test76() throws Throwable {
        int i0 = 9;
        ClassA classa0 = new ClassA(i0);
        int i1 = 16;
        int i2 = 47;
        int i3 = 73;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 54;
        int i5 = 19;
        int i6 = 44;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 2;
        int i8 = 27;
        int i9 = 23;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 55;
        int i11 = 68;
        int i12 = classa0.getSum(i10, i11);
        Assert.assertTrue(ClassA.validateGetSum(i10, i11, i12));
    }

    @Test
    public void test77() throws Throwable {
        int i0 = 91;
        ClassA classa0 = new ClassA(i0);
        int i1 = 1;
        int i2 = 20;
        int i3 = 40;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 95;
        int i5 = 25;
        int i6 = 8;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 12;
        int i8 = 56;
        int i9 = 50;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 56;
        int i11 = 38;
        int i12 = 82;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 99;
        int i14 = 83;
        int i15 = 50;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 73;
        int i17 = 93;
        int i18 = 52;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 38;
        int i20 = 15;
        int i21 = 65;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 60;
        int i23 = 10;
        int i24 = 52;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 95;
        int i26 = 13;
        int i27 = 83;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 69;
        int i29 = 2;
        int i30 = 98;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = classa0.getSum(i12, i9);
        Assert.assertTrue(ClassA.validateGetSum(i12, i9, i31));
    }

    @Test
    public void test78() throws Throwable {
        int i0 = 11;
        ClassA classa0 = new ClassA(i0);
        int i1 = 36;
        int i2 = classa0.getSum(i1, i0);
        Assert.assertTrue(ClassA.validateGetSum(i1, i0, i2));
    }

    @Test
    public void test79() throws Throwable {
        int i0 = 86;
        ClassA classa0 = new ClassA(i0);
        int i1 = 34;
        int i2 = 36;
        int i3 = 42;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 78;
        int i5 = 20;
        int i6 = 9;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 14;
        int i8 = 93;
        int i9 = 97;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 10;
        int i11 = 55;
        int i12 = 77;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 38;
        int i14 = 94;
        int i15 = 22;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 35;
        int i17 = classa0.getSum(i16, i14);
        Assert.assertTrue(ClassA.validateGetSum(i16, i14, i17));
    }

    @Test
    public void test80() throws Throwable {
        int i0 = 49;
        ClassA classa0 = new ClassA(i0);
        int i1 = 45;
        int i2 = 56;
        int i3 = 64;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 77;
        int i5 = 53;
        int i6 = 95;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 68;
        int i8 = 51;
        int i9 = 34;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 15;
        int i11 = 71;
        int i12 = 98;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 54;
        int i14 = 66;
        int i15 = 75;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 92;
        int i17 = classa0.getSum(i16, i4);
        Assert.assertTrue(ClassA.validateGetSum(i16, i4, i17));
    }

    @Test
    public void test81() throws Throwable {
        int i0 = 84;
        ClassA classa0 = new ClassA(i0);
        int i1 = 27;
        int i2 = 57;
        int i3 = 96;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 50;
        int i5 = 33;
        int i6 = 69;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 77;
        int i8 = 12;
        int i9 = 98;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 21;
        int i11 = 46;
        int i12 = 62;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 11;
        int i14 = classa0.getSum(i2, i13);
        Assert.assertTrue(ClassA.validateGetSum(i2, i13, i14));
    }

    @Test
    public void test82() throws Throwable {
        int i0 = 53;
        ClassA classa0 = new ClassA(i0);
        int i1 = 64;
        int i2 = 36;
        int i3 = 2;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 17;
        int i5 = 56;
        int i6 = 78;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 98;
        int i8 = 96;
        int i9 = 46;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 62;
        int i11 = 79;
        int i12 = 79;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 31;
        int i14 = 41;
        int i15 = 8;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 88;
        int i17 = 60;
        int i18 = 27;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = classa0.getSum(i15, i17);
        Assert.assertTrue(ClassA.validateGetSum(i15, i17, i19));
    }

    @Test
    public void test83() throws Throwable {
        int i0 = 6;
        ClassA classa0 = new ClassA(i0);
        int i1 = 32;
        int i2 = 73;
        int i3 = 100;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 18;
        int i5 = 51;
        int i6 = 6;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 51;
        int i8 = 92;
        int i9 = 18;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 100;
        int i11 = 51;
        int i12 = 66;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 78;
        int i14 = 67;
        int i15 = classa0.getSum(i13, i14);
        Assert.assertTrue(ClassA.validateGetSum(i13, i14, i15));
    }

    @Test
    public void test84() throws Throwable {
        int i0 = 56;
        ClassA classa0 = new ClassA(i0);
        int i1 = 64;
        int i2 = 39;
        int i3 = 95;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 93;
        int i5 = 38;
        int i6 = 100;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 10;
        int i8 = 14;
        int i9 = 97;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 47;
        int i11 = 67;
        int i12 = 63;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 4;
        int i14 = 55;
        int i15 = 87;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 36;
        int i17 = classa0.getSum(i16, i7);
        Assert.assertTrue(ClassA.validateGetSum(i16, i7, i17));
    }

    @Test
    public void test85() throws Throwable {
        int i0 = 43;
        ClassA classa0 = new ClassA(i0);
        int i1 = 40;
        int i2 = 98;
        int i3 = 80;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 71;
        int i5 = 8;
        int i6 = 81;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = classa0.getSum(i5, i3);
        Assert.assertTrue(ClassA.validateGetSum(i5, i3, i7));
    }

    @Test
    public void test86() throws Throwable {
        int i0 = 94;
        ClassA classa0 = new ClassA(i0);
        int i1 = 50;
        int i2 = 37;
        int i3 = 22;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 58;
        int i5 = 84;
        int i6 = 44;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 60;
        int i8 = 70;
        int i9 = 19;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 91;
        int i11 = 25;
        int i12 = 58;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 38;
        int i14 = 65;
        int i15 = 19;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 94;
        int i17 = 3;
        int i18 = 25;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 8;
        int i20 = 40;
        int i21 = 43;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 22;
        int i23 = classa0.getSum(i22, i2);
        Assert.assertTrue(ClassA.validateGetSum(i22, i2, i23));
    }

    @Test
    public void test87() throws Throwable {
        int i0 = 82;
        ClassA classa0 = new ClassA(i0);
        int i1 = 63;
        int i2 = 68;
        int i3 = 93;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 29;
        int i5 = 49;
        int i6 = 36;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 98;
        int i8 = 82;
        int i9 = 67;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 30;
        int i11 = 95;
        int i12 = 74;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 13;
        int i14 = 83;
        int i15 = 83;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 98;
        int i17 = classa0.getSum(i7, i16);
        Assert.assertTrue(ClassA.validateGetSum(i7, i16, i17));
    }

    @Test
    public void test88() throws Throwable {
        int i0 = 65;
        ClassA classa0 = new ClassA(i0);
        int i1 = 67;
        int i2 = 28;
        int i3 = classa0.getSum(i1, i2);
        Assert.assertTrue(ClassA.validateGetSum(i1, i2, i3));
    }

    @Test
    public void test89() throws Throwable {
        int i0 = 8;
        ClassA classa0 = new ClassA(i0);
        int i1 = 13;
        int i2 = 24;
        int i3 = 69;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 93;
        int i5 = 84;
        int i6 = 64;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 11;
        int i8 = 81;
        int i9 = 43;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 62;
        int i11 = 16;
        int i12 = 34;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 0;
        int i14 = 23;
        int i15 = 83;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 89;
        int i17 = 95;
        int i18 = 83;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 24;
        int i20 = 34;
        int i21 = 75;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 14;
        int i23 = 93;
        int i24 = 35;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 70;
        int i26 = 78;
        int i27 = 57;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = classa0.getSum(i9, i25);
        Assert.assertTrue(ClassA.validateGetSum(i9, i25, i28));
    }

    @Test
    public void test90() throws Throwable {
        int i0 = 78;
        ClassA classa0 = new ClassA(i0);
        int i1 = 23;
        int i2 = 96;
        int i3 = 17;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 86;
        int i5 = 52;
        int i6 = 34;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 8;
        int i8 = 96;
        int i9 = 86;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 22;
        int i11 = classa0.getSum(i2, i10);
        Assert.assertTrue(ClassA.validateGetSum(i2, i10, i11));
    }

    @Test
    public void test91() throws Throwable {
        int i0 = 32;
        ClassA classa0 = new ClassA(i0);
        int i1 = 17;
        int i2 = 2;
        int i3 = 15;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 20;
        int i5 = 19;
        int i6 = 52;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 24;
        int i8 = 92;
        int i9 = 14;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 43;
        int i11 = 12;
        int i12 = 90;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 24;
        int i14 = 58;
        int i15 = 38;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 54;
        int i17 = 64;
        int i18 = 47;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 35;
        int i20 = 100;
        int i21 = 92;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 79;
        int i23 = 95;
        int i24 = 90;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 99;
        int i26 = 1;
        int i27 = classa0.getSum(i25, i26);
        Assert.assertTrue(ClassA.validateGetSum(i25, i26, i27));
    }

    @Test
    public void test92() throws Throwable {
        int i0 = 50;
        ClassA classa0 = new ClassA(i0);
        int i1 = 74;
        int i2 = 46;
        int i3 = 72;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 71;
        int i5 = 14;
        int i6 = 43;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 61;
        int i8 = 80;
        int i9 = 28;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 86;
        int i11 = 47;
        int i12 = 87;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 52;
        int i14 = 28;
        int i15 = 17;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 94;
        int i17 = 100;
        int i18 = 38;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 23;
        int i20 = 76;
        int i21 = 94;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 42;
        int i23 = 10;
        int i24 = 15;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = classa0.getSum(i7, i5);
        Assert.assertTrue(ClassA.validateGetSum(i7, i5, i25));
    }

    @Test
    public void test93() throws Throwable {
        int i0 = 64;
        ClassA classa0 = new ClassA(i0);
        int i1 = 25;
        int i2 = 80;
        int i3 = 89;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 25;
        int i5 = classa0.getSum(i0, i4);
        Assert.assertTrue(ClassA.validateGetSum(i0, i4, i5));
    }

    @Test
    public void test94() throws Throwable {
        int i0 = 9;
        ClassA classa0 = new ClassA(i0);
        int i1 = 82;
        int i2 = 56;
        int i3 = 76;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 67;
        int i5 = 70;
        int i6 = 77;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 92;
        int i8 = 10;
        int i9 = 78;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 0;
        int i11 = 9;
        int i12 = 28;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 27;
        int i14 = 19;
        int i15 = 58;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 100;
        int i17 = 8;
        int i18 = 8;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 20;
        int i20 = 40;
        int i21 = 48;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = classa0.getSum(i17, i11);
        Assert.assertTrue(ClassA.validateGetSum(i17, i11, i22));
    }

    @Test
    public void test95() throws Throwable {
        int i0 = 68;
        ClassA classa0 = new ClassA(i0);
        int i1 = 89;
        int i2 = 88;
        int i3 = 27;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 11;
        int i5 = 49;
        int i6 = 14;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 69;
        int i8 = 48;
        int i9 = 93;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 96;
        int i11 = 33;
        int i12 = 81;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = classa0.getSum(i2, i8);
        Assert.assertTrue(ClassA.validateGetSum(i2, i8, i13));
    }

    @Test
    public void test96() throws Throwable {
        int i0 = 30;
        ClassA classa0 = new ClassA(i0);
        int i1 = 48;
        int i2 = 94;
        int i3 = 74;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 45;
        int i5 = 23;
        int i6 = 45;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 75;
        int i8 = 43;
        int i9 = 28;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 8;
        int i11 = 81;
        int i12 = 80;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 33;
        int i14 = 49;
        int i15 = 31;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 59;
        int i17 = 93;
        int i18 = 83;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 43;
        int i20 = 88;
        int i21 = 13;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 60;
        int i23 = 14;
        int i24 = 78;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 67;
        int i26 = 95;
        int i27 = 61;
        classa0.validateGetSum(i25, i26, i27);
        int i28 = 10;
        int i29 = 39;
        int i30 = 98;
        classa0.validateGetSum(i28, i29, i30);
        int i31 = 3;
        int i32 = 88;
        int i33 = classa0.getSum(i31, i32);
        Assert.assertTrue(ClassA.validateGetSum(i31, i32, i33));
    }

    @Test
    public void test97() throws Throwable {
        int i0 = 6;
        ClassA classa0 = new ClassA(i0);
        int i1 = 81;
        int i2 = 10;
        int i3 = 16;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 45;
        int i5 = 49;
        int i6 = 83;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 67;
        int i8 = 12;
        int i9 = 29;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 24;
        int i11 = 71;
        int i12 = 56;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 57;
        int i14 = classa0.getSum(i11, i13);
        Assert.assertTrue(ClassA.validateGetSum(i11, i13, i14));
    }

    @Test
    public void test98() throws Throwable {
        int i0 = 4;
        ClassA classa0 = new ClassA(i0);
        int i1 = 100;
        int i2 = 9;
        int i3 = 32;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 36;
        int i5 = 22;
        int i6 = 84;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 65;
        int i8 = 55;
        int i9 = 76;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 51;
        int i11 = 86;
        int i12 = 17;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 87;
        int i14 = classa0.getSum(i13, i9);
        Assert.assertTrue(ClassA.validateGetSum(i13, i9, i14));
    }

    @Test
    public void test99() throws Throwable {
        int i0 = 86;
        ClassA classa0 = new ClassA(i0);
        int i1 = 47;
        int i2 = 14;
        int i3 = 92;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 86;
        int i5 = 29;
        int i6 = 2;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 92;
        int i8 = 78;
        int i9 = 59;
        classa0.validateGetSum(i7, i8, i9);
        int i10 = 53;
        int i11 = 94;
        int i12 = 76;
        classa0.validateGetSum(i10, i11, i12);
        int i13 = 38;
        int i14 = 58;
        int i15 = 100;
        classa0.validateGetSum(i13, i14, i15);
        int i16 = 47;
        int i17 = 4;
        int i18 = 60;
        classa0.validateGetSum(i16, i17, i18);
        int i19 = 100;
        int i20 = 6;
        int i21 = 69;
        classa0.validateGetSum(i19, i20, i21);
        int i22 = 48;
        int i23 = 81;
        int i24 = 24;
        classa0.validateGetSum(i22, i23, i24);
        int i25 = 86;
        int i26 = classa0.getSum(i20, i25);
        Assert.assertTrue(ClassA.validateGetSum(i20, i25, i26));
    }

    @Test
    public void test100() throws Throwable {
        int i0 = 58;
        ClassA classa0 = new ClassA(i0);
        int i1 = 77;
        int i2 = 18;
        int i3 = 95;
        classa0.validateGetSum(i1, i2, i3);
        int i4 = 96;
        int i5 = 97;
        int i6 = 92;
        classa0.validateGetSum(i4, i5, i6);
        int i7 = 22;
        int i8 = classa0.getSum(i7, i4);
        Assert.assertTrue(ClassA.validateGetSum(i7, i4, i8));
    }
}

