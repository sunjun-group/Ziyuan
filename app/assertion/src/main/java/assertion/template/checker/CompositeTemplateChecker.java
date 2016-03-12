package assertion.template.checker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import invariant.templates.CompositeTemplate;
import invariant.templates.ConjunctionTemplate;
import invariant.templates.DisjunctionTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import invariant.templates.onefeature.OneNumIlpTemplate;
import invariant.templates.threefeatures.ThreeNumIlpTemplate;
import invariant.templates.twofeatures.TwoNumIlpTemplate;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.core.StringDividerProcessor;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.strategies.dto.execute.value.ExecValue;

public class CompositeTemplateChecker {

	private List<Template> satisfiedPassTemplates;
	
	private List<Template> satisfiedFailTemplates;

	private List<Template> compositeTemplates;
	
	private static Logger log = LoggerFactory.getLogger(CompositeTemplateChecker.class);
	
// 	private boolean needCombine;
	
	public CompositeTemplateChecker(List<Template> satifiedPassTemplates,
			List<Template> satifiedFailTemplates) {
		this.satisfiedPassTemplates = satifiedPassTemplates;
		this.satisfiedFailTemplates = satifiedFailTemplates;
		compositeTemplates = new ArrayList<Template>();
	}
	
	public List<Template> getCompositeTemplates() {
		return compositeTemplates;
	}
	
	public void checkCompositeTemplates() {
		checkConjunction();
		checkDisjunction();
	}
	
//	public void setCombine(boolean b) {
//		needCombine = b;
//	}
	
