/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.utils.filter;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.core.SourceType;

import sav.common.core.utils.PrimitiveUtils;

/**
 * @author LLT
 *
 */
public class TestableMethodFilter implements IMethodFilter {
	
//	@Override
//	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
//		if(md.isConstructor() || md.parameters().isEmpty()
//				|| !Modifier.isPublic(md.getModifiers()) || Modifier.isAbstract(md.getModifiers())
//						|| !checkPrimitiveType(md, cu)){
//			return false;
//		}
//		if (CollectionUtils.isEmpty(md.getBody().statements())) {
//			return false;
//		}
//		return true;
//	}
	
	List<String> ok = new LinkedList<>();
	List<String> constructors = new LinkedList<>();
	List<String> emptyVars = new LinkedList<>();
	List<String> notPublicMethods = new LinkedList<>();
	List<String> abstracts = new LinkedList<>();
	List<String> natives = new LinkedList<>();
	List<String> noPrimitiveVars = new LinkedList<>();
	List<String> allPrimitiveVars = new LinkedList<>();
	List<String> somePrimitiveVars = new LinkedList<>();
	List<String> interfaceParams = new LinkedList<>();
	
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		if(md.parameters().isEmpty() && !hasField(cu)) {
			emptyVars.add(md.getName().toString());
			return false;			
		}
		else if(!Modifier.isPublic(md.getModifiers())){
			notPublicMethods.add(md.getName().toString());
			return false;
		}
		else if (Modifier.isAbstract(md.getModifiers())){
			abstracts.add(md.getName().toString());
			return false;
		}
		else if(Modifier.isNative(md.getModifiers())){
			natives.add(md.getName().toString());
			return false;
		}
		else if (!checkPrimitiveType(md, cu)){
			return false;
		}
		else if(containsInterfaceParam(md, cu)){
			interfaceParams.add(md.getName().toString());
			return false;
		}
		else if(containsInterfaceField(md, cu)){
//			System.out.println(md.getName());
			interfaceParams.add(md.getName().toString());
			return false;
		}
//		if (CollectionUtils.isEmpty(md.getBody().statements())) {
//			emptyBody.add(md.getName().toString());
//			return false;
//		}
		ok.add(md.getName().toString());
		return true;
	}

	private boolean containsInterfaceParam(MethodDeclaration md, CompilationUnit cu) {
		for(Object obj: md.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				Type t = svd.getType();
				if(t.resolveBinding().isInterface()){
					return true;
				}
			}
		}
		return false;
	}
	
	
	class AccessedFieldVisitor extends ASTVisitor{
		boolean containsInterface = false;
		
		@Override
		public boolean visit(SimpleName name){
			if(!containsInterface){
				IBinding binding = name.resolveBinding();
				if(binding instanceof IVariableBinding){
					IVariableBinding vBinding = (IVariableBinding)binding;
					ITypeBinding type = vBinding.getType();
					if(type.isInterface()){
						containsInterface = true;
					}
				}
			}
			return false;
		}
	}
	
	private boolean containsInterfaceField(MethodDeclaration md, CompilationUnit cu){
		AccessedFieldVisitor afVisitor = new AccessedFieldVisitor();
		md.accept(afVisitor);
		return afVisitor.containsInterface;
	}

	private boolean checkPrimitiveType(MethodDeclaration md, CompilationUnit cu){
		if (containsAtLeastOnePrimitiveTypeParam(md.parameters()) || containsAtLeastOnePrimitiveTypeField(cu)){
			if (containsAllPrimitiveTypeParam(md.parameters())
					&& containsAllPrimitiveTypeField(cu)) {
				allPrimitiveVars.add(md.getName().toString());
			}else {
				somePrimitiveVars.add(md.getName().toString());
			}
			return true;
		}
		noPrimitiveVars.add(md.getName().toString());
		return false;
	}
	
	/* backup from previous implementation */
	@SuppressWarnings("unused")
	private boolean containsAtLeastOnePrimitiveTypeParam(List<?> parameters){
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				if(type.isPrimitiveType()){
					return true;
				}
				if(type.isArrayType()){
					ArrayType aType = (ArrayType)type;
					if(aType.getElementType().isPrimitiveType()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("restriction")
	private boolean containsAtLeastOnePrimitiveTypeField(CompilationUnit cu){
		ITypeRoot type = cu.getTypeRoot();
		try {
			IJavaElement[] elements = type.getChildren();
			for (int i = 0; i < elements.length; i++) {
				IJavaElement element = elements[i];
				if (element instanceof SourceType) {
					SourceType source = (SourceType) element;
					IField[] fields = source.getFields();
					for (IField iField : fields) {
						String fieldT = iField.getTypeSignature();
						if (isPrimitiveType(fieldT)) {
							return true;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean containsAllPrimitiveTypeParam(List<?> parameters){
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				if(!type.isPrimitiveType()){
					return false;
				}
				if(type.isArrayType()){
					ArrayType aType = (ArrayType)type;
					if(!aType.getElementType().isPrimitiveType()){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("restriction")
	public static boolean containsAllPrimitiveTypeField(CompilationUnit cu){
		ITypeRoot type = cu.getTypeRoot();
		try {
			IJavaElement[] elements = type.getChildren();
			for (int i = 0; i < elements.length; i++) {
				IJavaElement element = elements[i];
				if (element instanceof SourceType) {
					SourceType source = (SourceType) element;
					IField[] fields = source.getFields();
					for (IField iField : fields) {
						String fieldT = iField.getTypeSignature();
						if (!isPrimitiveType(fieldT)) {
							return false;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean hasField(CompilationUnit cu){
		ITypeRoot type = cu.getTypeRoot();
		try {
			IJavaElement[] elements = type.getChildren();
			for (int i = 0; i < elements.length; i++) {
				IJavaElement element = elements[i];
				if (element instanceof SourceType) {
					SourceType source = (SourceType) element;
					IField[] fields = source.getFields();
					if (fields.length > 0) {
						return true;
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static boolean isPrimitiveType(String signature) {
		if (signature.equals(Signature.SIG_INT)
				|| signature.equals(Signature.SIG_SHORT) 
				|| signature.equals(Signature.SIG_BYTE)
				|| signature.equals(Signature.SIG_BOOLEAN) 
				|| signature.equals(Signature.SIG_CHAR) 
				|| signature.equals(Signature.SIG_DOUBLE)
				|| signature.equals(Signature.SIG_FLOAT)
				|| signature.equals(Signature.SIG_LONG) ) {
			return true;
		}else if (signature.equals("[" + Signature.SIG_INT)
				|| signature.equals("[" + Signature.SIG_SHORT) 
				|| signature.equals("[" + Signature.SIG_BYTE)
				|| signature.equals("[" + Signature.SIG_BOOLEAN) 
				|| signature.equals("[" + Signature.SIG_CHAR) 
				|| signature.equals("[" + Signature.SIG_DOUBLE)
				|| signature.equals("[" + Signature.SIG_FLOAT)
				|| signature.equals("[" + Signature.SIG_LONG)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean containsAllPrimitiveType(List<?> parameters){
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				String typeString = type.toString();
				if(!PrimitiveUtils.isPrimitive(typeString) || svd.getExtraDimensions() > 0){
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private boolean containsArrayOrString(List parameters) {
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				if (type.isArrayType() || type.toString().contains("String")) {
					return true;
				}
			}
		}
		return false;
	}

	public List<String> getOk() {
		return ok;
	}

	public List<String> getConstructors() {
		return constructors;
	}

	public List<String> getEmptyVars() {
		return emptyVars;
	}

	public List<String> getNotPublicMethods() {
		return notPublicMethods;
	}
	
	public List<String> getNoPrimitiveVars() {
		return noPrimitiveVars;
	}

	public List<String> getAbstracts() {
		return abstracts;
	}

	public List<String> getNatives() {
		return natives;
	}

	public List<String> getAllPrimitiveVars() {
		return allPrimitiveVars;
	}

	public List<String> getSomePrimitiveVars() {
		return somePrimitiveVars;
	}
}
