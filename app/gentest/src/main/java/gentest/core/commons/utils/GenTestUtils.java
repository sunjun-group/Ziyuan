/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

import java.lang.reflect.Modifier;

import gentest.main.GentestConstants;
import gentest.service.impl.SubTypesScanner;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class GenTestUtils {
	private GenTestUtils() {}

	public static Class<?> toClassItselfOrItsDelegate(Class<?> clazz) {
		// TODO LLT: TO REVIEW
		if (Object.class.equals(clazz)) {
			return Randomness
					.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_OBJECT);
		}
		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			return SubTypesScanner.getInstance().getRandomImplClzz(clazz);
		}
		return clazz;
	}
}
