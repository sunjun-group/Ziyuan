package lstar;
import junit.framework.TestCase;
import testdata.BoundedStack;

public class BoundedStack0 extends TestCase { 

  public static boolean debug = false;

  public void test0() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test1");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
    Integer i4 = (-6);
    boolean b1 = boundedStack0.push(i4);
  }

  public void test1() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test2");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-10);
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = (-10);
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
  }

  public void test2() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test3");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
  }

  public void test3() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test4");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = (-8);
    boolean b1 = boundedStack0.push(i2);
  }

  public void test4() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test5");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = boundedStack0.pop();
    int i3 = boundedStack0.size();
  }

  public void test5() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test6");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    int i2 = boundedStack0.size();
  }

  public void test6() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test7");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    Integer i2 = (-1);
    boolean b0 = boundedStack0.push(i2);
  }

  public void test7() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test8");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-5);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = (-5);
    boolean b1 = boundedStack0.push(i1);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-5);
    boolean b2 = boundedStack1.push(i2);
    boolean b3 = boundedStack0.push(i2);
  }

  public void test8() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test9");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-9);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-9);
    boolean b1 = boundedStack1.push(i2);
    int i3 = boundedStack1.size();
    boolean b2 = boundedStack0.push((Integer)i3);
    Integer i4 = 10;
    boolean b3 = boundedStack0.push(i4);
  }

  public void test9() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test10");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
  }

  public void test10() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test11");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = (-6);
    boolean b0 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
  }

  public void test11() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test12");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 8;
    boolean b0 = boundedStack0.push(i0);
  }

  public void test12() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test13");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 3;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 3;
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
  }

  public void test13() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test14");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 4;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = (-6);
    boolean b1 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
    Integer i4 = boundedStack0.pop();
  }

  public void test14() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test15");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    int i2 = boundedStack0.size();
    Integer i3 = (-10);
    boolean b1 = boundedStack0.push(i3);
  }

  public void test15() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test16");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = 3;
    boolean b0 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
  }

  public void test16() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test17");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 5;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
  }

  public void test17() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test18");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-10);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    Integer i2 = (-6);
    boolean b1 = boundedStack0.push(i2);
  }

  public void test18() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test19");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = 1;
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
    int i4 = boundedStack0.size();
  }

  public void test19() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test20");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-8);
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
  }

  public void test20() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test21");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i2 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i2);
    BoundedStack boundedStack2 = new BoundedStack();
    int i3 = boundedStack2.size();
    int i4 = boundedStack2.size();
    BoundedStack boundedStack3 = new BoundedStack();
    int i5 = boundedStack3.size();
    boolean b1 = boundedStack2.push((Integer)i5);
    boolean b2 = boundedStack0.push(i5);
  }

  public void test21() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test22");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 6;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 6;
    boolean b1 = boundedStack0.push(i1);
    Integer i2 = 7;
    boolean b2 = boundedStack0.push(i2);
  }

  public void test22() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test23");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-3);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-3);
    boolean b1 = boundedStack0.push(i2);
    BoundedStack boundedStack2 = new BoundedStack();
    Integer i3 = (-3);
    boolean b2 = boundedStack2.push(i3);
    boolean b3 = boundedStack0.push(i3);
  }

  public void test23() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test24");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-6);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    int i2 = boundedStack0.size();
  }

  public void test24() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test25");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = boundedStack0.pop();
  }

  public void test25() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test26");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-1);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = 9;
    boolean b1 = boundedStack0.push(i3);
    Integer i4 = 9;
    boolean b2 = boundedStack0.push(i4);
  }

  public void test26() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test27");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-10);
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = (-10);
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
    Integer i3 = boundedStack0.pop();
    BoundedStack boundedStack2 = new BoundedStack();
    Integer i4 = (-10);
    boolean b3 = boundedStack2.push(i4);
    BoundedStack boundedStack3 = new BoundedStack();
    Integer i5 = (-10);
    boolean b4 = boundedStack3.push(i5);
    boolean b5 = boundedStack2.push(i5);
    Integer i6 = boundedStack2.pop();
    Integer i7 = boundedStack2.pop();
    boolean b6 = boundedStack0.push(i7);
  }

  public void test27() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test28");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 6;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 6;
    boolean b1 = boundedStack0.push(i1);
    Integer i2 = 7;
    boolean b2 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
    Integer i4 = 10;
    boolean b3 = boundedStack0.push(i4);
  }

  public void test28() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test29");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 4;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
  }

  public void test29() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test30");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
  }

  public void test30() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test31");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 4;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i3 = 4;
    boolean b1 = boundedStack0.push(i3);
    int i4 = boundedStack0.size();
  }

  public void test31() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test32");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = 3;
    boolean b0 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
    int i3 = boundedStack0.size();
    Integer i4 = 10;
    boolean b1 = boundedStack0.push(i4);
  }

  public void test32() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test33");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 3;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 3;
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
    Integer i3 = 9;
    boolean b3 = boundedStack0.push(i3);
  }

  public void test33() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test34");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i2 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i2);
    BoundedStack boundedStack2 = new BoundedStack();
    int i3 = boundedStack2.size();
    int i4 = boundedStack2.size();
    BoundedStack boundedStack3 = new BoundedStack();
    int i5 = boundedStack3.size();
    boolean b1 = boundedStack2.push((Integer)i5);
    boolean b2 = boundedStack0.push(i5);
    Integer i6 = (-4);
    boolean b3 = boundedStack0.push(i6);
  }

  public void test34() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test35");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 5;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
  }

  public void test35() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test36");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 4;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = (-6);
    boolean b1 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
  }

  public void test36() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test37");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
    int i3 = boundedStack0.size();
  }

  public void test37() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test38");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = boundedStack0.pop();
  }

  public void test38() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test39");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 8;
    boolean b0 = boundedStack0.push(i0);
  }

  public void test39() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test40");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-5);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
  }

  public void test40() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test41");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-1);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
  }

  public void test41() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test42");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 4;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
    Integer i3 = (-4);
    boolean b1 = boundedStack0.push(i3);
  }

  public void test42() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test43");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
  }

  public void test43() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test44");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-2);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = (-8);
    boolean b1 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i3 = (-2);
    boolean b2 = boundedStack1.push(i3);
    Integer i4 = (-8);
    boolean b3 = boundedStack0.push(i4);
  }

  public void test44() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test45");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-6);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
  }

  public void test45() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test46");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-1);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
  }

  public void test46() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test47");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    Integer i2 = 7;
    boolean b0 = boundedStack0.push(i2);
  }

  public void test47() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test48");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    Integer i2 = 8;
    boolean b0 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
    Integer i4 = 6;
    boolean b1 = boundedStack0.push(i4);
  }

  public void test48() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test49");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-1);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = (-4);
    boolean b1 = boundedStack0.push(i3);
  }

  public void test49() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test50");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 2;
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = 5;
    boolean b1 = boundedStack0.push(i1);
  }

  public void test50() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test51");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 0;
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    Integer i2 = (-3);
    boolean b1 = boundedStack0.push(i2);
  }

  public void test51() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test52");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
    int i3 = boundedStack0.size();
    Integer i4 = (-3);
    boolean b1 = boundedStack0.push(i4);
  }

  public void test52() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test53");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 5;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
    Integer i4 = boundedStack0.pop();
  }

  public void test53() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test54");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
  }

  public void test54() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test55");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
  }

  public void test55() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test56");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 10;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i3 = 10;
    boolean b1 = boundedStack1.push(i3);
    int i4 = boundedStack1.size();
    boolean b2 = boundedStack0.push((Integer)i4);
  }

  public void test56() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test57");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-6);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
    int i4 = boundedStack0.size();
  }

  public void test57() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test58");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 8;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = boundedStack0.pop();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i4 = 8;
    boolean b1 = boundedStack0.push(i4);
  }

  public void test58() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test59");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 1;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = boundedStack0.pop();
    Integer i4 = (-1);
    boolean b1 = boundedStack0.push(i4);
  }

  public void test59() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test60");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 1;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = boundedStack0.pop();
  }

  public void test60() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test61");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = 3;
    boolean b0 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
    int i3 = boundedStack0.size();
  }

  public void test61() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test62");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-7);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = 1;
    boolean b1 = boundedStack0.push(i1);
  }

  public void test62() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test63");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 0;
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    Integer i2 = (-3);
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
  }

  public void test63() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test64");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 10;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
  }

  public void test64() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test65");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
  }

  public void test65() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test66");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 6;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 6;
    boolean b1 = boundedStack0.push(i1);
    Integer i2 = 7;
    boolean b2 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
  }

  public void test66() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test67");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 8;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    Integer i3 = boundedStack0.pop();
  }

  public void test67() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test68");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-2);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = (-8);
    boolean b1 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
  }

  public void test68() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test69");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-8);
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
    int i4 = boundedStack0.size();
  }

  public void test69() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test70");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    Integer i2 = 1;
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
  }

  public void test70() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test71");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 5;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
  }

  public void test71() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test72");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-10);
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
  }

  public void test72() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test73");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    int i1 = boundedStack0.size();
    Integer i2 = (-1);
    boolean b0 = boundedStack0.push(i2);
    Integer i3 = boundedStack0.pop();
  }

  public void test73() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test74");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-8);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = 0;
    boolean b1 = boundedStack0.push(i2);
  }

  public void test74() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test75");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-3);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i2 = (-3);
    boolean b1 = boundedStack0.push(i2);
  }

  public void test75() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test76");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 5;
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    Integer i2 = boundedStack0.pop();
    Integer i3 = (-7);
    boolean b1 = boundedStack0.push(i3);
  }

  public void test76() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test77");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 3;
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = 3;
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
    Integer i3 = 9;
    boolean b3 = boundedStack0.push(i3);
    Integer i4 = boundedStack0.pop();
  }

  public void test77() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test78");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = 0;
    boolean b0 = boundedStack0.push(i0);
    Integer i1 = boundedStack0.pop();
    Integer i2 = (-3);
    boolean b1 = boundedStack0.push(i2);
    int i3 = boundedStack0.size();
    int i4 = boundedStack0.size();
  }

  public void test78() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test79");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-10);
    boolean b0 = boundedStack0.push(i0);
    BoundedStack boundedStack1 = new BoundedStack();
    Integer i1 = (-10);
    boolean b1 = boundedStack1.push(i1);
    boolean b2 = boundedStack0.push(i1);
    Integer i2 = boundedStack0.pop();
    Integer i3 = boundedStack0.pop();
  }

  public void test79() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test80");

    BoundedStack boundedStack0 = new BoundedStack();
    Integer i0 = (-5);
    boolean b0 = boundedStack0.push(i0);
    int i1 = boundedStack0.size();
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
    Integer i4 = boundedStack0.pop();
  }

  public void test80() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test81");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = (-6);
    boolean b0 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
    int i3 = boundedStack0.size();
    Integer i4 = boundedStack0.pop();
  }

  public void test81() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test82");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    BoundedStack boundedStack1 = new BoundedStack();
    int i1 = boundedStack1.size();
    boolean b0 = boundedStack0.push((Integer)i1);
    int i2 = boundedStack0.size();
    Integer i3 = (-10);
    boolean b1 = boundedStack0.push(i3);
    BoundedStack boundedStack2 = new BoundedStack();
    int i4 = boundedStack2.size();
    BoundedStack boundedStack3 = new BoundedStack();
    int i5 = boundedStack3.size();
    boolean b2 = boundedStack2.push((Integer)i5);
    int i6 = boundedStack2.size();
    Integer i7 = (-10);
    boolean b3 = boundedStack0.push(i7);
  }

  public void test82() throws Throwable {

    if(debug) System.out.println("%nBoundedStack0.test83");

    BoundedStack boundedStack0 = new BoundedStack();
    int i0 = boundedStack0.size();
    Integer i1 = (-6);
    boolean b0 = boundedStack0.push(i1);
    int i2 = boundedStack0.size();
  }

  public static void main(String[] args) {
    BoundedStack0 mainObj = new BoundedStack0();
    try {
      mainObj.test0();
      mainObj.test1();
      mainObj.test2();
      mainObj.test3();
      mainObj.test4();
      mainObj.test5();
      mainObj.test6();
      mainObj.test7();
      mainObj.test8();
      mainObj.test9();
      mainObj.test10();
      mainObj.test11();
      mainObj.test12();
      mainObj.test13();
      mainObj.test14();
      mainObj.test15();
      mainObj.test16();
      mainObj.test17();
      mainObj.test18();
      mainObj.test19();
      mainObj.test20();
      mainObj.test21();
      mainObj.test22();
      mainObj.test23();
      mainObj.test24();
      mainObj.test25();
      mainObj.test26();
      mainObj.test27();
      mainObj.test28();
      mainObj.test29();
      mainObj.test30();
      mainObj.test31();
      mainObj.test32();
      mainObj.test33();
      mainObj.test34();
      mainObj.test35();
      mainObj.test36();
      mainObj.test37();
      mainObj.test38();
      mainObj.test39();
      mainObj.test40();
      mainObj.test41();
      mainObj.test42();
      mainObj.test43();
      mainObj.test44();
      mainObj.test45();
      mainObj.test46();
      mainObj.test47();
      mainObj.test48();
      mainObj.test49();
      mainObj.test50();
      mainObj.test51();
      mainObj.test52();
      mainObj.test53();
      mainObj.test54();
      mainObj.test55();
      mainObj.test56();
      mainObj.test57();
      mainObj.test58();
      mainObj.test59();
      mainObj.test60();
      mainObj.test61();
      mainObj.test62();
      mainObj.test63();
      mainObj.test64();
      mainObj.test65();
      mainObj.test66();
      mainObj.test67();
      mainObj.test68();
      mainObj.test69();
      mainObj.test70();
      mainObj.test71();
      mainObj.test72();
      mainObj.test73();
      mainObj.test74();
      mainObj.test75();
      mainObj.test76();
      mainObj.test77();
      mainObj.test78();
      mainObj.test79();
      mainObj.test80();
      mainObj.test81();
      mainObj.test82();
    } catch (Throwable e) {
    }
  }
}