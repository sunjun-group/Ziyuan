package faultLocalization;

public abstract class AbstractClassCoverage {

	private final String className;

	public AbstractClassCoverage(final String className) {
		this.className = className;
	}
	
	public String getClassResourcePath() {
		return className;
	}

}
