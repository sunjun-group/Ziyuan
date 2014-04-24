package tzuyu.engine.model;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import tzuyu.engine.TzClass;
import tzuyu.engine.bool.utils.FormulaNegation;
import tzuyu.engine.bool.utils.FormulaUtils;
import tzuyu.engine.bool.utils.Simplifier;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.dfa.Alphabet;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.utils.Randomness;

public class TzuYuAlphabet extends Alphabet<TzuYuAction> {
	private TzClass project;
	private IPrintStream out;
	
	private TzuYuAlphabet(TzuYuAlphabet from) {
		super();
		this.project = from.project;
		this.out = from.out;
	}
	
	public static TzuYuAlphabet forClass(TzClass project) {
		return new TzuYuAlphabet(project, null);
	}
	
	public static TzuYuAlphabet forStaticGroup(TzClass project) {
		return new TzuYuAlphabet(project, true);
	}
	
	public static TzuYuAlphabet forNonStaticGroup(TzClass project) {
		return new TzuYuAlphabet(project, false);
	}

	private TzuYuAlphabet(TzClass project, Boolean staticGroup) {
		super();
		this.project = project;
		// add constructors as the initial alphabet

		/*
		 * ConstructorInfo[] ctors = Analytics.getTargetClassInfo().getConstructors(); 
		 * for (ConstructorInfo ctor : ctors) { 
		 * 		TzuYuAction action = TzuYuAction.fromCtor(ctor); 
		 * 		sigma.addSymbol(action); 
		 * }
		 */

		// add methods as the initial alphabet
		MethodInfo[] methods = project.getTargetClassInfo().getMethods(
				project.getConfiguration());

		for (MethodInfo method : Randomness.randomSubList(
				Arrays.asList(methods), methods.length)) {
			if (staticGroup == null
					|| Modifier.isStatic(method.getModifiers()) == staticGroup) {
				TzuYuAction action = TzuYuAction.fromMethod(method,
						project.getConfiguration());
				if (action == null) {
					throw new TzRuntimeException(
							"Cannot create the initial alphabet");
				}
				addSymbol(action);
			}
		}
	}

	/**
	 * Incrementally refine the old alphabet by combining the two new alphabet
	 * symbols with the old alphabet and remove the old alphabet symbol which
	 * has the same statement as the two new alphabet symbols.
	 * 
	 * @param action
	 * @param fAction
	 *            the positive action which is the divider.
	 * @param tAction
	 *            the negative action which is the negation of divider.
	 * @return the new alphabet which contains the two alphabets.
	 */
	public TzuYuAlphabet refine(Formula divider, Action action) {

		if (!(action instanceof TzuYuAction)) {
			throw new TzRuntimeException("The action is not an TzuYu alphabet");
		}

		TzuYuAction dividedAction = (TzuYuAction) action;

		StatementKind stmt = dividedAction.getAction();

		Formula trueDivider = divider;

		Formula falseDivider = FormulaNegation.notOf(divider);

		TzuYuAlphabet newSigma = new TzuYuAlphabet(this);

		for (TzuYuAction act : getActions()) {
			if (dividedAction.equals(act)) {
				Formula newTrueFormula = FormulaUtils.andOf(act.getGuard(),
						trueDivider);
				newTrueFormula = Simplifier.simplify(newTrueFormula);

				Formula newFalseFormula = FormulaUtils.andOf(act.getGuard(),
						falseDivider);
				newFalseFormula = Simplifier.simplify(newFalseFormula);
				if (newTrueFormula.equals(Formula.FALSE)) {
					out.println("new guard evaluates to false.");
				} else {
					TzuYuAction trueAction = new TzuYuAction(newTrueFormula,
							stmt);
					newSigma.addSymbol(trueAction);
				}
				if (newFalseFormula.equals(Formula.FALSE)) {
					out.println("new guard evaluates to false.");
				} else {
					TzuYuAction falseAction = new TzuYuAction(newFalseFormula,
							stmt);
					newSigma.addSymbol(falseAction);
				}
			} else {
				newSigma.addSymbol(act);
			}
		}
		
		return newSigma;
	}

	public TzClass getProject() {
		return project;
	}

	public void setOutStream(IPrintStream outStream) {
		this.out = outStream;
	}
}
