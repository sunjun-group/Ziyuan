package tester;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.TzClass;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.utils.ListOfLists;
import tzuyu.engine.utils.PrimitiveGenerator;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.Randomness;
import tzuyu.engine.utils.ReflectionUtils.Match;
import tzuyu.engine.utils.SimpleList;

public class ParameterSelectorV2 implements IParameterSelector {
	private final ComponentManager componentManager;
	private TzClass project;
	private TzConfiguration config;

	public ParameterSelectorV2() {
		componentManager = new ComponentManager();
	}

	public void setProject(TzClass project) {
		this.project = project;
		this.config = project.getConfiguration();
		componentManager.config(project.getConfiguration());
	}

	public Variable selectNewVariable(Class<?> type) {
		assert project != null : "Project is not set to class "
				+ this.getClass().getSimpleName();

		if (config.isObjectToInteger() && type.equals(Object.class)) {
			type = int.class;
		}

		if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(type)) {

			// Construct the primitive statement which returns the variable
			Object value = PrimitiveGenerator.chooseValue(type, config.getStringMaxLength());
			StatementKind stmt = RAssignment
					.statementForAssignment(type, value, config);
			// For primitive assign statement, we add an empty input variables
			TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
			Sequence seq = new Sequence().extend(gStmt,
					new ArrayList<Variable>());
			// Construct the variable created by the above primitive statement
			Variable var = new Variable(seq, seq.size() - 1);
			return var;
		} else {
			// Randomly return null for reference type with probability of 1/4
			// We may use nextRandomBool() method which return true with
			// probability
			// of 1/2 which is to high to run some normal cases, thus we use
			// 1/4.
			boolean retNull = Randomness.nextRandomInt(2) == 0;
			if (retNull) {
				StatementKind stmt = RAssignment.statementForAssignment(type,
						null, config);
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
				Sequence seq = new Sequence().extend(gStmt,
						new ArrayList<Variable>());
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			}

			if (type.isArray()) {

				Class<?> elementType = type.getComponentType();
				// Randomly choose a length between [0, 4].
				int length = Randomness.nextRandomInt(5);

				// Prepare the input variables
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
							+ var.stmtIdx));
					totStmts += var.owner.size();
				}

				// Construct the statement which create the return variable
				RArrayDeclaration array = initRArrayDeclaration(elementType, length);
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(array);
				Sequence seq = paramSeq.extend(gStmt, parameters);
				// Construct the return variable
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			} else { // Object type
				ClassInfo ci = project.getClassInfo(type);
				ConstructorInfo[] ctors = ci.getConstructors();

				if (ctors.length == 0) {
					// we choose an accessible constructor of a subclass of this
					// type
					List<ClassInfo> subClasses = project
							.getAccessibleClasses(type);
					if (subClasses.size() == 0) {
						// TODO: what to do if there is no accessible
						// constructor
						throw new TzuYuException(
								"no accessible constructor for class: " + type);
					} else {
						for (ClassInfo subClass : subClasses) {
							if (subClass.getConstructors().length > 0
									&& !subClass.isAbstract()) {
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
							+ var.stmtIdx));
					totStmts += var.owner.size();
				}
				// Construct the statement which creates the variable
				RConstructor rctor = RConstructor.getCtor(ctors[index]
						.getConstructor(), config);
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
				Sequence seq = paramSeq.extend(gStmt, parameters);

				// Construct the variable which is created by the above
				// statement
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			}
		}
	}

	private RArrayDeclaration initRArrayDeclaration(Class<?> elementType,
			int length) {
		RArrayDeclaration rArrayDeclaration = new RArrayDeclaration(elementType,
						length, config);
		return rArrayDeclaration;
	}

	@Override
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
	 * 
	 * @param receiver
	 * @param type
	 * @return
	 */
	@Override
	public Variable selectExistingVariable(Variable receiver, Class<?> type) {

		List<Variable> vars = receiver.owner.getMatchedVariable(type);

		if (vars.size() != 0) {
			return Randomness.randomMember(vars);
		} else {
			return selectNewVariable(type);
		}
	}

	@Override
	public Variable selectNullReceiver(Class<?> type) {
		StatementKind stmt = RAssignment.statementForAssignment(type, null, config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
		Sequence seq = new Sequence().extend(gStmt, new ArrayList<Variable>());
		Variable var = new Variable(seq, seq.size() - 1, -1);
		return var;
	}

	/*
	 * Generate a default constructor as the main object of the method calls.
	 * The main difference of this method and the selectNewVariable is that we
	 * don't allow to generate null objects for the main object and the class
	 * must be a reference type with accessible constructor.
	 */
	@Override
	public Variable selectDefaultReceiver(Class<?> type) {
		ConstructorInfo[] ctors = project.getClassInfo(type)
				.getConstructors();

		if (ctors.length == 0) {
			// we choose an accessible constructor of a subclass of this type
			List<ClassInfo> subClasses = project.getAccessibleClasses(
					type);
			if (subClasses.size() == 0) {
				// TODO what to do if there is no accessible constructor
				throw new TzuYuException(
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
			parameters.add(new Variable(paramSeq, totStmts + var.stmtIdx));
			totStmts += var.owner.size();
		}
		// Construct the statement which creates the variable
		RConstructor rctor = RConstructor
				.getCtor(ctors[index].getConstructor(), config);
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
		Sequence seq = paramSeq.extend(gStmt, parameters);

		// Construct the variable which is created by the above statement
		Variable var = new Variable(seq, seq.size() - 1);
		return var;
	}

}
