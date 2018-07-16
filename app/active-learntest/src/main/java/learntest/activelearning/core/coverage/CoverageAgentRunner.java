package learntest.activelearning.core.coverage;

import java.io.File;

import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import sav.common.core.SavException;
import sav.common.core.utils.FileUtils;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class CoverageAgentRunner extends AgentVmRunner {
	private VMConfiguration vmConfig;

	public CoverageAgentRunner(String agentJarPath, VMConfiguration vmConfig) {
		super(agentJarPath);
		this.vmConfig = vmConfig;
	}

	public CoverageOutput run(CoverageAgentParams agentParams) throws SavException {
		addAgentParam(CoverageAgentParams.OPT_IS_COUNT_COVERAGE, true);
		addAgentParam(CoverageAgentParams.OPT_TARGET_METHOD, agentParams.getTargetMethodParam());
		addAgentParams(CoverageAgentParams.OPT_CLASS_PATH, agentParams.getClassPaths());
		addAgentParam(CoverageAgentParams.OPT_CDG_LAYER, agentParams.getCdgLayer());
		File dumpFile;
		String dumpFilePath = agentParams.getDumpFile();
		boolean toDeleteDumpFile = false;
		if (dumpFilePath == null) {
			dumpFile = FileUtils.createTempFile("coverage", ".info");
			toDeleteDumpFile = true;
		} else {
			dumpFile = FileUtils.getFileCreateIfNotExist(dumpFilePath);
		}
		
		addAgentParam(CoverageAgentParams.OPT_DUMP_FILE, agentParams.getDumpFile());
		
		startAndWaitUntilStop(vmConfig);
		CoverageOutput coverageOutput = CoverageOutput.readFromFile(agentParams.getDumpFile());
		if (toDeleteDumpFile) {
			dumpFile.delete();
		}
		return coverageOutput;
	}
}
