package lstar;

public class BitString {

  public static String extend(String value, boolean val) {
    String newValue = value + (val ? "1" : "0");
    return newValue;
  }
}
