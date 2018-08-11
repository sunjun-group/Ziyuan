package learntest.activelearning.core.coverage;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gentest.core.data.Sequence;
import microbat.instrumentation.AgentParams.LogType;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.output.CoverageOutputReader;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.FileUtils;
import sav.junit.SavSocketTestRunner;
import sav.junit.SavSocketTestRunner.Request;
import sav.junit.SavSocketTestRunner.RunningMode;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.ProgramArgumentBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.tcp.OutputWriter;

/**
 * @author LLT
 *
 */
public class CoverageAgentRunner extends AgentVmRunner {
	private VMConfiguration vmConfig;
	private ServerSocket serverSocket;
	private OutputWriter outputWriter;
	private CoverageOutputReader coverageReader;
	private boolean usingSimpleRunner = false;
	
	public CoverageAgentRunner(String agentJarPath, String savJunitRunnerJarPath, AppJavaClassPath appClasspath,
			boolean runCoverageAsMethodInvoke) {
		super(agentJarPath);
		this.vmConfig = new VMConfiguration(appClasspath);
		vmConfig.setNoVerify(true);
		
		if (runCoverageAsMethodInvoke) {
			vmConfig.setLaunchClass("sav.junit.SavSimpleRunner");
			usingSimpleRunner = true;
		} else {
			vmConfig.setLaunchClass("sav.junit.SavJunitRunner");
		}
		List<String> classpaths = new ArrayList<String>(vmConfig.getClasspaths());
		classpaths.add(savJunitRunnerJarPath);
		vmConfig.setClasspath(classpaths);
	}
	
	@Override
	protected void buildVmOption(CollectionBuilder<String, ?> builder, VMConfiguration config) {
		builder.appendIf("-Xmx10g", true);
//		builder.append("-agentlib:jdwp=transport=dt_socket,server=y,address=9595");
		super.buildVmOption(builder, config);
	}
	
	public void reset() {
		stop();
		serverSocket = null;
		outputWriter = null;
		coverageReader = null;
	}
	
	public CoverageOutput runWithSocket(CoverageAgentParams agentParams, long methodTimeout,
			List<String> junitMethods, List<Sequence> sequences) throws SavException {
		try {
			RunningMode runningMode;
			if (sequences != null) {
				runningMode = RunningMode.SEQUENCE_EXECUTION;
			} else if (usingSimpleRunner) {
				runningMode = RunningMode.SIMPLE_RUN;
			} else {
				runningMode = RunningMode.JUNIT;
			}
			addAgentParams(agentParams);
			vmConfig.setLaunchClass(SavSocketTestRunner.class.getName());
			if (serverSocket == null) {
				int port = VMConfiguration.findFreePort();
				serverSocket = new ServerSocket(port);
				/* check param options in sav.junit.JunitRunnerParamters class */
				List<String> programArgs = new ProgramArgumentBuilder()
						.addArgument(SavSocketTestRunner.OPT_COMUNICATE_TCP_PORT, port)
						.addArgument(SavSocketTestRunner.OPT_RUNNNING_MODE, runningMode.name())
						.build();
				vmConfig.setProgramArgs(programArgs);
				super.startVm(vmConfig);
				Socket client = serverSocket.accept();
				outputWriter = new OutputWriter(client.getOutputStream());
				coverageReader = new CoverageOutputReader(client.getInputStream());
			}
			outputWriter.writeString(Request.RUN_TEST.name());
			outputWriter.writeLong(methodTimeout);
			outputWriter.writeListString(junitMethods);
			if (sequences != null) {
				outputWriter.writeSerializableList(sequences);
			}
			outputWriter.flush();
			CoverageOutput output = new CoverageOutput();
			output.setCoverageGraph(coverageReader.readCfgCoverage());
			coverageReader.readInputData();
			return output;
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}
	
	public CoverageOutput run(CoverageAgentParams agentParams, long methodTimeout, List<String> junitMethods) throws SavException {
		addAgentParams(agentParams);
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

	protected void addAgentParams(CoverageAgentParams agentParams) {
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
		addAgentParam(CoverageAgentParams.OPT_COVERAGE_COLLECTION_TYPE, agentParams.getCoverageType().name());
	}
	
	@Override
	protected void printOut(String line, boolean error) {
//		if (error || line.startsWith(AgentConstants.LOG_HEADER)) {
			System.out.println(line);
//		}
	};
}
