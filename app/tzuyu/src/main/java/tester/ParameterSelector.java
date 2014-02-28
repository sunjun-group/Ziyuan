package tester;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.IAlgorithmFactory;
import tzuyu.engine.iface.IReferencesAnalyzer;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.Variable;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.model.exception.TzuyuException;
import tzuyu.engine.model.exception.TzuyuException.Type;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.utils.CollectionUtils;
import tzuyu.engine.utils.ListOfLists;
import tzuyu.engine.utils.PrimitiveGenerator;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.Randomness;
import tzuyu.engine.utils.ReflectionUtils.Match;
import tzuyu.engine.utils.SimpleList;

/**
 *  
 * @author Spencer Xiao
 * 
 */
public class ParameterSelector implements IParameterSelector {
	private static final int INPUT_ARRAY_MAX_LENGTH = 5;
	private static final int IMPL_CLASS_MAX_TRY = 5;
	
	private final ComponentManager componentManager;
	private IReferencesAnalyzer refAnalyzer;
	private TzClass project;
	private TzConfiguration config;

	public ParameterSelector(IAlgorithmFactory<?> prjFactory) {
		componentManager = new ComponentManager();
		refAnalyzer = prjFactory.getRefAnalyzer();
	}

	public void setProject(TzClass project) {
		this.project = project;
		this.config = project.getConfiguration();
		componentManager.config(project.getConfiguration());
	}
	
	public Variable selectNewVariable(Class<?> inputType) {
		Class<?> type = adaptConfiguration(inputType); 
		ParamType paramType = ParamType.getParamType(type);
		Object inputVal = null;
		switch (paramType) {
		case EXT_PRIMITIVE:
			inputVal = PrimitiveGenerator.chooseValue(type,
					config.getStringMaxLength());
			break;
		case GENERIC_CLASS:
			inputVal = refAnalyzer.getRandomClass();
			break;
		case GENERIC_ENUM:
			Class<?> enumType = refAnalyzer.getRandomEnum();
			if (enumType != null) {
				inputVal = PrimitiveGenerator.chooseValue(enumType,
						config.getStringMaxLength());
			}
			break;
		case ARRAY_OBJECT_NULL:
			inputVal = null;
			break;
		case ARRAY:
			return selectVariableForArray(type);
		case OBJECT:
			return selectNewVariableForObjectWithTry(type);
		}
		return createAssignmentVariable(type, inputVal);
	}

	private Variable selectVariableForArray(Class<?> type) {
		// init array length
		Class<?> elementType = type.getComponentType();
		int length = Randomness.nextRandomInt(INPUT_ARRAY_MAX_LENGTH);
		// auto generate values(and build sequence as well) for
		// all array elements
		List<Variable> inputVars = new ArrayList<Variable>(length);
		List<Sequence> seqs = new ArrayList<Sequence>();
		for (int i = 0; i < length; i++) {
			Variable inputVar = selectNewVariable(elementType);
			inputVars.add(inputVar);
			seqs.add(inputVar.owner);
		}

		Sequence paramSeq = Sequence.concatenate(seqs);

		List<Variable> parameters = new ArrayList<Variable>(length);
		int totStmts = 0;
		for (Variable var : inputVars) {
			parameters.add(new Variable(paramSeq, totStmts
					+ var.getStmtIdx()));
			totStmts += var.owner.size();
		}

		// Construct the statement which create the return variable
		RArrayDeclaration array = initRArrayDeclaration(elementType,
				length);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(array);
		Sequence seq = paramSeq.extend(gStmt, parameters);
		// Construct the return variable
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}

	private Class<?> adaptConfiguration(Class<?> inputType) {
		//TODO-LLT: why??? why we need this??
		if (config.isObjectToInteger() && inputType.equals(Object.class)) {
			return int.class;
		}
		return inputType;
	}
	
	private Variable selectNewVariableForObjectWithTry(Class<?> type) {
		int i = 0;
		boolean retry = true;
		while(i < IMPL_CLASS_MAX_TRY && retry) {
			retry = false;
			try {
				return selectNewVariableForObject(type);
			} catch (TzuyuException e) {
				if (e.getType() == Type.ParameterSelector_Fail_Init_Class) {
					retry = true;
				} else {
					throw new TzRuntimeException(e);
				}
			}
		}
		return null;
	}

