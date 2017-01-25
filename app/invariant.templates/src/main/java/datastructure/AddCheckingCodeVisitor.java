package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddCheckingCodeVisitor<A> extends ModifierVisitorAdapter<A> {

	private String methodName;

	private int lineNo;
	
	private String path;
	
	// list of constant in the program
	// used in octagon template
	private List<Integer> consts;

	// variables at learning location {x, y, ...}
	private List<Variable> learnVars;

	// list of data structure templates {NullTemplate, NodeTemplate,
	// SllTemplate, ...}
	private List<DataStructureTemplate> heapTemplates;

	// list of pure templates {EqTemplate, Gt0Template, GtTemplate, ...}
	private List<DataStructureTemplate> pureTemplates;
	
	// list of bag templates {AllGt0Template, ContainTemplate, ...}
	private List<DataStructureTemplate> bagTemplates;

	// index for results, result0, result1, ...
	private static int count = 0;

	// map from template name to template variable
	private Map<String, String> nameVarMap = new HashMap<String, String>();

	// from heap variable to a list of results for that variable, x --> result0,
	// result2, ...
	// use in star template, only need to add heap vars
	private Map<String, List<String>> varResultMap = new HashMap<String, List<String>>();

	// resultX to templates and variables for that result, result2 -->
	// NodeTemplate;x;
	// use in star template, only need to add heap results
	private Map<String, String> resTemplateMap = new HashMap<String, String>();

	// list of pure variables capture pure properties
	private List<String> pureVars = new ArrayList<String>();

	// list of heap variables
	private List<String> heapVars = new ArrayList<String>();

	public AddCheckingCodeVisitor(String methodName, int lineNo, List<Variable> learnVars,
			List<DataStructureTemplate> heapTemplates, List<DataStructureTemplate> pureTemplates,
			List<DataStructureTemplate> bagTemplates, List<Integer> consts, String path) {
		this.methodName = methodName;
		this.lineNo = lineNo;
		this.learnVars = learnVars;
		this.heapTemplates = heapTemplates;
		this.pureTemplates = pureTemplates;
		this.bagTemplates = bagTemplates;
		this.path = path;
		this.consts = consts;
		
		classifyVars();
	}

	private void classifyVars() {
		for (Variable var : learnVars) {
			if (var.getType().equals("int")) {
				pureVars.add(var.getFullName());
			} else {
				heapVars.add(var.getFullName());
			}
		}
	}

	@Override
	public Node visit(CompilationUnit n, A arg) {
		NameExpr name1 = new NameExpr("templates");
		ImportDeclaration imp1 = new ImportDeclaration(name1, false, true);

		NameExpr name2 = new NameExpr("templates.heap");
		ImportDeclaration imp2 = new ImportDeclaration(name2, false, true);

		NameExpr name3 = new NameExpr("templates.pure");
		ImportDeclaration imp3 = new ImportDeclaration(name3, false, true);
		
		NameExpr name4 = new NameExpr("templates.bag");
		ImportDeclaration imp4 = new ImportDeclaration(name4, false, true);

		List<ImportDeclaration> imps = n.getImports();

		if (imps == null)
			imps = new ArrayList<ImportDeclaration>();
		imps.add(imp1);
		imps.add(imp2);
		imps.add(imp3);
		imps.add(imp4);

		n.setImports(imps);

		return super.visit(n, arg);
	}

	@Override
	public Node visit(ClassOrInterfaceDeclaration n, A arg) {
		int size1 = heapTemplates.size();
		int size2 = pureTemplates.size();
		int size3 = bagTemplates.size();

		int size = size1 + size2 + size3;

		for (int i = 0; i < size; i++)
			addDeclaration(n, i);

		return super.visit(n, arg);
	}

	private void addDeclaration(ClassOrInterfaceDeclaration n, int index) {
		int size1 = heapTemplates.size();
		int size2 = pureTemplates.size();
		int size3 = bagTemplates.size();

		String templateName = null;

		if (index < size1) {
			templateName = heapTemplates.get(index).name;
		} else if (index < size1 + size2) {
			templateName = pureTemplates.get(index - size1).name;
		} else if (index < size1 + size2 + size3) {
			templateName = bagTemplates.get(index - size1 - size2).name;
		}

		String varName = "template" + index;
		VariableDeclaratorId varDeclId = new VariableDeclaratorId(varName);
		Expression init = new ObjectCreationExpr(null, new ClassOrInterfaceType(templateName), null);
		VariableDeclarator varDecl = new VariableDeclarator(varDeclId, init);

		FieldDeclaration field = ASTHelper.createFieldDeclaration(ModifierSet.PRIVATE,
				ASTHelper.createReferenceType(templateName, 0), varDecl);

		nameVarMap.put(templateName, varName);

		ASTHelper.addMember(n, field);
	}

	@Override
	public Node visit(MethodDeclaration n, A arg) {
		if (n.getName().equals(methodName)) {
			return super.visit(n, arg);
		} else {
			return n;
		}
	}

	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);

				if (stmt.getBeginLine() == lineNo) {
					createPureVariables(block);
					addHeapChecking(block);
					addPureChecking(block);
					addBagChecking(block);
				}

				ASTHelper.addStmt(block, stmt);
				stmts.set(i, (Statement) stmt.accept(this, arg));
			}

			removeNulls(stmts);
		}

		return block;
	}
	
	// create new pure variables for data structure properties such as length
	private void createPureVariables(BlockStmt block) {
		for (int i = 0; i < heapTemplates.size(); i++) {
			DataStructureTemplate t = heapTemplates.get(i);

			for (String prop : t.pureProperties) {
				List<List<String>> totalVars = prepareVars(t);

				for (List<String> vars : totalVars) {
					Type type = ASTHelper.createReferenceType("int", 0);
					String name = prop + "_" + t.name + "_" + getVarsName(vars);

					NameExpr field = new NameExpr(nameVarMap.get(t.name));
					MethodCallExpr check = new MethodCallExpr(field, prop);
					for (String var : vars)
						ASTHelper.addArgument(check, new NameExpr(var));

					VariableDeclarationExpr declaration = createVariableDeclarationExpr(type, name, check);

					ASTHelper.addStmt(block, declaration);

					pureVars.add(name);
				}
			}
		}
	}
	
	private void addHeapChecking(BlockStmt block) {
		for (int i = 0; i < heapTemplates.size(); i++) {
			DataStructureTemplate t = heapTemplates.get(i);
			String templateName = t.name;

			if (templateName.equals("EqTemplate")) {
				addEqChecking(block, true);
			} else if (templateName.equals("NEqTemplate")) {
				addEqChecking(block, false);
			} else if (templateName.equals("StarTemplate")) {
				addStarChecking(block);
			} else {
				List<List<String>> totalVars = prepareVars(t);
				
				for (List<String> vars : totalVars) {
					addCommonCode(block, t.name, vars, true);
				}
			}
		}
	}
	
	private void addBagChecking(BlockStmt block) {
		for (int i = 0; i < bagTemplates.size(); i++) {
			DataStructureTemplate t = bagTemplates.get(i);
			List<List<String>> totalVars = prepareVars(t);
			
			for (List<String> vars : totalVars) {
				addCommonCode(block, t.name, vars, false);
			}
		}
	}

	private void addPureChecking(BlockStmt block) {
		for (int i = 0; i < pureTemplates.size(); i++) {
			DataStructureTemplate t = pureTemplates.get(i);
			
			List<List<String>> totalVars = prepareVars(t);
			
			for (List<String> vars : totalVars) {
				if (t.name.equals("GtPPCTemplate") || t.name.equals("GtPMCTemplate") || 
						t.name.equals("GtMPCTemplate") || t.name.equals("GtMMCTemplate") ||
						t.name.equals("PEqPPCTemplate") || t.name.equals("PEqPMCTemplate") ||
						t.name.equals("PEqMPCTemplate") || t.name.equals("PEqMMCTemplate") ||
						t.name.equals("GtCTemplate") || t.name.equals("LtCTemplate") ||
						t.name.equals("PEqCTemplate")) {
					for (Integer con : consts) {
						List<String> vars2 = new ArrayList<String>(vars);
						vars2.add(con + "");
						addCommonCode(block, t.name, vars2, false);
					}
				} else {
					List<String> vars2 = new ArrayList<String>(vars);
					addCommonCode(block, t.name, vars2, false);
				}
			}
		}
	}
	
	private List<List<String>> prepareVars(DataStructureTemplate t) {
		List<List<String>> totalVars = new ArrayList<List<String>>();
		
		List<List<String>> heapComb = Utilities.comb(heapVars, t.heapArgc);
		List<List<String>> pureComb = Utilities.comb(pureVars, t.pureArgc);
		
		List<List<String>> heapPerm = new ArrayList<List<String>>();
		List<List<String>> purePerm = new ArrayList<List<String>>();
		
		for (List<String> vars : heapComb) {	
			List<List<String>> perm = Utilities.perm(vars);
			heapPerm.addAll(perm);
		}
		
		if (t.name.equals("GtPPCTemplate") || t.name.equals("GtPMCTemplate") || 
				t.name.equals("GtMPCTemplate") || t.name.equals("GtMMCTemplate") ||
				t.name.equals("PEqPPCTemplate") || t.name.equals("PEqPMCTemplate") ||
				t.name.equals("PEqMPCTemplate") || t.name.equals("PEqMMCTemplate")) {
			purePerm.addAll(pureComb);
		} else {
			for (List<String> vars : pureComb) {
				List<List<String>> perm = Utilities.perm(vars);
				purePerm.addAll(perm);
			}
		}
		
		for (List<String> perm1 : heapPerm) {
			for (List<String> perm2 : purePerm) {
				List<String> vars = new ArrayList<String>(perm1);
				vars.addAll(perm2);
				totalVars.add(vars);
			}
		}
		
		return totalVars;
	}
	
	private void addEqChecking(BlockStmt block, boolean isEq) {
		if (heapVars.size() < 2)
			return;

		List<List<String>> varsList = Utilities.comb(heapVars, 2);

		for (List<String> vars : varsList) {
			Type type = ASTHelper.createReferenceType("Result", 0);
			String name = "result" + (++count);

			NameExpr field = null;
			
			if (isEq)
				field = new NameExpr(nameVarMap.get("EqTemplate"));
			else
				field = new NameExpr(nameVarMap.get("NEqTemplate"));
			
			MethodCallExpr check = new MethodCallExpr(field, "check");

			for (String var : vars) {
				ASTHelper.addArgument(check, new NameExpr(var));
			}

			VariableDeclarationExpr result = createVariableDeclarationExpr(type, name, check);

			ASTHelper.addStmt(block, result);

			if (isEq)
				addLoggingCode(block, name, "EqTemplate", getVarsNameLog(vars));
			else
				addLoggingCode(block, name, "NEqTemplate", getVarsNameLog(vars));
		}
	}

	private void addStarChecking(BlockStmt block) {
		if (heapVars.size() < 2)
			return;

		List<List<String>> totalResults = new ArrayList<List<String>>();

		for (String var : heapVars) {
			List<String> results = varResultMap.get(var);
			totalResults = extract(totalResults, results);
		}

		addStarCode(block, totalResults, true);
	}

	private List<List<String>> extract(List<List<String>> currResults, List<String> results) {
		List<List<String>> newResults = new ArrayList<List<String>>();

		if (currResults.isEmpty()) {
			for (String result : results) {
				List<String> tmp = new ArrayList<String>();
				tmp.add(result);

				newResults.add(tmp);
			}
		} else {
			for (List<String> currResult : currResults) {
				for (String result : results) {
					List<String> tmp = new ArrayList<String>(currResult);
					tmp.add(result);

					newResults.add(tmp);
				}
			}
		}

		return newResults;
	}

	private void addStarCode(BlockStmt block, List<List<String>> totalResults, boolean b) {
		outer:
		for (List<String> results : totalResults) {
			for (String result : results) {
				if (resTemplateMap.get(result).contains("NodeTemplate"))
					continue outer;
			}
			addStarCode(block, results);
		}
	}

	private void addStarCode(BlockStmt block, List<String> results) {
		Type type = ASTHelper.createReferenceType("Result", 0);
		String name = "result" + (++count);

		NameExpr field = new NameExpr(nameVarMap.get("StarTemplate"));
		MethodCallExpr check = new MethodCallExpr(field, "check");

		String s = "";

		for (String result : results) {
			ASTHelper.addArgument(check, new NameExpr(result));
			s += resTemplateMap.get(result) + "-";
		}

		VariableDeclarationExpr declaration = createVariableDeclarationExpr(type, name, check);

		ASTHelper.addStmt(block, declaration);

		addLoggingCode(block, name, "StarTemplate", s);
	}
	
	private void addCommonCode(BlockStmt block, String templateName, List<String> vars,
			boolean isAddHeap) {
		Type type = ASTHelper.createReferenceType("Result", 0);
		String name = "result" + (++count);
		
		NameExpr field = new NameExpr(nameVarMap.get(templateName));
		MethodCallExpr check = new MethodCallExpr(field, "check");

		if (isAddHeap) {
			for (int i = 0; i < vars.size(); i++) {
				String var = vars.get(i);
				if (!templateName.equals("NullTemplate") && i == 0) {
					if (!varResultMap.containsKey(var)) {
						List<String> results = new ArrayList<String>();
						results.add(name);
						varResultMap.put(var, results);
					} else {
						List<String> results = varResultMap.get(var);
						results.add(name);
						varResultMap.put(var, results);
					}
				}

				ASTHelper.addArgument(check, new NameExpr(var));
			}
		} else {
			for (int i = 0; i < vars.size(); i++) {
				String var = vars.get(i);
	
				ASTHelper.addArgument(check, new NameExpr(var));
			}
		}

		VariableDeclarationExpr result = createVariableDeclarationExpr(type, name, check);

		ASTHelper.addStmt(block, result);

		if (isAddHeap)
			resTemplateMap.put(name, templateName + "_" + getVarsName(vars));

		addLoggingCode(block, name, templateName, getVarsNameLog(vars));
	}

	private void addLoggingCode(BlockStmt block, String result, String template, String args) {
		NameExpr clazz = new NameExpr("Utility");
		MethodCallExpr log = new MethodCallExpr(clazz, "log");

		FieldAccessExpr access = new FieldAccessExpr(new NameExpr(result), "res");

		ASTHelper.addArgument(log, access);
		ASTHelper.addArgument(log, new StringLiteralExpr(template));
		ASTHelper.addArgument(log, new StringLiteralExpr(args));
		ASTHelper.addArgument(log, new StringLiteralExpr(path));

		ASTHelper.addStmt(block, log);
	}

	private VariableDeclarationExpr createVariableDeclarationExpr(Type type, String name, Expression init) {
		List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId(name), init));
		return new VariableDeclarationExpr(type, vars);
	}

	private String getVarsName(List<String> vars) {
		String s = "";

		for (String var : vars) {
			s += var + "_";
		}

		return s;
	}
	
	private String getVarsNameLog(List<String> vars) {
		String s = "";

		for (String var : vars) {
			s += var + "-";
		}

		return s;
	}

	private void removeNulls(final List<?> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i) == null) {
				list.remove(i);
			}
		}
	}

}
