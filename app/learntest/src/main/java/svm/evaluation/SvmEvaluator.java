package svm.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.machinelearning.LearningMachine;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ByDistanceNegativePointSelection;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.Randomness;
import sav.common.core.utils.StringUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

/**
 * @author LLT
 *
 */
public class SvmEvaluator {
	private static Logger log = LoggerFactory.getLogger(SvmEvaluator.class);
	static String baseDir = getBaseDir();
	static String svmEvaluationFile = baseDir + "svm_evaluation.txt";
	static String svmDpsFile = baseDir + "svm_dps.txt";
	static String svmEvaluationCsv = baseDir + "svm_evaluation.csv";

	public static void main(String[] args) throws Exception {
//		FileUtils.copyFilesSilently(Arrays.asList(new File(svmEvaluationFile), new File(svmDpsFile)),  
//				baseDir.substring(0, baseDir.length() - 1));
		FileUtils.deleteFileByName(svmEvaluationFile);
		FileUtils.deleteFileByName(svmDpsFile);
		FileUtils.deleteFileByName(svmEvaluationCsv);
		
		List<PolynomialFunction> functions = loadPolynomialFunctions();
		SvmEvaluator evaluator = new SvmEvaluator();
		for (int fi = 0; fi < functions.size(); fi++) {
			PolynomialFunction f = functions.get(fi);
			log.info("f: " + f.toString());
			List<DataPoint> svmDataset = new ArrayList<>();
			int datasetSize = 100;
			for (int i = 0; i < datasetSize; i++) {
				double[] x = generateDatapoint(f.n);
				DataPoint datapoint = new DataPoint(f.n);
				datapoint.setValues(x);
				datapoint.setCategory(f.getCategory(x));
				svmDataset.add(datapoint);
			}

			List<DataPoint> activeSvmDataset = new ArrayList<>();
			for (int i = 0; i < 20; i++) {
				double[] x = generateDatapoint(f.n);
				DataPoint datapoint = new DataPoint(f.n);
				datapoint.setValues(x);
				datapoint.setCategory(f.getCategory(x));
				activeSvmDataset.add(datapoint);
			}

			List<String> dataLabels = new ArrayList<>();
			for (int i = 0; i < f.n; i++) {
				dataLabels.add("x" + i);
			}
			evaluator.evaluate(f, activeSvmDataset, dataLabels, fi, datasetSize);
		}
	}

	private static List<PolynomialFunction> genreatePolynomialFunctions() {
		List<PolynomialFunction> functions = new ArrayList<PolynomialFunction>();
		int size = 20;
		for (int i = 0; i < size; i++) {
			int n = Randomness.nextInt(2, 10);
			double[] a = new double[n];
			for (int idx = 0; idx < n; idx++) {
				a[idx] = Randomness.nextInt(-1000, 1000);
			}
			functions.add(new PolynomialFunction(a));
		}
		return functions;
	}
	
	private static List<PolynomialFunction> loadPolynomialFunctions() throws Exception {
		List<PolynomialFunction> functions = new ArrayList<PolynomialFunction>();
		List<?> lines = org.apache.commons.io.FileUtils
				.readLines(new File(baseDir + "src/main/java/svm/evaluation/function.txt"));
		for (Object line : lines) {
			String str = (String) line;
			List<Double> a = new ArrayList<>();
			while(true) {
				int idx = str.indexOf("*");
				if (idx < 0) {
					break;
				}
				double ai = Double.valueOf(str.substring(0, idx).trim());
				a.add(ai);
				int mi = str.indexOf("-", idx);
				int pi = str.indexOf("+", idx);
				int ni = -1;
				if (mi < 0) {
					if (pi > 0) {
						ni = pi + 1;
					}
				} else if (pi < 0) {
					if (mi > 0) {
						ni = mi;
					}
				} else {
					ni = Math.min(mi, pi + 1);
				}
				if (ni < 0) {
					break;
				}
				str = str.substring(ni);
			}
			double[] aArr = new double[a.size()];
			for (int i = 0; i < a.size(); i++) {
				aArr[i] = a.get(i);
			}
			functions.add(new PolynomialFunction(aArr));
		}
		return functions;
	}
	
