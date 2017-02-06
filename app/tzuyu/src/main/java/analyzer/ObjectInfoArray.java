package analyzer;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ObjectInfo;

public class ObjectInfoArray implements ObjectInfo {

	private ClassInfo type;

	private int level;

	private List<ObjectInfo> elements;

	private boolean isValueNull;

	public ObjectInfoArray(ClassInfo typeInfo, int level,
			List<ObjectInfo> elementValues, boolean nullValue) {
		this.type = typeInfo;
		this.level = level;
		this.elements = elementValues;
		this.isValueNull = nullValue;
	}

	public double getNumericValue() {
		return isValueNull ? 0 : 1;
	}

	public List<ObjectInfo> getValues(int level) {
		if (level < this.level) {
			return new ArrayList<ObjectInfo>();
		} else if (level == this.level) {
			List<ObjectInfo> objs = new ArrayList<ObjectInfo>();
			objs.add(this);
			return objs;
		} else if (level == this.level + 1) {
			List<ObjectInfo> objs = new ArrayList<ObjectInfo>();
			objs.add(new ObjectInfoPrimitive(new ClassInfoPrimitive(int.class),
					level, elements.size()));
			return objs;
		} else {
			List<ObjectInfo> objs = new ArrayList<ObjectInfo>();
			for (ObjectInfo obj : elements) {
				objs.addAll(obj.getValues(level));
			}
			return objs;
		}
	}

	public boolean isValueNull() {
		return isValueNull;
	}

	public ClassInfo getType() {
		return type;
	}

}
