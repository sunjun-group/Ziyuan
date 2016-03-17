package assertion.template.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

public class SingleTemplateChecker {
	
	private List<List<ExecValue>> passValues;
	
	private List<List<ExecValue>> failValues;
	
	private List<Template> singleTemplates;

	private List<Template> satPassTemplates;
	
	private List<Template> satFailTemplates;
	
	private static Logger log = LoggerFactory.getLogger(SingleTemplateChecker.class);
	
	public SingleTemplateChecker(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		this.passValues = passValues;
		this.failValues = failValues;
		
		singleTemplates = new ArrayList<Template>();
		satPassTemplates = new ArrayList<Template>();
		satFailTemplates = new ArrayList<Template>();
	}
	
	public List<Template> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<Template> getSatPassTemplates() {
		return satPassTemplates;
	}
	
	public List<Template> getSatFailTemplates() {
		return satFailTemplates;
	}
	
	/*
	public void recheckSingleTemplate(SingleTemplate st) {
		List<ExecValue> origEvl = st.getPassExecValuesList().get(0);
		
		for (List<ExecValue> evl : newPassExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			
			for (ExecValue ev1 : origEvl) {
				for (ExecValue ev2 : evl) {
					if (ev1.getVarId().equals(ev2.getVarId())) {
						newEvl.add(ev2);
					}
				}
			}
			
			st.addPassValues(newEvl);
		}
		
		for (List<ExecValue> evl : newFailExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			
			for (ExecValue ev1 : origEvl) {
				for (ExecValue ev2 : evl) {
					if (ev1.getVarId().equals(ev2.getVarId())) {
						newEvl.add(ev2);
					}
				}
			}
			
			st.addFailValues(newEvl);
		}
	}
	*/
	
//	public void addNewExecValuesList(SingleTemplate st,
//			List<List<ExecValue>> newPassExecValuesList,
//			List<List<ExecValue>> newFailExecValuesList) {
//		List<ExecValue> origEvl = st.getPassExecValuesList().get(0);
//		
//		for (List<ExecValue> evl : newPassExecValuesList) {
//			List<ExecValue> newEvl = new ArrayList<ExecValue>();
//			
//			for (ExecValue ev1 : origEvl) {
//				for (ExecValue ev2 : evl) {
//					if (ev1.getVarId().equals(ev2.getVarId())) {
//						newEvl.add(ev2);
//						break;
//					}
//				}
//			}
//			
//			st.addPassValues(newEvl);
//		}
//		
//		for (List<ExecValue> evl : newFailExecValuesList) {
//			List<ExecValue> newEvl = new ArrayList<ExecValue>();
//			
//			for (ExecValue ev1 : origEvl) {
//				for (ExecValue ev2 : evl) {
//					if (ev1.getVarId().equals(ev2.getVarId())) {
//						newEvl.add(ev2);
//						break;
//					}
//				}
//			}
//			
//			st.addFailValues(newEvl);
//		}
//		
//		
//	}
	
//	public void removeTemplates(List<Template> toRemove, List<Template> templates) {
//		for (Template t : toRemove) {
//			templates.remove(t);
//		}
//		
//		toRemove.clear();
//	}
//	
//	public boolean recheckSingleTemplates(List<List<ExecValue>> newPassExecValuesList,
//			List<List<ExecValue>> newFailExecValuesList) {
//		boolean moreTemplates = false;
//		
//		passValues.addAll(newPassExecValuesList);
//		failValues.addAll(newFailExecValuesList);
//		
//		List<List<ExecValue>> passExecValuesList = new ArrayList<List<ExecValue>>();
//		flattenValues(passExecValuesList, newPassExecValuesList);
//		
//		List<List<ExecValue>> failExecValuesList = new ArrayList<List<ExecValue>>();
//		flattenValues(failExecValuesList, newFailExecValuesList);
//		
//		List<Template> toRemove = new ArrayList<Template>();
//		
//		for (Template t : satifiedPassTemplates) {
//			SingleTemplate st = (SingleTemplate) t;
//			addNewExecValuesList(st, passExecValuesList, failExecValuesList);
//			if (st instanceof OnePrimIlpTemplate || st instanceof TwoPrimIlpTemplate ||
//					st instanceof ThreePrimIlpTemplate) {
//				continue;
//			} else {
//				st.check();
//				if (!st.isSatisfiedAllPassValues()) toRemove.add(st);
//			}
//		}
//		
//		removeTemplates(toRemove, satifiedPassTemplates);
//		
//		for (Template t : satifiedFailTemplates) {
//			SingleTemplate st = (SingleTemplate) t;
//			if (st instanceof OnePrimIlpTemplate || st instanceof TwoPrimIlpTemplate ||
//					st instanceof ThreePrimIlpTemplate) {
//				continue;
//			} else {
//				// already add before
//				addNewExecValuesList(st, passExecValuesList, failExecValuesList);
//				st.check();
//				if (!st.isSatisfiedAllFailValues()) toRemove.add(st);
//			}
//		}
//		
//		removeTemplates(toRemove, satifiedFailTemplates);
//		
//		for (Template t : singleTemplates) {
//			SingleTemplate st = (SingleTemplate) t;
//			addNewExecValuesList(st, passExecValuesList, failExecValuesList);
//			boolean valid = st.check();
//			if (!valid) toRemove.add(st);
//			if (!valid && st.isSatisfiedAllPassValues()) {
//				moreTemplates = true;
//				satifiedPassTemplates.add(st);
//			}
//			if (!valid && st.isSatisfiedAllFailValues()) {
//				moreTemplates = true;
//				satifiedFailTemplates.add(st);
//			}
//		}
//		
//		removeTemplates(toRemove, singleTemplates);
//		
//		return moreTemplates;
//	}
	
