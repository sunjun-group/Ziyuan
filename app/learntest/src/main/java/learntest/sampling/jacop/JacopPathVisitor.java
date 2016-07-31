package learntest.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Atom;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.NotFormula;
import sav.common.core.formula.utils.ExpressionVisitor;

public class JacopPathVisitor extends ExpressionVisitor {
	
	private List<LIAAtom> atoms;
	private List<List<LIAAtom>> choices;

	private List<Integer> indeices;
	private boolean hasNext;
	
	public JacopPathVisitor() {
		atoms = new ArrayList<LIAAtom>();
		choices = new ArrayList<List<LIAAtom>>();
		indeices = new ArrayList<Integer>();
		hasNext = true;
	}
	
	@Override
	public void visit(NotFormula notFormula) {
		List<LIAAtom> choice = new ArrayList<LIAAtom>();
		List<Atom> atomics = notFormula.getAtomics();
		for (Atom atom : atomics) {
			if (atom instanceof LIAAtom) {
				choice.add((LIAAtom) atom);
			}
		}
		if (choice.size() >= 1) {
			choices.add(choice);
			indeices.add(0);
		}
	}

	@Override
	public void visit(LIAAtom liaAtom) {
		atoms.add(liaAtom);
	}

	@Override
	public void visit(AndFormula and) {
		List<Formula> elements = and.getElements();
		for (Formula element : elements) {
			element.accept(this);
		}
	}
	
	public boolean hasNextChoice() {
		if (indeices.isEmpty() && hasNext) {
			hasNext = false;
			return true;
		}
		return hasNext;
	}
	
	public List<LIAAtom> getNextChoice() {
		List<LIAAtom> choice = new ArrayList<LIAAtom>();
		int idx = 0;
		for (int i : indeices) {
			choice.add(choices.get(idx ++).get(i));
		}
		calculateNext();
		return choice;
	}
	
	private void calculateNext() {
		int index = 0;
		for (int i : indeices) {
			if (i < choices.get(index).size() - 1) {
				break;
			}
			index ++;
		}
		if (index < choices.size()) {
			for (int i = 0; i < index; i++) {
				indeices.set(i, 0);
			}
			indeices.set(index, indeices.get(index) + 1);
		} else {
			hasNext = false;
		}
	}

	public List<LIAAtom> getAtoms() {
		return atoms;
	}
	
}
