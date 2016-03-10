package learntest.cfg;

import japa.parser.ast.Node;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;

public class CfgCreator extends CfgConverter {
	
	private static final CfgCreator INSTANCE = new CfgCreator();
	
	private CFG toCFG(Node node) {
		if (node != null) {
			node.accept(this, null);
			return getCFG();
		}
		return newInstance(node);
	}

	@Override
	protected CFG convert(MethodDeclaration n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(AssertStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(BlockStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(BreakStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ContinueStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(DoStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(EmptyStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ExpressionStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ForeachStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ForStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(IfStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(LabeledStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ReturnStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(SynchronizedStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(TryStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(TypeDeclarationStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(WhileStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ExplicitConstructorInvocationStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(SwitchStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG convert(ThrowStmt n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CFG newInstance(Node n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static CFG createCFG(Node node) {
		return getInstance().toCFG(node);
	}
	
	private static CfgCreator getInstance() {
		return INSTANCE;
	}

}
