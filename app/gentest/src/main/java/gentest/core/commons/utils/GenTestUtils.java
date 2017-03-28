/**
 * Copyright TODO
 */
package gentest.core.commons.utils;

/**
 * @author LLT
 *
 */
public class GenTestUtils {
	private GenTestUtils() {}

	public static ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
