package learntest.gentest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import gentest.core.data.IDataProvider;
import gentest.core.data.LocalVariable;
import gentest.core.data.Sequence;
import gentest.core.data.type.IType;
import gentest.core.value.generator.ValueGeneratorMediator;
import net.sf.javailp.Result;
import sav.common.core.SavException;

public class TestSeqGenerator {

	@Inject
	private IDataProvider<Sequence> sequenceProvider;

	@Inject
	private ValueGeneratorMediator valueGenerator;
	
	private Map<String, IType> typeMap;
	
	public TestSeqGenerator() {
		typeMap = new HashMap<String, IType>();
	}

	public Sequence generateSequence(Result result, Set<String> vars) throws SavException {
		Sequence sequence = new Sequence();
		sequenceProvider.setData(sequence);
		Map<String, LocalVariable> varMap = new HashMap<String, LocalVariable>();
		//prepare inputs for target method
		for (String var : vars) {
			if (result.containsVar(var)) {
				//TODO handle other types
				int value  = result.get(var).intValue();
				String[] parts = var.split("[.]");
				if (parts.length == 1) {
					//TODO generate direct input value
				} else {
					String receiver = parts[0];
					if (!typeMap.containsKey(receiver)) {
						System.out.println("error: not input parameter");
						continue;
					}
					for (int i = 1; i < parts.length - 1; i++) {
						receiver += "." + parts[i];
						if (!typeMap.containsKey(receiver)) {
							//TODO handle new receiver
						}
					}
					LocalVariable variable = varMap.get(receiver);
					//TODO create new field and set for receiver
				}
			}			
		}
		//TODO add method call to target method
		return sequence;
	}
	
}
