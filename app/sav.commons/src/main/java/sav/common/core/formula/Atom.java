package sav.common.core.formula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sav.common.core.formula.utils.DisplayVisitor;

/**
 * The abstract atomic expression used in this boolean library. The subclasses
 * of this class can represents different atomic expressions in its related
 * theories(e.g., the Linear Integer Arithmetic LIA for short) and must override
 * the unimplemented abstract methods.
 * 
 * @author Spencer Xiao
 * 
 */
public abstract class Atom implements Formula {

	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		for (int index = 0; index < vars.size(); index++) {
			Atom atom = vars.get(index);
			if (this.equals(atom)) {
				if (vals.get(index) == 0) {
					return Formula.FALSE;
				} else {
					return Formula.TRUE;
				}
			}
		}

		return this;
	}

	public List<Atom> getAtomics() {
		List<Atom> atoms = new ArrayList<Atom>();
		atoms.add(this);
		return atoms;
	}

	public Formula simplify() {
		return this;
	}
	
	@Override
	public String toString() {
		DisplayVisitor visitor = new DisplayVisitor();
		accept(visitor);
		return visitor.getResult();
	}
	
	public static boolean equals(List<Atom> l1, List<Atom> l2){
		if (l1.size() != l2.size()) {
			return false;
		}

		List<Atom> l2Temp = new ArrayList<Atom>(l2.size());
		l2Temp.addAll(l2);
		for (Iterator<Atom> iterator = l1.iterator(); iterator.hasNext();) {
			Atom liaTerm1 = iterator.next();
			boolean found = false;
			int i = 0;
			for (; i< l2Temp.size(); i++) {
				Atom liaTerm2 = l2Temp.get(i);
				if (liaTerm1.equals(liaTerm2)) {
					found = true;
					break;
				}
			}
			if (found) {
				l2Temp.remove(i);
			}else {
				return false;
			}
		}
		return true;
	}
}
