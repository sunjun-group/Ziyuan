package learntest.gentest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacop.core.Domain;
import org.jacop.floats.core.FloatDomain;

import com.google.inject.Inject;

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
import sav.strategies.dto.execute.value.ExecVar;

public class TestSeqGenerator {
	
	@Inject
	private ITypeCreator typeCreator;
	@Inject
	private ValueGeneratorMediator valueGenerator;
	@Inject
	private PrimitiveFixValueGenerator fixValueGenerator;
	@Inject
	private FixLengthArrayValueGenerator arrayValueGenerator;
	
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
	
	public Sequence generateSequence(Domain[] solution, List<ExecVar> vars) throws SavException {
		Sequence sequence = new Sequence();

		int firstVarIdx = 0;		
		Map<String, ISelectedVariable> varMap = new HashMap<String, ISelectedVariable>();
		Set<String> nullVars = new HashSet<String>();
		//prepare inputs for target method
		int idx = 0;
		for (ExecVar var : vars) {
			double value  = ((FloatDomain) solution[idx ++]).min();
			
			String[] parts = var.getLabel().split("[.]");
			String receiver = parts[0];
			if (!classMap.containsKey(receiver)) {
				System.err.println("not input parameter [" + var.getLabel() + "]");
				continue;
			}
			if (parts.length == 1) {
				IType type = typeMap.get(classMap.get(var.getLabel()));
				GeneratedVariable variable = fixValueGenerator.generate(type, firstVarIdx, value);
				sequence.append(variable);
				varMap.put(var.getLabel(), variable);
				firstVarIdx += variable.getNewVariables().size();
			} else {
				ISelectedVariable variable = varMap.get(receiver);
				if (variable == null) {
					IType type = typeMap.get(classMap.get(receiver));
					if (type.isArray() && parts.length == 2 && parts[1].equals("length")) {
						variable = arrayValueGenerator.generate(type, firstVarIdx, value < 0 ? 0 : (int)value);
						sequence.append(variable);
						firstVarIdx += variable.getNewVariables().size();
						varMap.put(receiver, variable);
						continue;
					} else {
						variable = valueGenerator.generate(typeMap.get(classMap.get(receiver)), firstVarIdx, true);
						sequence.append(variable);
						firstVarIdx += variable.getNewVariables().size();
						varMap.put(receiver, variable);
					}
				}
				
				for (int i = 1; i < parts.length - 2; i++) {
					String cur = receiver + "." + parts[i];
					if (varMap.containsKey(cur)) {
						receiver = cur;
						variable = varMap.get(receiver);
					} else {
						Class<?> clazz = classMap.get(receiver);
						try {
							Class<?> fieldClazz = clazz.getDeclaredField(parts[i]).getType();
							classMap.put(cur, fieldClazz);
							IType type = typeMap.get(fieldClazz);
							if (type == null) {
								type = typeCreator.forClass(fieldClazz);
								typeMap.put(fieldClazz, type);
							}
							ISelectedVariable field = valueGenerator.generate(type, firstVarIdx, true);
							firstVarIdx += field.getNewVariables().size();
							sequence.append(field);
							varMap.put(cur, field);
							
							String methodName = "set" + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
							Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
							RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
							int[] varId = new int[] {field.getReturnVarId()};
							method.setInVarIds(varId);
							sequence.append(method);
							
							variable = field;
							receiver = cur;
						} catch (Exception e) {
							System.err.println("can not find setter for " + cur);
							break;
						}
					}
				}
				
				String last = parts[parts.length -  1];
				
				int i = parts.length - 2;
				if (i > 0) {
					String cur = receiver + "." + parts[i];				
					if (last.equals("isNull") && value == 1) {
						nullVars.add(cur);
						continue;
					}					
					if (varMap.containsKey(cur)) {
						receiver = cur;
						variable = varMap.get(receiver);
					} else {
						Class<?> clazz = classMap.get(receiver);
						try {
							Class<?> fieldClazz = clazz.getDeclaredField(parts[i]).getType();
							classMap.put(cur, fieldClazz);
							IType type = typeMap.get(fieldClazz);
							if (type == null) {
								type = typeCreator.forClass(fieldClazz);
								typeMap.put(fieldClazz, type);
							}
							ISelectedVariable field = valueGenerator.generate(type, firstVarIdx, true);
							firstVarIdx += field.getNewVariables().size();
							sequence.append(field);
							varMap.put(cur, field);
							
							String methodName = "set" + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
							Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
							RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
							int[] varId = new int[] {field.getReturnVarId()};
							method.setInVarIds(varId);
							sequence.append(method);
							
							variable = field;
							receiver = cur;
						} catch (Exception e) {
							System.err.println("can not find setter for " + cur);
							break;
						}
					}
					if (last.equals("isNull")) {
						continue;
					}
				}
				String cur = receiver + "." + last;
				if (varMap.containsKey(cur)) {
					continue;
				}
				Class<?> clazz = classMap.get(receiver);
				try {
					Class<?> fieldClazz = clazz.getDeclaredField(last).getType();
					classMap.put(cur, fieldClazz);
					IType type = typeMap.get(fieldClazz);
					if (type == null) {
						type = typeCreator.forClass(fieldClazz);
						typeMap.put(fieldClazz, type);
					}
					ISelectedVariable field = fixValueGenerator.generate(type, firstVarIdx, value);
					sequence.append(field);
					firstVarIdx += field.getNewVariables().size();
					varMap.put(cur, field);
					
					String methodName = "set" + last.substring(0, 1).toUpperCase() + last.substring(1);
					Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
					RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
					int[] varId = new int[] {field.getReturnVarId()};
					method.setInVarIds(varId);
					sequence.append(method);
				} catch (Exception e) {
					System.err.println("can not find setter for " + cur);
					continue;
				}
			}	
		}
		
		String[] paramNames = target.getParamNames();
		int[] paramIds = new int[paramNames.length];
		for (int i = 0; i < paramIds.length; i++) {
			ISelectedVariable param = varMap.get(paramNames[i]);
			if (param == null) {
				param = valueGenerator.generate(typeMap.get(classMap.get(paramNames[i])), firstVarIdx, false);
				sequence.append(param);
				firstVarIdx += param.getNewVariables().size();
				varMap.put(paramNames[i], param);
			}
			paramIds[i] = param.getReturnVarId();
		}		

		RqueryMethod rmethod = null;
		if (target.requireReceiver()) {
			ISelectedVariable receiverParam = valueGenerator.generate(receiverType, firstVarIdx, true);
			/*sequence.appendReceiver(receiverParam, target.getReceiverType());
			rmethod = new RqueryMethod(target, sequence.getReceiver(target.getReceiverType()).getVarId());*/
			sequence.append(receiverParam);
			firstVarIdx += receiverParam.getNewVariables().size();
			rmethod = new RqueryMethod(target, receiverParam.getReturnVarId());
		} else {
			rmethod = new RqueryMethod(target);
		}
		rmethod.setInVarIds(paramIds);
		sequence.append(rmethod);
		
		return sequence;
	}
	

