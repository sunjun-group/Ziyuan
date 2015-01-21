/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class GenTestUtils {
	private GenTestUtils() {}

	public static Class<?> toClassItselfOrItsDelegate(Class<?> clazz) {
		if (Object.class.equals(clazz)) {
			return Randomness.randomMember(new Class<?>[] { Integer.class,
					Long.class, String.class, Short.class, Byte.class });
		}
		return clazz;
	}
}
