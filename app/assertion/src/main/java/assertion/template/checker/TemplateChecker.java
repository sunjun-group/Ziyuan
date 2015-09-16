package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import assertion.template.Template;
import icsetlv.InvariantMediator;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.dto.ExecValue;
import icsetlv.common.dto.ExecVar;
import icsetlv.common.dto.PrimitiveValue;
import icsetlv.sampling.SelectiveSampling;
import sav.common.core.Pair;
import sav.common.core.formula.Eq;
import sav.common.core.utils.CollectionUtils;

public class TemplateChecker {

	private InvariantMediator im;
	
	public TemplateChecker() {
		
	}
	
	public TemplateChecker(InvariantMediator im) {
		this.im = im;
	}
	
	public List<Pair<BreakpointData, List<Template>>> checkTemplates(List<BreakpointData> bkpsData) {
		List<Pair<BreakpointData, List<Template>>> bkpsTemplates = new ArrayList<Pair<BreakpointData, List<Template>>>();
		
		for (BreakpointData bkpData : bkpsData) {
			bkpsTemplates.add(checkTemplates(bkpData));
		}
		
		return bkpsTemplates;
	}
	
	public Pair<BreakpointData, List<Template>> checkTemplates(BreakpointData bkpData) {
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
			
		List<Template> initTemplates = checkTemplateWithExecValues(passExecValuesList, failExecValuesList);
		List<Template> finalTemplates = new ArrayList<Template>();
		
		for (Template template : initTemplates) {
			boolean isSatified = true;
			
			// check again with sampling values
			for (int i = 0; i < 10; i++) {
			// for (;;) {
				System.out.println("Template: " + template);
				
				List<List<Eq<?>>> assignments = new ArrayList<List<Eq<?>>>();
				assignments.addAll(template.sampling());
				
				SelectiveSampling ss = new SelectiveSampling(im);
				
				for (List<Eq<?>> valSet : assignments) {
					List<BreakpointData> newBkpsData = null;
					
					try {
						newBkpsData = im.instDebugAndCollectData(
								CollectionUtils.listOf(bkpData.getBkp()), ss.toInstrVarMap(valSet));
					} catch (Exception e) {
						e.printStackTrace();
					}
							
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
				
				// check template again
				isSatified = template.check();
				if (!isSatified) break;
				else if (!template.isChanged()) break;
			}
			
			if (isSatified) finalTemplates.add(template);
		}
		
		System.out.println("Final templates: " + finalTemplates);
		return new Pair<BreakpointData, List<Template>>(bkpData, finalTemplates);
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
	
	private List<Template> checkTemplateWithExecValues(List<List<ExecValue>> origPassExecValuesList,
			List<List<ExecValue>> origFailExecValuesList) {
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
		
		// classify exec values according to variables' types
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
		
		System.out.println("pass reference: " + passReferenceExecValuesList);
		System.out.println("fail reference: " + failReferenceExecValuesList);
		
		System.out.println("pass boolean: " + passBooleanExecValuesList);
		System.out.println("fail boolean: " + failBooleanExecValuesList);
		
		System.out.println("pass string: " + passStringExecValuesList);
		System.out.println("fail string: " + failStringExecValuesList);
		
		System.out.println("pass primitive: " + passPrimitiveExecValuesList);
		System.out.println("fail primitive: " + failPrimitiveExecValuesList);
		
		System.out.println("pass array: " + passArrayExecValuesList);
		System.out.println("fail array: " + failArrayExecValuesList);
		
		List<Template> templates = new ArrayList<Template>();
		
		TypeTemplateChecker tc = null;
		
		tc = new PrimitiveTemplateChecker();
		List<Template> primitiveTemplates = tc.checkTemplates(passPrimitiveExecValuesList, failPrimitiveExecValuesList);
		
		templates.addAll(primitiveTemplates);
		
		return templates;
	}
	
}
