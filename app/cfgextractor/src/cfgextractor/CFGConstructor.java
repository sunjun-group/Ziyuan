package cfgextractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StoreInstruction;

import variable.ArrayElementVar;
import variable.FieldVar;
import variable.LocalVar;
import variable.Variable;


/**
 * Construct CFG based on bytecode of a method.
 * 
 * @author Yun Lin
 *
 */
public class CFGConstructor {
	/**
	 * This field is remained for debugging purpose
	 */
	private Code code;
	
	public CFG buildCFGWithControlDomiance(Code code){
		this.setCode(code);
		
		CFG cfg = constructCFG(code);
		
		attachDUVar(code, cfg);
		Map<Integer, List<Variable>> map = analyzeRelevantVars(cfg);
		cfg.setRelevantVarMap(map);
		
		constructPostDomination(cfg);
		constructControlDependency(cfg);
		
//		System.currentTimeMillis();
		
		return cfg;
	}
	
	private Map<Integer, List<Variable>> analyzeRelevantVars(CFG cfg) {
		System.currentTimeMillis();
		Map<Integer, List<Variable>> map = new HashMap<>();
		for(int i=0; i<cfg.getNodeList().size(); i++){
			CFGNode node = cfg.getNodeList().get(i);
			if(i==4){
				System.currentTimeMillis();
			}
			List<Variable> varList = getRelevantVarList(node);
			map.put(node.getInstructionHandle().getPosition(), varList);
		}
		
		return map;
	}

	private List<Variable> getRelevantVarList(CFGNode node) {

		List<CFGNode> block = retrieveBlock(node);
		Collections.sort(block, new Comparator<CFGNode>() {
			@Override
			public int compare(CFGNode o1, CFGNode o2) {
				return o2.getInstructionHandle().getPosition()-o1.getInstructionHandle().getPosition();
			}
		});
		List<Variable> relevantVars = new ArrayList<>();
		for(CFGNode n: block){
			for(Variable readV: n.getReadVars()){
				if(!isVarListContains(readV, relevantVars)){
					relevantVars.add(readV);
				}
			}
		}
		
		System.currentTimeMillis();
		
		boolean isChange = true;
		while(isChange){
			int relSize = relevantVars.size();
			List<Integer> visitedNodes = new ArrayList<>();
			increaseRelVars(relevantVars, node, visitedNodes);
			
			isChange = relSize!=relevantVars.size();
		}
		
		System.currentTimeMillis();
		
		return relevantVars;
	}

	private void increaseRelVars(List<Variable> relevantVars, CFGNode node, List<Integer> visitedNodes) {
		visitedNodes.add(node.getIndex());	
		
		List<CFGNode> blockParents = retrieveBlock(node);
		if(blockParents.isEmpty()){
			return;
		}
		for(CFGNode bN: blockParents){
			visitedNodes.add(bN.getIndex());				
		}
		
		Variable writtenVar = null;
		for(CFGNode parent: blockParents){
			if(!parent.getWritenVars().isEmpty()){
				writtenVar = parent.getWritenVars().get(0);
			}
			
			if(writtenVar!=null){
				if(isVarListContains(writtenVar, relevantVars)){
					for(Variable readVar: parent.getReadVars()){
						if(!isVarListContains(readVar, relevantVars)){
							relevantVars.add(readVar);						
						}
					}
				}
				
			}
		}
		
		
		if(!blockParents.isEmpty()){
			CFGNode newStart = blockParents.get(blockParents.size()-1);
			for(CFGNode parent: newStart.getParents()){
				if(!visitedNodes.contains(parent.getIndex())){
					increaseRelVars(relevantVars, parent, visitedNodes);				
				}
			}
		}
		
	}

	private List<CFGNode> retrieveBlock(CFGNode node) {
		/**
		 * identify a block
		 */
		List<CFGNode> blockParents = new ArrayList<>();
		blockParents.add(node);
		
		if(!node.getParents().isEmpty() && node.getParents().size()==1){
			CFGNode n = node.getParents().get(0);
			while(n!=null && n.getParents().size()<=1 && n.getChildren().size()<=1){
				blockParents.add(n);
				if(n.getParents().size()==1){
					n = n.getParents().get(0);					
				}
				else{
					break;
				}
			}
			if (n != null) {
				blockParents.add(n);
			}
		}
		
		if(!node.getChildren().isEmpty() && node.getChildren().size()==1){
			CFGNode n = node.getChildren().get(0);
			while(n!=null && n.getChildren().size()<=1 && n.getParents().size()<=1){
				blockParents.add(n);
				if(n.getChildren().size()==1){
					n = n.getChildren().get(0);					
				}
				else{
					break;
				}
			}
			if (n != null) {
				blockParents.add(n);
			}
		}
		
		
		return blockParents;
	}

