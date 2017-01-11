package tzuyu.engine.main;

import static tzuyu.engine.main.Option.OptionType.INHERITED_METHOD;
import static tzuyu.engine.main.Option.OptionType.METHODS;
import static tzuyu.engine.main.Option.OptionType.OBJECT_TO_INTEGER;
import static tzuyu.engine.main.Option.OptionType.OUTPUT;
import static tzuyu.engine.main.Option.OptionType.TARGET;
import static tzuyu.engine.main.Option.OptionType.TEST_PERS_QUERY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzClass;

public abstract class Option<T extends Object> {

	private static final String targetHelpStr = " the target class for which to learn specifications.";
	private static final String outputHelpStr = " directory\t the directory to which the results are put.";
	private static final String testsPerQueryHelpStr = " number\t number of test cases for each query.";
	private static final String object2IntegerHelpStr = " flag\t whether to use integers as the Object type values.";
	private static final String inheritedMethodsHelpStr = " flag\t whether to learn specifications for inherited methods.";
	private static final String methodsHelpStr = " only learn specifications for the specified method(s).";

	public static final StringOption target = new StringOption(TARGET, "",
			targetHelpStr);
	public static final StringOption output = new StringOption(OUTPUT, "",
			outputHelpStr);
	public static final IntegerOption testsPerQuery = new IntegerOption(
			TEST_PERS_QUERY, 1, testsPerQueryHelpStr);
	public static final BooleanOption object2Integer = new BooleanOption(
			OBJECT_TO_INTEGER, false, object2IntegerHelpStr);
	public static final BooleanOption inheritedMethods = new BooleanOption(
			INHERITED_METHOD, false, inheritedMethodsHelpStr);

	public static final MultipleValueOption methods = new MultipleValueOption(
			METHODS, methodsHelpStr);

	public static final Map<OptionType, Option<?>> options = new HashMap<OptionType, Option<?>>();

	static {
		options.put(TARGET, target);
		options.put(OUTPUT, output);
		options.put(TEST_PERS_QUERY, testsPerQuery);
		options.put(OBJECT_TO_INTEGER, object2Integer);
		options.put(METHODS, methods);
		options.put(INHERITED_METHOD, inheritedMethods);
	}

	private OptionType type;

	protected Option(OptionType type) {
		this.type = type;
	}

	public OptionType getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public void transferToTzConfig(TzClass project) {
		TzConfiguration config = project.getConfiguration();
		switch (type) {
		case TARGET:
			project.setClassName((String) getValue());
			break;
		case OUTPUT:
			config.setOutput((String) getValue());
			break;
		case TEST_PERS_QUERY:
			config.setTestsPerQuery((Integer) getValue());
			break;
		case OBJECT_TO_INTEGER:
			config.setObjectToInteger((Boolean) getValue());
			break;
//		case METHODS:
//			project.setMethods((List<String>) getValue());
//			break;
		case INHERITED_METHOD:
			config.setInheritedMethod((Boolean) getValue());
			break;
		}
	}

	public abstract T getValue();

	@Deprecated
	// too risk to allow access option like this.
	public static Option<?> getOption(String optName) {
		Option<?> option = options.get(OptionType.getType(optName));
		return option;
	}

	public abstract boolean parseValue(String val);

	public abstract String getHelp();

	public enum OptionType {
		TARGET("target"), OUTPUT("out"), TEST_PERS_QUERY("tpq"), OBJECT_TO_INTEGER(
				"o2i"), INHERITED_METHOD("im"), METHODS("methods");
		private String opt;

		private OptionType(String opt) {
			this.opt = opt;
		}

		public String getOpt() {
			return opt;
		}
		
		public static OptionType getType(String opt) {
			for (OptionType type : values()) {
				if (type.getOpt().equals(opt)) {
					return type;
				}
			}
			return null;
		}
	}
}

class IntegerOption extends Option<Integer> {
	private String helpStr;
	private int val;

	public IntegerOption(OptionType name, int defaultVal, String help) {
		super(name);
		val = defaultVal;
		helpStr = help;
	}

	public Integer getValue() {
		return val;
	}

	@Override
	public boolean parseValue(String val) {
		this.val = Integer.valueOf(val);
		return true;
	}

	@Override
	public String getHelp() {
		return helpStr;
	}
}

class StringOption extends Option<String> {
	private String val;
	private String helpStr;

	public StringOption(OptionType name, String defaultVal, String help) {
		super(name);
		this.val = defaultVal;
		this.helpStr = help;
	}

	@Override
	public boolean parseValue(String val) {
		this.val = val;
		return true;
	}

	public String getValue() {
		return val;
	}

	@Override
	public String getHelp() {
		return helpStr;
	}
}

class BooleanOption extends Option<Boolean> {
	private boolean val;
	private String helpStr;

	public BooleanOption(OptionType name, boolean defaultVal, String help) {
		super(name);
		val = defaultVal;
		helpStr = help;
	}

	@Override
	public boolean parseValue(String val) {
		this.val = Boolean.valueOf(val);
		return true;
	}

	public Boolean getValue() {
		return val;
	}

	@Override
	public String getHelp() {
		return helpStr;
	}
}

class MultipleValueOption extends Option<List<String>> {
	private List<String> vals;

	private String helpStr;

	public MultipleValueOption(OptionType name, String help) {
		super(name);
		vals = new ArrayList<String>();
		helpStr = help;
	}

	@Override
	public boolean parseValue(String val) {
		vals.add(val);
		return true;
	}

	public List<String> getValue() {
		return vals;
	}

	@Override
	public String getHelp() {
		return helpStr;
	}
}
