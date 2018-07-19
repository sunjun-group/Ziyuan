package sav.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LLT
 *
 */
public class CommandLine {
	private Map<String, Object> argMap = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static CommandLine parse(String[] args) {
		CommandLine cmd = new CommandLine();
		Map<String, Object> argumentMap = cmd.argMap;
		String curOpt = null;
		for (String arg : args) {
			boolean isNumber = isNumber(arg);
			if (!isNumber && arg.startsWith("-")) {
				if (curOpt != null && !argumentMap.containsKey(curOpt)) {
					argumentMap.put(curOpt, "TRUE");
				}
				curOpt = arg.substring(1, arg.length());
			} else {
				if (argumentMap.containsKey(curOpt)) {
					List<String> multiValues = null;
					Object value = argumentMap.get(curOpt);
					if (value instanceof String) {
						multiValues = new ArrayList<>();
						multiValues.add((String) value);
					} else if (value instanceof List) {
						multiValues = (List<String>) value;
					}
					multiValues.add(arg);
					argumentMap.put(curOpt, multiValues);
				} else {
					argumentMap.put(curOpt, arg);
				}
			}
		}
		
		return cmd;
	}

	private static boolean isNumber(String arg) {
		try {
			Long.valueOf(arg);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String option) {
		Object value = get(option);
		if (value == null) {
			return Collections.emptyList();
		}
		if (value instanceof String) {
			return Arrays.asList((String)value);
		}
		if (value instanceof List<?>) {
			return (List<String>) value;
		}
		throw new IllegalArgumentException("Invalid type: " + value);
	}

	private Object get(String option) {
		return argMap.get(option);
	}

	public String getString(String option) {
		Object value = get(option);
		return value == null ? null : (String) value;
	}

	public long getLong(String option, long defaultVal) {
		String strVal = getString(option);
		return strVal == null ? defaultVal : Long.valueOf(strVal);
	}
}
