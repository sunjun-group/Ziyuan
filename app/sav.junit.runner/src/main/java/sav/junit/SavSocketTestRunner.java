package sav.junit;

import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import gentest.core.data.Sequence;
import sav.tcp.InputReader;
import sav.tcp.TcpConnector;
import sav.utils.ExecutionTimerUtils;
import sav.utils.IExecutionTimer;

public class SavSocketTestRunner {
	public static final String OPT_COMUNICATE_TCP_PORT = "tcp_port";
	public static final String OPT_RUNNNING_MODE = "test_running_mode";
	public static final String OPT_ENABLE_TIME_OUT = "enable_timeout";
	
	public static void main(String[] args) throws Exception {
		SavSocketTestRunner testRunner = new SavSocketTestRunner();
		testRunner.run(args);
	}
	
	public void run(String[] args) throws Exception {
		CommandLine cmd = CommandLine.parse(args);
		int tcpPort = cmd.getInt(OPT_COMUNICATE_TCP_PORT, -1);
		RunningMode runningMode = RunningMode.valueOf(cmd.getString(OPT_RUNNNING_MODE));
		boolean enableTimeout = cmd.getBoolean(OPT_ENABLE_TIME_OUT, false);
		
		TcpConnector tcpConnnector = new TcpConnector(tcpPort);
		Socket server = tcpConnnector.connect();
		InputReader inputReader = new InputReader(server.getInputStream());
		IExecutionTimer timer = ExecutionTimerUtils.getExecutionTimer(enableTimeout);
		try {
			while (true) {
				Request request = Request.getValue(inputReader.readString());
				if (request == Request.STOP) {
					server.getOutputStream().close();
					server.close();
					return;
				}
				if (request == Request.UNKNOWN) {
					continue;
				}
				/* Request.RUN_TEST */
				try {
					long timeout = inputReader.readLong();
					List<String> tcs = inputReader.readListString();
					JunitRunnerParameters params = new JunitRunnerParameters();
					params.setTimeout(timeout);
					params.setTestcases(tcs);
					switch (runningMode) {
					case SIMPLE_RUN:
						SavSimpleRunner.executeTestcases(params, timer);
						break;
					case JUNIT:
						SavJunitRunner.executeTestcases(params, timer);
						break;
					case SEQUENCE_EXECUTION:
						List<Sequence> sequences = inputReader.readSerializableList();
						SavSequenceRunner.executeTestcases(sequences, tcs, timeout, timer);
						break;
					}
					$storeCoverage(server.getOutputStream(), true);
				} catch(Exception e) {
					e.printStackTrace();
				}
				Runtime.getRuntime().freeMemory();
			}
		} finally {
			System.out.println("SavSocketTestRunner - Exit program..");
			timer.shutdown();
			if (inputReader != null) {
				inputReader.close();
			}
			Runtime.getRuntime().halt(1);
		}
	}
	
	private void $storeCoverage(OutputStream outputStream, Boolean reset) {
		// for agent part.
	}
	
	public static enum Request {
		UNKNOWN,
		RUN_TEST,
		STOP;
		
		public static Request getValue(String str) {
			try {
				return Request.valueOf(str);
			} catch (Throwable t) {
				return UNKNOWN;
			}
		}
	}
	
	public static enum RunningMode {
		JUNIT,
		SIMPLE_RUN,
		SEQUENCE_EXECUTION
	}
}