	private boolean isVarListContains(Variable writtenVar, List<Variable> relevantVars) {
		for(Variable var: relevantVars){
			if(var.getClass().getName()==writtenVar.getClass().getName()){
				if(var.getName().equals(writtenVar.getName())){
					return true;
				}
			}
		}
		return false;
	}

	private void attachDUVar(Code code, CFG cfg) {
		ConstantPoolGen pool = new ConstantPoolGen(code.getConstantPool()); 
		for(CFGNode node: cfg.getNodeList()){
			InstructionHandle handle = node.getInstructionHandle();
			if(handle.getInstruction() instanceof FieldInstruction){
				FieldInstruction gIns = (FieldInstruction)handle.getInstruction();
				String fullFieldName = gIns.getFieldName(pool);
				if(fullFieldName != null){
					/** rw being true means read; and rw being false means write. **/
					boolean rw = handle.getInstruction().getName().toLowerCase().contains("get");;
					boolean isStatic = handle.getInstruction().getName().toLowerCase().contains("static");
					String type = gIns.getFieldType(pool).getSignature();
					FieldVar var = new FieldVar(isStatic, fullFieldName, type);
					
					if(rw){
						node.addReadVariable(var);										
					}
					else{
						node.addWrittenVariable(var);
					}
				}
			}
			else if(handle.getInstruction() instanceof LocalVariableInstruction){
				LocalVariableInstruction lIns = (LocalVariableInstruction)handle.getInstruction();
				LocalVariable variable = code.getLocalVariableTable().getLocalVariable(lIns.getIndex(), handle.getPosition());
				if(variable == null){
					variable = findOptimalVariable(handle, lIns.getIndex(), code.getLocalVariableTable());
				}
				
				if(variable != null && !variable.getName().equals("this")){
					LocalVar var = new LocalVar(variable.getName(), variable.getSignature(), 
							null, 0);
					if(handle.getInstruction() instanceof IINC){
						node.addReadVariable(var);
						node.addWrittenVariable(var);
					}
					else if(handle.getInstruction() instanceof LoadInstruction){
						node.addReadVariable(var);
					}
					else if(handle.getInstruction() instanceof StoreInstruction){
						node.addWrittenVariable(var);
					}
				}
			}
			else if(handle.getInstruction() instanceof ArrayInstruction){
				ArrayInstruction aIns = (ArrayInstruction)handle.getInstruction();
				String typeSig = aIns.getType(pool).getSignature();
				
				if(handle.getInstruction().getName().toLowerCase().contains("load")){
					ArrayElementVar var = new ArrayElementVar(aIns.getName(), typeSig);
					node.addReadVariable(var);	
				}
				else if(handle.getInstruction().getName().toLowerCase().contains("store")){
					ArrayElementVar var = new ArrayElementVar(aIns.getName(), typeSig);
					node.addWrittenVariable(var);
				}
			}
		}
		
	}
	
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

	@SuppressWarnings("rawtypes")
	public CFG constructCFG(Code code){
		CFG cfg = new CFG();
		CFGNode previousNode = null;
		
		InstructionList list = new InstructionList(code.getCode());
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			InstructionHandle instructionHandle = (InstructionHandle)iter.next();
			CFGNode node = cfg.findOrCreateNewNode(instructionHandle, code);
			
			if(previousNode != null){
				Instruction ins = previousNode.getInstructionHandle().getInstruction();
				if(JavaUtil.isNonJumpInstruction(ins)){
					node.addParent(previousNode);
					previousNode.addChild(node);					
				}
				
			}
			else{
				cfg.setStartNode(node);
			}
			
			if(instructionHandle.getInstruction() instanceof Select){
				Select switchIns = (Select)instructionHandle.getInstruction();
				InstructionHandle[] targets = switchIns.getTargets();
				
				for(InstructionHandle targetHandle: targets){
					CFGNode targetNode = cfg.findOrCreateNewNode(targetHandle, code);
					targetNode.addParent(node);
					node.addChild(targetNode);
				}
			}
			else if(instructionHandle.getInstruction() instanceof BranchInstruction){
				BranchInstruction bIns = (BranchInstruction)instructionHandle.getInstruction();
				InstructionHandle target = bIns.getTarget();
				
				CFGNode targetNode = cfg.findOrCreateNewNode(target, code);
				targetNode.addParent(node);
				node.addChild(targetNode);
			}
			
			previousNode = node;
		}
		
