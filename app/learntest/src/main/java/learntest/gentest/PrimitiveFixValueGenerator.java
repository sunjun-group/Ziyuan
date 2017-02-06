package learntest.gentest;

import com.google.inject.Inject;

import gentest.core.commons.utils.TypeUtils;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.generator.PrimitiveValueGenerator;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;
import gentest.main.GentestConstants;
import japa.parser.ast.type.PrimitiveType.Primitive;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;
import sav.strategies.gentest.ISubTypesScanner;

public class PrimitiveFixValueGenerator {

	@Inject
	private IVariableStore variableStore;
	@Inject
	private ISubTypesScanner subTypeScanner;
	@Inject
	private ITypeMethodCallStore typeMethodCallsStore;
	
	public GeneratedVariable generate(IType type, int firstVarId, Number value) throws SavException {
		GeneratedVariable root = new GeneratedVariable(firstVarId);
		GeneratedVariable variable = root.newVariable();
		Class<?> clazz = type.getRawType();
		if (PrimitiveValueGenerator.accept(clazz)) {
			if (Object.class.equals(clazz)) {
				clazz = Randomness.randomMember(GentestConstants.DELEGATING_CANDIDATES_FOR_OBJECT);
			}
			Primitive primitiveType = TypeUtils.getAssociatePrimitiveType(clazz);
			if (primitiveType == null) {
				System.out.println("error: try to assign value for non-primitive type");
				return variable;
			}
			switch (primitiveType) {
			case Boolean:
				variable.append(RAssignment.assignmentFor(clazz, value.intValue() <= 0 ? false : true));
				break;
			case Byte:
				variable.append(RAssignment.assignmentFor(clazz, value.byteValue()));
				break;
			case Char:
				variable.append(RAssignment.assignmentFor(clazz, (char)value.intValue()));
				break;
			case Double:
				variable.append(RAssignment.assignmentFor(clazz, value.doubleValue()));
				break;
			case Float:
				variable.append(RAssignment.assignmentFor(clazz, value.floatValue()));
				break;
			case Int:
				variable.append(RAssignment.assignmentFor(clazz, value.intValue()));
				break;
			case Long:
				variable.append(RAssignment.assignmentFor(clazz, value.longValue()));
				break;
			case Short:
				variable.append(RAssignment.assignmentFor(clazz, value.shortValue()));
				break;
			}
			getVariableStore().put(type, variable);
			root.append(variable);			
		} else {
			System.out.println("error: try to assign value for non-primitive type");
		}
		return variable;
	}
	
	public IVariableStore getVariableStore() {
		return variableStore;
	}

	public void setVariableStore(IVariableStore variableStore) {
		this.variableStore = variableStore;
	}

	public ISubTypesScanner getSubTypeScanner() {
		return subTypeScanner;
	}

	public void setSubTypeScanner(ISubTypesScanner subTypeScanner) {
		this.subTypeScanner = subTypeScanner;
	}

	public ITypeMethodCallStore getTypeMethodCallsStore() {
		return typeMethodCallsStore;
	}

	public void setTypeMethodCallsStore(ITypeMethodCallStore typeMethodCallsStore) {
		this.typeMethodCallsStore = typeMethodCallsStore;
	}
	
}
