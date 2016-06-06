package learntest.breakpoint.data;

import sav.common.core.utils.SignatureUtils;

public class DecisionLocation implements Comparable<DecisionLocation>{
	
	protected String id;
	protected String classCanonicalName;
	protected String methodSign; // methodName or signature
	protected int lineNo = -1; // started with 1?
	private boolean loop;

	public DecisionLocation(String className, String methodName, int lineNumber, boolean loop) {
		this.classCanonicalName = className;
		this.methodSign = methodName;
		this.lineNo = lineNumber;
		this.loop = loop;
	}

	public String getClassCanonicalName() {
		return classCanonicalName;
	}

	public void setClassCanonicalName(String classCanonicalName) {
		this.classCanonicalName = classCanonicalName;
	}

	public String getMethodSign() {
		return methodSign;
	}

	public void setMethodSign(String methodSign) {
		this.methodSign = methodSign;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	public String getId() {
		if (id == null) {
			id = String.format("%s:%s", classCanonicalName.replace("/", "."), lineNo);
		}
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		return classCanonicalName.hashCode() * prime + lineNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		DecisionLocation other = (DecisionLocation) obj;
		
		return classCanonicalName.equals(other.getClassCanonicalName())
				&& lineNo == other.getLineNo();
	}

	@Override
	public String toString() {
		return this.getId();
	}
	
	public String getMethodName() {
		return SignatureUtils.extractMethodName(methodSign);
	}

	@Override
	public int compareTo(DecisionLocation location) {
		return lineNo - location.lineNo;
	}
	
}
