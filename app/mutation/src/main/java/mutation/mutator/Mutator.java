package mutation.mutator;

import java.io.File;
import java.util.List;

import mutation.parser.Parser;

import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */
public class Mutator implements IMutator {
	
	@Override
	public MutationResult mutate(List<ClassLocation> locations,
			File sourceFolder) {
		Parser parser = new Parser();
		return null;
	}

	@Override
	public MutationResult insertFakeLine(List<ClassLocation> locations,
			File sourcFolder) {
		return null;
	}
}
