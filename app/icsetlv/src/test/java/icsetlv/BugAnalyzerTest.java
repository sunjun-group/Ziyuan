package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.exception.IcsetlvException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BugAnalyzerTest extends AbstractTest {
	private List<BreakPoint> bprsin = new ArrayList<BreakPoint>();
	private List<BreakPoint> bprsout = new ArrayList<BreakPoint>();
	
	@Before
	public void beforeTest(){
		BreakPoint bkp1 = new BreakPoint("testdata.slice.FindMax", "findMax",
				15);
		bkp1.addVars(new Variable("max"));
		bprsin.add(bkp1);

		BreakPoint bkp2 = new BreakPoint("testdata.slice.FindMax", "findMax",
				11);
		bkp2.addVars(new Variable("max"));
		bprsin.add(bkp2);
	}
	
	@Test
	public void testBugAnalyzer() throws IcsetlvException{
		System.out.println("Before analyzing:");
		printBkps(bprsin);
//		BugAnalyzer bga = new BugAnalyzer(
//				CollectionUtils.listOf("example.MaxFind.test.MaxFindPassTest"),
//				CollectionUtils.listOf("example.MaxFind.test.MaxFindFailTest"),
//				initVmConfig());
//		bprsout = bga.analyze(bprsin);
		System.out.println("After analyzing");
		printBkps(bprsout);
	}
}
