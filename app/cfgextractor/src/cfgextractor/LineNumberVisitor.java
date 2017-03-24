package cfgextractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StoreInstruction;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.ClassLocation;

/**
 * collect information such as method signature, and read/written variables in a certain line.
 * 
 * @author Yun Lin
 *
 */
public class LineNumberVisitor extends EmptyVisitor {
	
	private List<BreakPoint> breakPoints;
	
	public LineNumberVisitor(List<BreakPoint> breakPoints) {
		super();
		this.breakPoints = breakPoints;
	}

	
	public void visitMethod(Method method){
		Code code = method.getCode();
		
		if(method.getName().contains("getInstance")){
			System.currentTimeMillis();
		}
		
		if(code == null){
			return;
		}
		
		List<BreakPoint> includedBreakPoints = findIncludeBreakPoints(code, this.breakPoints);
		if(includedBreakPoints.isEmpty()){
			return;
		}
		
		CFG cfg = new CFGConstructor().buildCFGWithControlDomiance(code);
		
		
		
    }

	private List<BreakPoint> findIncludeBreakPoints(Code code, List<BreakPoint> breakPoints) {
		List<BreakPoint> includedPoints = new ArrayList<>();
		
		LineNumber[] table = code.getLineNumberTable().getLineNumberTable();
		
		int startLine = table[0].getLineNumber()-1;
		int endLine = table[table.length-1].getLineNumber();
		
		for(BreakPoint point: breakPoints){
			if(startLine<=point.getLineNo() && point.getLineNo()<=endLine){
				includedPoints.add(point);
			}
		}
		
		return includedPoints;
	}


	@SuppressWarnings("rawtypes")
	private List<InstructionHandle> findCorrespondingInstructions(int lineNumber, Code code) {
		List<InstructionHandle> correspondingInstructions = new ArrayList<>();
		
		InstructionList list = new InstructionList(code.getCode());
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			InstructionHandle insHandle = (InstructionHandle) iter.next();
			int instructionLine = code.getLineNumberTable().getSourceLine(insHandle.getPosition());
			
			if(instructionLine == lineNumber){
				correspondingInstructions.add(insHandle);
			}
		}
		
		return correspondingInstructions;
	}

	private ClassLocation transferToLocation(InstructionHandle insHandle, Code code, BreakPoint point) {
		LineNumberTable table = code.getLineNumberTable();
		int sourceLine = table.getSourceLine(insHandle.getPosition());
		
		ClassLocation location = new ClassLocation(point.getClassCanonicalName(), null, sourceLine);
		return location;
	}

	/**
	 * Based on my observation, if an instruction stores a value into a local variable, the start position of this instruction
	 * will be smaller than the start PC of this local variable. In other words, the instruction is out of the scope of this
	 * local variable, which may cause me to miss-find the correct variable. The diff will be between 1 or 2. <p>
	 * 
	 * In order to address this issue, I searched all the variables in local variable table and return the variable with smallest
	 * diff with the start position of the given instruciton.
	 * @param insHandle
	 * @param variableIndex
	 * @param localVariableTable
	 * @return
	 */
	private LocalVariable findOptimalVariable(InstructionHandle insHandle, int variableIndex, LocalVariableTable localVariableTable) {
		LocalVariable bestVar = null;
		int diff = -1;
		
		for(LocalVariable var: localVariableTable.getLocalVariableTable()){
			if(var.getIndex() == variableIndex){
				if(diff == -1){
					bestVar = var;
					diff = Math.abs(insHandle.getPosition() - var.getStartPC());
				}
				else{
					int newDiff = Math.abs(insHandle.getPosition() - var.getStartPC());
					if(newDiff < diff){
						bestVar = var;
						diff = newDiff;
					}
				}
			}
		}
		
		return bestVar;
	}
	
}
