package libsvm.core;

import java.util.HashMap;
import java.util.Map;

/**
 * SVC = Support Vector Classification<br/>
 * SVR = Support Vector Regression<br/>
 * 
 * One-class SVM is used for novelty detection, that is, given a set of samples,
 * it will detect the soft boundary of that set so as to classify new points as
 * belonging to that set or not.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public enum MachineType {

	C_SVC(0), NU_SVC(1), ONE_CLASS(2), EPSILON_SVR(3), NU_SVR(4);

	private static final Map<Integer, MachineType> reverseMap = buildReverseMap();

	private final int index;

	MachineType(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	private static Map<Integer, MachineType> buildReverseMap() {
		Map<Integer, MachineType> map = new HashMap<Integer, MachineType>(
				MachineType.values().length);
		for (MachineType type : MachineType.values()) {
			map.put(type.index, type);
		}
		return map;
	}

	public static MachineType of(int index) {
		return reverseMap.get(index);
	}
}
