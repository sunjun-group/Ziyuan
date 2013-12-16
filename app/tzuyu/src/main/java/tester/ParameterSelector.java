package tester;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzProject;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.ConstructorInfo;
import tzuyu.engine.model.InputAndSuccessFlag;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.VarIndex;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.store.MethodParameterStore;
import tzuyu.engine.utils.Options;
import tzuyu.engine.utils.PrimitiveGenerator;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.Randomness;
import tzuyu.engine.utils.ReflectionUtils;

/**
 * The class is responsible for the selection of parameters for a StatementKind.
 * 
 * @author Spencer Xiao
 * 
 */
public class ParameterSelector {
	private MethodParameterStore parameterStore;

	private TzProject project;

	public ParameterSelector() {
		parameterStore = new MethodParameterStore();
	}

	/**
	 * Select a list of variables for the <code>stmt</code> from existing
	 * <code>seq</code> as the receiver and other input variables from either
	 * <code>seq</code>, cached variable generating sequences or randomly
	 * generated variables.
	 * 
	 * @param seq
	 *            the sequence which generates the receiver for the
	 *            <code>stmt</code>.
	 * @param stmt
	 *            for which variables are to be generated.
	 * @return a list of matching variables.
	 */
	/*
	 * private List<Variable> selectParameters(Sequence seq, TzuYuAction stmt) {
	 * List<Variable> variables = new ArrayList<Variable>(); List<Class<?>>
	 * inputTypes = stmt.getInputTypes(); for (int index = 0; index <
	 * inputTypes.size(); index++) { Class<?> type = inputTypes.get(index); //
	 * there are 3 sources of input variables int source =
	 * Randomness.nextRandomInt(3); Variable selectedVar = null; if (source ==
	 * 0) {// select from the sequence List<Variable> vars =
	 * seq.getMatchedVariable(type); if (vars.size() != 0) { selectedVar =
	 * Randomness.randomMember(vars); }// there may be no matching variable in
	 * the cache } else if (source == 1) { selectedVar =
	 * this.selectCachedParameters(type); } else { selectedVar =
	 * this.generateNewVariableForType(type); } // Default case when there is no
	 * sequence in the cache, // then we have to generate a variable for this
	 * type. if (selectedVar == null) { selectedVar =
	 * this.generateNewVariableForType(type); }
	 * 
	 * variables.add(selectedVar); }
	 * 
	 * return variables; }
	 */

	/*
	 * private Variable selectCachedParameters(Class<?> t) {
	 * 
	 * SimpleList<Sequence> l = null; if (Options.alwaysUseIntsAsObjects() &&
	 * t.equals(Object.class)) { l =
	 * componentManager.getSequencesForType(int.class, false); } else if
	 * (t.isArray()) { SimpleList<Sequence> l1 =
	 * componentManager.getSequencesForType(t, false); SimpleList<Sequence> l2 =
	 * HelperSequenceCreator.createSequence( componentManager, t); l = new
	 * ListOfLists<Sequence>(l1, l2); } else { l =
	 * componentManager.getSequencesForType(t, false); }
	 * 
	 * // If there is no none in the cache, we randomly generate one sequence if
	 * (l.size() == 0) { Variable var = generateNewVariableForType(t);
	 * componentManager.addGeneratedSequence(var.owner); return var; }
	 * 
	 * Match m = Match.COMPATIBLE_TYPE; Sequence seqChosen =
	 * Randomness.randomMember(l); Variable var =
	 * seqChosen.randomVarOfTypeLastStatement(t, m); return var; }
	 */
	/**
	 * Randomly generate a sequence of statements which creates a variable with
	 * the given type.
	 * 
	 * @param type
	 * @return
	 */
	private Variable generateNewVariableForType(Class<?> type) {

		if (Options.alwaysUseIntsAsObjects() && type.equals(Object.class)) {
			type = int.class;
		}

		if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(type)) {

			// Construct the primitive statement which returns the variable
			Object value = PrimitiveGenerator.chooseValue(type);
			StatementKind stmt = RAssignment
					.statementForAssignment(type, value);
			// For primitive assign statement, we add an empty input variables
			TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
			Sequence seq = new Sequence().extend(gStmt,
					new ArrayList<Variable>());
			// Construct the variable created by the above primitive statement
			Variable var = new Variable(seq, seq.size() - 1);
			return var;
		} else {
			// Randomly return null for reference type.
			boolean retNull = Randomness.nextRandomBool();
			if (retNull) {
				StatementKind stmt = RAssignment.statementForAssignment(type,
						null);
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
					Variable inputVar = generateNewVariableForType(elementType);
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
				RArrayDeclaration array = new RArrayDeclaration(elementType,
						length);
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(array);
				Sequence seq = paramSeq.extend(gStmt, parameters);
				// Construct the return variable
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			} else { // Object type
				ClassInfo ci = ensureProject().getClassInfo(type);
				ConstructorInfo[] ctors = ci.getConstructors();

				if (ctors.length == 0) {
					// we choose an accessible constructor of a subclass of this
					// type
					List<ClassInfo> subClasses = ensureProject()
							.getAccessibleClasses(type);
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
				// Randomly choose a constructor
				int index = Randomness.nextRandomInt(ctors.length);

				// Construct the input variables
				ClassInfo[] types = ctors[index].getParameterTypes();
				List<Variable> inputVars = new ArrayList<Variable>(types.length);
				List<Sequence> seqs = new ArrayList<Sequence>(types.length);
				for (int j = 0; j < types.length; j++) {
					Variable inputVar = generateNewVariableForType(types[j]
							.getType());
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
						.getConstructor());
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
				Sequence seq = paramSeq.extend(gStmt, parameters);

				// Construct the variable which is created by the above
				// statement
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			}
		}
	}

