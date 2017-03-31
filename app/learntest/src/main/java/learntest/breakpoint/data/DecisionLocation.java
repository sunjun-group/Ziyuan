package learntest.breakpoint.data;

import sav.strategies.dto.ClassLocation;

public class DecisionLocation extends ClassLocation implements Comparable<DecisionLocation> {
	private boolean loop;

	public DecisionLocation(String className, String methodName, int lineNumber, boolean loop) {
		super(className, methodName, lineNumber);
		this.loop = loop;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	@Override
	public int compareTo(DecisionLocation location) {
		return lineNo - location.lineNo;
	}
	
}
