package tzuyu.engine.bool.bdd;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tzuyu.engine.bool.Var;
import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.bool.utils.EquivalenceChecker;
import tzuyu.engine.bool.utils.FormulaNegation;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

public class TestBDD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AtomicTest p = new AtomicTest(1);
		AtomicTest q = new AtomicTest(3);

		List<Atom> atoms = new ArrayList<Atom>();
		atoms.add(p);
		atoms.add(q);

		Formula pLit = p;
		Formula nPLit = FormulaNegation.notOf(p);
		Formula qLit = q;
		Formula nQLit = FormulaNegation.notOf(q);

		AndFormula t1 = new AndFormula();
		t1.add(pLit);
		t1.add(qLit);

		AndFormula t2 = new AndFormula();
		t2.add(pLit);
		t2.add(nQLit);

		AndFormula t3 = new AndFormula();
		t3.add(nPLit);
		t3.add(qLit);

		OrFormula t = new OrFormula();
		t.add(t1);
		t.add(t2);
		t.add(t3);

		OrFormula c = new OrFormula();
		c.add(pLit);
		c.add(qLit);

		BDDManager manager = new BDDManager(atoms);

		BDDNode bdd = manager.build(t);

		boolean equivalent = EquivalenceChecker.checkEquivalence(t, c);
		System.out.println("the answer to t == c is " + equivalent);

		List<Map<Integer, Integer>> result = bdd.allSAT();

		for (int index = 0; index < result.size(); index++) {
			Map<Integer, Integer> map = result.get(index);
			System.out.print("[");
			for (Integer key : map.keySet()) {
				System.out.print("" + key + "->" + map.get(key) + ",");
			}
			System.out.println("]");
		}

		String dot = bdd.createDotRepresentation();

		BDDNode andBdd = bdd.and(bdd);
		String andStr = andBdd.createDotRepresentation();

		BDDNode notBdd = bdd.not();
		String notStr = notBdd.createDotRepresentation();

		try {
			// String fileName = Options.getAbsoluteAddress("BDD.dot");
			String fileName = "BDD.dot";
			FileWriter writer = new FileWriter(fileName);
			writer.write(dot);
			writer.write(andStr);
			writer.write(notStr);
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

class AtomicTest extends Atom {

	private final VariableTest var;
	private final int value;

	public AtomicTest(int val) {
		var = new VariableTest("x");
		this.value = val;
	}

	@Override
	public String toString() {
		return "2*" + var.toString() + " >= " + value;
	}

	public boolean evaluate(Object[] objects) {
		return false;
	}

	public List<Var> getReferencedVariables() {
		List<Var> result = new ArrayList<Var>();
		result.add(var);
		return result;
	}

	public boolean evaluate(Prestate state) {
		return false;
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}

class VariableTest implements Var {
	private final String name;

	public VariableTest(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Object getValue(Object[] objs) {
		return null;
	}

}
