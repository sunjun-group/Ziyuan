package assertion.visitor;

import java.util.List;

import assertion.creator.AssertionCreator;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.AddedLineData;
import mutation.mutator.insertdebugline.DebugLineData;

public class AddAssertStmtVisitor extends VoidVisitorAdapter<List<DebugLineData>> {
	
	private AssertionCreator creator;
	
	// private List<DebugLineData> debugLineData;
	
	public AddAssertStmtVisitor(VariableSubstitution subst) {
		super();
		creator = new AssertionCreator(subst);
		// debugLineData = new ArrayList<DebugLineData>();
	}
	
	public AddAssertStmtVisitor(List<ImportDeclaration> imports, VariableSubstitution subst) {
		super();
		creator = new AssertionCreator(imports, subst);
		// debugLineData = new ArrayList<DebugLineData>();
	}
	
	/*
	public List<DebugLineData> getDebugLineData() {
		return debugLineData;
	}
	*/
	
	/*
	@Override
	public void visit(final BlockStmt n, final List<AssertStmt> arg) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		super.visit(n, al);
		
		addAssertStmt(n, al);
	}
	
	@Override
	public void visit(final ExpressionStmt n, final List<AssertStmt> arg) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		super.visit(n, al);
		
		addAssertStmt(n, al);
	}
	
	@Override
	public void visit(final ReturnStmt n, final List<AssertStmt> arg) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		super.visit(n, al);
		
		addAssertStmt(n, al);
	}
	
	@Override
	public void visit(final IfStmt n, final List<AssertStmt> arg) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		super.visit(n, al);
		
		addAssertStmt(n, al);
	}
	*/
	
	/*
	@Override
	public void visit(final ObjectCreationExpr n, final List<DebugLineData> arg) {
		//System.out.println(n.getScope());
		//System.out.println(n.getType());
		
		super.visit(n, arg);
	}
	*/
	
	@Override
	public void visit(final MethodCallExpr n, final List<DebugLineData> arg) {
		List<AssertStmt> al = creator.createAssertionForMethodCallExpr(n);
		addAssertStmt(al, n, arg);
		// arg.addAll(al);
		
		super.visit(n, arg);
	}
	
	@Override
	public void visit(final BinaryExpr n, final List<DebugLineData> arg) {
		List<AssertStmt> al = creator.createAssertionForBinaryExpr(n);
		addAssertStmt(al, n, arg);
		// arg.addAll(al);
		
		super.visit(n, arg);
	}
	
	@Override
	public void visit(final ArrayAccessExpr n, final List<DebugLineData> arg) {
		List<AssertStmt> al = creator.createAssertionForArrayAccessExpr(n);
		addAssertStmt(al, n, arg);
		// arg.addAll(al);
		
		super.visit(n, arg);
	}
	
	private void addAssertStmt(List<AssertStmt> al, Node n, List<DebugLineData> arg) {
		if (al != null) {
			for (AssertStmt a : al) {
				a.setBeginLine(n.getBeginLine());
				AddedLineData d = new AddedLineData(n.getBeginLine(), a);
				arg.add(d);
			}
		}
	}
	
	/*
	private void addAssertStmt(List<AssertStmt> al, Node n, List<DebugLineData> arg) {
		if (al != null) {
			List<Node> replacedNode = new ArrayList<Node>(al);
			replacedNode.add(n);
			
			DebugLineData d = new ReplacedLineData(n.getBeginLine(), n, replacedNode);
			arg.add(d);
		}
	}
	*/
	
	/*
	private void addAssertStmt(Node n, List<AssertStmt> al) {
		if (al != null) {
			List<Node> replacedNode = new ArrayList<Node>(al);
			replacedNode.add(n);
			
			DebugLineData d = new ReplacedLineData(n.getBeginLine(), n, replacedNode);
			debugLineData.add(d);
		}
	}
	*/
	
}
