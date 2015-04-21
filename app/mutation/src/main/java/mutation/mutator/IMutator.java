package mutation.mutator;

import java.util.List;
import java.util.Map;

import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */
public interface IMutator
{
    /**
     * running mutation at specific lines defined in locations.
     */
	public MutationResult mutate(
			Map<String, List<ClassLocation>> classLocationMap,
			String sourceFolder);

    /**
     * Insert a new line of code right after a specific line of a class,
     * the new statement must be valid, and simple, 
     * and added just for debugging purpose.
     * ex: 
     * for return statement like:
     * return foo;
     * we need to generate something like this:
     * Type temp = foo;
     * return temp;
     */
	public MutationResult insertDebugLine(
			Map<String, List<ClassLocation>> classLocationMap,
			String sourceFolder);
}
