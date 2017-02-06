package assertion.creator;

import java.util.ArrayList;
import java.util.List;

import assertion.factory.ArrayListAssertionFactory;
import assertion.factory.ArraysAssertionFactory;
import assertion.factory.HashMapAssertionFactory;
import assertion.factory.LinkedListAssertionFactory;
import assertion.factory.ListAssertionFactory;
import assertion.factory.MathAssertionFactory;
import assertion.factory.ObjectAssertionFactory;
import assertion.factory.ScannerAssertionFactory;
import assertion.factory.StackAssertionFactory;
import assertion.factory.StringAssertionFactory;
import assertion.utility.Utility;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.type.Type;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class AssertionCreator {

	private VariableSubstitution subst;
	
	private List<ImportDeclaration> imports;

	public AssertionCreator(VariableSubstitution subst) {
		this.subst = subst;
	}
	
	public AssertionCreator(List<ImportDeclaration> imports, VariableSubstitution subst) {
		this.subst = subst;
		this.imports = imports;
	}

	public List<AssertStmt> createAssertionForMethodCallExpr(final MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		if (n.getScope() != null) {
			Type t = subst.getType(n.getScope().toString(), n.getBeginLine(), n.getBeginColumn());
			
			if (t != null) {
				// receiver is not null
				al.add(Utility.createNeqNullAssertion(n.getScope()));
				// assertion for specific method
				al.addAll(createAssertionForSpecificMethod(t.toString(), n));
			} else {
				al.addAll(createAssertionForSpecificMethod(n.getScope().toString(), n));
			}
		}
		
		return al;
	}
	
	public List<AssertStmt> createAssertionForFieldAccessExpr(final FieldAccessExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		if (n.getScope() != null) {
			Expression scope = n.getScope();
			if (scope instanceof NameExpr) {
				NameExpr name = (NameExpr) scope;
				char c = name.getName().charAt(0);
				if (c >= 65 && c <= 90) return al;
			}
			al.add(Utility.createNeqNullAssertion(n.getScope()));
		}
		
		return al;
	}

	public List<AssertStmt> createAssertionForSpecificMethod(String t, MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		ObjectAssertionFactory factory = new ObjectAssertionFactory();
		
		if ((t.equals("List") || t.startsWith("List<")) &&
				(hasImport("java.util.List") || hasImportAsterisk("java.util"))) {
			factory = new ListAssertionFactory(subst);
		} else if ((t.equals("ArrayList") || t.startsWith("ArrayList<")) &&
				(hasImport("java.util.ArrayList") || hasImportAsterisk("java.util"))) {
			factory = new ArrayListAssertionFactory(subst);
		} else if ((t.equals("LinkedList") || t.startsWith("LinkedList<")) &&
				(hasImport("java.util.LinkedList") || hasImportAsterisk("java.util"))) {
			factory = new LinkedListAssertionFactory(subst);
		} else if ((t.equals("Stack") || t.startsWith("Stack<")) &&
				(hasImport("java.util.Stack") || hasImportAsterisk("java.util"))) {
			factory = new StackAssertionFactory(subst);
		} else if ((t.equals("HashMap") || t.startsWith("HashMap<")) &&
				(hasImport("java.util.HashMap") || hasImportAsterisk("java.util"))) {
			factory = new HashMapAssertionFactory(subst);
		} else if ((t.equals("Scanner")) &&
				(hasImport("java.util.Scanner") || hasImportAsterisk("java.util"))) {
			factory = new ScannerAssertionFactory(subst);
		} else if (t.equals("String")) {
			factory = new StringAssertionFactory(subst);
		} else if ((t.equals("Math"))) {
			factory = new MathAssertionFactory(subst);
		} else if ((t.equals("Arrays"))) {
			factory = new ArraysAssertionFactory(subst);
		}
		
		al.addAll(factory.createAssertion(n));
		
		return al;
	}

	public List<AssertStmt> createAssertionForBinaryExpr(final BinaryExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		if (n.getOperator() == BinaryExpr.Operator.divide ||
				n.getOperator() == BinaryExpr.Operator.remainder) {
			// denominator is not zero
			CollectionUtils.addIfNotNull(al, Utility.createNeqZeroAssertion(n.getRight()));
		}
		
		return al;
	}

	public List<AssertStmt> createAssertionForArrayAccessExpr(final ArrayAccessExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		Expression index = n.getIndex();
		
		if (index instanceof UnaryExpr) {
			UnaryExpr un = (UnaryExpr) index;
			if (un.getOperator() == UnaryExpr.Operator.preIncrement) {
				index = new BinaryExpr(un.getExpr(), new IntegerLiteralExpr("1"), BinaryExpr.Operator.plus);
			} else if (un.getOperator() ==  UnaryExpr.Operator.preDecrement) {
				index = new BinaryExpr(un.getExpr(), new IntegerLiteralExpr("1"), BinaryExpr.Operator.minus);
			} else if (un.getOperator() == UnaryExpr.Operator.posIncrement ||
					un.getOperator() ==  UnaryExpr.Operator.posDecrement) {
				index = un.getExpr();
			}
		}
		
		al.add(Utility.createNeqNullAssertion(n.getName()));
		
		Expression length = new FieldAccessExpr(n.getName(), "length");
		
		// index >= 0
		CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
		// index < array.length
		CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, length, BinaryExpr.Operator.less));
		
		return al;
	}
	
	private boolean hasImport(String packageName) {
		for (ImportDeclaration id : imports) {
			if (!id.isAsterisk() && id.getName().toString().equals(packageName)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean hasImportAsterisk(String packageName) {
		for (ImportDeclaration id : imports) {
			if (id.isAsterisk() && id.getName().toString().equals(packageName)) {
				return true;
			}
		}
		return false;
	}
	
}
