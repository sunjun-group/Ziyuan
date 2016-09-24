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
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.ByteValue;
import sav.strategies.dto.execute.value.CharValue;
import sav.strategies.dto.execute.value.DoubleValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.FloatValue;
import sav.strategies.dto.execute.value.IntegerValue;
import sav.strategies.dto.execute.value.LongValue;
import sav.strategies.dto.execute.value.ShortValue;

public class BreakpointTemplateChecker {

	private InvariantMediator im;
	
	private List<BreakpointTemplate> bkpsTemplates;
	
	private List<ExecValue> allExecValues;
	
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
		List<ExecValue> oldAllExecValues = null;
		
		for (int level = 0; level < 3; level++) {
			allExecValues = new ArrayList<ExecValue>();
			
			List<List<ExecValue>> passValues = new ArrayList<List<ExecValue>>();
			List<List<ExecValue>> failValues = new ArrayList<List<ExecValue>>();
			
			try {
				extractValues(passValues, failValues, bkpData, level);
			} catch (NullPointerException e) {
				log.info("Can not get values at level {}\n", level);
				break;
			}
			
			if (noNewFeature(allExecValues, oldAllExecValues)) break;
			
			TemplateChecker tc = new TemplateChecker(passValues, failValues);
			List<Template> singleTemplates = new ArrayList<Template>();
			List<Template> compositeTemplates = new ArrayList<Template>();
			
			if (checkTemplates(bkpData, tc, level, true)) {
				singleTemplates.addAll(tc.getSingleTemplates());
				compositeTemplates.addAll(tc.getCompositeTemplates());
			
				BreakpointTemplate bkt = new BreakpointTemplate(bkpData.getBkp(),
						singleTemplates, compositeTemplates);
				log.info("Breakpoint templates: {}\n", bkt);
				return bkt;
			}
			
			oldAllExecValues = allExecValues;
			
		}
		
