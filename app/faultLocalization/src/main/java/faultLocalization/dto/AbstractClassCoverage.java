package faultLocalization.dto;

public abstract class AbstractClassCoverage {

	private final String classResourcePath;

	public AbstractClassCoverage(final String classResourcePath) {
		this.classResourcePath = classResourcePath;
	}
	
	public String getClassResourcePath() {
		return classResourcePath;
	}

}