	public void checkSingleTemplates() {
		HashMap<ExecVarType, List<List<ExecValue>>> passMap = classifyExecValuesList(passValues);
		HashMap<ExecVarType, List<List<ExecValue>>> failMap = classifyExecValuesList(failValues);
		
		for (ExecVarType t : passMap.keySet()) {
			if (failMap.containsKey(t)) {
				TypeTemplateChecker tc = null;
				switch (t) {
				case PRIMITIVE:
				case DOUBLE:
				case FLOAT:
				case BYTE:
				case CHAR:
				case INTEGER:
				case LONG:
				case SHORT:
					tc = new NumberTemplateChecker();
					break;
				case BOOLEAN:
					tc = new BooleanTemplateChecker();
				default:
					break;
				}
				if (tc != null) {
					try {
						if (passValues.size() == passMap.get(t).size() &&
								failValues.size() == failMap.get(t).size()) {
							tc.checkTemplates(passMap.get(t), failMap.get(t));
					
							singleTemplates.addAll(tc.getSingleTemplates());
							satPassTemplates.addAll(tc.getSatPassTemplates());
							satFailTemplates.addAll(tc.getSatFailTemplates());
						}
					} catch (IndexOutOfBoundsException e) {
						log.info("The list of features do not have the same length\n");
					}
				}
			}
		}
	}

	private HashMap<ExecVarType, List<List<ExecValue>>> classifyExecValuesList(
			List<List<ExecValue>> execValuesList) {
		HashMap<ExecVarType, List<List<ExecValue>>> hm = new HashMap<ExecVarType, List<List<ExecValue>>>();
		
		for (List<ExecValue> evl : execValuesList) {
			HashMap<ExecVarType, List<ExecValue>> tmp = classifyExecValues(evl);
			
			for (ExecVarType t : tmp.keySet()) {
				if (hm.containsKey(t)) {
					hm.get(t).add(tmp.get(t));
				} else {
					List<List<ExecValue>> evsl = new ArrayList<List<ExecValue>>();
					evsl.add(tmp.get(t));
					hm.put(t, evsl);
				}
				
			}
		}
		
		return hm;
	}
	
	private HashMap<ExecVarType, List<ExecValue>> classifyExecValues(List<ExecValue> execValues) {
		HashMap<ExecVarType, List<ExecValue>> hm = new HashMap<ExecVarType, List<ExecValue>>();
		
		for (ExecValue ev : execValues) {
			ExecVarType t = ev.getType();
			
			if (hm.containsKey(t)) {
				hm.get(ev.getType()).add(ev);
			} else {
				List<ExecValue> evl = new ArrayList<ExecValue>();
				evl.add(ev);
				hm.put(t, evl);
			}
		}
		
		return hm;
	}

}
