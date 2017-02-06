package tester;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import tzuyu.engine.TzClass;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.iface.ITzManager;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.Variable;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzExceptionType;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.utils.ListOfLists;
import tzuyu.engine.utils.PrimitiveGenerator;
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
	private sav.strategies.gentest.ISubTypesScanner refAnalyzer;
	private TzClass project;
	private TzConfiguration config;

	public ParameterSelector(ITzManager<?> prjFactory) {
		componentManager = new ComponentManager();
		refAnalyzer = prjFactory.getRefAnalyzer();
	}

	public void setProject(TzClass project) {
		this.project = project;
		this.config = project.getConfiguration();
		componentManager.config(project.getConfiguration());
	}
	
	@Override
	public Variable selectVariable(Variable receiver, Class<?> type) {
		switch (ParamSelectionType.randomType(receiver, type)) {
		case NEW:
			return selectNewVariable(type);
		case CACHE:
			return selectCachedVariable(type);
		case EXISTING:
			return selectExistingVariable(receiver, type);
		}
		return null;
	}
	
	public Variable selectNewVariable(Class<?> inputType) {
		ObjectType paramType = ObjectType.ofClass(inputType);
		Object inputVal = null;
		
		// if paramType is not primitive, it's probably null.
		if (!CollectionUtils.existIn(paramType, ObjectType.PRIMITIVE_TYPE,
				ObjectType.STRING_OR_PRIMITIVE_OBJECT) &&
				Randomness.nextBoolean(8)) {
			// Randomly return null for reference type with probability of 1/4
			// We may use nextRandomBool() method which return true with
			// probability
			// of 1/2 which is to high to run some normal cases, thus we use
			// 1/4. (LLT: 1/4 seems still too high rate, try 1/8 instead.
			return createAssignmentVariable(inputType, null);
		}
		
		switch (paramType) {
		case ARRAY:
			return selectVariableForArray(inputType);
		case GENERIC_OBJ:
			inputType = refAnalyzer.getRandomImplClzz(inputType);
			// NO BREAK, after select a specific class for object
			// continue to do selectNewVariableForObjectWithTry(inputType)
			if (inputType == null) {
				inputType = Integer.class;
			}
		case OTHER_OBJECT:
			Variable var = selectNewVariableForObjectWithTry(inputType);
			// already tried, but no successful, just assign it null
			if (var != null) {
				return var;
			}
			break;
		// for other cases, an assignment variable will be created.	
		case PRIMITIVE_TYPE:
		case STRING_OR_PRIMITIVE_OBJECT:
		case ENUM:
			inputVal = PrimitiveGenerator.chooseValue(inputType, config);
			break;
		case GENERIC_CLASS:
			inputVal = refAnalyzer.getRandomImplClzz(inputType);
			break;
		case GENERIC_ENUM:
			Class<?> enumType = refAnalyzer.getRandomImplClzz(inputType);
			if (enumType != null) {
				inputVal = PrimitiveGenerator.chooseValue(enumType, config);
			}
			break;
		}
		
		return createAssignmentVariable(inputType, inputVal);
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

	private Variable selectNewVariableForObjectWithTry(Class<?> type) {
		int i = 0;
		boolean retry = true;
		while(i < IMPL_CLASS_MAX_TRY && retry) {
			retry = false;
			try {
				return selectNewVariableForObject(type);
			} catch (TzException e) {
				if (e.getType() == TzExceptionType.PARAMETER_SELECTOR_FAIL_INIT_CLASS) {
					retry = true;
					i++;
				} else {
					throw new TzRuntimeException(e);
				}
			}
		}
		return null;
	}

	private Variable selectNewVariableForObject(Class<?> type) throws TzException {
		Class<?> implClazz = refAnalyzer.getRandomImplClzz(type);
		if (implClazz == null) {
			return createAssignmentVariable(type, null);
		}
		type = implClazz;
		ClassInfo ci = project.getClassInfo(type);
		ConstructorInfo[] ctors = ci.getConstructors();

		if (CollectionUtils.isEmptyCheckNull(ctors)) {
			throw new TzException(
					TzExceptionType.PARAMETER_SELECTOR_FAIL_INIT_CLASS, type);
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
		RConstructor rctor = RConstructor.getCtor(ctors[index].getConstructor());
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
				elementType, length);
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
		Assert.notNull(receiver,
				"receiver for selecting existing variable must not be null");
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

		/*
		 * TODO LLT
		 * we should be in the case that the class doesn't have a public
		 * constructor
		 */
		if (ctors.length == 0) {
			
		}
			
		// Construct the input variables
		Assert.assertTrue(ctors.length > index && ctors[index] != null, 
				"can not find constructor for class: " + type.getName());
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
		RConstructor rctor = RConstructor.getCtor(ctors[index].getConstructor());
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
		Sequence seq = paramSeq.extend(gStmt, parameters);

		// Construct the variable which is created by the above statement
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}
	
	private enum ParamSelectionType {
		NEW,
		CACHE,
		EXISTING;

		public static ParamSelectionType randomType(Variable receiver,
				Class<?> type) {
			if (receiver == null) {
				return Randomness.randomMember(new ParamSelectionType[]{NEW, CACHE});
			}
			return Randomness.randomMember(values());
		}
	}
}
