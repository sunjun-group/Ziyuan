package assertion.template.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

public class SingleTemplateChecker {
	
	private List<List<ExecValue>> origPassExecValuesList;
	
	private List<List<ExecValue>> origFailExecValuesList;
	
	private List<Template> singleTemplates;

	private List<Template> satifiedPassTemplates;
	
	private List<Template> satifiedFailTemplates;

	public SingleTemplateChecker(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
		this.origPassExecValuesList = origPassExecValuesList;
		this.origFailExecValuesList = origFailExecValuesList;
		
		singleTemplates = new ArrayList<Template>();
		satifiedPassTemplates = new ArrayList<Template>();
		satifiedFailTemplates = new ArrayList<Template>();
	}
	
	public List<Template> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<Template> getSatifiedPassTemplates() {
		return satifiedPassTemplates;
	}
	
	public List<Template> getSatifiedFailTemplates() {
		return satifiedFailTemplates;
	}
	
	public void checkSingleTemplates() {
		List<List<ExecValue>> passExecValuesList = new ArrayList<List<ExecValue>>();
		for (List<ExecValue> evl : origPassExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
					List<ExecValue> newValues = new ArrayList<ExecValue>();
					flattenValues(newValues, ev);
					newEvl.addAll(newValues);
					break;
				default:
					newEvl.add(ev);
					break;
				}
			}
			
			passExecValuesList.add(newEvl);
		}
		
		List<List<ExecValue>> failExecValuesList = new ArrayList<List<ExecValue>>();
		for (List<ExecValue> evl : origFailExecValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
					
			for (ExecValue ev : evl) {
				switch (ev.getType()) {
				case REFERENCE:
					List<ExecValue> newValues = new ArrayList<ExecValue>();
					flattenValues(newValues, ev);
					newEvl.addAll(newValues);
					break;
				default:
					newEvl.add(ev);
					break;
				}
			}
					
			failExecValuesList.add(newEvl);
		}
		
		HashMap<ExecVarType, List<List<ExecValue>>> passMap = classifyExecValuesList(passExecValuesList);
		HashMap<ExecVarType, List<List<ExecValue>>> failMap = classifyExecValuesList(failExecValuesList);
		
		for (ExecVarType t : passMap.keySet()) {
			if (failMap.containsKey(t)) {
				TypeTemplateChecker tc = null;
				switch (t) {
				case PRIMITIVE:
					tc = new PrimitiveTemplateChecker();
					break;
				default:
					break;
				}
				tc.checkTemplates(passMap.get(t), failMap.get(t));
				
				singleTemplates.addAll(tc.getSingleTemplates());
				satifiedPassTemplates.addAll(tc.getSatifiedPassTemplates());
				satifiedFailTemplates.addAll(tc.getSatifiedFailTemplates());
			}
		}
	}
	
	private void flattenValues(List<ExecValue> props, ExecValue ev) {
		for (ExecValue child : ev.getChildren()) {
			if (child.getChildren() != null) {
				flattenValues(props, child);
			} else {
				props.add(child);
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
