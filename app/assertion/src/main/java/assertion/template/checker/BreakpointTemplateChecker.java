package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.sampling.SelectiveSampling;
import invariant.templates.SingleTemplate;
import sav.common.core.formula.Eq;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.PrimitiveValue;

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
		
		BreakPoint bkp = bkpData.getBkp();
		
		System.out.println(passExecValuesList);
		System.out.println(failExecValuesList);
		TemplateChecker tc = new TemplateChecker(passExecValuesList, failExecValuesList);
		
		tc.checkSingleTemplates();
		int size = tc.getSingleTemplates().size();
		
		while (size > 0) {
			samplingNewData(bkp, tc);
			tc.checkSingleTemplates();
			if (tc.getSingleTemplates().size() == size) break;
			else size = tc.getSingleTemplates().size();
		}
		
		tc.checkCompositeTemplates();
		
		BreakpointTemplate bt = new BreakpointTemplate(bkpData.getBkp(),
				tc.getSingleTemplates(), tc.getCompositeTemplates());
		System.out.println(bt);
		bkpsTemplates.add(bt);
	}
	
	private void samplingNewData(BreakPoint bkp, TemplateChecker tc) {
		for (SingleTemplate st : tc.getSingleTemplates()) {
			List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
			assignments.addAll(st.sampling());
			
			SelectiveSampling ss = new SelectiveSampling(im);
			
			for (List<Eq<?>> valSet : assignments) {
				List<BreakpointData> newBkpsData = null;
				
				try {
					newBkpsData = im.instDebugAndCollectData(
							CollectionUtils.listOf(bkp), ss.toInstrVarMap(valSet));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (newBkpsData != null) {
					BreakpointData newBkpData = newBkpsData.get(0);
					
					List<List<ExecValue>> newPassExecValuesList = new ArrayList<List<ExecValue>>();
					List<List<ExecValue>> newFailExecValuesList = new ArrayList<List<ExecValue>>();
						
					// get pass values
					for (BreakpointValue bv : newBkpData.getPassValues()) {
						newPassExecValuesList.add(bv.getChildren());
					}
						
					// get fail values
					for (BreakpointValue bv : newBkpData.getFailValues()) {
						newFailExecValuesList.add(bv.getChildren());
					}
					
					tc.addExecValuesList(newPassExecValuesList, newFailExecValuesList);
				}
			}
		}
	}
	
}
