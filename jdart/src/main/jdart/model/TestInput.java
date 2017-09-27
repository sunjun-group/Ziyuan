package jdart.model;

import java.io.Serializable;
import java.util.LinkedList;

public class TestInput implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6978495569842605977L;
	private LinkedList<TestVar> paramList;

	public LinkedList<TestVar> getParamList() {
		return paramList;
	}

	public void setParamList(LinkedList<TestVar> paramList) {
		this.paramList = paramList;
	}

	@Override
	public String toString() {
		return paramList == null ? "empty" : paramList.toString();
	}
}
