package svm.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	static String svmEvaluationFile = baseDir + "svm_evaluation_{}.txt";
	static String svmDpsFile = baseDir + "svm_dps.txt";
	static String svmVerificationDpsFile = baseDir + "svm_vef_dps_{}.txt";
	static String svmEvaluationCsv = baseDir + "svm_evaluation.csv";
	static String activeIterationDps = baseDir + "svm_iterator_dps_{}.txt";
	
	int maxHeuristicSampling = 10;
	int maxRandomSelection = 10;

	public static void main(String[] args) throws Exception {
//		FileUtils.copyFilesSilently(Arrays.asList(new File(svmEvaluationFile), new File(svmDpsFile)),  
//				baseDir.substring(0, baseDir.length() - 1));
//		FileUtils.deleteFileByName(svmEvaluationFile);
//		FileUtils.deleteFileByName(svmDpsFile);
//		FileUtils.deleteFileByName(svmVerificationDpsFile);
//		FileUtils.deleteFileByName(svmEvaluationCsv);
		
		List<PolynomialFunction> functions = genreatePolynomialFunctions(1000);
//		List<PolynomialFunction> functions = loadPolynomialFunctions();
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
			int it = 1;
			for (int i = 0; i < it; i++) {
				evaluator.evaluate(f, dataLabels, fi, datasetSize);
			}
		}
	}

	private static List<PolynomialFunction> genreatePolynomialFunctions(int total) {
		List<PolynomialFunction> functions = new ArrayList<PolynomialFunction>();
		for (int i = 0; i < total; i++) {
//			int n = 10;
			int n = Randomness.nextInt(2, 11);
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
		int total = 1000000;
		log.info(String.format("Generate %d verification datapoints: ", total));
		List<DataPoint> verificationDps = generateRandomDatapoints(f, total);
		logDatapointCategory(verificationDps);
		LearnInfo svmInfo = new LearnInfo();
		
		List<DataPoint> svmDataset = generateRandomDatapoints(f, 100);
		logDatapointCategory(svmDataset);
		svmInfo = basicLearn(f, svmDataset, dataLabels, verificationDps, false);
		
		List<DataPoint> activeSvmDataset = Randomness.randomSubList(svmDataset, 20);

		LearnInfo posSvmInfo = new LearnInfo();
		posSvmInfo = basicLearn(f, svmDataset, dataLabels, verificationDps, true);
		
		LearnInfo activeSvmInfo = new LearnInfo();
		logDatapointCategory(activeSvmDataset);
		activeSvmInfo = activeLearn(f, activeSvmDataset, dataLabels, verificationDps, datasetSize);
		
		String fileIdx = String.valueOf(fi);
		StringBuilder sb = new StringBuilder();
		sb.append("f").append(fi).append(":   ").append(f.toString()).append("\n");
		sb.append("svm: ").append("\n")
		.append(svmInfo.classifier).append("\n")
		.append("acc: ").append(svmInfo.acc).append("\n");
		sb.append("active_svm: ").append("\n")
		.append(activeSvmInfo.classifier).append("\n")
		.append("acc: ").append(activeSvmInfo.acc).append("\n\n");
		FileUtils.appendFile(svmEvaluationFile.replace("{}", fileIdx), sb.toString());
		
		sb = new StringBuilder();
		sb.append("f").append(fi).append(":   ").append(f.toString()).append("\n");
		sb.append("svm: ").append("\nInput: \n")
		.append(toString(svmInfo.inputDps)).append("\n\n\n\n");
		sb.append("active_svm: ").append("\nInput: \n")
		.append(toString(activeSvmInfo.inputDps)).append("\n\n\n\n");
		
		sb.append("active_svm_learndps: ").append("\nInput: \n")
		.append(toString(activeSvmInfo.learnedDps)).append("\n\n\n\n");
		FileUtils.appendFile(svmDpsFile.replace("{}", fileIdx), sb.toString());
		
//		sb = new StringBuilder();
//		sb.append("verificationDps: \n").append(toString(verificationDps))
//		.append("\n\n\n\n");
//		FileUtils.appendFile(svmVerificationDpsFile.replace("{}", fileIdx), sb.toString());
		
		Report report = new Report();
		report.storeCsv(svmEvaluationCsv, f, svmInfo, posSvmInfo, activeSvmInfo);
		
		sb = new StringBuilder();
		for (int i = 0; i < activeSvmInfo.iteratorInfos.size(); i++) {
			ActiveIteratorInfo iterationInfo = activeSvmInfo.iteratorInfos.get(i);
			sb.append("Iteration ").append(i).append(": \n");
			sb.append("Input: \n").append(toString(iterationInfo.inputDps)).append("\n\n\n\n");
			sb.append("New Sample Datapoints: \n").append(toString(iterationInfo.newSampleDps)).append("\n\n\n\n");
			sb.append("New Random Datapoints: \n").append(toString(iterationInfo.newRandomDps)).append("\n\n\n\n");
			sb.append("clasifier: ").append(iterationInfo.classifier).append("\n\n");
			sb.append("Acc: ").append(iterationInfo.acc).append("\n");
			sb.append("-------------------------------------------------------------------------------\n\n\n");
		}
		FileUtils.appendFile(activeIterationDps.replace("{}", fileIdx), sb.toString());
	}

	private String toString(List<DataPoint> dps) {
		if (dps == null || dps.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (DataPoint dp : dps) {
			for (int i = 0; i < dp.getNumberOfFeatures(); i++) {
				sb.append(dp.getValue(i)).append("\t");
			}
			sb.append(dp.getCategory());
			sb.append("\n");
		}
		return sb.toString();
	}

	private void logDatapointCategory(List<DataPoint> datapoints) {
		int neg = 0;
		int pos = 0;
		for (DataPoint dp : datapoints) {
			if (dp.getCategory() == Category.NEGATIVE) {
				neg ++;
			} else {
				pos ++;
			}
		}
		log.debug("Neg: " + neg);
		log.debug("Pos: " + pos);
	}
	
	private List<DataPoint> generateVerificationDatapointsCategoryBalance(PolynomialFunction f, int total) {
		List<DataPoint> generatedDatapoints = new ArrayList<>();
		List<DataPoint> negativePoints = new ArrayList<>();
		List<DataPoint> positivePoints = new ArrayList<>();
		generateNegativePositivePoints(f, total, negativePoints, positivePoints);
		generatedDatapoints.addAll(negativePoints);
		generatedDatapoints.addAll(positivePoints);
		
		generatedDatapoints.addAll(generateRandomDatapoints(f, total - generatedDatapoints.size(), null));
		return generatedDatapoints;
	}
	
	private List<DataPoint> generateVerificationGradeDatapoints(PolynomialFunction f, int total) {
		int min = -1000;
		int max = 1000;
		int n = f.n;
		int gradeSize = total;
		int c = (int) Math.pow(gradeSize, (double) 1/n);
		int jump = (int) Math.round(((max - min) / c) + 1);
		
		List<double[]> genDps = new ArrayList<>();
		for (int val = min; val < max; val+= jump) {
			double[] dp = new double[n];
			dp[0] = val;
			for (int idx = 1; idx < n; idx++) {
				dp[idx] = min;
			}
			genDps.add(dp);
		}
		for (int i = 1; i < n; i++) {
			int curSize = genDps.size();
			for (int ci = 0; ci < curSize; ci++) {
				double[] dp = genDps.get(ci);
				for (int val = min + jump; val < max; val+= jump) {
					double[] newDp = Arrays.copyOf(dp, n);
					newDp[i] = val;
					genDps.add(newDp);
				}
			}
		}
		
		List<DataPoint> generatedDatapoints = new ArrayList<>();
		for (double[] dp : genDps) {
			DataPoint dataPoint = new DataPoint(n);
			Category category = f.getCategory(dp);
			dataPoint.setValues(dp);
			dataPoint.setCategory(category);
			generatedDatapoints.add(dataPoint);
		}
		
		generatedDatapoints.addAll(generateRandomDatapoints(f, total - generatedDatapoints.size()));
		return generatedDatapoints;
	}
	
	private List<DataPoint> generateRandomDatapoints(PolynomialFunction f, int total) {
		List<DataPoint> generatedDatapoints = new ArrayList<>();
		List<DataPoint> negativePoints = new ArrayList<>();
		List<DataPoint> positivePoints = new ArrayList<>();
		generateNegativePositivePoints(f, total, negativePoints, positivePoints);
		generatedDatapoints.addAll(Randomness.randomSubList(negativePoints, Randomness.nextInt(Math.min(negativePoints.size(), total / 4), Math.min(negativePoints.size(), total / 2))));
		generatedDatapoints.addAll(Randomness.randomSubList(positivePoints, Randomness.nextInt(Math.min(positivePoints.size(), total / 4), Math.min(positivePoints.size(), total / 2))));
		
		generatedDatapoints.addAll(generateRandomDatapoints(f, total - generatedDatapoints.size(), null));
		return generatedDatapoints;
	}

	private void generateNegativePositivePoints(PolynomialFunction f, int total, List<DataPoint> negativePoints,
			List<DataPoint> positivePoints) {
		int dpLimit = Math.max(100000, total);
		long start = System.currentTimeMillis();
		while (negativePoints.isEmpty() || positivePoints.isEmpty() && 
				!isTimeout(start)) {
			if (negativePoints.isEmpty()) {
				while (negativePoints.size() < (dpLimit / 2) && !isTimeout(start)) {
					negativePoints.addAll(generateRandomDatapoints(f, dpLimit, Category.NEGATIVE));
				}
			} 
			if (positivePoints.isEmpty()) {
				while (positivePoints.size() < (dpLimit / 2) && !isTimeout(start)) {
					positivePoints.addAll(generateRandomDatapoints(f, dpLimit, Category.POSITIVE));
				}
			}
		}
	}

	private boolean isTimeout(long start) {
		return ((System.currentTimeMillis() - start) / 1000) >= 1500;
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
		sampling.setMaxSamplesPerSelect(20);
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
					ActiveIteratorInfo itInfo = new ActiveIteratorInfo();
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
					
					if (selectedSamples.size() > Math.min(remainSize, maxHeuristicSampling)) {
						selectedSamples = Randomness.randomSubList(selectedSamples, Math.min(remainSize, maxHeuristicSampling));
					} 
					remainSize -= selectedSamples.size();
					itInfo.newSampleDps = toDatapoints(selectedSamples, f);
				
					int randomSelectionSize = Randomness.nextInt(0, Math.min(Math.min(remainSize, selectedSamples.size()), maxRandomSelection));
					itInfo.newRandomDps = new ArrayList<>();
					for (int i = 0; i < randomSelectionSize; i++) {
						double[] dp = generateDatapoint(f.n);
						selectedSamples.add(dp);
						itInfo.newRandomDps.add(toDatapoint(dp, f));
					}
					log.debug("selected samples size = " + selectedSamples.size());
					sampleSize += selectedSamples.size();
					List<DataPoint> newDatapoints = new ArrayList<>(selectedSamples.size());
					for (double[] sample : selectedSamples) {
						DataPoint dp = new DataPoint(sample.length);
						dp.setCategory(f.getCategory(sample));
						dp.setValues(sample);
						svm.addDataPoint(dp);
						newDatapoints.add(dp);
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
					
					itInfo.inputDps = new ArrayList<>(svm.getDataPoints());
					itInfo.classifier = svm.getLearnedLogic(true);
					itInfo.acc = acc;
					info.iteratorInfos.add(itInfo);
				}
				stop = true;
				log.info("Sample size = " + svm.getDataPoints().size());
				info.iterations = time;
				info.classifier = svm.getLearnedLogic(true); // + " " + svm.getConjuntionType() ;
				acc = svm.getModelAccuracy(verificationDps, svm.getLearnedModels());
				log.info("acc = " + acc);
				info.acc = acc;
				info.inputAcc = svm.getModelAccuracy();
				info.learnedDps = svm.getDataPoints();
			} catch (SAVExecutionTimeOutException e) {
				e.printStackTrace();
			}
		}
		return info;
	}
	
	private List<DataPoint> toDatapoints(List<double[]> values, PolynomialFunction f) {
		List<DataPoint> dps = new ArrayList<>(values.size());
		for (double[] vlue : values) {
			dps.add(toDatapoint(vlue, f));
		}
		return dps;
	}
	
	private DataPoint toDatapoint(double[] value, PolynomialFunction f) {
		DataPoint dp = new DataPoint(value.length);
		dp.setCategory(f.getCategory(value));
		dp.setValues(value);
		return dp;
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
			info.classifier = svm.getLearnedLogic(true) + ("and");
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
		List<DataPoint> learnedDps;
		String classifier;
		double acc = 0.0;
		double inputAcc = 0.0;
		int iterations = 0;
		List<ActiveIteratorInfo> iteratorInfos = new ArrayList<>();
	}
	
	static class ActiveIteratorInfo {
		List<DataPoint> inputDps;
		List<DataPoint> newSampleDps;
		List<DataPoint> newRandomDps;
		String classifier;
		double acc;
		int it;
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
