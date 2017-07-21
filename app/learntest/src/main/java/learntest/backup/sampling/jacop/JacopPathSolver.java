package learntest.backup.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Domain;
import org.jacop.core.Store;

import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.strategies.dto.execute.value.ExecVar;

public class JacopPathSolver {
	
	private List<ExecVar> vars;
	
	public JacopPathSolver(List<ExecVar> vars) {
		this.vars = vars;
	}
	
	public List<Domain[]> solve(List<List<Formula>> paths) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		for (List<Formula> path : paths) {
			slove(path, res);
		}
		return res;
	}

	private void slove(List<Formula> path, List<Domain[]> res) {
		JacopPathVisitor visitor = new JacopPathVisitor();
		for (Formula formula : path) {
			formula.accept(visitor);
		}
		List<LIAAtom> atoms = visitor.getAtoms();
		while (visitor.hasNextChoice()) {
			List<LIAAtom> nots = visitor.getNextChoice();
			Store store = StoreBuilder.build(vars, atoms, nots, 0);
			if (store != null) {
				Domain[] solution = StoreSearcher.minSolve(store);
				if (solution != null) {
					res.add(solution);
					break;
				}
			}
		}
	}

}
