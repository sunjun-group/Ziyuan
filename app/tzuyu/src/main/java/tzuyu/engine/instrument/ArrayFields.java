package tzuyu.engine.instrument;

import tzuyu.engine.iface.IPrintStream;

import tzuyu.engine.model.exception.TzRuntimeException;


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

  public void printElements(IPrintStream ps, int max) {
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

  protected abstract void printValue(IPrintStream ps, int index);

  public abstract Object getValues();

  @Override
  public int getIntValue(int index) {
    throw new TzRuntimeException("not a int[]");
  }

  @Override
  public int getReferenceValue(int index) {
    throw new TzRuntimeException("not a reference array");
  }

  @Override
  public long getLongValue(int index) {
    throw new TzRuntimeException("not a long[]");
  }

  @Override
  public boolean getBooleanValue(int index) {
    throw new TzRuntimeException("not a boolean[]");
  }

  @Override
  public byte getByteValue(int index) {
    throw new TzRuntimeException("not a byte[]");
  }

  @Override
  public char getCharValue(int index) {
    throw new TzRuntimeException("not a char[]");
  }

  @Override
  public short getShortValue(int index) {
    throw new TzRuntimeException("not a short[]");
  }

  @Override
  public double getDoubleValue(int index) {
    throw new TzRuntimeException("not a double[]");
  }

  @Override
  public float getFloatValue(int index) {
    throw new TzRuntimeException("not a float[]");
  }

  @Override
  public void setIntValue(int index, int newValue) {
    throw new TzRuntimeException("not a int[]");

  }

  @Override
  public void setReferenceValue(int index, int newValue) {
    throw new TzRuntimeException("not a referene array");

  }

  @Override
  public void setLongValue(int index, long newValue) {
    throw new TzRuntimeException("not a long[]");

  }

  @Override
  public void setBooleanValue(int index, boolean newValue) {
    throw new TzRuntimeException("not a boolean[]");

  }

  @Override
  public void setByteValue(int index, byte newValue) {
    throw new TzRuntimeException("not a byte[]");

  }

  @Override
  public void setCharValue(int index, char newValue) {
    throw new TzRuntimeException("not a char[]");

  }

  @Override
  public void setShortValue(int index, short newValue) {
    throw new TzRuntimeException("not a short[]");

  }

  @Override
  public void setDoubleValue(int index, double newValue) {
    throw new TzRuntimeException("not a double[]");

  }

  @Override
  public void setFloatValue(int index, float newValue) {
    throw new TzRuntimeException("not a float[]");

  }

  public boolean[] asBooleanArray() {
    throw new TzRuntimeException("not a boolean[]");
  }

  public byte[] asByteArray() {
    throw new TzRuntimeException("not a byte[]");
  }

  public char[] asCharArray() {
    throw new TzRuntimeException("not a char[]");
  }

  public char[] asCharArray(int offset, int length) {
    throw new TzRuntimeException("not a char[]");
  }

  public short[] asShortArray() {
    throw new TzRuntimeException("not a short[]");
  }

  public int[] asIntArray() {
    throw new TzRuntimeException("not a int[]");
  }

  public int[] asReferenceArray() {
    throw new TzRuntimeException("not a reference array");
  }

  public long[] asLongArray() {
    throw new TzRuntimeException("not a long[]");
  }

  public float[] asFloatArray() {
    throw new TzRuntimeException("not a float[]");
  }

  public double[] asDoubleArray() {
    throw new TzRuntimeException("not a double[]");
  }

}
