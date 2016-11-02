package learntest.io.excel;

public class Trial {
	/**
	 * It should be a full name of method, including declaring class name.
	 */
	private String methodName;
	
	private double l2tTime;
	private double l2tCoverage;
	private double ramdoopTime;
	private double randoopCoverage;

	public Trial(){
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trial other = (Trial) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}



	public Trial(String methodName, double l2tTime, double l2tCoverage, double ramdoopTime, double randoopCoverage) {
		super();
		this.methodName = methodName;
		this.l2tTime = l2tTime;
		this.l2tCoverage = l2tCoverage;
		this.ramdoopTime = ramdoopTime;
		this.randoopCoverage = randoopCoverage;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public double getL2tTime() {
		return l2tTime;
	}

	public void setL2tTime(double l2tTime) {
		this.l2tTime = l2tTime;
	}

	public double getL2tCoverage() {
		return l2tCoverage;
	}

	public void setL2tCoverage(double l2tCoverage) {
		this.l2tCoverage = l2tCoverage;
	}

	public double getRamdoopTime() {
		return ramdoopTime;
	}

	public void setRamdoopTime(double ramdoopTime) {
		this.ramdoopTime = ramdoopTime;
	}

	public double getRandoopCoverage() {
		return randoopCoverage;
	}

	public void setRandoopCoverage(double randoopCoverage) {
		this.randoopCoverage = randoopCoverage;
	}

}
