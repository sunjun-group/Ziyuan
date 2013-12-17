package tzuyu.engine.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.bool.EquivalenceChecker;
import tzuyu.engine.runtime.RMethod;

/**
 * The TzuYu action is essentially a guarded statement which consists of a
 * normal statement( method call) and a guard condition, where the guard
 * condition is an boolean expression. The guard condition can only refers
 * fields defined in the parameters of the method.
 * 
 * @author Spencer Xiao
 * 
 */
public class TzuYuAction extends Action {

	private final Formula guard;
	private final StatementKind method;

	private boolean hashCodeCached;
	private int savedHashCode;

	public TzuYuAction(Formula g, StatementKind stmt) {
		this.guard = g;
		this.method = stmt;
	}

	public static TzuYuAction fromStatmentKind(StatementKind stmt) {
		return new TzuYuAction(Formula.TRUE, stmt);
	}

	// Here we use reflection to circumvent the problem of creating an object of
	// a subclass without knowing the subclass and without the access to the
	// package in which the object is contained.
	public static TzuYuAction fromMethod(MethodInfo method, TzConfiguration config) {
		try {
			Class<?> RMethodClazz = RMethod.class;
			Method methodMethod = RMethodClazz.getMethod("getMethod",
					Method.class, TzConfiguration.class);
			Object resulObject = methodMethod.invoke(null, method.getMethod(), config);
			// Object resulObject = RMethod.getMethod(method.getMethod());

			return fromStatmentKind((StatementKind) resulObject);
			// } catch (ClassNotFoundException | NoSuchMethodException |
			// SecurityException
			// | IllegalAccessException | IllegalArgumentException
			// | InvocationTargetException e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static TzuYuAction fromCtor(ConstructorInfo ctor) {
		try {
			Class<?> RCtor = Class.forName("runtime.RConstructor");
			Method rctorMethod = RCtor.getMethod("getCtor", Constructor.class);
			Object resulObject = rctorMethod
					.invoke(null, ctor.getConstructor());

			return fromStatmentKind((StatementKind) resulObject);
			// } catch (ClassNotFoundException | NoSuchMethodException |
			// SecurityException
			// | IllegalAccessException | IllegalArgumentException
			// | InvocationTargetException e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Class<?> getOutputType() {
		return method.getReturnType();
	}

	public List<Class<?>> getInputTypes() {
		return method.getInputTypes();
	}

	public boolean isPrimitive() {
		return method.isPrimitive();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof TzuYuAction) {
			TzuYuAction obj = (TzuYuAction) o;
			// Here we compare the method first since it is less expensive to
			// compare methods than comparing two boolean formula
			return obj.method.equals(method)
					&& EquivalenceChecker.checkEquivalence(guard, obj.guard);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (hashCodeCached) {
			return savedHashCode;
		} else {
			savedHashCode = 31 * guard.hashCode();
			savedHashCode += method.hashCode();
			hashCodeCached = true;
			return savedHashCode;
		}
	}

	@Override
	public String toString() {
		return "[" + guard.toString() + "]" + method.toString();
	}

	public StatementKind getAction() {
		return method;
	}

	public Formula getGuard() {
		return guard;
	}

	@Override
	public boolean isConstructor() {
		return this.method.isConstructor();
	}
}
