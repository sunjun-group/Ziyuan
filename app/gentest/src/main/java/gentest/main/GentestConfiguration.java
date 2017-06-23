/**
 * Copyright TODO
 */
package gentest.main;

/**
 * @author LLT
 * 
 */
public class GentestConfiguration {
	private int queryMaxLength;
	private int testPerQuery;
	private int numberOfTcs;
	private int testPerClass;

	public int getTestPerClass() {
		return testPerClass;
	}

	public void setTestPerClass(int testPerClass) {
		this.testPerClass = testPerClass;
	}

	public int getQueryMaxLength() {
		return queryMaxLength;
	}

	public void setQueryMaxLength(int queryMaxLength) {
		this.queryMaxLength = queryMaxLength;
	}

	public int getTestPerQuery() {
		return testPerQuery;
	}

	public void setTestPerQuery(int testPerQuery) {
		this.testPerQuery = testPerQuery;
	}

	public int getNumberOfTcs() {
		return numberOfTcs;
	}

	public void setNumberOfTcs(int numberOfTcs) {
		this.numberOfTcs = numberOfTcs;
	}

}
