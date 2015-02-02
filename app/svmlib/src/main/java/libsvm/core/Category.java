package libsvm.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Category {
	POSITIVE(1), NEGATIVE(-1);

	private final int intValue;

	Category(int intValue) {
		this.intValue = intValue;
	}

	public int intValue() {
		return intValue;
	}

	private static final List<Category> VALUES = Collections.unmodifiableList(Arrays
			.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	public static Category random() {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}

	public static List<Category> getValues() {
		return VALUES; // Use the cached one to optimize performance
	}
}
