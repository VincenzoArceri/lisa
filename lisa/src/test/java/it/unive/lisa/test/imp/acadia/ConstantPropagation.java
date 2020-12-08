package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.BaseNonRelationalValueDomain;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.types.IntType;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;

public class ConstantPropagation extends BaseNonRelationalValueDomain<ConstantPropagation> {

	public static final ConstantPropagation TOP = new ConstantPropagation(true, false);

	private static final ConstantPropagation BOTTOM = new ConstantPropagation(false, true);

	private final boolean isTop, isBottom;

	private final Constant value;

	private ConstantPropagation(Constant value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	public ConstantPropagation(Constant value) {
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
		return new ConstantPropagation(NullConstant.INSTANCE);
	}

	@Override
	protected ConstantPropagation evalNonNullConstant(Constant constant) {
		return new ConstantPropagation(constant);
	}

	@Override
	protected ConstantPropagation evalTypeConversion(Type type, ConstantPropagation arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ConstantPropagation evalUnaryExpression(UnaryOperator operator, ConstantPropagation arg) {
		if (arg.isTop())
			return top();
		else if (arg.isBottom())
			return bottom();

		switch (operator) {
		case LOGICAL_NOT:
			break;
		case NUMERIC_NEG:
			if (arg.value.getValue() instanceof Integer)
				return new ConstantPropagation(new Constant(IntType.INSTANCE, 0 - ((Integer) arg.value.getValue())));
		case STRING_LENGTH:
			break;
		default:
			return top();
		}

		return bottom();
	}

	@Override
	protected ConstantPropagation evalBinaryExpression(BinaryOperator operator, ConstantPropagation left, ConstantPropagation right) {
		if (left.isTop() || right.isTop())
			return TOP;
		
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
			if (left.value.getValue() instanceof Integer && right.value.getValue() instanceof Integer)
				return new ConstantPropagation(new Constant(IntType.INSTANCE, ((Integer) left.value.getValue()) + ((Integer) right.value.getValue())));
		case NUMERIC_DIV:
			break;
		case NUMERIC_MOD:
			break;
		case NUMERIC_MUL:
			if (left.value.getValue() instanceof Integer && right.value.getValue() instanceof Integer)
				return new ConstantPropagation(new Constant(IntType.INSTANCE, ((Integer) left.value.getValue()) * ((Integer) right.value.getValue())));
		case NUMERIC_SUB:
			if (left.value.getValue() instanceof Integer && right.value.getValue() instanceof Integer)
				return new ConstantPropagation(new Constant(IntType.INSTANCE, ((Integer) left.value.getValue()) - ((Integer) right.value.getValue())));
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
		return TOP;
	}

	@Override
	protected ConstantPropagation evalTernaryExpression(TernaryOperator operator, ConstantPropagation left, ConstantPropagation middle, ConstantPropagation right) {
		if (left.isTop() || right.isTop())
			return new ConstantPropagation(true, false);
		
		switch(operator) {
		case STRING_REPLACE:
			break;
		case STRING_SUBSTRING:
			break;
		default:
			break;
		}
		
		return TOP;
	}

	@Override
	protected ConstantPropagation lubAux(ConstantPropagation other) throws SemanticException {
		if (equals(other))
			return other;
		return new ConstantPropagation(true, false);
	}

	@Override
	protected ConstantPropagation wideningAux(ConstantPropagation other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(ConstantPropagation other) throws SemanticException {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesNonNullConstant(Constant constant) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesTypeConversion(Type type, ConstantPropagation right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, ConstantPropagation arg) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, ConstantPropagation left,
			ConstantPropagation right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, ConstantPropagation left,
			ConstantPropagation middle, ConstantPropagation right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}
}