		log.info("Reach the max level. No learned assertions\n");
		return null;
	}
	
	private boolean noNewFeature(List<ExecValue> execValues, List<ExecValue> oldExecValues) {
		if (oldExecValues == null) return false;
		
		for (ExecValue ev : execValues) {
			if (!contains(oldExecValues, ev)) return false;
		}
		
		return true;
	}
	
	private boolean checkTemplatesInactive(BreakpointData bkpData, TemplateChecker tc, int level) {
		List<Template> templates = new ArrayList<Template>();
		
		tc.checkSingleTemplates();
		templates.addAll(tc.getSingleTemplates());
		
		if (!templates.isEmpty()) return true;
		
		tc.checkCompositeTemplates();
		templates.addAll(tc.getCompositeTemplates());
		
		if (!templates.isEmpty()) return true;
		
		return false;
	}
	
	private boolean checkTemplatesActive(BreakpointData bkpData, TemplateChecker tc, int level) {
		List<Template> templates = null;
		
		for (int i = 0; i < 10; i++) {
			templates = new ArrayList<Template>();
			
			tc.checkSingleTemplates();
			templates.addAll(tc.getSingleTemplates());
			
			if (!templates.isEmpty()) {
				Template t = templates.get(0);
//				log.info("Single template {}\n", t);
				
				samplingNewData(bkpData.getBkp(), tc, t, level);
				
				if (!tc.hasNewData) return true;
				else continue;
			}
			
			tc.checkCompositeTemplates();
			templates.addAll(tc.getCompositeTemplates());
			
			if (!templates.isEmpty()) {
				Template t = templates.get(0);
//				log.info("Composite template {}\n", t);
				
				samplingNewData(bkpData.getBkp(), tc, t, level);
				
				if (!tc.hasNewData) return true;
				else continue;
			}
			
			return false;
		}
		
		if (templates.isEmpty()) return false;
		else return true;
	}
	
	private boolean checkTemplates(BreakpointData bkpData, TemplateChecker tc,
			int level, boolean isActive) {
		if (isActive) return checkTemplatesActive(bkpData, tc, level);
		else return checkTemplatesInactive(bkpData, tc, level);
	}
	
	private void samplingNewData(BreakPoint bkp, TemplateChecker tc, Template t, int level) {
		tc.hasNewData = false;
		
		List<Template> stl = new ArrayList<Template>();
		
		if (t instanceof SingleTemplate) {
			stl.add(t);
		} else if (t instanceof CompositeTemplate) {
			stl.addAll(((CompositeTemplate) t).getSingleTemplates());
		}
		
		List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
		
		for (Template st : stl) {
			CollectionUtils.addIfNotNullNotExist(assignments, ((SingleTemplate) st).sampling());
		}
			
		SelectiveSampling ss = new SelectiveSampling(im);
		
//		log.info("Assignments: {}\n", assignments);
			
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
				
//				log.info("New breakpoint data: {}\n", newBkpData);
					
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
		
		normalise(passValues);
		normalise(failValues);
	}
	
	private void normalise(List<List<ExecValue>> execValuesList) {
		for (List<ExecValue> evl: execValuesList) {
			if (evl.size() != allExecValues.size()) {
				for (int i = 0; i < allExecValues.size(); i++) {
					if (i >= evl.size()) {
						evl.add(i, newExecValue(allExecValues.get(i)));
					} else {
						ExecValue ev1 = evl.get(i);
						ExecValue ev2 = allExecValues.get(i);
						
						if (!ev1.getVarId().equals(ev2.getVarId())) {
							evl.add(i, newExecValue(ev2));
						}
					}
				}
			}
		}
	}
	
	private void flattenValues(List<List<ExecValue>> execValuesList,
			List<List<ExecValue>> origExecValuesList, int level) {
		for (List<ExecValue> evl : origExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			List<ExecValue> newValues = null;
			
			// get primitive value first
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
				case ARRAY:
					break;
				default:
					if (!contains(newEvl, ev)) newEvl.add(ev);
					if (!contains(allExecValues, ev)) allExecValues.add(newExecValue(ev));
					break;
				}
			}
			
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
				case ARRAY:
					newValues = new ArrayList<ExecValue>();
					flattenRefValues(newValues, ev, level);
					
					for (ExecValue newValue : newValues) {
						if (!contains(newEvl, newValue)) newEvl.add(newValue);
						if (!contains(allExecValues, newValue)) allExecValues.add(newExecValue(newValue));
					}
					break;
				default:
					break;
				}
			}
			
			
			
			execValuesList.add(newEvl);
		}
	}
	
	private ExecValue newExecValue(ExecValue ev) {
		ExecValue newEv = null;
		
		switch (ev.getType()) {
		case BOOLEAN:
			newEv = new BooleanValue(ev.getVarId(), null);
			break;
		case BYTE:
			newEv = new ByteValue(ev.getVarId(), null);
			break;
		case CHAR:
			newEv = new CharValue(ev.getVarId(), null);
			break;
		case DOUBLE:
			newEv = new DoubleValue(ev.getVarId(), null);
			break;
		case FLOAT:
			newEv = new FloatValue(ev.getVarId(), null);
			break;
		case INTEGER:
			newEv = new IntegerValue(ev.getVarId(), null);
			break;
		case LONG:
			newEv = new LongValue(ev.getVarId(), null);
			break;
		case SHORT:
			newEv = new ShortValue(ev.getVarId(), null);
			break;
		default:
			break;
		}
		
		return newEv;
	}
	
	private boolean contains(List<ExecValue> props, ExecValue child) {
		for (ExecValue ev : props) {
			if (ev.getVarId().equals(child.getVarId())) return true;
		}
		return false;
	}
	
	private void flattenRefValues(List<ExecValue> props, ExecValue ev, int level) {
		if (level == 0) {
			for (ExecValue child : ev.getChildren()) {
				if (child.getVarId().contains(".isNull") && !contains(props, child))
					props.add(child);
			}
		} else if (level > 0) {
			for (ExecValue child : ev.getChildren()) {
				if (child.getType() == ExecVarType.BOOLEAN || child.getType() == ExecVarType.PRIMITIVE ||
						child.getType() == ExecVarType.BYTE || child.getType() == ExecVarType.CHAR ||
						child.getType() == ExecVarType.DOUBLE || child.getType() == ExecVarType.FLOAT ||
						child.getType() == ExecVarType.INTEGER || child.getType() == ExecVarType.LONG ||
						child.getType() == ExecVarType.SHORT) {
					if (!contains(props, child)) props.add(child);
				} else if (child.getType() == ExecVarType.REFERENCE || child.getType() == ExecVarType.ARRAY) {
					flattenRefValues(props, child, level - 1);
				}
			}
		}
	}
	
}
