/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.utils;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;

import sav.eclipse.plugin.PluginException;

/**
 * @author LLT
 *
 */
public class AstUtils {
	
	public static CompilationUnit toAstNode(ICompilationUnit cu) {
		ASTParser p = ASTParser.newParser(AST.JLS8);
		p.setSource(cu);
		p.setResolveBindings(true);
		return (CompilationUnit) p.createAST(null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ASTNode> T findNode(CompilationUnit cu, ISourceReference isource) throws PluginException {
		ASTNode astNode;
		try {
			astNode = NodeFinder.perform(cu, isource.getSourceRange());
			return (T) astNode;
		} catch (JavaModelException e) {
			throw PluginException.wrapEx(e);
		}
	}

	public static IMethod findImethod(MethodDeclaration method, List<IMethod> iMethods) {
		for (IMethod iMethod : iMethods) {
			try {
				if (iMethod.getSourceRange().getOffset() == method.getStartPosition()) {
					return iMethod;
				}
			} catch (JavaModelException e) {
				// do nothing
			}
		}
		return null;
	}
}