		setExitNodes(cfg);
		
		return cfg;
	}

	private void setExitNodes(CFG cfg) {
		for(CFGNode node: cfg.getNodeList()){
			if(node.getChildren().isEmpty()){
				cfg.addExitNode(node);
			}
		}
	}

	public void constructPostDomination(CFG cfg){
		/** connect basic post domination relation */
		for(CFGNode node: cfg.getNodeList()){
			node.addPostDominatee(node);
			for(CFGNode parent: node.getParents()){
				if(!parent.isBranch()){
					node.addPostDominatee(parent);
					for(CFGNode postDominatee: parent.getPostDominatee()){
						node.addPostDominatee(postDominatee);
					}
				}
			}
		}
		
		/** extend */
		extendPostDominatee(cfg);
	}

	@SuppressWarnings("unchecked")
	private void extendPostDominatee0(CFG cfg) {
		boolean isClose = false;
		while(!isClose){
			isClose = true;
			
			for(CFGNode node: cfg.getNodeList()){
				HashSet<CFGNode> originalSet = (HashSet<CFGNode>) node.getPostDominatee().clone();
				int originalSize = originalSet.size();
				
				long t5 = System.currentTimeMillis();
				for(CFGNode postDominatee: node.getPostDominatee()){
					originalSet.addAll(postDominatee.getPostDominatee());
					int newSize = node.getPostDominatee().size();
					
					boolean isAppendNew =  originalSize != newSize;
					isClose = isClose && !isAppendNew;
				}
				long t6 = System.currentTimeMillis();
				System.out.println("time for travese post dominatee: " + (t6-t5));
				
				node.setPostDominatee(originalSet);
			}
			
			
			for(CFGNode nodei: cfg.getNodeList()){
				if(nodei.isBranch()){
					for(CFGNode nodej: cfg.getNodeList()){
						if(!nodei.equals(nodej)){
							boolean isExpend = checkBranchDomination0(nodei, nodej);
							isClose = isClose && !isExpend;
						}
					}
					
				}
				
			}
		}
	}
	

	private boolean checkBranchDomination0(CFGNode branchNode, CFGNode postDominator) {
		boolean isExpend = false;
		if(allBranchTargetsIncludedInDominatees(branchNode, postDominator)){
			if(!postDominator.getPostDominatee().contains(branchNode)){
				isExpend = true;
				postDominator.getPostDominatee().add(branchNode);
			}
		}
		return isExpend;
	}

	private boolean allBranchTargetsIncludedInDominatees(CFGNode branchNode, CFGNode postDominator) {
		for(CFGNode target: branchNode.getChildren()){
			if(!postDominator.getPostDominatee().contains(target)){
				return false;
			}
		}
		
		return true;
	}
	
	private void extendPostDominatee(CFG cfg) {
		boolean isClose = false;
		
		while(!isClose){
			isClose = true;
			for(CFGNode nodei: cfg.getNodeList()){
				if(nodei.isBranch()){
					for(CFGNode nodej: cfg.getNodeList()){
						if(!nodei.equals(nodej)){
							boolean isAppend = checkBranchDomination(nodei, nodej);
							isClose = isClose && !isAppend;
						}
					}
				}
			}
		}
	}
	
	private boolean checkBranchDomination(CFGNode branchNode, CFGNode postDominator) {
		boolean isExpend = false;
		if(allBranchTargetsReachedByDominatees(branchNode, postDominator)){
			if(!postDominator.getPostDominatee().contains(branchNode)){
				isExpend = true;
				postDominator.getPostDominatee().add(branchNode);
			}
		}
		return isExpend;
	}
	
	private boolean allBranchTargetsReachedByDominatees(CFGNode branchNode, CFGNode postDominator) {
		for(CFGNode target: branchNode.getChildren()){
			if(!postDominator.canReachDominatee(target)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method can only be called after the post domination relation is built in {@code cfg}.
	 * Given a branch node, I traverse its children in first order. All its non-post-dominatees are
	 * its control dependentees.
	 * 
	 * @param cfg
	 */
	public void constructControlDependency(CFG cfg){
		for(CFGNode branchNode: cfg.getNodeList()){
			if(branchNode.isBranch()){
				computeControlDependentees(branchNode, branchNode.getChildren());
			}
		}
	}

	private void computeControlDependentees(CFGNode branchNode, List<CFGNode> list) {
		for(CFGNode child: list){
			if(!child.canReachDominatee(branchNode) && !branchNode.getControlDependentees().contains(child)){
				branchNode.addControlDominatee(child);
				computeControlDependentees(branchNode, child.getChildren());
			}
		}
		
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}
}
