package learntest.plugin.handler;

import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import learntest.plugin.utils.LearnTestUtil;

public class SetterMethodGenerationHandler extends AbstractLearntestHandler {

	@Override
	protected IStatus execute(IProgressMonitor monitor) {
		//LearnTestParams params = initLearntestParamsFromPreference();
		//CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(params.getTargetMethod().getClassName());
		
		final List<IPackageFragmentRoot> roots = LearnTestUtil.findAllPackageRootInProject();
		Job job = new Job("Generating setter method") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					for(IPackageFragmentRoot root: roots){
						for (IJavaElement element : root.getChildren()) {
							if (element instanceof IPackageFragment) {
								generateSetter((IPackageFragment) element);
							}
						}						
					}
					
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}

				
			private int generateSetter(IPackageFragment pack) throws JavaModelException {
				int sum = 0;
				
				for (IJavaElement javaElement : pack.getChildren()) {
					if (javaElement instanceof IPackageFragment) {
						generateSetter((IPackageFragment) javaElement);
					} else if (javaElement instanceof ICompilationUnit) {
						ICompilationUnit icu = (ICompilationUnit) javaElement;
						CompilationUnit cu = LearnTestUtil.convertICompilationUnitToASTNode(icu);

						generateSetterMethod(cu);
					}
				}

				return sum;
			}

		};

		job.schedule();

		return Status.OK_STATUS;
	}
	
	@SuppressWarnings("rawtypes")
	private void generateSetterMethod(CompilationUnit cu){
		List types = cu.types();
		if(!types.isEmpty()){
			AbstractTypeDeclaration atype = (AbstractTypeDeclaration) types.get(0);
			if(atype instanceof TypeDeclaration){
				TypeDeclaration type = (TypeDeclaration)atype;
				
				for(Object obj: type.modifiers()){
					if(obj instanceof Modifier){
						Modifier mod = (Modifier)obj;
						if(mod.isAbstract()){
							return;
						}
					}
				}
				
				for(FieldDeclaration field: type.getFields()){
					String supposedSetterMethodName = getSupposedSetterMethodName(field);
					if(doesNotHaveSetter(field, supposedSetterMethodName, type)){
						try {
							generateSetterMethod(type, field, supposedSetterMethodName);
						} catch (CoreException e) {
							e.printStackTrace();
						} catch (MalformedTreeException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void generateSetterMethod(TypeDeclaration type, FieldDeclaration field, String supposedSetterMethodName) throws CoreException, MalformedTreeException, BadLocationException {
		String fieldName = getFieldName(field);
		
		for(Object obj: field.modifiers()){
			if(obj instanceof Modifier){
				Modifier mod = (Modifier)obj;
				if(mod.getKeyword().equals(ModifierKeyword.FINAL_KEYWORD)){
					return;
				}
			}
		}
		
		AST ast = type.getAST();
		ASTRewrite astRewrite = ASTRewrite.create(ast);
		MethodDeclaration md = createSetterMethod(field, supposedSetterMethodName, fieldName, ast);
		if(md==null){
			return;
		}
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); // get the buffer manager
		IPath path = ((CompilationUnit)type.getRoot()).getJavaElement().getPath(); // unit: instance of CompilationUnit
		try {
			bufferManager.connect(path, null); // (1)
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
			// retrieve the buffer
			IDocument document = textFileBuffer.getDocument(); //(2)
			// ... edit the document here ... 
			
			ListRewrite statementsListRewrite = astRewrite.getListRewrite(type, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			statementsListRewrite.insertLast(md, null);
			TextEdit edit = astRewrite.rewriteAST(document, null);
			edit.apply(document);
			
			// commit changes to underlying file
			textFileBuffer.commit(null /* ProgressMonitor */, true /* Overwrite */); // (3)

		} finally {
			bufferManager.disconnect(path, null); // (4)
		}
	}

	@SuppressWarnings("unchecked")
	private MethodDeclaration createSetterMethod(FieldDeclaration field, String supposedSetterMethodName, String fieldName,
			AST ast) {
		MethodDeclaration method = ast.newMethodDeclaration();
		//modifier
		Modifier mod = ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD);
		method.modifiers().add(mod);
		
		//return type
		PrimitiveType returnType = ast.newPrimitiveType(PrimitiveType.VOID);
		method.setReturnType2(returnType);
		
		//methodName
		SimpleName methodName = ast.newSimpleName(supposedSetterMethodName);
		method.setName(methodName);
		
		//parameter
		SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
		SimpleName paramName = ast.newSimpleName(fieldName);
		svd.setName(paramName);
		Type fieldType = field.getType();
		Type paramType = copyType(fieldType, ast);
		if(paramType==null){
			return null;
		}
		svd.setType(paramType);
		method.parameters().add(svd);
		
		//body
		Block body = ast.newBlock();
		Assignment assign = ast.newAssignment();
		FieldAccess access = ast.newFieldAccess();
		ThisExpression thisExpr = ast.newThisExpression();
		access.setExpression(thisExpr);
		SimpleName fName = ast.newSimpleName(fieldName);
		access.setName(fName);
		assign.setLeftHandSide(access);
		SimpleName rightHand = ast.newSimpleName(fieldName);
		assign.setRightHandSide(rightHand);
		ExpressionStatement eStatement = ast.newExpressionStatement(assign);
		body.statements().add(eStatement);
		
		method.setBody(body);
		return method;
	}
	
	private Type copyType(Type type, AST ast) {
		
		if(type.isSimpleType()){
			SimpleType sName0 = (SimpleType)type;
			SimpleName sName = ast.newSimpleName(sName0.getName().getFullyQualifiedName()); 
			SimpleType sType = ast.newSimpleType(sName);
			return sType;
		}
		else if(type.isPrimitiveType()){
			PrimitiveType pType0 = (PrimitiveType)type;
			PrimitiveType pType = ast.newPrimitiveType(pType0.getPrimitiveTypeCode());
			return pType;
		}
		else if(type.isQualifiedType()){
			QualifiedType qType0 = (QualifiedType)type;
			Type cType0 = qType0.getQualifier();
			Type cType = copyType(cType0, ast);
			SimpleName sName0 = qType0.getName();
			SimpleName sName = ast.newSimpleName(sName0.getIdentifier()); 
			QualifiedType qType = ast.newQualifiedType(cType, sName);
			return qType;
		}
		else if(type.isArrayType()){
			ArrayType aType0 = (ArrayType)type;
			Type eType0 = aType0.getElementType();
			Type eType = copyType(eType0, ast);
			ArrayType aType = ast.newArrayType(eType, aType0.getDimensions());
			return aType;
		}
		
		return null;
	}

	private String getSupposedSetterMethodName(FieldDeclaration field){
		Object obj = field.fragments().get(0);
		if(obj instanceof VariableDeclarationFragment){
			VariableDeclarationFragment frag = ((VariableDeclarationFragment)obj);
			String fieldName = frag.getName().getIdentifier();
			String firstChar = fieldName.substring(0, 1).toUpperCase();
			String restChars = fieldName.substring(1, fieldName.length());
			String supposedMethodName = "set" + firstChar + restChars;
			return supposedMethodName;
		}
		
		return null;
	}
	
	private String getFieldName(FieldDeclaration field){
		Object obj = field.fragments().get(0);
		if(obj instanceof VariableDeclarationFragment){
			VariableDeclarationFragment frag = ((VariableDeclarationFragment)obj);
			String fieldName = frag.getName().getIdentifier();
			return fieldName;
		}
		
		return null;
	}

	private boolean doesNotHaveSetter(FieldDeclaration field, String supposedMethodName, TypeDeclaration type) {
		String fieldType = field.getType().toString();
		Object obj = field.fragments().get(0);
		if(obj instanceof VariableDeclarationFragment){
			for(MethodDeclaration method: type.getMethods()){
				Type returnType = method.getReturnType2();
				
				if(returnType!=null && returnType.toString().equals("void")){
					String methodName = method.getName().getIdentifier();
					if(methodName.equals(supposedMethodName)){
						if(method.parameters().size()==1){
							Object o = method.parameters().get(0);
							if(o instanceof SingleVariableDeclaration){
								SingleVariableDeclaration svd = (SingleVariableDeclaration)o;
								String setType = svd.getType().toString();
								if(setType.equals(fieldType)){
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}

	@Override
	protected String getJobName() {
		return "generate setter methods";
	}

}
