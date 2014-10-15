package tzuyu.core.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import tzuyu.core.main.context.SystemConfiguredDataProvider;
import faultLocalization.LineCoverageInfo;
import fit.TimedActionFixture;

public class SingleSeededBugFixture extends TimedActionFixture {
	// Parameters
	private List<String> programClasses = new ArrayList<String>();
	private List<String> programTestClasses = new ArrayList<String>();
	private String expectedBugLine;

	// Results
	private List<LineCoverageInfo> infos;
	private double maxSuspiciousness = -1.0;
	private double foundLineSuspiciousness = -1.0;

	private TzuyuCore program;

	public void projectClassPath(final String path) throws FileNotFoundException {
		program = new TzuyuCore(new SystemConfiguredDataProvider(path));
	}

	public void programClass(final String clazz) {
		programClasses.add(clazz);
	}

	public void programTestClass(final String clazz) {
		programTestClasses.add(clazz);
	}

	public void expectedBugLine(final String line) {
		expectedBugLine = line;
	}

	public boolean analyze() throws Exception {
		infos = program.faultLocalization(programClasses, programTestClasses);

		for (LineCoverageInfo info : infos) {
			if (maxSuspiciousness < info.getSuspiciousness()) {
				maxSuspiciousness = info.getSuspiciousness();
			}
			if (expectedBugLine.equals(info.getLocId())) {
				foundLineSuspiciousness = info.getSuspiciousness();
			}
		}
		return true;
	}

	public boolean bugWasFound() {
		return foundLineSuspiciousness > 0;
	}

	public boolean foundBugHasMaxSuspiciousness() {
		return bugWasFound() && foundLineSuspiciousness == maxSuspiciousness;
	}
}
