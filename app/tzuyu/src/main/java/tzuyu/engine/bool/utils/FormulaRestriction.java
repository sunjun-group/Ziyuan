/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.utils;

import static tzuyu.engine.bool.Operator.EQ;

import java.util.List;

import tzuyu.engine.bool.LIATerm;
import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.bool.formula.True;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.utils.Pair;

/**
 * @author LLT
 * 
 */
@SuppressWarnings("deprecation")
public class FormulaRestriction extends ExpressionVisitor {
	private List<Atom> vars;
	private List<Integer> vals;
	private Formula result;
	
	@Override
	public void visit(False atom) {
		result = atom;
	}
	
	@Override
	public void visit(True atom) {
		result = atom;
	}
	
	@Override
	public void visit(AndFormula and) {
		AndFormula newAnd = new AndFormula();
		for (Formula clause : and.getElements()) {
			// TODO LLT: NICE TO HAVE - extend the current visitor instead?
			Formula expr = restrict(clause, vars, vals);
			if (Formula.FALSE.equals(expr)) {
				result = Formula.FALSE;
				return;
			} else if (!Formula.TRUE.equals(expr)) {
				newAnd.add(expr);
			}
		}

		if (newAnd.getElements().size() == 0) {
			result = Formula.TRUE;
		} else if (newAnd.getElements().size() == 1) {
			result = newAnd.getElements().get(0);
		} else {
			result = newAnd;
		}
	}
	
	@Override
	public void visit(OrFormula or) {
		OrFormula newOr = new OrFormula();
		for (Formula term : or.getElements()) {
			Formula expr = restrict(term, vars, vals);
			if (Formula.TRUE.equals(expr)) {
				result = Formula.TRUE;
				return;
			} else if (!Formula.FALSE.equals(expr)) {
				newOr.add(expr);
			}
		}

		if (newOr.getElements().size() == 0) {
			result = Formula.FALSE;
		} else if (newOr.getElements().size() == 1) {
			result = newOr.getElements().get(0);
		} else {
			result = newOr;
		}
	}
	
	@Override
	public void visitAtom(Atom atom) {
		for (int index = 0; index < vars.size(); index++) {
			Atom curAtom = vars.get(index);
			if (atom.equals(curAtom)) {
				result = associatedTo(vals.get(index));
				break;
			}
		}
	}
	
	@Override
	public void visit(LIAAtom liaAtom) {
		if (liaAtom.getMVFOExpr().size() != 1) {
			super.visit(liaAtom);
			return;
		}
		for (int index = 0; index < vars.size(); index++) {
			Atom curAtom = vars.get(index);
			if (curAtom instanceof LIAAtom) {
				int relationship = getRelationship((LIAAtom)curAtom, liaAtom);
				if (relationship == ATOMS_NO_RELATIONSHIP) {
					continue;
				}
				Formula curVal = associatedTo(vals.get(index));
				if (relationship == ATOMS_EQUAL) {
					result = curVal;
					break;
				}					
				if (relationship == ATOMS_HALF_NEGATION) {
					result = FormulaUtils.not(curVal);
					break;
				}
				if (relationship == ATOMS_A_BOUND_B && curVal == Formula.TRUE) {
					result = curVal;
					break;
				}
				if (relationship == ATOMS_B_BOUND_A && curVal == Formula.FALSE) {
					result = curVal;
					break;
				}
			}
		}
	}
	
	private Formula associatedTo(int binaryValue) {
		if (binaryValue == 0) {
			return Formula.FALSE;
		}
		if (binaryValue == 1) {
			return Formula.TRUE;
		}
		throw new TzRuntimeException("Expect binary value(0, 1), get "
				+ binaryValue);
	}
	
	public static final int ATOMS_NO_RELATIONSHIP = 0;
	public static final int ATOMS_EQUAL = 1;
	public static final int ATOMS_A_BOUND_B = 2;
	public static final int ATOMS_B_BOUND_A = 3;
	public static final int ATOMS_HALF_NEGATION = 4;
	public static int getRelationship(LIAAtom curAtom, LIAAtom liaAtom) {
		if (liaAtom.equals(curAtom)) {
			return ATOMS_EQUAL;
		}
		LIATerm curTerm = curAtom.getSingleTerm();
		LIATerm liaTerm = liaAtom.getSingleTerm();
		if (curTerm != null && curTerm.getVariable().equals(liaTerm.getVariable())) {
			Pair<Operator, Operator> opPair = Pair.of(
					curAtom.getOperator().negateIfPlusNegValue(curTerm.getCoefficient()), 
					liaAtom.getOperator().negateIfPlusNegValue(liaTerm.getCoefficient()));
			 if (opPair.equals(Pair.of(EQ, EQ))) {
				 return ATOMS_HALF_NEGATION;
			 }
			 double curConst = curAtom.getConstant() / curTerm.getCoefficient();
			 double liaConst = liaAtom.getConstant() / liaTerm.getCoefficient();
			if (isNegation(opPair.a, opPair.b, curConst, liaConst)
					|| isHalfNegation(opPair.a, opPair.b, curConst, liaConst)
					|| isHalfNegation(opPair.b, opPair.a, liaConst, curConst)) {
				 return ATOMS_HALF_NEGATION;
			 }
			 if (isBound(opPair.a, opPair.b, curConst, liaConst)) {
				 return ATOMS_A_BOUND_B;
			 }
			 if (isBound(opPair.b, opPair.a, liaConst, curConst)) {
				 return ATOMS_B_BOUND_A;
			 }
		}
		return ATOMS_NO_RELATIONSHIP;
	}
	
	private static boolean isBound(Operator a, Operator b,
			double aConst, double bConst) {
		return (a.isGT() && b.isGT() && aConst > bConst) || 
				(a.isLT() && b.isLT() && aConst < bConst);
	}
	
	private static boolean isNegation(Operator a, Operator b, double aConst,
			double bConst) {
		return (aConst == bConst) && (a.negative() == b);
	}
	
	private static boolean isHalfNegation(Operator a, Operator b, double aConst,
			double bConst) {
		if (a.isGT() && b.isLT() && (aConst > bConst)) {
			return true;
		}
		return false;
	}

	@Override
	public void visit(NotFormula notFormula) {
		result = FormulaUtils.not(restrict(notFormula.getChild(), vars, vals));
	}
	
	public static Formula restrict(Formula formula, List<Atom> vars, List<Integer> vals) {
		FormulaRestriction visitor = new FormulaRestriction();
		visitor.vars = vars;
		visitor.vals = vals;
		formula.accept(visitor);
		if (visitor.result == null) {
			return formula;
		}
		return visitor.result;
	}

}
