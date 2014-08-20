/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.adapter;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.utils.SignatureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import sav.common.core.utils.CollectionUtils;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.SignatureParser;

/**
 * @author LLT
 *
 */
public class AssertionDetector {

	public static List<BreakPoint> scan(Map<ICompilationUnit, List<IMethod>> assertionSources) {
		List<BreakPoint> bkps = new ArrayList<BreakPoint>();
		for (Entry<ICompilationUnit, List<IMethod>> entry : assertionSources.entrySet()) {
			extractAssertion(entry, bkps);
		}
		return bkps;
	}
	
	private static void extractAssertion(
			Entry<ICompilationUnit, List<IMethod>> entry, List<BreakPoint> bkps) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(entry.getKey());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		AssertionVisitor visitor = new AssertionVisitor(cu, entry.getValue(), bkps);
		cu.accept(visitor);
			
	}
	
	private static class AssertionVisitor extends ASTVisitor {
		private List<IMethod> methods;
		private List<BreakPoint> bkps;
		private BreakPoint curBreakpoint;
		private IMethod curMethod;
		private CompilationUnit cu;
		
		public AssertionVisitor(CompilationUnit cu, List<IMethod> methods, List<BreakPoint> bkps) {
			this.methods = CollectionUtils.emptyToNull(methods);
			this.cu = cu;
			this.bkps = bkps;
		}
		
		@Override
		public boolean visit(MethodDeclaration node) {
			IMethod method = (IMethod) node.resolveBinding().getJavaElement();
			if (isSelectedMth(method)) {
				curMethod = method;
				return true;
			}
			return false;
		}
		
		private boolean isSelectedMth(IMethod method) {
			return methods == null || methods.contains(method);
		}

		@Override
		public boolean visit(AssertStatement node) {
			initBreakpoint(node);
			return super.visit(node);
		}
		
		private void initBreakpoint(AssertStatement n) {
			try {
				String className = curMethod.getDeclaringType().getFullyQualifiedName();
				String methodSign = SignatureUtils.createMethodNameSign(curMethod.getElementName(), 
						new SignatureParser(curMethod.getDeclaringType()).toMethodJVMSignature(
								curMethod.getParameterTypes(), curMethod.getReturnType()));
				curBreakpoint = new BreakPoint(className, methodSign,
						cu.getLineNumber(n.getStartPosition()));
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PluginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void endVisit(AssertStatement node) {
			CollectionUtils.addIfNotNull(bkps, curBreakpoint);
			curBreakpoint = null;
		}
		
		@Override
		public boolean visit(SimpleName node) {
			if (curBreakpoint != null) {
				Variable var = new Variable(node.getFullyQualifiedName());
				var.setCode(var.getName());
				curBreakpoint.addVars(var);
			}
			return super.visit(node);
		}
	}
}