	private Variable generateNewVariableForCtor(Class<?> type) {

		if (Options.alwaysUseIntsAsObjects() && type.equals(Object.class)) {
			type = int.class;
		}

		if (PrimitiveTypes.isBoxedOrPrimitiveOrStringOrEnumType(type)) {

			// Construct the primitive statement which returns the variable
			Object value = PrimitiveGenerator.chooseValue(type);
			StatementKind stmt = RAssignment
					.statementForAssignment(type, value);
			// For primitive assign statement, we add an empty input variables
			TzuYuAction gStmt = TzuYuAction.fromStatmentKind(stmt);
			Sequence seq = new Sequence().extend(gStmt,
					new ArrayList<Variable>());
			// Construct the variable created by the above primitive statement
			Variable var = new Variable(seq, seq.size() - 1);
			return var;
		} else {
			// Randomly return null for reference type.
			// boolean retNull = Randomness.nextRandomBool();
			boolean retNull = false;
			if (retNull) {
				StatementKind stmt = RAssignment.statementForAssignment(type,
						null);
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
					Variable inputVar = generateNewVariableForCtor(elementType);
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
				RArrayDeclaration array = new RArrayDeclaration(elementType,
						length);
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(array);
				Sequence seq = paramSeq.extend(gStmt, parameters);
				// Construct the return variable
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			} else { // Object type
				ClassInfo ci = ensureProject().getClassInfo(type);
				ConstructorInfo[] ctors = ci.getConstructors();

				if (ctors.length == 0) {
					// we choose an accessible constructor of a subclass of this
					// type
					List<ClassInfo> subClasses = ensureProject()
							.getAccessibleClasses(type);
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
				// Randomly choose a constructor
				int index = Randomness.nextRandomInt(ctors.length);

				// Construct the input variables
				ClassInfo[] types = ctors[index].getParameterTypes();
				List<Variable> inputVars = new ArrayList<Variable>(types.length);
				List<Sequence> seqs = new ArrayList<Sequence>(types.length);
				for (int j = 0; j < types.length; j++) {
					Variable inputVar = generateNewVariableForCtor(types[j]
							.getType());
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
						.getConstructor());
				TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
				Sequence seq = paramSeq.extend(gStmt, parameters);

				// Construct the variable which is created by the above
				// statement
				Variable var = new Variable(seq, seq.size() - 1);
				return var;
			}
		}
	}

	/*
	 * private InputAndSuccessFlag selectInputs(Variable receiver, TzuYuAction
	 * stmt) { // the input types contains the values required as // input to
	 * the given statement List<Class<?>> inputTypes = stmt.getInputTypes(); //
	 * The list contains the sequences that generated the input variables //
	 * corresponding to the input types. List<Sequence> sequences = new
	 * ArrayList<Sequence>(); // The variable indices for each generated input
	 * variable. List<VarIndex> variables = new ArrayList<VarIndex>(); int
	 * totStatements = 0;
	 * 
	 * for (int index = 0; index < inputTypes.size(); index++) { Class<?> type =
	 * inputTypes.get(index);
	 * 
	 * if (!ReflectionUtils.isVisible(type)) { return new
	 * InputAndSuccessFlag(false, null, null); }
	 * 
	 * boolean isReceiver = (index == 0 && stmt.getAction() instanceof RMethod
	 * && !((RMethod) stmt.getAction()).isStatic()); Sequence chosenSeq = null;
	 * VarIndex chosenVar = null; if (isReceiver) { // For receiver we only use
	 * the main object modified by the // input main sequence can be used.
	 * chosenSeq = receiver.owner; chosenVar = new VarIndex(receiver.stmtIdx,
	 * receiver.argIdx); } else { // For non-receiver parameters there are 3
	 * sources: int source = Randomness.nextRandomInt(3); Variable randomVar =
	 * null; if (source == 0) {// Source 1: select from the sequence
	 * List<Variable> vars = receiver.owner.getMatchedVariable(type); if
	 * (vars.size() != 0) { randomVar = Randomness.randomMember(vars); }// There
	 * may be no matching variable in the cache } else if (source == 1) { //
	 * Source 2: from cached sequences randomVar =
	 * this.selectCachedParameters(type); } else {// Source 3: Randomly generate
	 * new one randomVar = this.generateNewVariableForType(type); } // Default
	 * case when there is no sequence in the cache, // then we have to generate
	 * a variable for this type. if (randomVar == null) { randomVar =
	 * this.generateNewVariableForType(type); } // If there is still no matching
	 * variable, we abort, so // cannot get a variable for the specified input.
	 * if (randomVar == null) { return new InputAndSuccessFlag(false, sequences,
	 * variables); } else { chosenSeq = randomVar.owner; Variable newVar = new
	 * Variable(chosenSeq, randomVar.stmtIdx, randomVar.argIdx);
	 * parameterStore.add(stmt.getAction(), index, newVar);
	 * 
	 * chosenVar = new VarIndex(randomVar.stmtIdx, randomVar.argIdx); } }
	 * 
	 * sequences.add(chosenSeq); // Update the statement index of the chosen
	 * variable chosenVar.stmtIdx += totStatements;
	 * 
	 * variables.add(chosenVar); totStatements += chosenSeq.size(); }
	 * 
	 * return new InputAndSuccessFlag(true, sequences, variables); }
	 */

	/**
	 * Also ways create new inputs for the parameters(exclusive of the receiver)
	 * in the statement. For receiver variable, we still use the one modified by
	 * the main sequence.
	 * 
	 * @param main
	 * @param stmt
	 * @return
	 */
	public InputAndSuccessFlag selectNewInputs(Variable receiver,
			TzuYuAction stmt) {
		List<Class<?>> inputTypes = stmt.getInputTypes();

		List<Sequence> sequences = new ArrayList<Sequence>();

		List<VarIndex> indices = new ArrayList<VarIndex>();
		int totStatements = 0;
		StatementKind statement = stmt.getAction();

		boolean isStatic = statement.isStatic();

		for (int index = 0; index < inputTypes.size(); index++) {
			Class<?> type = inputTypes.get(index);

			if (!ReflectionUtils.isVisible(type)) {
				return new InputAndSuccessFlag(false, null, null);
			}

			boolean isReceiver = (index == 0 && isStatic);

			Sequence chosenSeq = null;
			VarIndex chosenVar = null;

			if (isReceiver) {
				chosenSeq = receiver.owner;
				chosenVar = new VarIndex(receiver.stmtIdx, receiver.argIdx);
			} else {
				Variable randomVar = this.generateNewVariableForType(type);
				if (randomVar == null) {
					return new InputAndSuccessFlag(false, sequences, indices);
				}
				chosenSeq = randomVar.owner;
				chosenVar = new VarIndex(chosenSeq.size() - 1, -1);

				Variable newVar = new Variable(chosenSeq, randomVar.stmtIdx,
						randomVar.argIdx);
				parameterStore.add(stmt.getAction(), index, newVar);
			}
			// Add the sequence to the final sequence for return
			sequences.add(chosenSeq);
			// Update the indices to absolute index
			chosenVar.stmtIdx += totStatements;
			// Add to the final indices for return
			indices.add(chosenVar);
			totStatements += chosenSeq.size();
		}
		return new InputAndSuccessFlag(true, sequences, indices);
	}

	/**
	 * Generate a constructor variable for the given class type by randomly
	 * calling one of the constructors defined in this class. If the constructor
	 * needs other parameters we need to generate them. If the constructor needs
	 * a parameter of the same type as the <code>target</code>, we should take
	 * care to handle the recursive parameter construction procedure by limiting
	 * the depth of this recursive call.
	 * 
	 * @param target
	 * @return
	 */
	public InputAndSuccessFlag selectCtor(Class<?> target) {

		List<Sequence> sequences = new ArrayList<Sequence>(1);

		List<VarIndex> variables = new ArrayList<VarIndex>(1);

		if (!ReflectionUtils.isVisible(target)) {
			return new InputAndSuccessFlag(false, null, null);
		}

		ConstructorInfo[] ctors = ensureProject().getClassInfo(target)
				.getConstructors();
		// TODO if there is no accessible constructors
		// we should try to use the static methods which returns an
		// object with the target type, if this is not the case, we
		// cannot generate a constructor for this, thus return null.
		if (ctors.length == 0) {
			// we should choose a constructor of a subclass of this type.
			List<ClassInfo> subClasses = ensureProject().getAccessibleClasses(target);
			if (subClasses.size() == 0) {
				throw new TzuYuException(
						"no accessible constructor for class: " + target);
			} else {
				for (ClassInfo subClass : subClasses) {
					if (subClass.getConstructors().length > 0) {
						ctors = subClass.getConstructors();
						break;
					}
				}
			}
		}
		// Randomly choose a constructor
		int index = Randomness.nextRandomInt(ctors.length);

		// Construct the input variables
		ClassInfo[] types = ctors[index].getParameterTypes();
		List<Variable> inputVars = new ArrayList<Variable>(types.length);
		List<Sequence> seqs = new ArrayList<Sequence>(types.length);
		for (int j = 0; j < types.length; j++) {
			Variable inputVar = generateNewVariableForCtor(types[j].getType());
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
				.getCtor(ctors[index].getConstructor());
		TzuYuAction gStmt = TzuYuAction.fromStatmentKind(rctor);
		Sequence seq = paramSeq.extend(gStmt, parameters);

		VarIndex selectedVar = new VarIndex(seq.size() - 1, -1);

		sequences.add(seq);

		variables.add(selectedVar);

		return new InputAndSuccessFlag(true, sequences, variables);

	}

	/**
	 * Get a set of least used parameters for the statement which may not cause
	 * all the statement execution fail.
	 * 
	 * @param receiver
	 * @param statement
	 * @param argIdx
	 *            the list of argument index to choose.
	 * @return
	 */
	public InputAndSuccessFlag getInputsAfterFailed(Variable receiver,
			TzuYuAction statement, List<Integer> argIdx) {

		StatementKind stmt = statement.getAction();
		List<Class<?>> inputTypes = statement.getInputTypes();

		List<Sequence> sequences = new ArrayList<Sequence>();

		List<VarIndex> indices = new ArrayList<VarIndex>();
		int totStatements = 0;

		boolean isStatic = stmt.isStatic();

		for (int index = 0; index < inputTypes.size(); index++) {

			boolean isReceiver = (index == 0 && isStatic);

			Sequence chosenSeq = null;
			VarIndex chosenVar = null;

			if (isReceiver) {
				chosenSeq = receiver.owner;
				chosenVar = new VarIndex(receiver.stmtIdx, receiver.argIdx);
			} else {
				Variable var = parameterStore.getLRU(stmt, index,
						argIdx.get(index));
				if (var == null) {
					return new InputAndSuccessFlag(false, sequences, indices);
				}
				chosenSeq = var.owner;
				chosenVar = new VarIndex(var.stmtIdx, var.argIdx);
			}

			sequences.add(chosenSeq);
			chosenVar.stmtIdx += totStatements;
			indices.add(chosenVar);

			totStatements += chosenSeq.size();
		}

		return new InputAndSuccessFlag(true, sequences, indices);

	}

	/**
	 * Get a list of integers, each corresponds to the number of different
	 * arguments have been generated for the parameter of the statement. If the
	 * statement is an instance method, the first element is always 1. If the
	 * statement is a class method(a.k.a. static method) the first element is
	 * number of the first non receiver parameter.
	 * 
	 * @param statement
	 * @return
	 */
	public List<Integer> getInputSizes(TzuYuAction statement) {
		StatementKind stmt = statement.getAction();
		List<Class<?>> inputTypes = statement.getInputTypes();
		List<Integer> inputSizes = new ArrayList<Integer>();

		boolean isStatic = stmt.isStatic();

		for (int index = 0; index < inputTypes.size(); index++) {
			boolean isReceiver = (isStatic && index == 0);
			if (isReceiver) {
				inputSizes.add(1);
			} else {
				int argSize = parameterStore.getParameterSize(stmt, index);
				inputSizes.add(argSize);
			}
		}
		return inputSizes;
	}

	public void setProject(TzProject project) {
		this.project = project;
	}

	private TzProject ensureProject() {
		if (project == null) {
			throw new TzuYuException("Tzuyu project not set for tester");
		}
		return project;
	}
}