	private Variable selectNewVariableForObject(Class<?> type) throws TzuyuException {
		Class<?> implClazz = refAnalyzer.getRandomImplClzz(type);
		if (implClazz == null) {
			return createAssignmentVariable(type, null);
		}
		type = implClazz;
		ClassInfo ci = project.getClassInfo(type);
		ConstructorInfo[] ctors = ci.getConstructors();

		if (CollectionUtils.isEmpty(ctors, true)) {
			throw new TzuyuException(Type.ParameterSelector_Fail_Init_Class,
					"no accessible constructor for class: " + type);
			// we choose an accessible constructor of a subclass of this
			// type
//			List<ClassInfo> subClasses = project
//					.getAccessibleClasses(type);
//			if (subClasses.size() == 0) {
//				// TODO: what to do if there is no accessible
//				// constructor
//				throw new TzuyuException(Type.ParameterSelector_Fail_Init_Class,
//						"no accessible constructor for class: " + type);
//			} else {
//				for (ClassInfo subClass : subClasses) {
//					if (subClass.getConstructors().length > 0
//							&& !subClass.isAbstract()) {
//						ctors = subClass.getConstructors();
//						break;
//					}
//				}
//			}
		}

		// Choose the constructor with smallest number of parameters
		int index = 0;
		int mimParameters = Integer.MAX_VALUE;
		for (int i = 0; i < ctors.length; i++) {
			ConstructorInfo ctor = ctors[i];
			if (ctor.getParameterTypes().length < mimParameters) {
				mimParameters = ctor.getParameterTypes().length;
				index = i;
			}
		}
		// Randomly choose a constructor
		// int index = Randomness.nextRandomInt(ctors.length);

		// Construct the input variables
		ClassInfo[] types = ctors[index].getParameterTypes();
		List<Variable> inputVars = new ArrayList<Variable>(types.length);
		List<Sequence> seqs = new ArrayList<Sequence>(types.length);
		for (int j = 0; j < types.length; j++) {
			Variable inputVar = selectNewVariable(types[j].getType());
			seqs.add(inputVar.owner);
			inputVars.add(inputVar);
		}
		Sequence paramSeq = Sequence.concatenate(seqs);
		List<Variable> parameters = new ArrayList<Variable>();
		int totStmts = 0;

		for (Variable var : inputVars) {
			parameters.add(new Variable(paramSeq, totStmts
					+ var.getStmtIdx()));
			totStmts += var.owner.size();
		}
		// Construct the statement which creates the variable
		RConstructor rctor = RConstructor.getCtor(
				ctors[index].getConstructor(), config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
		Sequence seq = paramSeq.extend(gStmt, parameters);

		// Construct the variable which is created by the above
		// statement
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}

	private Variable createAssignmentVariable(Class<?> type, Object value) {
		StatementKind stmt = RAssignment.statementForAssignment(type,
				value, config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
		Sequence seq = new Sequence().extend(gStmt,
				new ArrayList<Variable>());
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}

	private RArrayDeclaration initRArrayDeclaration(Class<?> elementType,
			int length) {
		RArrayDeclaration rArrayDeclaration = new RArrayDeclaration(
				elementType, length, config);
		return rArrayDeclaration;
	}

	public Variable selectCachedVariable(Class<?> type) {
		SimpleList<Sequence> l = null;
		if (config.isObjectToInteger() && type.equals(Object.class)) {
			l = componentManager.getSequencesForType(int.class, false);
		} else if (type.isArray()) {
			SimpleList<Sequence> l1 = componentManager.getSequencesForType(
					type, false);
			SimpleList<Sequence> l2 = HelperSequenceCreator.createSequence(
					componentManager, type, config);
			l = new ListOfLists<Sequence>(l1, l2);
		} else {
			l = componentManager.getSequencesForType(type, false);
		}

		// If there is no none in the cache, we randomly generate one sequence
		if (l.size() == 0) {
			Variable var = selectNewVariable(type);
			componentManager.addGeneratedSequence(var.owner);
			return var;
		}

		Match m = Match.COMPATIBLE_TYPE;
		Sequence seqChosen = Randomness.randomMember(l);
		Variable var = seqChosen.randomVarOfTypeLastStatement(type, m);
		return var;
	}

	/**
	 * Randomly select an existing matching variable in the receiver sequences
	 * for the given type. If there is no such a variable return a newly created
	 * variable for the given type.
	 */
	public Variable selectExistingVariable(Variable receiver, Class<?> type) {
		// TODO LLT: to test
		if (receiver == null) {
			return selectNullReceiver(type); 
		}
		//
		List<Variable> vars = receiver.owner.getMatchedVariable(type);

		if (vars.size() != 0) {
			return Randomness.randomMember(vars);
		} else {
			return selectNewVariable(type);
		}
	}

	public Variable selectNullReceiver(Class<?> type) {
		StatementKind stmt = RAssignment.statementForAssignment(type, null,
				config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
		Sequence seq = new Sequence().extend(gStmt, new ArrayList<Variable>());
		// LLT: -1 = Variable.VALUE_OF_LAST_STATEMENT (seems not make sense)
		Variable var = new Variable(seq, seq.size() - 1, -1);
		return var;
	}

	/*
	 * Generate a default constructor as the main object of the method calls.
	 * The main difference of this method and the selectNewVariable is that we
	 * don't allow to generate null objects for the main object and the class
	 * must be a reference type with accessible constructor.
	 */
	public Variable selectDefaultReceiver(Class<?> type) {
		ConstructorInfo[] ctors = project.getClassInfo(type).getConstructors();

		if (ctors.length == 0) {
			// we choose an accessible constructor of a subclass of this type
			List<ClassInfo> subClasses = project.getAccessibleClasses(type);
			if (subClasses.size() == 0) {
				// TODO what to do if there is no accessible constructor
				throw new TzRuntimeException(
						"no accessible constructor for class: " + type);
			} else {
				for (ClassInfo subClass : subClasses) {
					if (subClass.getConstructors().length > 0) {
						ctors = subClass.getConstructors();
						break;
					}
				}
			}
		}

		// Choose the constructor with smallest number of parameters
		int index = 0;
		int mimParameters = Integer.MAX_VALUE;
		for (int i = 0; i < ctors.length; i++) {
			ConstructorInfo ctor = ctors[i];
			if (ctor.getParameterTypes().length < mimParameters) {
				mimParameters = ctor.getParameterTypes().length;
				index = i;
			}
		}

		// Construct the input variables
		ClassInfo[] types = ctors[index].getParameterTypes();
		List<Variable> inputVars = new ArrayList<Variable>(types.length);
		List<Sequence> seqs = new ArrayList<Sequence>(types.length);
		for (int j = 0; j < types.length; j++) {
			Variable inputVar = selectNewVariable(types[j].getType());
			seqs.add(inputVar.owner);
			inputVars.add(inputVar);
		}
		Sequence paramSeq = Sequence.concatenate(seqs);
		List<Variable> parameters = new ArrayList<Variable>();
		int totStmts = 0;

		for (Variable var : inputVars) {
			parameters.add(new Variable(paramSeq, totStmts + var.getStmtIdx()));
			totStmts += var.owner.size();
		}
		// Construct the statement which creates the variable
		RConstructor rctor = RConstructor.getCtor(
				ctors[index].getConstructor(), config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
		Sequence seq = paramSeq.extend(gStmt, parameters);

		// Construct the variable which is created by the above statement
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}

	private static enum ParamType {
		EXT_PRIMITIVE, // include original Primitive types and String type 
		GENERIC_ENUM, // Enum<?>
		GENERIC_CLASS, // Class<?>
		ARRAY_OBJECT_NULL,
		ARRAY,
		OBJECT;
		
		public static ParamType getParamType(Class<?> type) {
			if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(type)) {
				return ParamType.EXT_PRIMITIVE;
			}
			// Randomly return null for reference type with probability of 1/4
			// We may use nextRandomBool() method which return true with
			// probability
			// of 1/2 which is to high to run some normal cases, thus we use
			// 1/4.
			boolean retNull = Randomness.nextBoolean(4);
			if (retNull) {
				return ParamType.ARRAY_OBJECT_NULL;
			}

			if (type == Enum.class) {
				return ParamType.GENERIC_ENUM;
			}
			if (type == Class.class) {
				return ParamType.GENERIC_CLASS;
			}
			if (type.isArray()) {
				return ParamType.ARRAY;
			}
			return ParamType.OBJECT;
		}
	}
}
