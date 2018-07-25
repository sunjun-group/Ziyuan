package learntest.activelearning.core.distribution;

public class DistributionRow {
	
	private Integer[] distribution;
	private String distributionMethodName;
	
	public DistributionRow()throws Exception{
		
	}
	

	public final void setDistribution(Integer[] distributionIn) {
		distribution = distributionIn;
	}
	
	public final void setMethodName(String methodName) {
		distributionMethodName = methodName;
	}
	
	public final String getMethodName() {
		return distributionMethodName;
	}
	
	public final Integer[] getDistribution() {
		return distribution;
	}
	

}
