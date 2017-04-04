/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import common.cfg.CFG;
import common.cfg.CfgCreator;
import common.cfg.CfgDecisionNode;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import learntest.cfg.javasource.BreakpointCreator;
import learntest.cfg.javasource.CfgHandler;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.model.MethodInfo;
import learntest.util.LearnTestUtil;
import sav.strategies.dto.BreakPoint.Variable;

/**
 * @author LLT
 *
 */
public class CfgHandlerScInitializer {
	private static CfgHandlerScInitializer INSTANCE;
	
	public CfgHandler create(LearnTestParams params) throws LearnTestException {
		CompilationUnit cu;
		try {
			cu = JavaParser.parse(new File(params.getFilePath()));
			MethodDeclaration method = getMethod(cu, params.getTestMethodInfo());
			List<Variable> variables = prepareVariables(cu, method);
			// create cfg
			return initCfgHanlder(method, params.getTestMethodInfo(), variables);
		} catch (ParseException | IOException e) {
			throw new LearnTestException(e);
		}
	}
	
	private CfgHandler initCfgHanlder(MethodDeclaration method, MethodInfo targetMethodInfo, List<Variable> variables)
			throws ParseException, IOException {
		CfgCreator creator = new CfgCreator();
		CFG cfg1 = creator.toCFG(method);
		CFG cfg2 = creator.dealWithReturnStmt(cfg1);
		CFG cfg = creator.dealWithBreakStmt(cfg2);
		
		//To handle recursion,can only handle recursion with returns
		Set<Integer> returns = new HashSet<Integer>();
		List<CfgDecisionNode> returnNodeList = creator.getReturnNodeList();
		for (CfgDecisionNode returnNode : returnNodeList) {
			returns.add(returnNode.getBeginLine());
		}

		BreakpointCreator bkpCreator = new BreakpointCreator(targetMethodInfo, variables,
				returns);

		return new CfgHandler(cfg, bkpCreator);
	}
	
	private List<Variable> prepareVariables(CompilationUnit cu, MethodDeclaration method) {
		List<Variable> variables = new ArrayList<Variable>();
		List<Parameter> parameters = method.getParameters();
		if (parameters != null) {
			for (Parameter parameter : parameters) {
				variables.add(new Variable(parameter.getId().getName()));
			}
		}		

		List<Variable> visitFields = findFields(cu, method);
		variables.addAll(visitFields);		
		return variables;
	}
	
	private List<Variable> findFields(CompilationUnit cu, MethodDeclaration method) {
		List<Variable> fields = new ArrayList<>();
		
		String pack = cu.getPackage().getChildrenNodes().get(0).toString();
		String type = cu.getTypes().get(0).getName();
		
		String cuName = pack + "." + type;
		
		org.eclipse.jdt.core.dom.CompilationUnit domCU = LearnTestUtil.findCompilationUnitInProject(cuName);
		MethodFinder finder = new MethodFinder(method);
		domCU.accept(finder);
		org.eclipse.jdt.core.dom.MethodDeclaration md = finder.foundMethod;
		if(md != null){
			FieldAccessChecker checker = new FieldAccessChecker();
			md.accept(checker);
			
			for(String fieldName: checker.fields){
				Variable field = new Variable(fieldName);
				fields.add(field);
			}
		}
		
		
//		System.currentTimeMillis();
		
		return fields;
	}
	
	private MethodDeclaration getMethod (CompilationUnit cu, MethodInfo targetMethodInfo) {
		String methodName = targetMethodInfo.getMethodName();
		int lineNumber = targetMethodInfo.getLineNum();
		String classSimpleName = targetMethodInfo.getClassSimpleName();
		for (TypeDeclaration type : cu.getTypes()) {
			if (type.getName().equals(classSimpleName)) {
				for (BodyDeclaration body : type.getMembers()) {
					if (body instanceof MethodDeclaration) {
						MethodDeclaration method = (MethodDeclaration) body;
						if (method.getName().equals(methodName)) {
							if(lineNumber != 0){
								if (!(method.getBeginLine() <= lineNumber && lineNumber <= method.getEndLine())) {
									continue;
								}
							}
							return method;
						}
					}
				}
			}
			return null;
		}
		return null;
	}


	class MethodFinder extends ASTVisitor {
		org.eclipse.jdt.core.dom.MethodDeclaration foundMethod;
		MethodDeclaration method;
		
		public MethodFinder(MethodDeclaration m){
			this.method = m;
		}
		
		public boolean visit(org.eclipse.jdt.core.dom.MethodDeclaration md){
			if(md.getName().getFullyQualifiedName().equals(method.getName())){
				if(md.parameters().size()==method.getParameters().size()){
					int size = md.parameters().size();
					for(int i=0; i<size; i++){
						Object obj = md.parameters().get(i);
						SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
						String p1String = svd.getType().toString();
						
						Parameter p2 = method.getParameters().get(i);
						String p2String = p2.getType().toString();
						
						if(!p1String.equals(p2String)){
							return false;
						}
					}
					
					foundMethod = md;
				}
			}
			
			return false;
		}
	}
	
	class FieldAccessChecker extends ASTVisitor{
		List<String> fields = new ArrayList<>();
		
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			if(binding instanceof IVariableBinding){
				IVariableBinding vb = (IVariableBinding)binding;
				if(vb.isField()){
					fields.add(vb.getName());
				}
			}
			
			return false;
		}
	} 
	
	public static CfgHandlerScInitializer getINSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new CfgHandlerScInitializer();
		}
		return INSTANCE;
	}
}
