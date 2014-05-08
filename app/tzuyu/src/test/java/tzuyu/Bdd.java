/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.LIATerm;
import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.formula.LIAAtom;
import static tzuyu.engine.bool.utils.FormulaUtils.*;
import tzuyu.engine.bool.utils.Simplifier;
import tzuyu.engine.iface.IPrintStream;
import tzuyu.engine.model.ExecutionOutcome;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.StatementKind;

/**
 * @author LLT
 *
 */
public class Bdd extends AbstractTest {
	private FieldVar var = newVar();
	
	@Test
	public void test() {
		LIAAtom atom1 = newAtom(Operator.GT, -336);
		LIAAtom atom2 = newAtom(Operator.LE, -63);
		LIAAtom atom3 = newAtom(Operator.LE, -336);
		/*
		 * (([1] <= -336.0)&&([1] > -336.0))||([1] <= -63.0)
		 * 
		 * */
		Formula formula1 = or(atom3, and(atom1, atom2));
		Formula formula2 = or(and(atom1, atom2), atom3);
		Formula formula3 = or(atom3, and(atom2, atom1));
		Formula formula = formula1;
//		formula = and(atom2, atom3);
		System.out.println(formula);
		System.out.println(Simplifier.simplify(formula));
	}

	private LIAAtom newAtom(Operator op, double right) {
		List<LIATerm> terms = new ArrayList<LIATerm>();
		LIATerm term = new LIATerm(var, 1);
		terms.add(term);
		return new LIAAtom(terms , op, right);
	}

	/**
	 * @return
	 */
	private FieldVar newVar() {
		FieldVar var = FieldVar.getVar(new StatementKind() {
			
			@Override
			public String toParseableString() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isPrimitive() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasReceiverParameter() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasNoArguments() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Class<?> getReturnType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<Class<?>> getInputTypes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ExecutionOutcome execute(Object[] inputVals, IPrintStream out) {
				// TODO Auto-generated method stub
				return null;
			}
		}, 1, null);
		return var;
	}
}
