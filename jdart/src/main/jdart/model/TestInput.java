package jdart.model;

import java.util.List;

public class TestInput {
	private List<TestVar> paramList;

	public List<TestVar> getParamList() {
		return paramList;
	}

	public void setParamList(List<TestVar> paramList) {
		this.paramList = paramList;
	}

	@Override
	public String toString() {
		return paramList == null ? "empty" : paramList.toString();
	}
}
