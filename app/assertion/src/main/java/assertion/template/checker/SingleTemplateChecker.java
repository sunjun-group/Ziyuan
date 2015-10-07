package assertion.template.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

public class SingleTemplateChecker {
	
	private List<List<ExecValue>> origPassExecValuesList;
	
	private List<List<ExecValue>> origFailExecValuesList;
	
	private List<SingleTemplate> singleTemplates;

	private List<SingleTemplate> satifiedPassTemplates;
	
	private List<SingleTemplate> satifiedFailTemplates;

	public SingleTemplateChecker(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
		this.origPassExecValuesList = origPassExecValuesList;
		this.origFailExecValuesList = origFailExecValuesList;
		
		singleTemplates = new ArrayList<SingleTemplate>();
		satifiedPassTemplates = new ArrayList<SingleTemplate>();
		satifiedFailTemplates = new ArrayList<SingleTemplate>();
	}
	
	/*
	public void checkSingleTemplates() {
		getInitTemplates();
		
		for (SingleTemplate template : initTemplates) {
			boolean isSatified = true;
			
			// check again with sampling values
			for (int i = 0; i < 10; i++) {
				List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
				assignments.addAll(template.sampling());
				
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
						
						List<ExecValue> newExecValues = new ArrayList<ExecValue>();
						for (Eq<?> e : valSet) {
							// the name of variable is not important here, only need the value
							ExecVar eVar = (ExecVar) e.getVar();
							String eVal = e.getValue().toString();
							ExecValue ev = new PrimitiveValue(eVar.getVarId(), eVal);
							newExecValues.add(ev);
						}
						
						// new pass value, add valSet into list of pass values of template
						if (newBkpData.getPassValues().size() != 0) {
							template.addPassValues(newExecValues);
							
						}
						
						// new fail value, add valSet into list of fail values of template
						if (newBkpData.getFailValues().size() != 0) {
							template.addFailValues(newExecValues);
						}
					}
				}
				
				// check template again
				isSatified = template.check();
				if (!isSatified) break;
				else if (!template.isChanged()) break;
			}
			
			if (isSatified) finalTemplates.add(template);
		}
	}
	*/
	
	public List<SingleTemplate> getSingleTemplates() {
		return singleTemplates;
	}
	
	public List<SingleTemplate> getSatifiedPassTemplates() {
		return satifiedPassTemplates;
	}
	
	public List<SingleTemplate> getSatifiedFailTemplates() {
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
		
		// hm.put(ExecVarType.REFERENCE, value)
		
		// classify exec values according to variables' types
		/*
		List<List<ExecValue>> passReferenceExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failReferenceExecValuesList = new ArrayList<List<ExecValue>>();

		List<List<ExecValue>> passBooleanExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failBooleanExecValuesList = new ArrayList<List<ExecValue>>();
				
		List<List<ExecValue>> passStringExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failStringExecValuesList = new ArrayList<List<ExecValue>>();
				
		List<List<ExecValue>> passPrimitiveExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failPrimitiveExecValuesList = new ArrayList<List<ExecValue>>();

		List<List<ExecValue>> passArrayExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> failArrayExecValuesList = new ArrayList<List<ExecValue>>();
				
		classifyExecValues(passReferenceExecValuesList, passBooleanExecValuesList, passStringExecValuesList,
				passPrimitiveExecValuesList, passArrayExecValuesList, passExecValuesList);
		classifyExecValues(failReferenceExecValuesList, failBooleanExecValuesList, failStringExecValuesList,
				failPrimitiveExecValuesList, failArrayExecValuesList, failExecValuesList);
		
		TypeTemplateChecker tc = null;
		
		tc = new PrimitiveTemplateChecker();
		tc.checkTemplates(passPrimitiveExecValuesList, failPrimitiveExecValuesList);
		
		singleTemplates.addAll(tc.getSingleTemplates());
		satifiedPassTemplates.addAll(tc.getSatifiedPassTemplates());
		satifiedFailTemplates.addAll(tc.getSatifiedFailTemplates());
		*/
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
	
	/*
	private void classifyExecValues(List<List<ExecValue>> referenceExecValuesList,
			List<List<ExecValue>> booleanExecValuesList,
			List<List<ExecValue>> stringExecValuesList,
			List<List<ExecValue>> primitiveExecValuesList,
			List<List<ExecValue>> arrayExecValuesList,
			List<List<ExecValue>> execValuesList)
	{
		for (List<ExecValue> evl : execValuesList) {
			List<ExecValue> referenceExecValues = new ArrayList<ExecValue>();
			List<ExecValue> booleanExecValues = new ArrayList<ExecValue>();
			List<ExecValue> stringExecValues = new ArrayList<ExecValue>();
			List<ExecValue> primitiveExecValues = new ArrayList<ExecValue>();
			List<ExecValue> arrayExecValues = new ArrayList<ExecValue>();
			
			for (ExecValue ev : evl) {
				switch(ev.getType()) {
				case REFERENCE:
					referenceExecValues.add(ev);
					break;
				case BOOLEAN:
					booleanExecValues.add(ev);
					break;
				case STRING:
					stringExecValues.add(ev);
					break;
				case PRIMITIVE:
					primitiveExecValues.add(ev);
					break;
				case ARRAY:
					arrayExecValues.add(ev);
					break;
				default:
					break;
				}
			}
			
			if (!referenceExecValues.isEmpty()) referenceExecValuesList.add(referenceExecValues);
			if (!booleanExecValues.isEmpty()) booleanExecValuesList.add(booleanExecValues);
			if (!stringExecValues.isEmpty()) stringExecValuesList.add(stringExecValues);
			if (!primitiveExecValues.isEmpty()) primitiveExecValuesList.add(primitiveExecValues);
			if (!arrayExecValues.isEmpty()) arrayExecValuesList.add(arrayExecValues);
		}
	}
	*/
	
}
