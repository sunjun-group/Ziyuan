package learntest.activelearning.core.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import microbat.instrumentation.AgentConstants;
import microbat.instrumentation.AgentParams.LogType;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import sav.common.core.SavException;
import sav.common.core.utils.FileUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.ProgramArgumentBuilder;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class CoverageAgentRunner extends AgentVmRunner {
	private VMConfiguration vmConfig;

	public CoverageAgentRunner(String agentJarPath, String savJunitRunnerJarPath, AppJavaClassPath appClasspath, boolean runCoverageAsMethodInvoke) {
		super(agentJarPath);
		this.vmConfig = new VMConfiguration(appClasspath);
		vmConfig.setNoVerify(true);
		if (runCoverageAsMethodInvoke) {
			vmConfig.setLaunchClass("sav.junit.SavSimpleRunner");
		} else {
			vmConfig.setLaunchClass("sav.junit.SavJunitRunner");
		}
		List<String> classpaths = new ArrayList<String>(vmConfig.getClasspaths());
		classpaths.add(savJunitRunnerJarPath);
		vmConfig.setClasspath(classpaths);
	}

	public CoverageOutput run(CoverageAgentParams agentParams, long methodTimeout, List<String> junitMethods) throws SavException {
		addAgentParam(CoverageAgentParams.OPT_IS_COUNT_COVERAGE, true);
		addAgentParam(CoverageAgentParams.OPT_TARGET_METHOD, agentParams.getTargetMethodParam());
		addAgentParams(CoverageAgentParams.OPT_CLASS_PATH, agentParams.getClassPaths());
		addAgentParam(CoverageAgentParams.OPT_CDG_LAYER, agentParams.getCdgLayer());
		addAgentParams(CoverageAgentParams.OPT_INCLUSIVE_METHOD_IDS, agentParams.getInclusiveMethodIds());
		addAgentParams(CoverageAgentParams.OPT_LOG, Arrays.asList(LogType.info,
															LogType.debug, 
															LogType.error));
		addAgentParam(CoverageAgentParams.OPT_WORKING_DIR, agentParams.getWorkingDirectory());
		if (agentParams.getVarLayer() > 0) {
			addAgentParam(CoverageAgentParams.OPT_VARIABLE_LAYER, agentParams.getVarLayer());
		}
		addAgentParam(CoverageAgentParams.OPT_COLLECT_CONDITION_VARIATION, agentParams.collectConditionVariation());
		File dumpFile;
		String dumpFilePath = agentParams.getDumpFile();
		boolean toDeleteDumpFile = false;
		if (dumpFilePath == null) {
			dumpFile = FileUtils.createTempFile("coverage", ".info");
			toDeleteDumpFile = true;
		} else {
			dumpFile = FileUtils.getFileCreateIfNotExist(dumpFilePath);
		}
		dumpFilePath = dumpFile.getAbsolutePath();
		addAgentParam(CoverageAgentParams.OPT_DUMP_FILE, dumpFilePath);
		/* check param options in sav.junit.JunitRunnerParamters class */
		List<String> programArgs = new ProgramArgumentBuilder()
						.addArgument("tc_timeout", methodTimeout)	
						.addArgument("testcases", junitMethods)
						.build();
		vmConfig.setProgramArgs(programArgs);
		startAndWaitUntilStop(vmConfig);
		CoverageOutput coverageOutput = CoverageOutput.readFromFile(dumpFilePath);
		if (toDeleteDumpFile) {
			dumpFile.delete();
		}
		return coverageOutput;
	}
	
	@Override
	protected void printOut(String line, boolean error) {
//		if (error || line.startsWith(AgentConstants.LOG_HEADER)) {
			System.out.println(line);
//		}
	};
}
