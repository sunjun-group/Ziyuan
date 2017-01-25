package icsetlv.variable;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;

public class VisibleVariableExtractor extends DebugValueExtractor {
	
	@Override
	public BreakpointValue extractValue(BreakPoint bkp, BreakpointEvent event)
			throws IncompatibleThreadStateException, AbsentInformationException, SavException {
		if (bkp == null) {
			return null;
		}
		
		BreakpointValue bkVal = new BreakpointValue(bkp.getId());
		
		ThreadReference thread = event.thread();
		synchronized (thread) {
			if (!thread.frames().isEmpty()) {
				StackFrame frame = findFrameByLocation(thread.frames(), event.location());
				bkVal.setVars(frame.visibleVariables());
			}
		}
		
		return bkVal;
	}

}
