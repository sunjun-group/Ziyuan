package learntest.gentest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import gentest.core.data.IDataProvider;
import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;
import gentest.core.data.statement.RqueryMethod;
import gentest.core.data.type.IType;
import gentest.core.data.type.ITypeCreator;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.data.variable.ISelectedVariable;
import gentest.core.value.generator.ValueGeneratorMediator;
import net.sf.javailp.Result;
import sav.common.core.SavException;

public class TestSeqGenerator {
	
	@Inject
	private ITypeCreator typeCreator;
	@Inject
	private IDataProvider<Sequence> sequenceProvider;
	@Inject
	private ValueGeneratorMediator valueGenerator;
	@Inject
	private PrimitiveFixValueGenerator fixValueGenerator;
	
	private Map<String, Class<?>> classMap;
	private Map<Class<?>, IType> typeMap;
	private MethodCall target;
	private IType receiverType;
	
	public TestSeqGenerator() {
		classMap = new HashMap<String, Class<?>>();
		typeMap = new HashMap<Class<?>, IType>();
	}

	public void setTarget(MethodCall target) {
		this.target = target;
		
		receiverType = null;
		if (target.requireReceiver()) {
			receiverType = typeCreator.forClass(target.getReceiverType());
		}
		
		IType[] paramTypes = null;
		if (receiverType == null) {
			paramTypes = typeCreator.forType(target.getMethod().getGenericParameterTypes());
		} else {
			paramTypes = receiverType.resolveType(target.getMethod().getGenericParameterTypes());
		}

		String[] params = target.getParamNames();
		int index = 0;
		for (String param : params) {
			Class<?> clazz = paramTypes[index].getRawType();
			classMap.put(param, clazz);
			typeMap.put(clazz, paramTypes[index ++]);
		}
	}

	public Sequence generateSequence(Result result, Set<String> vars) throws SavException {
		Sequence sequence = new Sequence();
		sequenceProvider.setData(sequence);

		RqueryMethod rmethod = null;
		if (target.requireReceiver()) {
			ISelectedVariable receiverParam = valueGenerator.generate(receiverType, sequence.getVarsSize(), true);
			sequence.appendReceiver(receiverParam, target.getReceiverType());
			rmethod = new RqueryMethod(target, sequence.getReceiver(target.getReceiverType()).getVarId());
		} else {
			rmethod = new RqueryMethod(target);
		}
		
		int firstVarIdx = getSequence().getVarsSize();
		Map<String, ISelectedVariable> varMap = new HashMap<String, ISelectedVariable>();
		//prepare inputs for target method
		for (String var : vars) {
			if (result.containsVar(var)) {
				//TODO handle other types
				Number value  = result.get(var);
				
				String[] parts = var.split("[.]");
				String receiver = parts[0];
				if (!classMap.containsKey(receiver)) {
					System.out.println("error: not input parameter [" + var + "]");
					continue;
				}
				if (parts.length == 1) {
					IType type = typeMap.get(classMap.get(var));
					GeneratedVariable variable = fixValueGenerator.generate(type, firstVarIdx, value);
					varMap.put(var, variable);
					firstVarIdx += variable.getNewVariables().size();
				} else {
					
					for (int i = 1; i < parts.length - 1; i++) {
						receiver += "." + parts[i];
						if (!typeMap.containsKey(receiver)) {
							//TODO handle new receiver
						}
					}
					ISelectedVariable variable = varMap.get(receiver);
					//TODO create new field and set for receiver
				}
			}			
		}
		
		String[] paramNames = target.getParamNames();
		int[] paramIds = new int[paramNames.length];
		List<ISelectedVariable> params = new ArrayList<ISelectedVariable>();
		for (int i = 0; i < paramIds.length; i++) {
			ISelectedVariable param = varMap.get(paramNames[i]);
			paramIds[i] = param.getReturnVarId();			
			params.add(param);
		}
		rmethod.setInVarIds(paramIds);
		sequence.appendMethodExecStmts(rmethod, params);
		return sequence;
	}
	
	private Sequence getSequence() {
		return sequenceProvider.getData();
	}
	
}
