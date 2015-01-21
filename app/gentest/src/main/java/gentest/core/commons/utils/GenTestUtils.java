/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

import gentest.main.GentestConstants;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class GenTestUtils {
	private GenTestUtils() {}

	public static Class<?> toClassItselfOrItsDelegate(Class<?> clazz) {
		if (Object.class.equals(clazz)) {
			return Randomness
					.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_OBJECT);
		}
		if (Number.class.equals(clazz)) {
			return Randomness
					.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_NUMBER);
		}
		return clazz;
	}
}
