package mutation.mutator;

import java.util.List;

import mutation.parser.VariableDescriptor;

public interface VariableSubstitution {
	public List<VariableDescriptor> find();
}
