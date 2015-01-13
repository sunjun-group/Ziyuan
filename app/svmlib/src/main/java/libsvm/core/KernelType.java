package libsvm.core;

import java.util.HashMap;
import java.util.Map;

public enum KernelType {
	LINEAR(0), POLY(1), RBF(2), SIGMOID(3), PRECOMPUTED(4);

	private static final Map<Integer, KernelType> reverseMap = buildReverseMap();

	private final int index;

	KernelType(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	private static Map<Integer, KernelType> buildReverseMap() {
		Map<Integer, KernelType> map = new HashMap<Integer, KernelType>(KernelType.values().length);
		for (KernelType type : KernelType.values()) {
			map.put(type.index, type);
		}
		return map;
	}

	public static KernelType of(int index) {
		return reverseMap.get(index);
	}
}
