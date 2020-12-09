package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.BaseNonRelationalValueDomain;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;

public class ConstantPropagation extends BaseNonRelationalValueDomain<ConstantPropagation> {

	public static final ConstantPropagation TOP = new ConstantPropagation(true, false);

	private static final ConstantPropagation BOTTOM = new ConstantPropagation(false, true);

	private final boolean isTop, isBottom;

	private final Integer value;

	private ConstantPropagation(Integer value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	public ConstantPropagation(Integer value) {
		this(value, false, false);
	}

	private ConstantPropagation(boolean isTop, boolean isBottom) {
		this(null, isTop, isBottom);
	}

	@Override
	public ConstantPropagation top() {
		return TOP;
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public ConstantPropagation bottom() {
		return BOTTOM;
	}

	@Override
	public boolean isBottom() {
		return isBottom;
	}

	@Override
	public String representation() {
		return isTop() ? "Non-constant" : isBottom() ?  "Bot" : value.toString();
	}

	@Override
	protected ConstantPropagation evalNullConstant() {
		return bottom();
	}

	@Override
	protected ConstantPropagation evalNonNullConstant(Constant constant) {
		if (constant.getValue() instanceof Integer)
			return new ConstantPropagation((Integer) constant.getValue());
		return bottom();
	}

	@Override
	protected ConstantPropagation evalTypeConversion(Type type, ConstantPropagation arg) {
		return bottom();
	}

	@Override
	protected ConstantPropagation evalUnaryExpression(UnaryOperator operator, ConstantPropagation arg) {

		switch (operator) {
		case LOGICAL_NOT:
			break;
		case NUMERIC_NEG:
				return new ConstantPropagation(0 - value);
		case STRING_LENGTH:
			break;
		default:
			return bottom();
		}

		return bottom();
	}

	@Override
	protected ConstantPropagation evalBinaryExpression(BinaryOperator operator, ConstantPropagation left, ConstantPropagation right) {
		switch(operator) {
		case COMPARISON_EQ:
			break;
		case COMPARISON_GE:
			break;
		case COMPARISON_GT:
			break;
		case COMPARISON_LE:
			break;
		case COMPARISON_LT:
			break;
		case COMPARISON_NE:
			break;
		case LOGICAL_AND:
			break;
		case LOGICAL_OR:
			break;
		case NUMERIC_ADD:
				return new ConstantPropagation(left.value + right.value);
		case NUMERIC_DIV:
			if (left.value % right.value == 0)
				return top();
			else
				return new ConstantPropagation(left.value / right.value);
		case NUMERIC_MOD:
			return new ConstantPropagation(left.value % right.value);
		case NUMERIC_MUL:
			return new ConstantPropagation(left.value * right.value);
		case NUMERIC_SUB:
			return new ConstantPropagation(left.value - right.value);
	case STRING_CONCAT:
			break;
		case STRING_CONTAINS:
			break;
		case STRING_ENDS_WITH:
			break;
		case STRING_EQUALS:
			break;
		case STRING_INDEX_OF:
			break;
		case STRING_STARTS_WITH:
			break;
		default:
			break;
		}
		return bottom();
	}

	@Override
	protected ConstantPropagation evalTernaryExpression(TernaryOperator operator, ConstantPropagation left, ConstantPropagation middle, ConstantPropagation right) {
		return bottom();
	}

	@Override
	protected ConstantPropagation lubAux(ConstantPropagation other) throws SemanticException {
		if (equals(other))
			return other;
		return top();
	}

	@Override
	protected ConstantPropagation wideningAux(ConstantPropagation other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(ConstantPropagation other) throws SemanticException {
		return value <= other.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstantPropagation other = (ConstantPropagation) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	protected Satisfiability satisfiesIdentifier(Identifier identifier) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesNullConstant() {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesNonNullConstant(Constant constant) {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesTypeConversion(Type type, ConstantPropagation right) {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, ConstantPropagation arg) {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, ConstantPropagation left,
			ConstantPropagation right) {
		
		switch(operator) {
		case COMPARISON_EQ:
			return left.value == right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case COMPARISON_GE:
			return left.value >= right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case COMPARISON_GT:
			return left.value > right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case COMPARISON_LE:
			return left.value <= right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case COMPARISON_LT:
			return left.value < right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case COMPARISON_NE:
			return left.value != right.value ? Satisfiability.SATISFIED : Satisfiability.NOT_SATISFIED;
		case LOGICAL_AND:
		case LOGICAL_OR:
		case NUMERIC_ADD:
		case NUMERIC_DIV:
		case NUMERIC_MOD:
		case NUMERIC_MUL:
		case NUMERIC_SUB:
		case STRING_CONCAT:
		case STRING_CONTAINS:
		case STRING_ENDS_WITH:
		case STRING_EQUALS:
		case STRING_INDEX_OF:
		case STRING_STARTS_WITH:
		default:
			break;
		}
		
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, ConstantPropagation left,
			ConstantPropagation middle, ConstantPropagation right) {
		return Satisfiability.UNKNOWN;
	}
}
