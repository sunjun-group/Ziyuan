package gentest.utils;

import java.util.Collection;

public class StringUtils {

	public static String join(Collection<?> vals, String separator) {
		if (vals == null || vals.isEmpty()) {
			return "";
		}
		StringBuilder varSb = new StringBuilder();
		int i = 0;
		for (Object val : vals) {
			i ++;
			if (val != null) {
				varSb.append(val.toString());
				if (i != vals.size()) {
					varSb.append(separator);
				}
			}
		}
		String str = varSb.toString();
		if (str.endsWith(separator)) {
			return str.substring(0, str.length() - separator.length());
		}
		return str;
	}
}
