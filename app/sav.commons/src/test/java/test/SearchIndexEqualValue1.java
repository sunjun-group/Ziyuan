package test;

import sav.commons.testdata.search1.SearchIndexEqualValue;
import org.junit.Test;
import org.junit.Assert;

public class SearchIndexEqualValue1 {

    @Test
    public void test1() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 14;
        arr0[0] = i0;
        int i1 = 95;
        arr0[1] = i1;
        int i2 = 41;
        arr0[2] = i2;
        int i3 = 0;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test2() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 40;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test3() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test4() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 61;
        arr0[0] = i0;
        int i1 = 85;
        arr0[1] = i1;
        int i2 = 19;
        arr0[2] = i2;
        int i3 = 45;
        arr0[3] = i3;
        int i4 = 36;
        arr0[4] = i4;
        int i5 = 21;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test5() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 3;
        arr0[0] = i0;
        int i1 = 88;
        arr0[1] = i1;
        int i2 = 53;
        arr0[2] = i2;
        int i3 = 20;
        arr0[3] = i3;
        int i4 = 97;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test6() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 70;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test7() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 90;
        arr0[0] = i0;
        int i1 = 47;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test8() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 60;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test9() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 5;
        arr0[0] = i0;
        int i1 = 50;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test10() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 4;
        arr0[0] = i0;
        int i1 = 3;
        arr0[1] = i1;
        int i2 = 43;
        arr0[2] = i2;
        int i3 = 50;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test11() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 4;
        arr0[0] = i0;
        int i1 = 91;
        arr0[1] = i1;
        int i2 = 94;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test12() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 56;
        arr0[0] = i0;
        int i1 = 52;
        arr0[1] = i1;
        int i2 = 72;
        arr0[2] = i2;
        int i3 = 84;
        arr0[3] = i3;
        int i4 = 66;
        arr0[4] = i4;
        int i5 = 24;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test13() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 41;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test14() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 46;
        arr0[0] = i0;
        int i1 = 53;
        arr0[1] = i1;
        int i2 = 52;
        arr0[2] = i2;
        int i3 = 59;
        arr0[3] = i3;
        int i4 = 25;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test15() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 2;
        arr0[0] = i0;
        int i1 = 81;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test16() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 3;
        arr0[0] = i0;
        int i1 = 17;
        arr0[1] = i1;
        int i2 = 30;
        arr0[2] = i2;
        int i3 = 39;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test17() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 51;
        arr0[0] = i0;
        int i1 = 80;
        arr0[1] = i1;
        int i2 = 66;
        arr0[2] = i2;
        int i3 = 82;
        arr0[3] = i3;
        int i4 = 81;
        arr0[4] = i4;
        int i5 = 17;
        arr0[5] = i5;
        int i6 = 60;
        arr0[6] = i6;
        int i7 = 74;
        arr0[7] = i7;
        int i8 = 91;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test18() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 60;
        arr0[0] = i0;
        int i1 = 35;
        arr0[1] = i1;
        int i2 = 57;
        arr0[2] = i2;
        int i3 = 32;
        arr0[3] = i3;
        int i4 = 64;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test19() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test20() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 62;
        arr0[0] = i0;
        int i1 = 57;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test21() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 67;
        arr0[0] = i0;
        int i1 = 33;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test22() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 83;
        arr0[0] = i0;
        int i1 = 90;
        arr0[1] = i1;
        int i2 = 3;
        arr0[2] = i2;
        int i3 = 11;
        arr0[3] = i3;
        int i4 = 3;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test23() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 51;
        arr0[0] = i0;
        int i1 = 72;
        arr0[1] = i1;
        int i2 = 84;
        arr0[2] = i2;
        int i3 = 42;
        arr0[3] = i3;
        int i4 = 67;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test24() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 39;
        arr0[0] = i0;
        int i1 = 30;
        arr0[1] = i1;
        int i2 = 48;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test25() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test26() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 82;
        arr0[0] = i0;
        int i1 = 55;
        arr0[1] = i1;
        int i2 = 73;
        arr0[2] = i2;
        int i3 = 76;
        arr0[3] = i3;
        int i4 = 7;
        arr0[4] = i4;
        int i5 = 76;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test27() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 46;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test28() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 2;
        arr0[0] = i0;
        int i1 = 9;
        arr0[1] = i1;
        int i2 = 21;
        arr0[2] = i2;
        int i3 = 65;
        arr0[3] = i3;
        int i4 = 20;
        arr0[4] = i4;
        int i5 = 77;
        arr0[5] = i5;
        int i6 = 93;
        arr0[6] = i6;
        int i7 = 56;
        arr0[7] = i7;
        int i8 = 62;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test29() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 8;
        arr0[0] = i0;
        int i1 = 55;
        arr0[1] = i1;
        int i2 = 28;
        arr0[2] = i2;
        int i3 = 88;
        arr0[3] = i3;
        int i4 = 23;
        arr0[4] = i4;
        int i5 = 56;
        arr0[5] = i5;
        int i6 = 39;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test30() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 63;
        arr0[0] = i0;
        int i1 = 15;
        arr0[1] = i1;
        int i2 = 69;
        arr0[2] = i2;
        int i3 = 38;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test31() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 7;
        arr0[0] = i0;
        int i1 = 49;
        arr0[1] = i1;
        int i2 = 58;
        arr0[2] = i2;
        int i3 = 60;
        arr0[3] = i3;
        int i4 = 36;
        arr0[4] = i4;
        int i5 = 42;
        arr0[5] = i5;
        int i6 = 32;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test32() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 49;
        arr0[0] = i0;
        int i1 = 77;
        arr0[1] = i1;
        int i2 = 1;
        arr0[2] = i2;
        int i3 = 92;
        arr0[3] = i3;
        int i4 = 28;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test33() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 5;
        arr0[0] = i0;
        int i1 = 10;
        arr0[1] = i1;
        int i2 = 55;
        arr0[2] = i2;
        int i3 = 85;
        arr0[3] = i3;
        int i4 = 24;
        arr0[4] = i4;
        int i5 = 73;
        arr0[5] = i5;
        int i6 = 46;
        arr0[6] = i6;
        int i7 = 16;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test34() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 29;
        arr0[0] = i0;
        int i1 = 88;
        arr0[1] = i1;
        int i2 = 73;
        arr0[2] = i2;
        int i3 = 91;
        arr0[3] = i3;
        int i4 = 52;
        arr0[4] = i4;
        int i5 = 23;
        arr0[5] = i5;
        int i6 = 66;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test35() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 75;
        arr0[0] = i0;
        int i1 = 51;
        arr0[1] = i1;
        int i2 = 65;
        arr0[2] = i2;
        int i3 = 33;
        arr0[3] = i3;
        int i4 = 67;
        arr0[4] = i4;
        int i5 = 80;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test36() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test37() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 77;
        arr0[0] = i0;
        int i1 = 74;
        arr0[1] = i1;
        int i2 = 26;
        arr0[2] = i2;
        int i3 = 84;
        arr0[3] = i3;
        int i4 = 82;
        arr0[4] = i4;
        int i5 = 82;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test38() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 12;
        arr0[0] = i0;
        int i1 = 73;
        arr0[1] = i1;
        int i2 = 10;
        arr0[2] = i2;
        int i3 = 0;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test39() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 63;
        arr0[0] = i0;
        int i1 = 50;
        arr0[1] = i1;
        int i2 = 50;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test40() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 3;
        arr0[0] = i0;
        int i1 = 93;
        arr0[1] = i1;
        int i2 = 23;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test41() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 58;
        arr0[0] = i0;
        int i1 = 60;
        arr0[1] = i1;
        int i2 = 99;
        arr0[2] = i2;
        int i3 = 7;
        arr0[3] = i3;
        int i4 = 23;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test42() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 57;
        arr0[0] = i0;
        int i1 = 5;
        arr0[1] = i1;
        int i2 = 38;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test43() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 85;
        arr0[0] = i0;
        int i1 = 27;
        arr0[1] = i1;
        int i2 = 28;
        arr0[2] = i2;
        int i3 = 79;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test44() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 22;
        arr0[0] = i0;
        int i1 = 39;
        arr0[1] = i1;
        int i2 = 22;
        arr0[2] = i2;
        int i3 = 13;
        arr0[3] = i3;
        int i4 = 49;
        arr0[4] = i4;
        int i5 = 74;
        arr0[5] = i5;
        int i6 = 5;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test45() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 50;
        arr0[0] = i0;
        int i1 = 39;
        arr0[1] = i1;
        int i2 = 7;
        arr0[2] = i2;
        int i3 = 79;
        arr0[3] = i3;
        int i4 = 93;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test46() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 84;
        arr0[0] = i0;
        int i1 = 83;
        arr0[1] = i1;
        int i2 = 45;
        arr0[2] = i2;
        int i3 = 78;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test47() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 38;
        arr0[0] = i0;
        int i1 = 23;
        arr0[1] = i1;
        int i2 = 71;
        arr0[2] = i2;
        int i3 = 77;
        arr0[3] = i3;
        int i4 = 40;
        arr0[4] = i4;
        int i5 = 32;
        arr0[5] = i5;
        int i6 = 54;
        arr0[6] = i6;
        int i7 = 27;
        arr0[7] = i7;
        int i8 = 2;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test48() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 48;
        arr0[0] = i0;
        int i1 = 42;
        arr0[1] = i1;
        int i2 = 30;
        arr0[2] = i2;
        int i3 = 65;
        arr0[3] = i3;
        int i4 = 82;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test49() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 56;
        arr0[0] = i0;
        int i1 = 24;
        arr0[1] = i1;
        int i2 = 91;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test50() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 40;
        arr0[0] = i0;
        int i1 = 49;
        arr0[1] = i1;
        int i2 = 43;
        arr0[2] = i2;
        int i3 = 92;
        arr0[3] = i3;
        int i4 = 21;
        arr0[4] = i4;
        int i5 = 39;
        arr0[5] = i5;
        int i6 = 28;
        arr0[6] = i6;
        int i7 = 17;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test51() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 25;
        arr0[0] = i0;
        int i1 = 94;
        arr0[1] = i1;
        int i2 = 84;
        arr0[2] = i2;
        int i3 = 49;
        arr0[3] = i3;
        int i4 = 12;
        arr0[4] = i4;
        int i5 = 35;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test52() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 68;
        arr0[0] = i0;
        int i1 = 43;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test53() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 35;
        arr0[0] = i0;
        int i1 = 17;
        arr0[1] = i1;
        int i2 = 21;
        arr0[2] = i2;
        int i3 = 6;
        arr0[3] = i3;
        int i4 = 27;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test54() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 4;
        arr0[0] = i0;
        int i1 = 55;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test55() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 40;
        arr0[0] = i0;
        int i1 = 23;
        arr0[1] = i1;
        int i2 = 19;
        arr0[2] = i2;
        int i3 = 100;
        arr0[3] = i3;
        int i4 = 38;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test56() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 80;
        arr0[0] = i0;
        int i1 = 3;
        arr0[1] = i1;
        int i2 = 94;
        arr0[2] = i2;
        int i3 = 30;
        arr0[3] = i3;
        int i4 = 0;
        arr0[4] = i4;
        int i5 = 48;
        arr0[5] = i5;
        int i6 = 48;
        arr0[6] = i6;
        int i7 = 11;
        arr0[7] = i7;
        int i8 = 70;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test57() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 90;
        arr0[0] = i0;
        int i1 = 55;
        arr0[1] = i1;
        int i2 = 91;
        arr0[2] = i2;
        int i3 = 81;
        arr0[3] = i3;
        int i4 = 15;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test58() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 18;
        arr0[0] = i0;
        int i1 = 71;
        arr0[1] = i1;
        int i2 = 46;
        arr0[2] = i2;
        int i3 = 80;
        arr0[3] = i3;
        int i4 = 77;
        arr0[4] = i4;
        int i5 = 2;
        arr0[5] = i5;
        int i6 = 74;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test59() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 66;
        arr0[0] = i0;
        int i1 = 24;
        arr0[1] = i1;
        int i2 = 16;
        arr0[2] = i2;
        int i3 = 28;
        arr0[3] = i3;
        int i4 = 78;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test60() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 47;
        arr0[0] = i0;
        int i1 = 100;
        arr0[1] = i1;
        int i2 = 16;
        arr0[2] = i2;
        int i3 = 44;
        arr0[3] = i3;
        int i4 = 9;
        arr0[4] = i4;
        int i5 = 83;
        arr0[5] = i5;
        int i6 = 56;
        arr0[6] = i6;
        int i7 = 32;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test61() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 6;
        arr0[0] = i0;
        int i1 = 78;
        arr0[1] = i1;
        int i2 = 86;
        arr0[2] = i2;
        int i3 = 22;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test62() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 65;
        arr0[0] = i0;
        int i1 = 86;
        arr0[1] = i1;
        int i2 = 54;
        arr0[2] = i2;
        int i3 = 10;
        arr0[3] = i3;
        int i4 = 30;
        arr0[4] = i4;
        int i5 = 40;
        arr0[5] = i5;
        int i6 = 98;
        arr0[6] = i6;
        int i7 = 69;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test63() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 11;
        arr0[0] = i0;
        int i1 = 86;
        arr0[1] = i1;
        int i2 = 19;
        arr0[2] = i2;
        int i3 = 57;
        arr0[3] = i3;
        int i4 = 55;
        arr0[4] = i4;
        int i5 = 18;
        arr0[5] = i5;
        int i6 = 52;
        arr0[6] = i6;
        int i7 = 62;
        arr0[7] = i7;
        int i8 = 54;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test64() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 89;
        arr0[0] = i0;
        int i1 = 4;
        arr0[1] = i1;
        int i2 = 83;
        arr0[2] = i2;
        int i3 = 18;
        arr0[3] = i3;
        int i4 = 2;
        arr0[4] = i4;
        int i5 = 94;
        arr0[5] = i5;
        int i6 = 7;
        arr0[6] = i6;
        int i7 = 78;
        arr0[7] = i7;
        int i8 = 94;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test65() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test66() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 91;
        arr0[0] = i0;
        int i1 = 61;
        arr0[1] = i1;
        int i2 = 7;
        arr0[2] = i2;
        int i3 = 39;
        arr0[3] = i3;
        int i4 = 15;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test67() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test68() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 25;
        arr0[0] = i0;
        int i1 = 52;
        arr0[1] = i1;
        int i2 = 77;
        arr0[2] = i2;
        int i3 = 33;
        arr0[3] = i3;
        int i4 = 80;
        arr0[4] = i4;
        int i5 = 49;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test69() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 14;
        arr0[0] = i0;
        int i1 = 79;
        arr0[1] = i1;
        int i2 = 13;
        arr0[2] = i2;
        int i3 = 55;
        arr0[3] = i3;
        int i4 = 96;
        arr0[4] = i4;
        int i5 = 83;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test70() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 20;
        arr0[0] = i0;
        int i1 = 61;
        arr0[1] = i1;
        int i2 = 34;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test71() throws Throwable {
        int[] arr0 = new int[3];
        int i0 = 68;
        arr0[0] = i0;
        int i1 = 40;
        arr0[1] = i1;
        int i2 = 18;
        arr0[2] = i2;
        int i3 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i3));
    }

    @Test
    public void test72() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 6;
        arr0[0] = i0;
        int i1 = 13;
        arr0[1] = i1;
        int i2 = 65;
        arr0[2] = i2;
        int i3 = 88;
        arr0[3] = i3;
        int i4 = 67;
        arr0[4] = i4;
        int i5 = 97;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test73() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 67;
        arr0[0] = i0;
        int i1 = 89;
        arr0[1] = i1;
        int i2 = 98;
        arr0[2] = i2;
        int i3 = 58;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test74() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 63;
        arr0[0] = i0;
        int i1 = 83;
        arr0[1] = i1;
        int i2 = 77;
        arr0[2] = i2;
        int i3 = 9;
        arr0[3] = i3;
        int i4 = 53;
        arr0[4] = i4;
        int i5 = 54;
        arr0[5] = i5;
        int i6 = 60;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test75() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 34;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test76() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 82;
        arr0[0] = i0;
        int i1 = 50;
        arr0[1] = i1;
        int i2 = 12;
        arr0[2] = i2;
        int i3 = 44;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test77() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 84;
        arr0[0] = i0;
        int i1 = 41;
        arr0[1] = i1;
        int i2 = 8;
        arr0[2] = i2;
        int i3 = 29;
        arr0[3] = i3;
        int i4 = 1;
        arr0[4] = i4;
        int i5 = 29;
        arr0[5] = i5;
        int i6 = 67;
        arr0[6] = i6;
        int i7 = 38;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test78() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test79() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 25;
        arr0[0] = i0;
        int i1 = 40;
        arr0[1] = i1;
        int i2 = 74;
        arr0[2] = i2;
        int i3 = 25;
        arr0[3] = i3;
        int i4 = 53;
        arr0[4] = i4;
        int i5 = 25;
        arr0[5] = i5;
        int i6 = 93;
        arr0[6] = i6;
        int i7 = 49;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test80() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 61;
        arr0[0] = i0;
        int i1 = 61;
        arr0[1] = i1;
        int i2 = 33;
        arr0[2] = i2;
        int i3 = 88;
        arr0[3] = i3;
        int i4 = 90;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test81() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test82() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 24;
        arr0[0] = i0;
        int i1 = 17;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test83() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 76;
        arr0[0] = i0;
        int i1 = 72;
        arr0[1] = i1;
        int i2 = 6;
        arr0[2] = i2;
        int i3 = 49;
        arr0[3] = i3;
        int i4 = 92;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test84() throws Throwable {
        int[] arr0 = new int[6];
        int i0 = 99;
        arr0[0] = i0;
        int i1 = 35;
        arr0[1] = i1;
        int i2 = 83;
        arr0[2] = i2;
        int i3 = 45;
        arr0[3] = i3;
        int i4 = 92;
        arr0[4] = i4;
        int i5 = 68;
        arr0[5] = i5;
        int i6 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i6));
    }

    @Test
    public void test85() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 88;
        arr0[0] = i0;
        int i1 = 36;
        arr0[1] = i1;
        int i2 = 85;
        arr0[2] = i2;
        int i3 = 82;
        arr0[3] = i3;
        int i4 = 89;
        arr0[4] = i4;
        int i5 = 20;
        arr0[5] = i5;
        int i6 = 40;
        arr0[6] = i6;
        int i7 = 31;
        arr0[7] = i7;
        int i8 = 17;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }

    @Test
    public void test86() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 8;
        arr0[0] = i0;
        int i1 = 66;
        arr0[1] = i1;
        int i2 = 74;
        arr0[2] = i2;
        int i3 = 35;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test87() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 95;
        arr0[0] = i0;
        int i1 = 59;
        arr0[1] = i1;
        int i2 = 93;
        arr0[2] = i2;
        int i3 = 80;
        arr0[3] = i3;
        int i4 = 66;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test88() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 24;
        arr0[0] = i0;
        int i1 = 79;
        arr0[1] = i1;
        int i2 = 15;
        arr0[2] = i2;
        int i3 = 12;
        arr0[3] = i3;
        int i4 = 5;
        arr0[4] = i4;
        int i5 = 2;
        arr0[5] = i5;
        int i6 = 85;
        arr0[6] = i6;
        int i7 = 80;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test89() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 53;
        arr0[0] = i0;
        int i1 = 21;
        arr0[1] = i1;
        int i2 = 14;
        arr0[2] = i2;
        int i3 = 72;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test90() throws Throwable {
        int[] arr0 = new int[2];
        int i0 = 18;
        arr0[0] = i0;
        int i1 = 71;
        arr0[1] = i1;
        int i2 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i2));
    }

    @Test
    public void test91() throws Throwable {
        int[] arr0 = new int[4];
        int i0 = 46;
        arr0[0] = i0;
        int i1 = 90;
        arr0[1] = i1;
        int i2 = 51;
        arr0[2] = i2;
        int i3 = 87;
        arr0[3] = i3;
        int i4 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i4));
    }

    @Test
    public void test92() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 62;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test93() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 96;
        arr0[0] = i0;
        int i1 = 2;
        arr0[1] = i1;
        int i2 = 5;
        arr0[2] = i2;
        int i3 = 69;
        arr0[3] = i3;
        int i4 = 3;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test94() throws Throwable {
        int[] arr0 = new int[0];
        int i0 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i0));
    }

    @Test
    public void test95() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 31;
        arr0[0] = i0;
        int i1 = 13;
        arr0[1] = i1;
        int i2 = 24;
        arr0[2] = i2;
        int i3 = 61;
        arr0[3] = i3;
        int i4 = 47;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test96() throws Throwable {
        int[] arr0 = new int[5];
        int i0 = 31;
        arr0[0] = i0;
        int i1 = 66;
        arr0[1] = i1;
        int i2 = 72;
        arr0[2] = i2;
        int i3 = 84;
        arr0[3] = i3;
        int i4 = 51;
        arr0[4] = i4;
        int i5 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i5));
    }

    @Test
    public void test97() throws Throwable {
        int[] arr0 = new int[1];
        int i0 = 21;
        arr0[0] = i0;
        int i1 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i1));
    }

    @Test
    public void test98() throws Throwable {
        int[] arr0 = new int[8];
        int i0 = 7;
        arr0[0] = i0;
        int i1 = 72;
        arr0[1] = i1;
        int i2 = 18;
        arr0[2] = i2;
        int i3 = 30;
        arr0[3] = i3;
        int i4 = 73;
        arr0[4] = i4;
        int i5 = 9;
        arr0[5] = i5;
        int i6 = 6;
        arr0[6] = i6;
        int i7 = 47;
        arr0[7] = i7;
        int i8 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i8));
    }

    @Test
    public void test99() throws Throwable {
        int[] arr0 = new int[7];
        int i0 = 100;
        arr0[0] = i0;
        int i1 = 1;
        arr0[1] = i1;
        int i2 = 10;
        arr0[2] = i2;
        int i3 = 52;
        arr0[3] = i3;
        int i4 = 67;
        arr0[4] = i4;
        int i5 = 20;
        arr0[5] = i5;
        int i6 = 65;
        arr0[6] = i6;
        int i7 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i7));
    }

    @Test
    public void test100() throws Throwable {
        int[] arr0 = new int[9];
        int i0 = 0;
        arr0[0] = i0;
        int i1 = 71;
        arr0[1] = i1;
        int i2 = 34;
        arr0[2] = i2;
        int i3 = 72;
        arr0[3] = i3;
        int i4 = 39;
        arr0[4] = i4;
        int i5 = 63;
        arr0[5] = i5;
        int i6 = 39;
        arr0[6] = i6;
        int i7 = 43;
        arr0[7] = i7;
        int i8 = 60;
        arr0[8] = i8;
        int i9 = SearchIndexEqualValue.search(arr0);
        Assert.assertTrue(SearchIndexEqualValue.validate(arr0, i9));
    }
}

