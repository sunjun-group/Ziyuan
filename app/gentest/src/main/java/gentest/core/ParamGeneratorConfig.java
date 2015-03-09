/**
 * Copyright TODO
 */
package gentest.core;

/**
 * @author LLT
 */
public class ParamGeneratorConfig {
	private static final int STRING_MAX_LENGTH = 10;

	public static ParamGeneratorConfig getDefault() {
		return new ParamGeneratorConfig();
	}

	public int getStringMaxLength() {
		return STRING_MAX_LENGTH;
	}
	
	
}
