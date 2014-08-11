package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.vm.BugAnalyzer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;

public class BugAnalyzerTest extends AbstractTest {
	private List<BreakPoint> bprsin = new ArrayList<BreakPoint>();
	private List<BreakPoint> bprsout = new ArrayList<BreakPoint>();
	
	
	@Before
	public void beforeTest(){
		BreakPoint bkp1 = new BreakPoint("testdata.slice.FindMax", "findMax");
		bkp1.addVars(new Variable("max"));
		bkp1.setLineNo(15);
		bprsin.add(bkp1);

		BreakPoint bkp2 = new BreakPoint("testdata.slice.FindMax", "findMax");
		bkp2.addVars(new Variable("max"));
		bkp2.setLineNo(11);
		bprsin.add(bkp2);
	}
	
	@Test
	public void testBugAnalyzer() throws IcsetlvException{
		System.out.println("Before analyzing:");
		printBkps(bprsin);
		BugAnalyzer bga = new BugAnalyzer(
				CollectionUtils.listOf("example.MaxFind.test.MaxFindPassTest"),
				CollectionUtils.listOf("example.MaxFind.test.MaxFindFailTest"),
				initVmConfig());
		bprsout = bga.analyze(bprsin);
		System.out.println("After analyzing");
		printBkps(bprsout);
	}
}
