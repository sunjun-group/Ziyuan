package assertion.template.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.SingleTemplate;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVarType;

public class SingleTemplateChecker {
	
	private List<List<ExecValue>> passValues;
	
	private List<List<ExecValue>> failValues;
	
	private List<SingleTemplate> validTemplates;

	private List<SingleTemplate> allTemplates;
	
	private static Logger log = LoggerFactory.getLogger(SingleTemplateChecker.class);
	
	public SingleTemplateChecker(List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues) {
		this.passValues = passValues;
		this.failValues = failValues;
		
		validTemplates = new ArrayList<SingleTemplate>();
		allTemplates = new ArrayList<SingleTemplate>();
	}
	
	public List<SingleTemplate> getValidTemplates() {
		return validTemplates;
	}
	
	public List<SingleTemplate> getAllTemplates() {
		return allTemplates;
	}
	
	public void checkSingleTemplates() {
		HashMap<ExecVarType, List<List<ExecValue>>> passMap = classifyExecValuesList(passValues);
		HashMap<ExecVarType, List<List<ExecValue>>> failMap = classifyExecValuesList(failValues);
		
		List<ExecVarType> ts = Arrays.asList(ExecVarType.BOOLEAN, ExecVarType.BYTE,
				ExecVarType.CHAR, ExecVarType.SHORT, ExecVarType.INTEGER, ExecVarType.LONG,
				ExecVarType.FLOAT, ExecVarType.DOUBLE);
		
		for (ExecVarType t : ts) {
			if (passMap.containsKey(t) && failMap.containsKey(t)) {
				TypeTemplateChecker tc = null;
				switch (t) {
//				case PRIMITIVE:
				case DOUBLE:
				case FLOAT:
				case BYTE:
//				case CHAR:
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
					
							validTemplates.addAll(tc.getValidTemplates());
							if (!validTemplates.isEmpty()) return;
							
							allTemplates.addAll(tc.getAllTemplates());
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
