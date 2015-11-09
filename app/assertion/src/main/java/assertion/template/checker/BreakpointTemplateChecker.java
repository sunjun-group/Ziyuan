package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.sampling.SelectiveSampling;
import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import sav.common.core.formula.Eq;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecValue;

public class BreakpointTemplateChecker {

	private InvariantMediator im;
	
	private List<BreakpointTemplate> bkpsTemplates;
	
	public BreakpointTemplateChecker(InvariantMediator im) {
		this.im = im;
		bkpsTemplates = new ArrayList<BreakpointTemplate>();
	}
	
	public List<BreakpointTemplate> checkTemplates(List<BreakpointData> bkpsData) {
		for (BreakpointData bkpData : bkpsData) {
			checkTemplates(bkpData);
		}
		
		return bkpsTemplates;
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
		
		TemplateChecker tc = new TemplateChecker(passExecValuesList, failExecValuesList);
		
		tc.checkSingleTemplates();
		List<Template> currSingleTemplates = tc.getSingleTemplates();
		
		tc.checkCompositeTemplates();
		List<Template> currCompositeTemplates = tc.getCompositeTemplates();
		
		System.out.println("single templates = " + currSingleTemplates);
		System.out.println("composite templates = " + currCompositeTemplates);
		
		int i = 0;
		while ((currSingleTemplates.size() + currCompositeTemplates.size()) > 0
				&& i < 10) {
			System.out.println("loop = " + i);
			
			samplingNewData(bkp, tc);
			
			tc.checkSingleTemplates();
			tc.checkCompositeTemplates();
			
			List<Template> newSingleTemplates = tc.getSingleTemplates();
			List<Template> newCompositeTemplates = tc.getCompositeTemplates();
			
			if (isEquals(currSingleTemplates, newSingleTemplates) &&
					isEquals(currCompositeTemplates, newCompositeTemplates)) {
				break;
			} else {
				currSingleTemplates = newSingleTemplates;
				currCompositeTemplates = newCompositeTemplates;
				
				System.out.println("single templates = " + currSingleTemplates);
				System.out.println("composite templates = " + currCompositeTemplates);
			}
			
			i++;
		}
		
		BreakpointTemplate bt = new BreakpointTemplate(bkpData.getBkp(),
				currSingleTemplates, currCompositeTemplates);
		
		System.out.println(bt);
		bkpsTemplates.add(bt);
	}
	
	private boolean isEquals(List<Template> oldTemplates,
			List<Template> newTemplates) {
		if (oldTemplates.size() != newTemplates.size()) {
			return false;
		} else {
			for (int i = 0; i < oldTemplates.size(); i++) {
				String oldTemplate = oldTemplates.get(i).toString();
				String newTemplate = newTemplates.get(i).toString();
				
				if (!oldTemplate.equals(newTemplate)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void samplingNewData(BreakPoint bkp, TemplateChecker tc) {
		List<Template> stl = new ArrayList<Template>();
		stl.addAll(tc.getSingleTemplates());
		for (Template ct : tc.getCompositeTemplates()) {
			stl.addAll(((CompositeTemplate) ct).templates);
		}
		
		for (Template st : stl) {
			List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
			assignments.addAll(((SingleTemplate) st).sampling());
			
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
