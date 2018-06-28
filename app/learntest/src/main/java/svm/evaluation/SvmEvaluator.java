package svm.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import sav.common.core.utils.RandomUtils;
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
		
//		List<PolynomialFunction> functions = genreatePolynomialFunctions();
		List<PolynomialFunction> functions = loadPolynomialFunctions();
//		List<PolynomialFunction> functions = Arrays.asList(new PolynomialFunction(new double[]{175, -83}));
		SvmEvaluator evaluator = new SvmEvaluator();
		for (int fi = 0; fi < functions.size(); fi++) {
			PolynomialFunction f = functions.get(fi);
			log.info("f: " + f.toString());
			int datasetSize = 100;
			List<String> dataLabels = new ArrayList<>();
			for (int i = 0; i < f.n; i++) {
				dataLabels.add("x" + i);
			}
			evaluator.evaluate(f, dataLabels, fi, datasetSize);
		}
	}

	private static List<PolynomialFunction> genreatePolynomialFunctions() {
		List<PolynomialFunction> functions = new ArrayList<PolynomialFunction>();
		int size = 20;
		for (int i = 0; i < size; i++) {
			int n = Randomness.nextInt(2, 3);
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
					idx = str.indexOf(">");
					if (idx < 0) {
						break;
					}
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
		int min = Randomness.nextInt(-1000, 0);
		int max = Randomness.nextInt(min + 10, 1000);
		int r = max - min;
		int f = 10;
		int d = r / f;
		return generateDatapoint(n, f, min, d);
	}
	
	public static double[] generateDatapoint(int n, int f, int min, int d) {
		double[] x = new double[n];
		Random random = new Random();
		for (int j = 0; j < n; j++) {
			int ri = RandomUtils.nextInt(1, f, random);
			x[j] = RandomUtils.nextInt(min + (ri - 1) * d, min + ri * d, random);
		}
		return x;
	}
	
	public void evaluate(PolynomialFunction f, List<String> dataLabels, int fi, int datasetSize) {
		int total = 100000;
		log.info(String.format("Generate %d verification datapoints: ", total));
		List<DataPoint> verificationDps = generateRandomDatapoints(f, total);
		LearnInfo svmInfo = new LearnInfo();
		int i = 0;
		int max = 10;
//		while (svmInfo.inputAcc == 0.0 && i++ < max) {
			List<DataPoint> svmDataset = generateRandomDatapoints(f, 100);
			svmInfo = basicLearn(f, svmDataset, dataLabels, verificationDps, false);
//		}
		
		LearnInfo posSvmInfo = new LearnInfo();
//		i = 0;
//		while (posSvmInfo.inputAcc == 0.0 && i++ < max) {
//			List<DataPoint> svmDataset = generateRandomDatapoints(f, 100);
			posSvmInfo = basicLearn(f, svmDataset, dataLabels, verificationDps, true);
//		}
		
		LearnInfo activeSvmInfo = new LearnInfo();
		i = 0;
//		while (activeSvmInfo.inputAcc == 0.0 && i++ < max) {
//			List<DataPoint> activeSvmDataset = generateRandomDatapoints(f, 20);
		List<DataPoint> activeSvmDataset = Randomness.randomSubList(svmDataset, 20);
			activeSvmInfo = activeLearn(f, activeSvmDataset, dataLabels, verificationDps, datasetSize);
//		}
		
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
		report.storeCsv(svmEvaluationCsv, f, svmInfo, posSvmInfo, activeSvmInfo);
	}

	private List<DataPoint> generateRandomDatapoints(PolynomialFunction f, int total) {
		List<DataPoint> verificationDps = new ArrayList<>();
		List<DataPoint> negativePoints = Collections.emptyList();
		List<DataPoint> positivePoints = Collections.emptyList();
		while (negativePoints.isEmpty() || positivePoints.isEmpty()) {
			if (negativePoints.isEmpty()) {
				negativePoints = generateRandomDatapoints(f, 1000, Category.NEGATIVE);
			} 
			if (positivePoints.isEmpty()) {
				positivePoints = generateRandomDatapoints(f, 1000, Category.POSITIVE);
			}
		}
		verificationDps.addAll(Randomness.randomSubList(negativePoints, Randomness.nextInt(Math.min(negativePoints.size(), total / 4), Math.min(negativePoints.size(), total / 2))));
		verificationDps.addAll(Randomness.randomSubList(positivePoints, Randomness.nextInt(Math.min(positivePoints.size(), total / 4), Math.min(positivePoints.size(), total / 2))));
		
		verificationDps.addAll(generateRandomDatapoints(f, total - verificationDps.size(), null));
		return verificationDps;
	}
	
	private List<DataPoint> generateRandomDatapoints(PolynomialFunction f, int total, Category expectCategory) {
		List<DataPoint> verificationDps = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			double[] x = generateDatapoint(f.n);
			Category category = f.getCategory(x);
			if (expectCategory != null && category != expectCategory) {
				continue;
			}
			DataPoint datapoint = new DataPoint(f.n);
			datapoint.setValues(x);
			datapoint.setCategory(category);
			verificationDps.add(datapoint);
		}
		return verificationDps;
	}
	
	private LearnInfo activeLearn(PolynomialFunction f, List<DataPoint> dataset,
			List<String> dataLabels, List<DataPoint> verificationDps, int datasetLimit) {
		LearningMachine svm = new LearningMachine(new ByDistanceNegativePointSelection());
		svm.setDefaultParams();
		
		List<ExecVar> vars = toExecVars(dataLabels);
		IlpSelectiveSampling sampling = new IlpSelectiveSampling(vars, toDoubleArrayList(dataset));
		LearnInfo info = new LearnInfo();
		info.inputDps = dataset;
		int sampleSize = dataset.size();
		boolean stop = false;
		svm.setDataLabels(dataLabels);
		log.info("Run active SVM...");
		svm.addDataPoints(dataset);
		while (!stop) {
			try {
				svm.train();
				String learnedLogic = svm.getLearnedLogic(true);
				log.info("Clasifier: " + learnedLogic);
				Formula learnedFormula = svm.getLearnedMultiFormula(vars, dataLabels);
				if (StringUtils.isEmpty(learnedLogic)) {
					if (sampleSize >= datasetLimit) {
						return info;
					}
					List<DataPoint> samples = generateRandomDatapoints(f, Randomness.nextInt(1, Math.min(datasetLimit - sampleSize, 20)));
					svm.addDataPoints(samples);
					sampleSize += samples.size();
					continue;
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
				while (learnedFormula != null && time < max && sampleSize < datasetLimit) {
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
	//				samples = Randomness.randomSequence(samples, datasetSize - sampleSize);
	//				sampleSize += samples.size();
					svm.getLearnedModels().clear();
					log.debug("all samples size = " + samples.size());
					List<double[]> selectedSamples = samples;
					int remainSize = datasetLimit - sampleSize;
					if (selectedSamples.size() > Math.min(remainSize, 10)) {
						selectedSamples = Randomness.randomSubList(selectedSamples, Math.min(remainSize, 10));
						remainSize -= selectedSamples.size();
					}
					for (int i = selectedSamples.size(); i < Randomness.nextInt(0, Math.min(remainSize, 20)); i++) {
						selectedSamples.add(generateDatapoint(f.n));
					}
					log.debug("selected samples size = " + selectedSamples.size());
					sampleSize += selectedSamples.size();
					for (double[] sample : selectedSamples) {
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
						log.info("learnedFormula is unchanged, stop learning.");
						break;
					}
				}
				stop = true;
				log.info("Sample size = " + svm.getDataPoints().size());
				info.iterations = time;
				info.classifier = svm.getLearnedLogic(true);
				acc = svm.getModelAccuracy(verificationDps, svm.getLearnedModels());
				log.info("acc = " + acc);
				info.acc = acc;
				info.inputAcc = svm.getModelAccuracy();
			} catch (SAVExecutionTimeOutException e) {
				e.printStackTrace();
			}
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
			List<DataPoint> verificationDps, boolean positiveSeparationMachine) {
		Machine svm;
		if (positiveSeparationMachine) {
			svm = new LearningMachine(new ByDistanceNegativePointSelection());
			log.info("Run pos_svm...");
		} else {
			svm = new Machine();
			log.info("Run SVM...");
		}
		svm.setDefaultParams();
		LearnInfo info = new LearnInfo();
		info.inputDps = dataset;
		try {
			svm.setDataLabels(dataLabels);
			svm.addDataPoints(dataset);
			svm.train();
			log.info("Clasifier: " + svm.getLearnedLogic(true));
			info.classifier = svm.getLearnedLogic(true);
			if (StringUtils.isEmpty(info.classifier)) {
				return info;
			}
			
			double acc = svm.getModelAccuracyOnDataset(verificationDps);
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
			n = a.length - 1;
		}
		
		public Category getCategory(double[] x) {
			double y = 0;
			for (int i = 0; i < n; i++) {
				y += a[i] * Math.pow(x[i], (n - i));
			}
			y += a[n];
			return y > 0 ? Category.POSITIVE : Category.NEGATIVE;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				if (i != 0 && a[i] >= 0) {
					sb.append(" + ");
				}
				sb.append(" ").append(a[i]).append("* x" + i).append("^").append(n - i);
			}
			if (a[n] >= 0) {
				sb.append(" + ");
			}
			sb.append(" ").append(a[n]);
			sb.append(" > 0");
			return sb.toString();
		}
	}
}
