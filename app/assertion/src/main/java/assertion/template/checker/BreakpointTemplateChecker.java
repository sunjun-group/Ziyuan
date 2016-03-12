package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import sav.strategies.dto.execute.value.ExecVarType;

public class BreakpointTemplateChecker {

	private InvariantMediator im;
	
	private List<BreakpointTemplate> bkpsTemplates;
	
	private static Logger log = LoggerFactory.getLogger(BreakpointTemplateChecker.class);
	
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
	
	public BreakpointTemplate checkTemplates(BreakpointData bkpData) {
		for (int level = 0; level < 3; level++) {
			List<List<ExecValue>> passValues = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> failValues = new ArrayList<List<ExecValue>>();
			
			try {
				extractValues(passValues, failValues, bkpData, level);
			} catch (NullPointerException e) {
				log.info("Can not get values at level {}\n", level);
				break;
			}
			
			TemplateChecker tc = new TemplateChecker(passValues, failValues);
			List<Template> singleTemplates = new ArrayList<Template>();
			List<Template> compositeTemplates = new ArrayList<Template>();
			
			// only stop when we have no templates or no new data
			int i = 0;
			
			while (i < 10) {
				tc.checkSingleTemplates();
				tc.checkCompositeTemplates();
				
				singleTemplates = tc.getSingleTemplates();
				compositeTemplates = tc.getCompositeTemplates();
				
				log.info("Single templates: {}\n", singleTemplates);
//				log.info("Sat pass templates: {}\n", tc.getSatPassTemplates());
//				log.info("Sat fail templates: {}\n", tc.getSatFailTemplates());
				log.info("Composite templates: {}\n", compositeTemplates);
				
				if (singleTemplates.isEmpty() && compositeTemplates.isEmpty()) {
					log.info("No templates. Continue with next level.\n");
					break;
				}
			
				samplingNewData(bkpData.getBkp(), tc, level);
				
				if (!tc.hasNewData) {
					log.info("No new data\n");
					break;
				}
				
				i++;
			}
			
			if (!singleTemplates.isEmpty() || !compositeTemplates.isEmpty()) {
				BreakpointTemplate bkt = new BreakpointTemplate(bkpData.getBkp(),
						singleTemplates, compositeTemplates);
				log.info("Breakpoint templates: {}\n", bkt);
				return bkt;
			}
		}
		
		log.info("Reach the max level. No learned assertions\n");
		return null;

//		BreakPoint bkp = bkpData.getBkp();
//		
//		for (int level = 0; level < 2; level++) {
//			System.out.println("level = " + level);
//			
//			TemplateChecker tc = new TemplateChecker(passExecValuesList, failExecValuesList, level);
//			
//			tc.checkSingleTemplates();
//			tc.checkCompositeTemplates();
//			
//			List<Template> currSingleTemplates = tc.getSingleTemplates();
//			List<Template> currCompositeTemplates = tc.getCompositeTemplates();
//			
//			String strCurrSingleTemplates = new String(currSingleTemplates.toString());
//			String strCurrCompositeTemplates = new String(currCompositeTemplates.toString());
//			
//			log.info("single templates = " + strCurrSingleTemplates);
//			log.info("composite templates = " + strCurrCompositeTemplates);
//			
//			int i = 0;
//			while ((currSingleTemplates.size() + currCompositeTemplates.size()) > 0
//					&& i < 20) {
//				log.info("Loop = " + i);
//				
//				samplingNewData(bkp, tc);
//				
//				if (tc.newPassExecValuesList.isEmpty() && tc.newFailExecValuesList.isEmpty()) break;
//				
//				tc.recheckSingleTemplates();
//				tc.recheckCompositeTemplates();
//				
//				List<Template> newSingleTemplates = tc.getSingleTemplates();
//				List<Template> newCompositeTemplates = tc.getCompositeTemplates();
//				
//				String strNewSingleTemplates = new String(newSingleTemplates.toString());
//				String strNewCompositeTemplates = new String(newCompositeTemplates.toString());
//				
//				// if (isEquals(currSingleTemplates, newSingleTemplates) &&
//				// 		isEquals(currCompositeTemplates, newCompositeTemplates)) {
//				if (strNewSingleTemplates.equals(strCurrSingleTemplates) &&
//						strNewCompositeTemplates.equals(strCurrCompositeTemplates)) {
//					break;
//				} else {
//					currSingleTemplates = newSingleTemplates;
//					currCompositeTemplates = newCompositeTemplates;
//					
//					strCurrSingleTemplates = new String(strNewSingleTemplates);
//					strCurrCompositeTemplates = new String(strNewCompositeTemplates);
//					
//					log.info("single templates = " + currSingleTemplates);
//					log.info("composite templates = " + currCompositeTemplates);
//				}
//				
//				i++;
//			}
//			
//			if (!currSingleTemplates.isEmpty() || !currCompositeTemplates.isEmpty()) {
//				BreakpointTemplate bt = new BreakpointTemplate(bkpData.getBkp(),
//						currSingleTemplates, currCompositeTemplates);
//				System.out.println(bt);
//				bkpsTemplates.add(bt);
//				break;
//			}
//		}
	}
	
//	private boolean isEquals(List<Template> oldTemplates,
//			List<Template> newTemplates) {
//		if (oldTemplates.size() != newTemplates.size()) {
//			return false;
//		} else {
//			for (int i = 0; i < oldTemplates.size(); i++) {
//				String oldTemplate = oldTemplates.get(i).toString();
//				String newTemplate = newTemplates.get(i).toString();
//				
//				if (!oldTemplate.equals(newTemplate)) {
//					return false;
//				}
//			}
//		}
//		
//		return true;
//	}
	
