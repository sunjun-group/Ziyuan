package tzuyu.engine.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.ReflectionUtils;


/**
 * Provides functionality for creating a set of sequences that create a set of
 * primitive values. Used by sequence generators.
 */
public final class SeedSequences {
  private SeedSequences() {
    throw new IllegalStateException("no instance");
  }

  private static final List<Object> primitiveSeeds = Arrays.<Object> asList(
      (byte) (-1), (byte) 0, (byte) 1, (byte) 10, (byte) 100, (short) (-1),
      (short) 0, (short) 1, (short) 10, (short) 100, (-1), 0, 1, 10, 100,
      (-1L), 0L, 1L, 10L, 100L, (float) -1.0, (float) 0.0, (float) 1.0,
      (float) 10.0, (float) 100.0, -1.0, 0.0, 1.0, 10.0, 100.0, '#', ' ', '4',
      'a', true, false, "", "hi!");

  /**
   * A set of sequences that create primitive values, e.g. int i = 0; or String
   * s = "hi";
 * @param config 
   */
  public static Set<Sequence> defaultSeeds(TzConfiguration config) {
    List<Object> seeds = new ArrayList<Object>(primitiveSeeds);
    return SeedSequences.objectsToSeeds(seeds, config);
  }

  /**
   * Precondition: objs consists exclusively of boxed primitives and strings.
   * Returns a set of sequences that create the given objects.
 * @param config 
   */
  private static Set<Sequence> objectsToSeeds(Collection<Object> objs, TzConfiguration config) {
    Set<Sequence> retval = new LinkedHashSet<Sequence>();
    for (Object o : objs) {
      retval.add(RAssignment.sequenceForPrimitive(o, config));
    }
    return retval;
  }

  public static Set<Object> getSeeds(Class<?> c) {
    Set<Object> result = new LinkedHashSet<Object>();
    for (Object seed : primitiveSeeds) {
      boolean seedOk = isOk(c, seed);
      if (seedOk)
        result.add(seed);
    }
    return result;
  }

  private static boolean isOk(Class<?> c, Object seed) {
    if (PrimitiveTypes.isBoxedPrimitiveTypeOrString(c)) {
      c = PrimitiveTypes.getUnboxType(c);
    }
    return ReflectionUtils.canBePassedAsArgument(seed, c);
  }

}