	public Sequence generateSequence(Result result, List<String> variables) throws SavException {
		Sequence sequence = new Sequence();

		int firstVarIdx = 0;		
		Map<String, ISelectedVariable> varMap = new HashMap<String, ISelectedVariable>();
		Set<String> nullVars = new HashSet<String>();
		//prepare inputs for target method
		for (String var : variables) {
			if (result.containsVar(var)) {
				Number value  = result.get(var);
				
				String[] parts = var.split("[.]");
				String receiver = parts[0];
				if (!classMap.containsKey(receiver)) {
					System.err.println("not input parameter [" + var + "]");
					continue;
				}
				if (parts.length == 1) {
					IType type = typeMap.get(classMap.get(var));
					GeneratedVariable variable = fixValueGenerator.generate(type, firstVarIdx, value);
					sequence.append(variable);
					varMap.put(var, variable);
					firstVarIdx += variable.getNewVariables().size();
				} else {
					ISelectedVariable variable = varMap.get(receiver);
					if (variable == null) {
						IType type = typeMap.get(classMap.get(receiver));
						if (type.isArray() && parts.length == 2 && parts[1].equals("length")) {
							variable = arrayValueGenerator.generate(type, firstVarIdx, value.intValue() < 0 ? 0 : value.intValue());
							sequence.append(variable);
							firstVarIdx += variable.getNewVariables().size();
							varMap.put(receiver, variable);
							continue;
						} else {
							variable = valueGenerator.generate(typeMap.get(classMap.get(receiver)), firstVarIdx, true);
							sequence.append(variable);
							firstVarIdx += variable.getNewVariables().size();
							varMap.put(receiver, variable);
						}
					}
					
					for (int i = 1; i < parts.length - 2; i++) {
						String cur = receiver + "." + parts[i];
						if (varMap.containsKey(cur)) {
							receiver = cur;
							variable = varMap.get(receiver);
						} else {
							Class<?> clazz = classMap.get(receiver);
							try {
								Class<?> fieldClazz = clazz.getDeclaredField(parts[i]).getType();
								classMap.put(cur, fieldClazz);
								IType type = typeMap.get(fieldClazz);
								if (type == null) {
									type = typeCreator.forClass(fieldClazz);
									typeMap.put(fieldClazz, type);
								}
								ISelectedVariable field = valueGenerator.generate(type, firstVarIdx, true);
								firstVarIdx += field.getNewVariables().size();
								sequence.append(field);
								varMap.put(cur, field);
								
								String methodName = "set" + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
								Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
								RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
								int[] varId = new int[] {field.getReturnVarId()};
								method.setInVarIds(varId);
								sequence.append(method);
								
								variable = field;
								receiver = cur;
							} catch (Exception e) {
								System.err.println("can not find setter for " + cur);
								break;
							}
						}
					}
					
					String last = parts[parts.length -  1];
					
					int i = parts.length - 2;
					if (i > 0) {
						String cur = receiver + "." + parts[i];				
						if (last.equals("isNull") && value.intValue() == 1) {
							nullVars.add(cur);
							continue;
						}					
						if (varMap.containsKey(cur)) {
							receiver = cur;
							variable = varMap.get(receiver);
						} else {
							Class<?> clazz = classMap.get(receiver);
							try {
								Class<?> fieldClazz = clazz.getDeclaredField(parts[i]).getType();
								classMap.put(cur, fieldClazz);
								IType type = typeMap.get(fieldClazz);
								if (type == null) {
									type = typeCreator.forClass(fieldClazz);
									typeMap.put(fieldClazz, type);
								}
								ISelectedVariable field = valueGenerator.generate(type, firstVarIdx, true);
								firstVarIdx += field.getNewVariables().size();
								sequence.append(field);
								varMap.put(cur, field);
								
								String methodName = "set" + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
								Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
								RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
								int[] varId = new int[] {field.getReturnVarId()};
								method.setInVarIds(varId);
								sequence.append(method);
								
								variable = field;
								receiver = cur;
							} catch (Exception e) {
								System.err.println("can not find setter for " + cur);
								break;
							}
						}
						if (last.equals("isNull")) {
							continue;
						}
					}
					String cur = receiver + "." + last;
					if (varMap.containsKey(cur)) {
						continue;
					}
					Class<?> clazz = classMap.get(receiver);
					try {
						Class<?> fieldClazz = clazz.getDeclaredField(last).getType();
						classMap.put(cur, fieldClazz);
						IType type = typeMap.get(fieldClazz);
						if (type == null) {
							type = typeCreator.forClass(fieldClazz);
							typeMap.put(fieldClazz, type);
						}
						ISelectedVariable field = fixValueGenerator.generate(type, firstVarIdx, value);
						sequence.append(field);
						firstVarIdx += field.getNewVariables().size();
						varMap.put(cur, field);
						
						String methodName = "set" + last.substring(0, 1).toUpperCase() + last.substring(1);
						Method setter = clazz.getDeclaredMethod(methodName, fieldClazz);
						RqueryMethod method = new RqueryMethod(MethodCall.of(setter, classMap.get(receiver)), variable.getReturnVarId());
						int[] varId = new int[] {field.getReturnVarId()};
						method.setInVarIds(varId);
						sequence.append(method);
					} catch (Exception e) {
						System.err.println("can not find setter for " + cur);
						continue;
					}
				}
			}			
		}
		
		String[] paramNames = target.getParamNames();
		int[] paramIds = new int[paramNames.length];
		for (int i = 0; i < paramIds.length; i++) {
			ISelectedVariable param = varMap.get(paramNames[i]);
			if (param == null) {
				param = valueGenerator.generate(typeMap.get(classMap.get(paramNames[i])), firstVarIdx, false);
				sequence.append(param);
				firstVarIdx += param.getNewVariables().size();
				varMap.put(paramNames[i], param);
			}
			paramIds[i] = param.getReturnVarId();
		}		

		RqueryMethod rmethod = null;
		if (target.requireReceiver()) {
			ISelectedVariable receiverParam = valueGenerator.generate(receiverType, firstVarIdx, true);
			/*sequence.appendReceiver(receiverParam, target.getReceiverType());
			rmethod = new RqueryMethod(target, sequence.getReceiver(target.getReceiverType()).getVarId());*/
			sequence.append(receiverParam);
			firstVarIdx += receiverParam.getNewVariables().size();
			rmethod = new RqueryMethod(target, receiverParam.getReturnVarId());
		} else {
			rmethod = new RqueryMethod(target);
		}
		rmethod.setInVarIds(paramIds);
		sequence.append(rmethod);
		
		return sequence;
	}
	
}
