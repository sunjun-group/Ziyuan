package tzuyu.engine.bool;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.utils.Pair;

public enum Operator {
	GE(">="), LE("<="), GT(">"), LT("<"), EQ("=="), NE("!="),
	AND("&&"), OR("||");

	public static final List<Pair<Operator, Operator>> OPPOSITE_PAIRS;
	
	static {
		OPPOSITE_PAIRS = new ArrayList<Pair<Operator,Operator>>();
		OPPOSITE_PAIRS.add(Pair.of(GE, LT));
		OPPOSITE_PAIRS.add(Pair.of(LE, GT));
		OPPOSITE_PAIRS.add(Pair.of(EQ, NE));
		OPPOSITE_PAIRS.add(Pair.of(AND, OR));
	}
	private final String operator;

	private Operator(String op) {
		this.operator = op;
	}

	public boolean evaluate(double left, double right) {
		switch (this) {
		case GE:
			return left >= right;
		case LE:
			return left <= right;
		case GT:
			return left > right;
		case LT:
			return left < right;
		case EQ:
			return left == right;
		case NE:
			return left != right;
		default:
			return false;
		}
	}
	
	@Override
	public String toString() {
		return operator;
	}

	public String getCode() {
		return operator;
	}
}
