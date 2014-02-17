package tzuyu.engine.experiment;

import java.lang.reflect.Modifier;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.LogicUtils;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.Types;

/**
 * This class prints a sequence with all variables renamed
 * */
class SequenceDumper {
	/**
	 * The sequence to be printed
	 * */
	public final Sequence sequenceToPrint;
	/**
	 * The class for renaming all output variables
	 * */
	public final VariableRenamer renamer;
	private TzConfiguration config;

	public SequenceDumper(Sequence sequenceToPrint, VariableRenamer renamer,
			TzConfiguration config) {
		this.sequenceToPrint = sequenceToPrint;
		this.renamer = renamer;
		this.config = config;
	}

	/**
	 * Print a sequence statement by statement, with variables renamed.
	 * */
	public String printSequenceAsCodeString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.sequenceToPrint.size(); i++) {
			Statement statement = this.sequenceToPrint.getStatement(i);
			Variable outputVar = this.sequenceToPrint.getVariable(i);
			List<Variable> inputVars = this.sequenceToPrint.getInputs(i);
			// store the code text of the current statement
			StringBuilder oneStatement = new StringBuilder();
			// print the current statement
			appendCode(oneStatement, statement, outputVar, inputVars);

			sb.append(oneStatement);

		}
		return sb.toString();
	}

	/**********************************************************
	 * The following code prints different types of statements.
	 **********************************************************/
	private void appendCode(StringBuilder sb, Statement stmt, Variable newVar,
			List<Variable> inputVars) {
		StatementKind statement = stmt.getAction().getAction();
		if (statement instanceof RAssignment) {
			if (config.isLongFormat()) {
				RAssignment primiveStatement = (RAssignment) statement;
				this.printPrimitiveType(primiveStatement, newVar, inputVars, sb);
			}
		} else if (statement instanceof RMethod) {
			this.printRMethod((RMethod) statement, newVar, inputVars, sb);
		} else if (statement instanceof RConstructor) {
			this.printRConstructor((RConstructor) statement, newVar, inputVars,
					sb);
		} else if (statement instanceof RArrayDeclaration) {
			RArrayDeclaration arrayDeclaration = (RArrayDeclaration) statement;
			this.printArrayDeclaration(arrayDeclaration, newVar, inputVars, sb);
		} else {
			throw new Error("Wrong type of statement: " + statement);
		}
	}

	private void printRMethod(RMethod rmethod, Variable newVar,
			List<Variable> inputVars, StringBuilder sb) {

		if (!rmethod.isVoid()) {
			sb.append(getSimpleCompilableName(rmethod.getMethod()
					.getReturnType()));
			String cast = "";
			sb.append(" "
					+ renamer.getRenamedVar(newVar.getStmtIdx(), newVar.argIdx)
					+ " = " + cast);
		}

		String receiverString = rmethod.isStatic() ? null : renamer
				.getRenamedVar(inputVars.get(0).getStmtIdx(),
						inputVars.get(0).argIdx);
		appendReceiverOrClassForStatics(rmethod, receiverString, sb);

		sb.append(".");
		sb.append(rmethod.getTypeArguments());
		sb.append(rmethod.getMethod().getName() + "(");

		int startIndex = (rmethod.isStatic() ? 0 : 1);
		for (int i = startIndex; i < inputVars.size(); i++) {
			if (i > startIndex) {
				sb.append(", ");
			}
			Class<?> type = rmethod.getInputTypes().get(i);
			Variable var = inputVars.get(i);
			// CASTING.
			// We cast whenever the variable and input types are not identical.
			// We also cast if input type is a primitive, because Randoop uses
			// boxed primitives, and need to convert back to primitive.
			if (PrimitiveTypes.isPrimitive(type) && config.isLongFormat()) {
				sb.append("(" + type.getSimpleName() + ")");
			} else if (!var.getType().equals(type)) {
				sb.append("(" + type.getSimpleName() + ")");
			}

			// In the short output format, statements like "int x = 3" are not
			// added
			// to a sequence; instead, the value (e.g. "3") is inserted directly
			// added as arguments to method calls.
			Statement statementCreatingVar = var.getDeclaringStatement();
			StatementKind stmt = statementCreatingVar.getAction().getAction();
			if (!config.isLongFormat() && stmt instanceof RAssignment) {
				sb.append(PrimitiveTypes.toCodeString(
						((RAssignment) stmt).getValue(),
						config.getStringMaxLength()));
			} else {
				sb.append(renamer.getRenamedVar(var.getStmtIdx(), var.argIdx));
			}
		}

		sb.append(");" + Globals.lineSep);
	}

	private void printRConstructor(RConstructor ctor, Variable newVar,
			List<Variable> inputVars, StringBuilder sb) {

		assert inputVars.size() == ctor.getInputTypes().size();

		Class<?> declaringClass = ctor.getConstructor().getDeclaringClass();
		boolean isNonStaticMember = !Modifier.isStatic(declaringClass
				.getModifiers()) && declaringClass.isMemberClass();
		assert LogicUtils.implies(isNonStaticMember, inputVars.size() > 0);

		// Note on isNonStaticMember: if a class is a non-static member class,
		// the runtime signature of the constructor will have an additional
		// argument (as the first argument) corresponding to the owning object.
		// When printing it out as source code, we need to treat it as a special
		// case: instead of printing "new Foo(x,y,z)" we have to print
		// "x.new Foo(y,z)".

		// TODO the last replace is ugly. There should be a method that does it.
		String declaringStr = getSimpleCompilableName(declaringClass);
		sb.append(declaringStr
				+ " "
				+ renamer.getRenamedVar(newVar.getStmtIdx(), newVar.argIdx)
				+ " = "
				+ (isNonStaticMember ? renamer.getRenamedVar(
						inputVars.get(0).getStmtIdx(), inputVars.get(0).argIdx)
						+ "." : "")
				+ "new "
				+ (isNonStaticMember ? declaringClass.getSimpleName()
						: declaringStr) + "(");
		for (int i = (isNonStaticMember ? 1 : 0); i < inputVars.size(); i++) {
			Variable var = inputVars.get(i);
			if (i > (isNonStaticMember ? 1 : 0)) {
				sb.append(", ");
			}

			// We cast whenever the variable and input types are not identical.
			if (!var.getType().equals(ctor.getInputTypes().get(i)))
				sb.append("(" + ctor.getInputTypes().get(i).getSimpleName()
						+ ")");

			// In the short output format, statements like "int x = 3" are not
			// dded to a sequence; instead, the value (e.g. "3") is inserted
			// directly added as arguments to method calls.
			Statement statement = var.getDeclaringStatement();
			StatementKind sk = statement.getAction().getAction();
			if (!config.isLongFormat() && sk instanceof RAssignment) {
				sb.append(PrimitiveTypes.toCodeString(
						((RAssignment) sk).getValue(),
						config.getStringMaxLength()));
			} else {
				sb.append(renamer.getRenamedVar(var.getStmtIdx(), var.argIdx));
			}
		}
		sb.append(");");
		sb.append(Globals.lineSep);
	}

	private void printPrimitiveType(RAssignment statement, Variable newVar,
			List<Variable> inputVars, StringBuilder sb) {
		Class<?> type = statement.getReturnType();
		// print primitive type
		if (type.isPrimitive()) {
			sb.append(PrimitiveTypes.boxedType(type).getSimpleName());
			sb.append(" ");
			sb.append(renamer.getRenamedVar(newVar.getStmtIdx(), newVar.argIdx));
			sb.append(" = new ");
			sb.append(PrimitiveTypes.boxedType(type).getSimpleName());
			sb.append("(");
			sb.append(PrimitiveTypes.toCodeString(statement.getValue(),
					config.getStringMaxLength()));
			sb.append(");");
			sb.append(Globals.lineSep);

		} else {
			sb.append(getSimpleCompilableName(type));
			sb.append(" ");
			sb.append(this.renamer.getRenamedVar(newVar.getStmtIdx(), newVar.argIdx));
			sb.append(" = ");
			sb.append(PrimitiveTypes.toCodeString(statement.getValue(),
					config.getStringMaxLength()));
			sb.append(";");
			sb.append(Globals.lineSep);
		}
	}

	private void printArrayDeclaration(RArrayDeclaration statement,
			Variable newVar, List<Variable> inputVars, StringBuilder sb) {
		int length = statement.getLength();
		if (inputVars.size() > length)
			throw new IllegalArgumentException("Too many arguments:"
					+ inputVars.size() + " capacity:" + length);
		String declaringClass = statement.getElementType().getSimpleName();
		sb.append(declaringClass + "[] "
				+ renamer.getRenamedVar(newVar.getStmtIdx(), newVar.argIdx)
				+ " = new " + declaringClass + "[] { ");
		for (int i = 0; i < inputVars.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}

			Variable var = inputVars.get(i);
			// In the short output format, statements like "int x = 3" are not
			// added
			// to a sequence; instead, the value (e.g. "3") is inserted directly
			// added as arguments to method calls.
			Statement stmt = var.getDeclaringStatement();
			StatementKind sk = stmt.getAction().getAction();
			if (!config.isLongFormat() && sk instanceof RAssignment) {
				sb.append(PrimitiveTypes.toCodeString(
						((RAssignment) sk).getValue(),
						config.getStringMaxLength()));
			} else {
				sb.append(renamer.getRenamedVar(var.getStmtIdx(), var.argIdx));
			}
		}
		sb.append("};");
		sb.append(Globals.lineSep);
	}

	private static String getSimpleCompilableName(Class<?> cls) {
		String retval = cls.getSimpleName();

		// If it's an array, it starts with "[".
		if (retval.charAt(0) == '[') {
			// Class.getName() returns a a string that is almost in JVML
			// format, except that it slashes are periods. So before calling
			// classnameFromJvm, we replace the period with slashes to
			// make the string true JVML.
			retval = Types.getTypeName(retval.replace('.', '/'));
		}

		// If inner classes are involved, Class.getName() will return
		// a string with "$" characters. To make it compilable, must replace
		// with
		// dots.
		retval = retval.replace('$', '.');

		return retval;
	}

	private static void appendReceiverOrClassForStatics(RMethod rmethod,
			String receiverString, StringBuilder b) {
		if (rmethod.isStatic()) {
			String s2 = rmethod.getMethod().getDeclaringClass().getSimpleName()
					.replace('$', '.'); // TODO combine this with last if clause
			b.append(s2);
		} else {
			Class<?> expectedType = rmethod.getInputTypes().get(0);
			String className = expectedType.getSimpleName();
			boolean mustCast = className != null
					&& PrimitiveTypes
							.isBoxedPrimitiveTypeOrString(expectedType)
					&& !expectedType.equals(String.class);
			if (mustCast) {
				// this is a little paranoid but we need to cast primitives in
				// order to get them boxed.
				b.append("((" + className + ")" + receiverString + ")");
			} else {
				b.append(receiverString);
			}
		}
	}
}