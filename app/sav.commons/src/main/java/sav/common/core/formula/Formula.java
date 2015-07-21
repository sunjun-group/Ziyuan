package sav.common.core.formula;

import java.util.List;

import sav.common.core.formula.utils.ExpressionVisitor;

public interface Formula {

	public static final Formula TRUE = True.getInstance();
	public static final Formula FALSE = False.getInstance();

	public List<Var> getReferencedVariables();

	public List<Atom> getAtomics();

	public void accept(ExpressionVisitor visitor);
}
