package tzuyu.core.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import sav.commons.utils.TestConfigUtils;
import faultLocalization.LineCoverageInfo;
import fit.TimedActionFixture;

public class SingleSeededBugFixture extends TimedActionFixture {
	protected static final String LINE_FEED = "<br/>";
	// Parameters
	protected List<String> programClasses = new ArrayList<String>();
	protected List<String> programTestClasses = new ArrayList<String>();
	protected boolean useSlicer = true;
	private String expectedBugLine;

	// Results
	protected List<LineCoverageInfo> infos;
	protected double maxSuspiciousness = -1.0;
	private double foundLineSuspiciousness = -1.0;

	private SystemConfiguredDataProvider context = new SystemConfiguredDataProvider();
	private TzuyuCore program;

	public void projectClassPath(final String path) throws FileNotFoundException {
		context.addProjectClassPath(path);
	}

	protected TzuyuCore getProgram() {
		if (program == null) {
			program = new TzuyuCore(context);
		}
		return program;
	}

	public void javaHome(final String path) {
		context.setJavaHome(path);
	}

	public void tracerJarPath(final String path) {
		context.setTracerJarPath(path);
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

	public void useSlicer(final boolean useSlicer) {
		this.useSlicer = useSlicer;
	}

	public void updateSysClassLoader() throws Exception {
		for (String path : context.getProjectClassPath()) {
			TestConfigUtils.addToSysClassLoader(new File(path));
		}
	}

	public final boolean analyze() throws Exception {
		infos = getProgram().faultLocalization(programClasses, programTestClasses, useSlicer);
		checkAnalyzedResults();
		return true;
	}

	protected void checkAnalyzedResults() {
		for (LineCoverageInfo info : infos) {
			if (maxSuspiciousness < info.getSuspiciousness()) {
				maxSuspiciousness = info.getSuspiciousness();
			}
			if (expectedBugLine.equals(info.getLocId())) {
				foundLineSuspiciousness = info.getSuspiciousness();
			}
		}
	}

	public boolean bugWasFound() {
		return foundLineSuspiciousness > 0;
	}

	public boolean foundBugHasMaxSuspiciousness() {
		return bugWasFound() && foundLineSuspiciousness == maxSuspiciousness;
	}

	public void show() throws Exception {
		final Object result = method(0).invoke(getActor());
		cells.last().more = td(result.toString());
	}

	public String lineCoverageInfo() {
		final StringBuilder builder = new StringBuilder();
		if (infos == null || infos.size() == 0) {
			builder.append("No line coverage information was detected.");
		} else {
			for (LineCoverageInfo info : infos) {
				if (info.getSuspiciousness() > 0.0) {
					builder.append(info.toString()).append(LINE_FEED);
				}
			}
		}
		return builder.toString();
	}

	public SystemConfiguredDataProvider getContext() {
		return context;
	}
}
