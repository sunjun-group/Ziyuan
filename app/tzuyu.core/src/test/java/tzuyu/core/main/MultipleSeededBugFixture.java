package tzuyu.core.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import faultLocalization.LineCoverageInfo;

public class MultipleSeededBugFixture extends SingleSeededBugFixture {
	// Map between an expected bug line and the found suspiciousness
	// (-1 if not found)
	private Map<String, Double> expectedBugLines = new HashMap<String, Double>();

	@Override
	public void expectedBugLine(final String line) {
		expectedBugLines.put(line, -1.0);
	}

	@Override
	protected void checkAnalyzedResults() {
		for (LineCoverageInfo info : infos) {
			final Double value = expectedBugLines.get(info.toString());
			if (value != null && value.doubleValue() < info.getSuspiciousness()) {
				expectedBugLines.put(info.toString(), info.getSuspiciousness());
			}
		}
	}

	@Override
	public boolean bugWasFound() {
		// At least 1 bug was found
		for (Entry<String, Double> entry : expectedBugLines.entrySet()) {
			final double suspiciousness = entry.getValue().doubleValue();
			if (suspiciousness > 0.0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean foundBugHasMaxSuspiciousness() {
		// TODO correct this
		return bugWasFound();
	}
	
	public String lineSuspiciousness() {
		final StringBuilder builder = new StringBuilder();
		for (Entry<String, Double> entry : expectedBugLines.entrySet()) {
			final String codeLine = entry.getKey();
			final double suspiciousness = entry.getValue().doubleValue();
			builder.append(codeLine).append(" > ").append(suspiciousness).append(LINE_FEED);
		}
		return builder.toString();
	}
}
