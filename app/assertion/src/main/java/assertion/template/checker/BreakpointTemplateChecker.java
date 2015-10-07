package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import sav.strategies.dto.execute.value.ExecValue;

public class BreakpointTemplateChecker {

	private InvariantMediator im;
	
	private List<BreakpointTemplate> bkpsTemplates;
	
	public BreakpointTemplateChecker(InvariantMediator im) {
		this.im = im;
		bkpsTemplates = new ArrayList<BreakpointTemplate>();
	}
	
	public void checkTemplates(List<BreakpointData> bkpsData) {
		for (BreakpointData bkpData : bkpsData) {
			checkTemplates(bkpData);
		}
	}
	
	public void checkTemplates(BreakpointData bkpData) {
		List<List<ExecValue>> passExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failExecValuesList = new ArrayList<List<ExecValue>>();
			
		// get pass values
		for (BreakpointValue bv : bkpData.getPassValues()) {
			passExecValuesList.add(bv.getChildren());
		}
			
		// get fail values
		for (BreakpointValue bv : bkpData.getFailValues()) {
			failExecValuesList.add(bv.getChildren());
		}
			
		TemplateChecker tc = new TemplateChecker(passExecValuesList, failExecValuesList);
		tc.checkTemplates();
		
		BreakpointTemplate bt = new BreakpointTemplate(bkpData.getBkp(),
				tc.getSingleTemplates(), tc.getCompositeTemplates());
		System.out.println(bt);
		bkpsTemplates.add(bt);
	}
	
}
