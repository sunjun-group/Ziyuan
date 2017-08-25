package learntest.backup.main;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import libsvm.core.Category;
import libsvm.core.Machine;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.formula.Formula;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class SVMTester {
	public static void testSVM() throws SAVExecutionTimeOutException {

		List<ExecVar> targetVars = new LinkedList<>();
		List<double[]> trueV = new LinkedList<>(), falseV = new LinkedList<>();
		constructDataPoint2(targetVars, trueV, falseV);

		List<String> labels = new LinkedList<>();
		for (ExecVar var : targetVars) {
			labels.add(var.getLabel());
		}
		
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);	
		mcm.setDefaultParams();		
		mcm.setDataLabels(labels);
		mcm.setDefaultParams();
		
		addDataPoint(mcm.getDataLabels(), trueV, falseV, mcm);
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(targetVars, labels);
		double acc = mcm.getModelAccuracy();
		System.out.println(newFormula);
		System.out.println(acc);
	}
	

	private static void constructDataPoint1(List<ExecVar> vars, List<double[]> trueV, List<double[]> falseV) {

		ExecVar var1 = new ExecVar("d0", ExecVarType.DOUBLE);
		ExecVar var2 = new ExecVar("d1", ExecVarType.DOUBLE);
		vars.add(var1);
		vars.add(var2);
		
		trueV.add(new double[]{	-442.72488110111044, -884.8060467036833});
		trueV.add(new double[]{	-516.6578796185977, 805.6012206356465});
		trueV.add(new double[]{	135.35333722042492, -813.5393100246629});
		trueV.add(new double[]{	156.71706282935838, 345.6053692165028});
		trueV.add(new double[]{	248.10152877460905, 765.2801397779053});
		trueV.add(new double[]{	260.35836197106005, 821.8102502207632});
		trueV.add(new double[]{	382.08702270166077, 59.1601946818173});
		trueV.add(new double[]{	564.1734964004527, -111.02378427561189});
		trueV.add(new double[]{	748.2916658288459, 507.7303484066729});
		trueV.add(new double[]{	762.6276633942941, -591.0351593525495});
		trueV.add(new double[]{	868.68058004983, 816.5625740202508});
		
		falseV.add(new double[]{ -1000.0, -1000.0 });
		falseV.add(new double[]{ -1000.0, 1000.0 });
		falseV.add(new double[]{ -1000.0, -1000.0 });
		falseV.add(new double[]{ -764.3188003918392, 357.8030987520333 });
		falseV.add(new double[]{ -363.41047470812396, -148.78071090259073 });
		falseV.add(new double[]{ 0.0, 0.0});
		falseV.add(new double[]{ 0.0, 0.0});
		falseV.add(new double[]{ 0.0, 0.0});
		falseV.add(new double[]{ 300.59115680166246, 491.62261722474227 });
		falseV.add(new double[]{ 950.7845399753703, -605.5741998258288 });
		falseV.add(new double[]{ 1000.0, 0.0 });
	}
	
	private static void constructDataPoint2(List<ExecVar> vars, List<double[]> trueV, List<double[]> falseV) {

		ExecVar var1 = new ExecVar("d0", ExecVarType.DOUBLE);
		ExecVar var2 = new ExecVar("d1", ExecVarType.DOUBLE);
		vars.add(var1);
		vars.add(var2);
		
		trueV.add(new double[]{	-442.72488110111044, -884.8060467036833});
		trueV.add(new double[]{	-516.6578796185977, 805.6012206356465});
		trueV.add(new double[]{	135.35333722042492, -813.5393100246629});
		trueV.add(new double[]{	156.71706282935838, 345.6053692165028});
		trueV.add(new double[]{	248.10152877460905, 765.2801397779053});
		trueV.add(new double[]{	260.35836197106005, 821.8102502207632});
		trueV.add(new double[]{	382.08702270166077, 59.1601946818173});
		trueV.add(new double[]{	564.1734964004527, -111.02378427561189});
		
		trueV.add(new double[]{ -1000.0, -1000.0 });
		trueV.add(new double[]{ -1000.0, 1000.0 });
		trueV.add(new double[]{ -1000.0, -1000.0 });
		trueV.add(new double[]{ -764.3188003918392, 357.8030987520333 });
		trueV.add(new double[]{ -363.41047470812396, -148.78071090259073 });
		trueV.add(new double[]{ 0.0, 0.0});
		trueV.add(new double[]{ 0.0, 0.0});
		trueV.add(new double[]{ 0.0, 0.0});
		trueV.add(new double[]{ 300.59115680166246, 491.62261722474227 });
		
		falseV.add(new double[]{	748.2916658288459, 507.7303484066729});
		falseV.add(new double[]{	762.6276633942941, -591.0351593525495});
		falseV.add(new double[]{	868.68058004983, 816.5625740202508});		
		falseV.add(new double[]{ 950.7845399753703, -605.5741998258288 });
		falseV.add(new double[]{ 1000.0, 0.0 });
	}

	private static void addDataPoint(List<String> labels, Collection<double[]> trueV, Collection<double[]> falseV,
			PositiveSeparationMachine mcm) {
		for (double[] value : trueV) {
			addBkp(value, Category.POSITIVE, mcm);
		}	
		for (double[] value : falseV) {
			addBkp(value, Category.NEGATIVE, mcm);
		}
	}

	private static void addBkp(double[] lineVals, Category category, Machine machine) {
		machine.addDataPoint(category, lineVals);
	}
}
