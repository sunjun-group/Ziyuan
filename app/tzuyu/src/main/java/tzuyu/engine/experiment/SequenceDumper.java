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
import tzuyu.engine.utils.ClassUtils;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.LogicUtils;
import tzuyu.engine.utils.PrimitiveTypes;

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
	private final VariableRenamer renamer;
	private TzConfiguration config;
	private JWriterFactory junitWriterFactory;

	public SequenceDumper(Sequence sequenceToPrint, VariableRenamer renamer,
			TzConfiguration config) {
		this.sequenceToPrint = sequenceToPrint;
		this.renamer = renamer;
		this.config = config;
		this.junitWriterFactory = new JWriterFactory(config.getJunitConfig(),
				renamer);
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
				RAssignment rAssginment = (RAssignment) statement;
				junitWriterFactory.createRAssgnmentWriter(rAssginment, newVar)
						.writeCode(sb);
			}
		} else if (statement instanceof RMethod) {
			junitWriterFactory.createRMethodWriter((RMethod) statement, newVar,
					inputVars).writeCode(sb);
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
		String declaringStr = ClassUtils.getSimpleCompilableName(declaringClass);
		sb.append(declaringStr
				+ " "
				+ renamer.getRenamedVar(newVar.getStmtIdx(), newVar.getArgIdx())
				+ " = "
				+ (isNonStaticMember ? renamer.getRenamedVar(
						inputVars.get(0).getStmtIdx(), inputVars.get(0).getArgIdx())
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
				sb.append(renamer.getRenamedVar(var.getStmtIdx(), var.getArgIdx()));
			}
		}
		sb.append(");");
		sb.append(Globals.lineSep);
	}


	private void printArrayDeclaration(RArrayDeclaration statement,
			Variable newVar, List<Variable> inputVars, StringBuilder sb) {
		int length = statement.getLength();
		if (inputVars.size() > length)
			throw new IllegalArgumentException("Too many arguments:"
					+ inputVars.size() + " capacity:" + length);
		String declaringClass = statement.getElementType().getSimpleName();
		sb.append(declaringClass + "[] "
				+ renamer.getRenamedVar(newVar.getStmtIdx(), newVar.getArgIdx())
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
				sb.append(renamer.getRenamedVar(var.getStmtIdx(), var.getArgIdx()));
			}
		}
		sb.append("};");
		sb.append(Globals.lineSep);
	}
}