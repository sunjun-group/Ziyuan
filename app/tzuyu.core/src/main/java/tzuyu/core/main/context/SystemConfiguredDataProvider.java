package tzuyu.core.main.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sav.common.core.SavLog4jPrintStream;
import sav.common.core.iface.IPrintStream;
import sav.common.core.utils.ConfigUtils;
import faultLocalization.SuspiciousnessCalculator.SuspiciousnessCalculationAlgorithm;

/**
 * Data provider which gets configurations from System properties.
 * 
 * @author Nguyen Phuoc Nguong Phuc (phuc@sutd.edu.sg)
 * 
 */
public class SystemConfiguredDataProvider extends AbstractApplicationContext {
	private static final String TZUYU_ALGO = "TZUYU_ALGO";
	private static final String JAVA_HOME = "java.home";
	private static final String JAVA_CLASS_FILE_EXTENSION = ".class";
	private List<String> projectClassPath;

	public SystemConfiguredDataProvider(final String path) throws FileNotFoundException {
		File folder = new File(path);
		if (!folder.exists()) {
			throw new FileNotFoundException("The path " + path + " does not exist.");
		}
		if (isClassFile(folder)) {
			// Only 1 file is provided
			// That file must contain both program code + test code
			projectClassPath = Arrays.asList(folder.getParent());
		} else if (folder.isDirectory()) {
			// A directory is provided
			// Scan the directory for classes and mark them as either program
			// code or test code
			projectClassPath = Arrays.asList(folder.getAbsolutePath());
		} else {
			throw new UnsupportedOperationException("The path " + path
					+ " is neither a .class file nor a directory.");
		}
	}

	private boolean isClassFile(File file) {
		return file.isFile() && file.getName().endsWith(JAVA_CLASS_FILE_EXTENSION);
	}

	@Override
	protected List<String> getProjectClasspath() {
		return projectClassPath;
	}

	@Override
	protected String getTracerJarPath() {
		return null;
	}

	@Override
	protected String getJavahome() {
		return ConfigUtils.getProperty(JAVA_HOME);
	}

	@Override
	public SuspiciousnessCalculationAlgorithm getSuspiciousnessCalculationAlgorithm() {
		final String algo = ConfigUtils.getProperty(TZUYU_ALGO);
		if (StringUtils.isNotBlank(algo)) {
			return SuspiciousnessCalculationAlgorithm.valueOf(algo);
		} else {
			return SuspiciousnessCalculationAlgorithm.TARANTULA;
		}
	}

	@Override
	public IPrintStream getVmRunnerPrintStream() {
		return new SavLog4jPrintStream();
	}

}