	public static double[] generateDatapoint(int n) {
		double[] x = new double[n];
		for (int j = 0; j < n; j++) {
			x[j] = Randomness.nextInt(-1000, 1000);
		}
		return x;
	}
	
	public void evaluate(PolynomialFunction f, List<DataPoint> dataset, List<String> dataLabels, int fi, int datasetSize) {
		int total = 100000;
		log.info(String.format("Generate %d datapoints: ", total));
		List<DataPoint> verificationDps = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			double[] x = generateDatapoint(f.n);
			DataPoint datapoint = new DataPoint(f.n);
			datapoint.setValues(x);
			datapoint.setCategory(f.getCategory(x));
			verificationDps.add(datapoint);
		}
		
		LearnInfo svmInfo = basicLearn(f, dataset, dataLabels, verificationDps);
		LearnInfo activeSvmInfo = activeLearn(f, dataset, dataLabels, verificationDps, datasetSize);
		StringBuilder sb = new StringBuilder();
		sb.append("f").append(fi).append(":   ").append(f.toString()).append("\n");
		sb.append("svm: ").append("\n")
			.append(svmInfo.classifier).append("\n")
			.append("acc: ").append(svmInfo.acc).append("\n");
		sb.append("active_svm: ").append("\n")
			.append(activeSvmInfo.classifier).append("\n")
			.append("acc: ").append(activeSvmInfo.acc).append("\n\n");
		FileUtils.appendFile(svmEvaluationFile, sb.toString());
		sb = new StringBuilder();
		sb.append("f").append(fi).append(":   ").append(f.toString()).append("\n");
		sb.append("svm: ").append("\nInput: \n")
			.append(svmInfo.inputDps).append("\n\n").append("\n\n");
		sb.append("active_svm: ").append("\nInput: \n")
			.append(activeSvmInfo.inputDps).append("\n\n")
			.append("verificationDps: \n").append(verificationDps)
			.append("\n\n");
		FileUtils.appendFile(svmDpsFile, sb.toString());
		Report report = new Report();
		report.storeCsv(svmEvaluationCsv, f, svmInfo, activeSvmInfo);
	}
	
	private LearnInfo activeLearn(PolynomialFunction f, List<DataPoint> dataset,
			List<String> dataLabels, List<DataPoint> verificationDps, int datasetSize) {
		LearningMachine svm = new LearningMachine(new ByDistanceNegativePointSelection());
		svm.setDefaultParams();
		
		List<ExecVar> vars = toExecVars(dataLabels);
		IlpSelectiveSampling sampling = new IlpSelectiveSampling(vars, toDoubleArrayList(dataset));
		LearnInfo info = new LearnInfo();
		info.inputDps = dataset;
		try {
			svm.setDataLabels(dataLabels);
			log.info("Run active SVM...");
			svm.addDataPoints(dataset);
			svm.train();
			String learnedLogic = svm.getLearnedLogic(true);
			log.info("Clasifier: " + learnedLogic);
			Formula learnedFormula = svm.getLearnedMultiFormula(vars, dataLabels);
			if (StringUtils.isEmpty(learnedLogic)) {
				return info;
			}

			double acc = svm.getModelAccuracy();
			if (svm.getDataPoints().size() <= 1) {
				log.info("there is only one data point !!! svm could not learn");
				return null;
			}
			List<Divider> dividers = svm.getFullLearnedDividers(svm.getDataLabels(), vars);
			log.info("=============learned multiple cut: " + learnedFormula);

			int max = 10;
			int time = 0;
			int sampleSize = dataset.size();
			while (learnedFormula != null && time < max && sampleSize < datasetSize) {
				IlpSelectiveSampling.iterationTime = max - time;
				time++;
				log.debug("selective sampling: ");
				List<double[]> samples = Collections.emptyList();
				try {
					samples = sampling.selectDataForModel(null, vars, new OrCategoryCalculator(new ArrayList<>(), vars, vars), dividers);
				} catch (SavException e) {
					e.printStackTrace();
				}
				
				if (samples.isEmpty()) {
					log.debug("empty samples!");
					continue;
				}
				samples = Randomness.randomSequence(samples, datasetSize - sampleSize);
				sampleSize += samples.size();
				svm.getLearnedModels().clear();
				for (double[] sample : samples) {
					svm.addDataPoint(f.getCategory(sample), sample);
				}
				
				svm.train();
				Formula tmp = svm.getLearnedMultiFormula(vars, svm.getDataLabels());
				log.info("improved the formula: " + tmp);
				if (tmp == null) {
					break;
				}

				double accTmp = svm.getModelAccuracy();
				acc = svm.getModelAccuracy();
				if (!tmp.equals(learnedFormula)) {
					learnedFormula = tmp;
					dividers = svm.getFullLearnedDividers(svm.getDataLabels(), vars);
					acc = accTmp;
				} else {
					break;
				}
			}
			log.info("Sample size = " + sampleSize);
			info.iterations = time;
			info.classifier = svm.getLearnedLogic(true);
			acc = svm.getModelAccuracy(verificationDps, svm.getLearnedModels());
			log.info("acc = " + acc);
			info.acc = acc;
			info.inputAcc = svm.getModelAccuracy();
		} catch (SAVExecutionTimeOutException e) {
			e.printStackTrace();
		}
		return info;
	}

	private List<double[]> toDoubleArrayList(List<DataPoint> dataset) {
		List<double[]> result = new ArrayList<>();
		for (DataPoint point : dataset) {
			result.add(point.getValues());
		}
		return result;
	}

	private List<ExecVar> toExecVars(List<String> dataLabels) {
		List<ExecVar> vars = new ArrayList<>(dataLabels.size());
		for (String label : dataLabels) {
			vars.add(new ExecVar(label, ExecVarType.DOUBLE));
		}
		return vars;
	}

	public static String getBaseDir() {
		String path = new File(SvmEvaluator.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		path = path.replace("\\", "/");
		path = path.replace("bin", "");
		return path;
	}
	
	public LearnInfo basicLearn(PolynomialFunction f, List<DataPoint> dataset, List<String> dataLabels,
			List<DataPoint> verificationDps) {
		Machine svm = new Machine();
		svm.setDefaultParams();
		LearnInfo info = new LearnInfo();
		info.inputDps = dataset;
		try {
			svm.setDataLabels(dataLabels);
			log.info("Run SVM...");
			svm.addDataPoints(dataset);
			svm.train();
			log.info("Clasifier: " + svm.getLearnedLogic(true));
			info.classifier = svm.getLearnedLogic(true);
			if (StringUtils.isEmpty(info.classifier)) {
				return info;
			}
			
			double acc = svm.getModelAccuracy(svm.getModel().getModel(), verificationDps);
			log.info("acc = " + acc);
			info.acc = acc;
			info.inputAcc = svm.getModelAccuracy();
		} catch (SAVExecutionTimeOutException e) {
			e.printStackTrace();
		}
		return info;
	}
	
	static class LearnInfo {
		List<DataPoint> inputDps;
		String classifier;
		double acc = 0.0;
		double inputAcc = 0.0;
		int iterations = 0;
	}
	
	public static class PolynomialFunction {
		double[] a;
		int n;

		public PolynomialFunction(double[] a) {
			this.a = a;
			n = a.length;
		}
		
		public Category getCategory(double[] x) {
			double y = 0;
			for (int i = 0; i < n; i++) {
				y += a[i] * Math.pow(x[i], (n - 1) - i);
			}
			return y > 0 ? Category.POSITIVE : Category.NEGATIVE;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				if (i != 0 && a[i] >= 0) {
					sb.append(" + ");
				}
				sb.append(" ").append(a[i]).append("* x" + i).append("^").append((n - 1) - i);
			}
			sb.append(" > 0");
			return sb.toString();
		}
	}
}