	private void samplingNewData(BreakPoint bkp, TemplateChecker tc, int level) {
		tc.hasNewData = false;
		
		List<Template> stl = new ArrayList<Template>();
		stl.addAll(tc.getSingleTemplates());
		
//		stl.addAll(tc.getSatisfiledPassTemplates());
//		stl.addAll(tc.getSatisfiledFailTemplates());

		for (Template ct : tc.getCompositeTemplates()) {
			stl.addAll(((CompositeTemplate) ct).templates);
		}
		
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		
		for (Template st : stl) {
			CollectionUtils.addIfNotNullNotExist(assignments, ((SingleTemplate) st).sampling());
		}
			
		SelectiveSampling ss = new SelectiveSampling(im);
		
		log.info("Assignments: {}\n", assignments);
			
		for (List<Eq<?>> valSet : assignments) {
			List<BreakpointData> newBkpsData = null;
				
			try {
				newBkpsData = im.instDebugAndCollectData(
						CollectionUtils.listOf(bkp), ss.toInstrVarMap(valSet));
			} catch (Exception e) {
				// e.printStackTrace();
			}
				
			if (newBkpsData != null) {
				BreakpointData newBkpData = newBkpsData.get(0);
				
				// log.info("New breakpoint data: {}\n", newBkpData);
					
				List<List<ExecValue>> newPassValues = new ArrayList<List<ExecValue>>();
				List<List<ExecValue>> newFailValues = new ArrayList<List<ExecValue>>();
				
				extractValues(newPassValues, newFailValues, newBkpData, level);
				
				tc.addExecValuesList(newPassValues, newFailValues);
			}
		}
	}
	
	private void extractValues(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues, BreakpointData bkpData, int level) {
		List<List<ExecValue>> origPassValues = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> origFailValues = new ArrayList<List<ExecValue>>();
		
		// get pass values
		for (BreakpointValue bv : bkpData.getPassValues()) {
			origPassValues.add(bv.getChildren());
		}
			
		// get fail values
		for (BreakpointValue bv : bkpData.getFailValues()) {
			origFailValues.add(bv.getChildren());
		}
		
		flattenValues(passValues, origPassValues, level);
		flattenValues(failValues, origFailValues, level);	
	}
	
	private void flattenValues(List<List<ExecValue>> execValuesList,
			List<List<ExecValue>> origExecValuesList, int level) {
		for (List<ExecValue> evl : origExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			List<ExecValue> newValues = null;
			
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
					newValues = new ArrayList<ExecValue>();
					flattenRefValues(newValues, ev, level);
					newEvl.addAll(newValues);
					break;
				case ARRAY:
					newValues = new ArrayList<ExecValue>();
					flattenRefValues(newValues, ev, level);
					newEvl.addAll(newValues);
					break;
				default:
					if (!contains(newEvl, ev)) newEvl.add(ev);
					break;
				}
			}
			
			execValuesList.add(newEvl);
		}
	}
	
	private boolean contains(List<ExecValue> props, ExecValue child) {
		for (ExecValue ev : props) {
			if (ev.getVarId().equals(child.getVarId())) return true;
		}
		return false;
	}
	
	private void flattenRefValues(List<ExecValue> props, ExecValue ev, int level) {
		if (level > 0) {
			for (ExecValue child : ev.getChildren()) {
				if (child.getType() == ExecVarType.BOOLEAN || child.getType() == ExecVarType.PRIMITIVE ||
						child.getType() == ExecVarType.BYTE || child.getType() == ExecVarType.CHAR ||
						child.getType() == ExecVarType.DOUBLE || child.getType() == ExecVarType.FLOAT ||
						child.getType() == ExecVarType.INTEGER || child.getType() == ExecVarType.LONG ||
						child.getType() == ExecVarType.SHORT) {
					if (!contains(props, child)) props.add(child);
				}
				
				flattenRefValues(props, child, level - 1);
			}
		}
	}
	
}
