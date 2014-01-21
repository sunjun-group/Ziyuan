package analyzer;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.utils.TzuYuPrimtiveTypes;

public class ObjectInfoPrimitive implements ObjectInfo {

	private ClassInfo type;

	private int level;
	private Object value;
	private boolean isValueNull;

	public ObjectInfoPrimitive(ClassInfo type, int level, Object value) {
		this.type = type;
		this.level = level;
		this.value = value;
		this.isValueNull = (value == null);
	}

	public double getNumericValue() {
		return TzuYuPrimtiveTypes.getNumericalValue(value);
	}

	public List<ObjectInfo> getValues(int level) {
		List<ObjectInfo> values = new ArrayList<ObjectInfo>();
		if (this.level == level) {
			values.add(this);
		}
		return values;
	}

	public boolean isValueNull() {
		return isValueNull;
	}

	public ClassInfo getType() {
		return type;
	}

}
