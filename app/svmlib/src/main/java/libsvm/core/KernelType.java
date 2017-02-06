package libsvm.core;

import java.util.HashMap;
import java.util.Map;

/**
 * The values for kernel type (function) is defined as follows:
 * <ul>
 * <li>Linear: <code>&ltx, x'&gt</code></li>
 * <li>Polynomial: <code>(γ&ltx, x'&gt + r)^d</code>.</li>
 * <li>RBF: <code>exp(-γ|x - x'|^2)</code></li>
 * <li>Sigmoid: <code>tanh(γ&ltx, x'&gt + r)</code></li>
 * </ul>
 * 
 * d is specified by <code>degree</code><br/>
 * r is specified by <code>coef0</code><br/>
 * γ is specified by <code>gamma</code>, must be greter than 0.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
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
