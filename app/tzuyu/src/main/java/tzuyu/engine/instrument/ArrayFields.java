package tzuyu.engine.instrument;

import java.io.PrintStream;

import tzuyu.engine.model.TzuYuException;


public abstract class ArrayFields extends Fields {

  int getNumberOfFieldsOrElements() {
    return arrayLength();
  }

  public abstract int arrayLength();

  public boolean isReferenceArray() {
    return false;
  }

  public int getNumerOfFields() {
    return 0;
  }

  public void printElements(PrintStream ps, int max) {
    int len = arrayLength();
    if (max < 0) {
      max = len;
    }
    int i = 0;
    for (; i < max; i++) {
      if (i > 0) {
        ps.print(",");
      }
      printValue(ps, i);
    }
    if (i < len) {
      ps.print("...");
    }
  }

  protected abstract void printValue(PrintStream ps, int index);

  public abstract Object getValues();

  @Override
  public int getIntValue(int index) {
    throw new TzuYuException("not a int[]");
  }

  @Override
  public int getReferenceValue(int index) {
    throw new TzuYuException("not a reference array");
  }

  @Override
  public long getLongValue(int index) {
    throw new TzuYuException("not a long[]");
  }

  @Override
  public boolean getBooleanValue(int index) {
    throw new TzuYuException("not a boolean[]");
  }

  @Override
  public byte getByteValue(int index) {
    throw new TzuYuException("not a byte[]");
  }

  @Override
  public char getCharValue(int index) {
    throw new TzuYuException("not a char[]");
  }

  @Override
  public short getShortValue(int index) {
    throw new TzuYuException("not a short[]");
  }

  @Override
  public double getDoubleValue(int index) {
    throw new TzuYuException("not a double[]");
  }

  @Override
  public float getFloatValue(int index) {
    throw new TzuYuException("not a float[]");
  }

  @Override
  public void setIntValue(int index, int newValue) {
    throw new TzuYuException("not a int[]");

  }

  @Override
  public void setReferenceValue(int index, int newValue) {
    throw new TzuYuException("not a referene array");

  }

  @Override
  public void setLongValue(int index, long newValue) {
    throw new TzuYuException("not a long[]");

  }

  @Override
  public void setBooleanValue(int index, boolean newValue) {
    throw new TzuYuException("not a boolean[]");

  }

  @Override
  public void setByteValue(int index, byte newValue) {
    throw new TzuYuException("not a byte[]");

  }

  @Override
  public void setCharValue(int index, char newValue) {
    throw new TzuYuException("not a char[]");

  }

  @Override
  public void setShortValue(int index, short newValue) {
    throw new TzuYuException("not a short[]");

  }

  @Override
  public void setDoubleValue(int index, double newValue) {
    throw new TzuYuException("not a double[]");

  }

  @Override
  public void setFloatValue(int index, float newValue) {
    throw new TzuYuException("not a float[]");

  }

  public boolean[] asBooleanArray() {
    throw new TzuYuException("not a boolean[]");
  }

  public byte[] asByteArray() {
    throw new TzuYuException("not a byte[]");
  }

  public char[] asCharArray() {
    throw new TzuYuException("not a char[]");
  }

  public char[] asCharArray(int offset, int length) {
    throw new TzuYuException("not a char[]");
  }

  public short[] asShortArray() {
    throw new TzuYuException("not a short[]");
  }

  public int[] asIntArray() {
    throw new TzuYuException("not a int[]");
  }

  public int[] asReferenceArray() {
    throw new TzuYuException("not a reference array");
  }

  public long[] asLongArray() {
    throw new TzuYuException("not a long[]");
  }

  public float[] asFloatArray() {
    throw new TzuYuException("not a float[]");
  }

  public double[] asDoubleArray() {
    throw new TzuYuException("not a double[]");
  }

}
