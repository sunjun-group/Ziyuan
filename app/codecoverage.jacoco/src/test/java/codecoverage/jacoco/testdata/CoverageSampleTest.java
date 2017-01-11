package codecoverage.jacoco.testdata;

import org.junit.Test;


public class CoverageSampleTest {
	
	@Test
	public void test1() {
		CoverageSample sample = new CoverageSample();
		sample.method1(10, 0);
	}
}
