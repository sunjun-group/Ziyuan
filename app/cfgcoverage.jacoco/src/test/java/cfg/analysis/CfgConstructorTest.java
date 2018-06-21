package cfg.analysis;

import org.junit.Assert;
import org.junit.Test;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructor;
import cfgcoverage.jacoco.testdata.ForSample;
import cfgcoverage.jacoco.testdata.IfInLoop;
import cfgcoverage.jacoco.testdata.IfSample;
import cfgcoverage.jacoco.testdata.InterproceduralSample;
import cfgcoverage.jacoco.testdata.LoopHeaderSample;
import cfgcoverage.jacoco.testdata.LoopSample;
import cfgcoverage.jacoco.testdata.MultiLevelLoopSample;
import cfgcoverage.jacoco.testdata.NestedLoopConditionSample;
import cfgcoverage.jacoco.testdata.SamplePrograms;
import cfgcoverage.jacoco.testdata.SwitchSample;
import cfgcoverage.jacoco.testdata.ThrowSample;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgConstructorTest {
	
	private CFG constructCfg(Class<?> clazz, String method) throws Exception {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.setTarget(TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
		CFG cfg = new CfgConstructor().constructCFG(appClasspath, clazz.getName(), method, 2);
		printCfg(cfg);
		return cfg;
	}
	
	private void printCfg(CFG cfg) {
		for (CfgNode cfgNode : cfg.getNodeList()) {
			System.out.println(cfgNode.getFullString());
			if (cfgNode.getSubCfg() != null) {
				printCfg(cfgNode.getSubCfg());
			}
		}
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		CFG cfg = constructCfg(SamplePrograms.class, "Max");
		Assert.assertEquals("node[6,IF_ICMPLE,line 10], decis{T=14,F=7}(14=PD,7=BPD_FALSE)]",
				cfg.getNode(6).toString());
		Assert.assertEquals("node[16,IF_ICMPLE,line 18], decis{T=26,F=17}(26=PD,17=BPD_FALSE)]",
				cfg.getNode(16).toString());
		System.out.println();
	}
	
	@Test
	public void testInterproduceralProgram() throws Exception {
		CFG cfg = constructCfg(InterproceduralSample.class, "Max");
		Assert.assertEquals("node[6,IF_ICMPLE,line 10], decis{T=14,F=7}(14=PD,7=BPD_FALSE)]",
				cfg.getNode(6).toString());
		Assert.assertEquals("node[16,IF_ICMPLE,line 18], decis{T=26,F=17}(26=PD,17=BPD_FALSE)]",
				cfg.getNode(16).toString());
		System.out.println();
	}

	@Test // TODO: node 12, loopHeader?
	public void testLoopProgram() throws Exception {
		CFG cfg = constructCfg(LoopSample.class, "run");
		Assert.assertEquals("node[12,IFLT,line 22], decis{T=4,F=13}(4=BPD_TRUE,13=BPD_FALSE), loopHeader]",
				cfg.getNode(12).toString());
		System.out.println();
	}
	
	@Test
	public void testSwitch() throws Exception {
		CFG cfg = constructCfg(SwitchSample.class, "getName");
		System.out.println();
	}
	
	@Test
	public void testFor() throws Exception {
		CFG cfg = constructCfg(ForSample.class, "run");
		Assert.assertEquals("node[5,IF_ICMPNE,line 19], decis{T=11,F=6}(11=BPD_TRUE,6=BPD_FALSE), inloop]",
				cfg.getNode(5).toString());
		Assert.assertEquals("node[14,IF_ICMPLT,line 18], decis{T=3,F=15}(3=BPD_TRUE,15=CD_TRUE_BPD_FALSE), loopHeader]",
				cfg.getNode(14).toString());
		System.out.println();
	}
	
	@Test
	public void testIfInLoop() throws Exception {
		CFG cfg = constructCfg(IfInLoop.class, "run");
		Assert.assertEquals("node[5,IF_ICMPNE,line 19], decis{T=9,F=6}(9=PD,6=BPD_FALSE), inloop]",
				cfg.getNode(5).toString());
		Assert.assertEquals("node[17,IF_ICMPLT,line 18], decis{T=3,F=18}(3=BPD_TRUE,18=PD), loopHeader]",
				cfg.getNode(17).toString());
		Assert.assertEquals("node[19,IFLE,line 25], decis{T=23,F=20}(23=PD,20=BPD_FALSE)]",
				cfg.getNode(19).toString());
		System.out.println();
	}
	
	@Test
	public void testIfMultiCondOr() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondOr");
		Assert.assertEquals("node[2,IF_ICMPEQ,line 30], decis{T=8,F=3}(8=BPD_TRUE_CD_FALSE,3=BPD_FALSE)]",
				cfg.getNode(2).toString());
		Assert.assertEquals("node[4,IFGT,line 30], decis{T=8,F=5}(8=BPD_TRUE_CD_FALSE,5=BPD_FALSE)]", 
				cfg.getNode(4).toString());
		Assert.assertEquals("node[7,IF_ICMPLE,line 30], decis{T=15,F=8}(15=PD,8=BPD_FALSE)]",
				cfg.getNode(7).toString());
		Assert.assertEquals("node[12,IF_ICMPGE,line 32], decis{T=15,F=13}(15=PD,13=BPD_FALSE)]",
				cfg.getNode(12).toString());
		System.out.println();
	}
	
	@Test
	public void testIfMultiCondAnd() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondAnd");
		Assert.assertEquals("node[2,IF_ICMPEQ,line 40], decis{T=15,F=3}(15=PD,3=BPD_FALSE)]",
				cfg.getNode(2).toString());
		Assert.assertEquals("node[4,IFLE,line 40], decis{T=15,F=5}(15=PD,5=BPD_FALSE)]",
				cfg.getNode(4).toString());
		Assert.assertEquals("node[7,IF_ICMPLE,line 40], decis{T=15,F=8}(15=PD,8=BPD_FALSE)]",
				cfg.getNode(7).toString());
		Assert.assertEquals("node[12,IF_ICMPGE,line 42], decis{T=15,F=13}(15=PD,13=BPD_FALSE)]",
				cfg.getNode(12).toString());
		System.out.println();
	}
	
	@Test
	public void testIfMultiCondAndOr() throws Exception {
		CFG cfg = constructCfg(IfSample.class, "multiCondAndOr");
		Assert.assertEquals("node[2,IF_ICMPEQ,line 50], decis{T=5,F=3}(5=BPD_TRUE_CD_FALSE,3=BPD_FALSE)]",
				cfg.getNode(2).toString());
		Assert.assertEquals("node[4,IFLE,line 50], decis{T=15,F=5}(15=PD,5=BPD_FALSE)]",
				cfg.getNode(4).toString());
		Assert.assertEquals("node[7,IF_ICMPLE,line 50], decis{T=15,F=8}(15=PD,8=BPD_FALSE)]",
				cfg.getNode(7).toString());
		Assert.assertEquals("node[12,IF_ICMPGE,line 52], decis{T=15,F=13}(15=PD,13=BPD_FALSE)]",
				cfg.getNode(12).toString());
		System.out.println();
	}
	
	@Test
	public void testLoopHeader() throws Exception {
		CFG cfg = null;
		System.out.println("multiLoopCond");
		cfg = constructCfg(LoopHeaderSample.class, "multiLoopCond");
		System.out.println("\n");
		System.out.println("multiLoopCondNeg");
		cfg = constructCfg(LoopHeaderSample.class, "multiLoopCondNeg");
		System.out.println("\n"); 
		System.out.println("singleLoopCond");
		cfg = constructCfg(LoopHeaderSample.class, "singleLoopCond");
		System.out.println("\n"); 
		System.out.println("forLoop");
		cfg = constructCfg(LoopHeaderSample.class, "forLoop");
		System.out.println("\n"); 
		System.out.println("forLoop2");
		cfg = constructCfg(LoopHeaderSample.class, "forLoop2");
		System.out.println("\n"); 
		System.out.println("doWhileMultiCond");
		cfg = constructCfg(LoopHeaderSample.class, "doWhileMultiCond");
		System.out.println("\n"); 
		System.out.println("doWhileSingleCondWithInLoopCond");
		cfg = constructCfg(LoopHeaderSample.class, "doWhileSingleCondWithInLoopCond");
		System.out.println("\n"); 
	}
	
	@Test
	public void testInnerLoop2() throws Exception {
		System.out.println("innerLoop2");
		CFG cfg = constructCfg(LoopHeaderSample.class, "innerLoop2");
		System.out.println("\n");
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
	
	@Test
	public void testThrow() throws Exception {
		CFG cfg = constructCfg(ThrowSample.class, "run");
		System.out.println();
	}
}
