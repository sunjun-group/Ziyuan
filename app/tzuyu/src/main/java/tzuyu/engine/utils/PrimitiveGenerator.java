package tzuyu.engine.utils;

import java.util.Random;

/**
 * The value generator for TzuYu 'primitive' types. We can generate a random
 * value of the specified type with this value generator. PLEASE NOTE THAT: Java
 * String and enumeration type are also treated as primitives in TzuYu, Refer to
 * {@link TzuYuPrimtiveTypes}.
 * 
 * @author Spencer Xiao
 */
public class PrimitiveGenerator {
	public static final char SPACE = ' ';
	private static Random random = new Random();

	/**
	 * Randomly generate a value for the given TzuYu primitive type.
	 * <ul>
	 * <li>For Char type, we generate a visible char in ASCII table from the
	 * 'space' to the 'tide'.
	 * <li>For String type, we generate a string of maximum length, which was
	 * set by the user as an option, of visible characters.
	 * <li>For enumeration type, we randomly choose a constant value of that
	 * type.
	 * <li>For each other numerical type, we use random generator to get a value
	 * for it.
	 * </ul>
	 * 
	 * @param type
	 *            the type of the value we want to generate. The type must be a
	 *            TzuYu primitive type. Otherwise, exceptions will be thrown.
	 * @param stringMaxLength
	 * @return
	 */
	public static Object chooseValue(Class<?> type, PrimitiveGeneratorConfig config) {
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return PrimitiveValueZones.INTEGER_ZONES.nextRandom();
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return PrimitiveValueZones.LONG_ZONES.nextRandom();
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return random.nextFloat();
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return random.nextDouble();
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			byte[] retByte = new byte[1];
			random.nextBytes(retByte);
			return retByte[0];
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return (short) ((random.nextInt(Short.MAX_VALUE - Short.MIN_VALUE) + Short.MIN_VALUE));
		} else if (type.equals(char.class) || type.equals(Character.class)) {
			// The set of visible characters in the ASCII staring from the
			// 'space'
			// (code-point: 32) character to the 'tide'(code-point: 126)
			// character.
			return (char) (random.nextInt(95) + SPACE);
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return random.nextBoolean();
		} else if (type.equals(String.class)) {
			int length = random.nextInt(config.getStringMaxLength());
			StringBuilder sb = new StringBuilder();
			for (int index = 0; index < length; index++) {
				sb.append(chooseValue(char.class, config));
			}
			return sb.toString();
		} else if (PrimitiveTypes.isEnum(type)) {// must be enumeration type
			Class<?> eType = type;
			Object[] constValues = eType.getEnumConstants();
			int index = random.nextInt(constValues.length);
			return constValues[index];
		} else {
			throw new IllegalArgumentException("input type is not a "
					+ "TzuYu primtive type:" + type.toString());
		}
	}
	
	
	
	public interface PrimitiveGeneratorConfig {
		public int getStringMaxLength();
	}
}
