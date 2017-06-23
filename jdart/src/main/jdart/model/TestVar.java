package jdart.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TestVar {
	//variable name, or array element index
	protected String name;
	
	protected String type;
	
	protected String value;
	
	protected List<TestVar> children = new ArrayList<>();

	@Override
	public String toString() {
		return "TestVar [name=" + name + ", type=" + type + ", value=" + value + ", children=" + children + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<TestVar> getChildren() {
		return children;
	}

	public void setChildren(List<TestVar> children) {
		this.children = children;
	}
	
	
}