	private void checkConjunction() {
		int size = satisfiedPassTemplates.size();
		
//		if (size >= 2 && needCombine) { 
		if (size >= 2) {	
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					 if (i != j) {
						Template t1 = satisfiedPassTemplates.get(i);
						Template t2 = satisfiedPassTemplates.get(j);
						
						if (!(t1 instanceof OneNumIlpTemplate) &&
								!(t1 instanceof TwoNumIlpTemplate) &&
								!(t1 instanceof ThreeNumIlpTemplate)) {
							CompositeTemplate ct = new ConjunctionTemplate();
							ct.addTemplates(t1, t2);
							try {
								if (ct.check()) compositeTemplates.add(ct);
							} catch (IndexOutOfBoundsException e) {
								log.info("The number of data is not the same\n");
							}
						}
					 }
				}
			}
		}
		
		checkIlpConjunction();
	}
	
	private void checkDisjunction() {
		int size = satisfiedFailTemplates.size();
		
//		if (size >= 2 && needCombine) {
		if (size >= 2) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (i != j) {
						Template t1 = satisfiedFailTemplates.get(i);
						Template t2 = satisfiedFailTemplates.get(j);
						
						if (!(t1 instanceof OneNumIlpTemplate) &&
								!(t1 instanceof TwoNumIlpTemplate) &&
								!(t1 instanceof ThreeNumIlpTemplate)) {
							CompositeTemplate ct = new DisjunctionTemplate();
							ct.addTemplates(t1, t2);
							try {
								if (ct.check()) compositeTemplates.add(ct);
							} catch (IndexOutOfBoundsException e) {
								log.info("The number of data is not the same\n");
							}
						}
					}
				}
			}
		}
	}
	
	private void checkIlpConjunction() {
		int size = satisfiedPassTemplates.size();
		
		for (int i = 0; i < size; i++) {
			SingleTemplate t = (SingleTemplate) satisfiedPassTemplates.get(i);
			if (t instanceof OneNumIlpTemplate ||
					t instanceof TwoNumIlpTemplate ||
					t instanceof ThreeNumIlpTemplate) {
				Machine m = t.getMultiCutMachine();
			
				List<List<ExecValue>> passExecValuesList = t.getPassExecValuesList();
				List<List<ExecValue>> failExecValuesList = t.getFailExecValuesList();
				
				List<String> labels = new ArrayList<String>();
				for (ExecValue ev : passExecValuesList.get(0)) {
					labels.add(ev.getVarId());
				}
				
				m = m.setDataLabels(labels);
				
				for (List<ExecValue> evl : passExecValuesList) {
					int size1 = evl.size();
					
					double[] v = new double[size1];
					for (int i1 = 0; i1 < size1; i1++) {
						v[i1] = evl.get(i1).getDoubleVal();
					}
					
					m.addDataPoint(Category.POSITIVE, v);
				}
				
				for (List<ExecValue> evl : failExecValuesList) {
					int size1 = evl.size();
					
					double[] v = new double[size1];
					for (int i1 = 0; i1 < size1; i1++) {
						v[i1] = evl.get(i1).getDoubleVal();
					}
					
					m.addDataPoint(Category.NEGATIVE, v);
				}

				m = m.train();
				if (m.getModel() != null) {
					PositiveSeparationMachine psm = (PositiveSeparationMachine) m;
					if (psm.getLearnedModels().size() > 1) {
						int numberOfFeatures = psm.getRandomData().getNumberOfFeatures();
						CompositeTemplate ct = new ConjunctionTemplate();
						
						for (svm_model svmModel : psm.getLearnedModels()) {
							Divider explicitDivider = new Model(svmModel, numberOfFeatures).getExplicitDivider();
							Formula formula = psm.getLearnedLogic(new StringDividerProcessor(), explicitDivider, true);
							
							if (formula instanceof LIAAtom) {
								LIAAtom lia = (LIAAtom) formula;
								if (lia.getMVFOExpr().size() == 1) {
									ct.addTemplates(createOnePrimIlpTemplate(t, lia));
								} else if (lia.getMVFOExpr().size() == 2) {
									ct.addTemplates(createTwoPrimIlpTemplate(t, lia));
								} else if (lia.getMVFOExpr().size() == 3) {
									ct.addTemplates(createThreePrimIlpTemplate(t, lia));
								}
							}
						}
						
						compositeTemplates.add(ct);
					}
				}
			}
		}
	}
	
	private Template createOnePrimIlpTemplate(SingleTemplate t, LIAAtom lia) {
		String id1 = lia.getMVFOExpr().get(0).getVariable().getLabel();
		
		List<List<ExecValue>> newPassExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> newFailExecValuesList = new ArrayList<List<ExecValue>>();
		
		addValues(newPassExecValuesList, t.getPassExecValuesList(), id1);
		addValues(newFailExecValuesList, t.getFailExecValuesList(), id1);

		OneNumIlpTemplate st = new OneNumIlpTemplate(newPassExecValuesList, newFailExecValuesList);
		
		st.setA(lia.getMVFOExpr().get(0).getCoefficient());
		st.setB(lia.getConstant());
		
		return st;
	}
	
	private Template createTwoPrimIlpTemplate(SingleTemplate t, LIAAtom lia) {
		String id1 = lia.getMVFOExpr().get(0).getVariable().getLabel();
		String id2 = lia.getMVFOExpr().get(1).getVariable().getLabel();
		
		List<List<ExecValue>> newPassExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> newFailExecValuesList = new ArrayList<List<ExecValue>>();
		
		addValues(newPassExecValuesList, t.getPassExecValuesList(), id1, id2);
		addValues(newFailExecValuesList, t.getFailExecValuesList(), id1, id2);
		
		TwoNumIlpTemplate st = new TwoNumIlpTemplate(newPassExecValuesList, newFailExecValuesList);
		
		st.setA(lia.getMVFOExpr().get(0).getCoefficient());
		st.setB(lia.getMVFOExpr().get(1).getCoefficient());
		st.setC(lia.getConstant());
		
		return st;
	}
	
	private Template createThreePrimIlpTemplate(SingleTemplate t, LIAAtom lia) {
		String id1 = lia.getMVFOExpr().get(0).getVariable().getLabel();
		String id2 = lia.getMVFOExpr().get(1).getVariable().getLabel();
		String id3 = lia.getMVFOExpr().get(2).getVariable().getLabel();
		
		List<List<ExecValue>> newPassExecValuesList = new ArrayList<List<ExecValue>>();
		List<List<ExecValue>> newFailExecValuesList = new ArrayList<List<ExecValue>>();
		
		addValues(newPassExecValuesList, t.getPassExecValuesList(), id1, id2, id3);
		addValues(newFailExecValuesList, t.getFailExecValuesList(), id1, id2, id3);
		
		ThreeNumIlpTemplate st = new ThreeNumIlpTemplate(newPassExecValuesList, newFailExecValuesList);
		
		st.setA(lia.getMVFOExpr().get(0).getCoefficient());
		st.setB(lia.getMVFOExpr().get(1).getCoefficient());
		st.setC(lia.getMVFOExpr().get(2).getCoefficient());
		st.setD(lia.getConstant());
		
		return st;
	}
	
	private void addValue(List<ExecValue> newEvl, List<ExecValue> evl, String id) {
		for (int i = 0; i < evl.size(); i++) {
			if (evl.get(i).getVarId().equals(id)) {
				newEvl.add(evl.get(i));
			}
		}
	}
	
	private void addValues(List<List<ExecValue>> newExecValuesList,
			List<List<ExecValue>> execValuesList, String... idl) {
		for (List<ExecValue> evl : execValuesList) {
			List<ExecValue> newEvl = new ArrayList<ExecValue>();
			for (String id : idl) addValue(newEvl, evl, id);
			newExecValuesList.add(newEvl);
		}
	}
	
	/*
	private void checkDisjunction() {
		int size = satifiedFailTemplates.size();
		
		if (size < 2) return;
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				Template t1 = satifiedFailTemplates.get(i);
				Template t2 = satifiedFailTemplates.get(j);
				
				CompositeTemplate ct = new DisjunctionTemplate();
				ct.addTemplates(t1, t2);
				if (ct.check()) compositeTemplates.add(ct);
			}
		}
	}
	*/
	
}
