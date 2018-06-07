package cfg.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructor;
import cfgcoverage.jacoco.testdata.ForSample;
import cfgcoverage.jacoco.testdata.IfInLoop;
import cfgcoverage.jacoco.testdata.IfSample;
import cfgcoverage.jacoco.testdata.LoopHeaderSample;
import cfgcoverage.jacoco.testdata.LoopSample;
import cfgcoverage.jacoco.testdata.MultiLevelLoopSample;
import cfgcoverage.jacoco.testdata.NestedLoopConditionSample;
import cfgcoverage.jacoco.testdata.SwitchSample;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SamplePrograms;

/**
 * @author LLT
 *
 */
public class CfgConstructorTest {
	
	private CFG constructCfg(Class<?> clazz, String method) throws Exception {
		String classFilePath = ClassUtils.getClassFilePath(TestConfiguration.getTestTarget("cfgcoverage.jacoco"), clazz.getName());
		File file = new File(classFilePath);
		if (!file.exists()) {
			classFilePath = ClassUtils.getClassFilePath(TestConfiguration.getTestTarget("sav.commons"), clazz.getName());
			file = new File(classFilePath);
		}
		InputStream is = new FileInputStream(file);
		CFG cfg = CfgConstructor.constructCFG(is, clazz.getName(), method);
		System.out.println(TextFormatUtils.printCol(cfg.getNodeList(), "\n"));
		return cfg;
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		CFG cfg = constructCfg(SamplePrograms.class, "Max");
		Assert.assertEquals("node[6,IF_ICMPLE,line 10], decis{T=14,F=7}(14=TRUE_FALSE,7=FALSE)]",
				cfg.getNode(6).toString());
		Assert.assertEquals("node[16,IF_ICMPLE,line 18], decis{T=26,F=17}(26=TRUE_FALSE,17=FALSE)]",
				cfg.getNode(16).toString());
		System.out.println();
	}

	@Test // TODO: node 12, loopHeader?
	public void testLoopProgram() throws Exception {
		CFG cfg = constructCfg(LoopSample.class, "run");
		Assert.assertEquals("node[12,IFLT,line 22], decis{T=4,F=13}(4=TRUE,13=FALSE), loopHeader]",
				cfg.getNode(12).toString());
		System.out.println();
	}
	
	@Test
	/**
	 * TODO: How should we handle switch case?
	 */
	public void testSwitch() throws Exception {
		CFG cfg = constructCfg(SwitchSample.class, "getName");
		System.out.println();
	}
	
	@Test
	public void testFor() throws Exception {
		CFG cfg = constructCfg(ForSample.class, "run");
		Assert.assertEquals("node[5,IF_ICMPNE,line 19], decis{T=11,F=6}(11=TRUE,6=FALSE), inloop]",
				cfg.getNode(5).toString());
		Assert.assertEquals("node[14,IF_ICMPLT,line 18], decis{T=3,F=15}(3=TRUE,15=FALSE), loopHeader]",
				cfg.getNode(14).toString());
		System.out.println();
	}
	
	
	@Test
	public void testIfInLoop() throws Exception {
		CFG cfg = constructCfg(IfInLoop.class, "run");
		Assert.assertEquals("node[5,IF_ICMPNE,line 19], decis{T=9,F=6}(9=TRUE_FALSE,6=FALSE), inloop]",
				cfg.getNode(5).toString());
		Assert.assertEquals("node[17,IF_ICMPLT,line 18], decis{T=3,F=18}(3=TRUE,18=FALSE), loopHeader]",
				cfg.getNode(17).toString());
		Assert.assertEquals("node[19,IFLE,line 25], decis{T=23,F=20}(23=TRUE_FALSE,20=FALSE)]",
				cfg.getNode(19).toString());
		System.out.println();
	}
	
	@Test
	public void testIfMultiCondOr() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondOr");
		Assert.assertEquals("node[2,IF_ICMPEQ,line 30], decis{T=8,F=3}(8=TRUE,3=FALSE)]", cfg.getNode(2).toString());
		Assert.assertEquals("node[4,IFGT,line 30], decis{T=8,F=5}(8=TRUE,5=FALSE)]", cfg.getNode(4).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[7,IF_ICMPLE,line 30], decis{T=15,F=8}], coveredTcs={4=1}, coveredBranches={8=[4]}]", 
//				cfg.getNode(7).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[12,IF_ICMPGE,line 32], decis{T=15,F=13}], coveredTcs={0=1, 3=1, 4=1}, coveredBranches={13=[3, 4], 15=[0]}]", 
//				cfg.getNode(12).toString());
		System.out.println();
	}
	
	public void testIfMultiCondAnd() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondAnd");
		System.out.println();
	}
	
	public void testIfMultiCondAndOr() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondAndOr");
		System.out.println();
	}
	
	@Test
	public void testLoopHeader() throws Exception {
		CFG cfg = constructCfg(LoopHeaderSample.class, "multiLoopCond");
		cfg = constructCfg(LoopHeaderSample.class, "multiLoopCondNeg");
		cfg = constructCfg(LoopHeaderSample.class, "singleLoopCond");
		cfg = constructCfg(LoopHeaderSample.class, "forLoop");
		cfg = constructCfg(LoopHeaderSample.class, "doWhileMultiCond");
		cfg = constructCfg(LoopHeaderSample.class, "doWhileSingleCondWithInLoopCond");
	}
	
	@Test
	public void testMultiLevelLoop() throws Exception {
		CFG cfg = constructCfg(MultiLevelLoopSample.class, "run");
	}
	
	@Test
	public void testNestedLoopCondition() throws Exception {
		CFG cfg = constructCfg(NestedLoopConditionSample.class, "run");
//		Assert.assertEquals(
//				"NodeCoverage [node[14,IFEQ,line 21], decis{T=19,F=15}, inloop], coveredTcs={0=2}, coveredBranches={15=[0]}]"
//				, cfg.getNode(14).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[17,IF_ICMPLT,line 21], decis{T=7,F=18}, loopHeader], coveredTcs={0=2}, coveredBranches={18=[0], 7=[0]}]", 
//				cfg.getNode(17).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[21,IF_ICMPLT,line 21], decis{T=7,F=22}, loopHeader], coveredTcs={}, coveredBranches={}]", 
//				cfg.getNode(21).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[24,IF_ICMPLE,line 26], decis{T=28,F=25}], coveredTcs={0=1}, coveredBranches={25=[0]}]", 
//				cfg.getNode(24).toString());
//		Assert.assertEquals(
//				"NodeCoverage [node[30,IF_ICMPNE,line 29], decis{T=34,F=31}], coveredTcs={0=1}, coveredBranches={34=[0]}]", 
//				cfg.getNode(30).toString());
		System.out.println();
	}
}
