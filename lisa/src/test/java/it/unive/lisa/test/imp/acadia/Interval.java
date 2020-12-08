package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.BaseNonRelationalValueDomain;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;

public class Interval extends BaseNonRelationalValueDomain<Interval> {


	private final IntervalValue val;

	private Interval(IntervalValue val) {
		this.val = val;
	}

	public Interval() {
		this(IntervalValue.mkTop());
	}

	@Override
	public Interval top() {
		return new Interval(IntervalValue.mkTop());
	}

	@Override
	public boolean isTop() {
		return val.isTop();
	}

	@Override
	public boolean isBottom() {
		return val.isBottom();
	}

	@Override
	public Interval bottom() {
		return new Interval(IntervalValue.mkBottom());
	}

	@Override
	public String representation() {
		return val.toString();
	}

	public IntervalValue getInterval() {
		return val;
	}

	@Override
	protected Interval evalNullConstant() {
		return bottom();
	}

	@Override
	protected Interval evalNonNullConstant(Constant constant) {
		if (constant.getValue() instanceof Integer) 
			return new Interval(new IntervalValue((Integer) constant.getValue(), (Integer) constant.getValue()));

		return bottom();
	}

	@Override
	protected Interval evalTypeConversion(Type type, Interval arg) {
		return bottom();
	}

	@Override
	protected Interval evalUnaryExpression(UnaryOperator operator, Interval arg) {

		switch (operator) {
		case NUMERIC_NEG:
			return new Interval(new IntervalValue(0, null).mul(new IntervalValue(-1, -1)));
		case STRING_LENGTH: 
			return new Interval(new IntervalValue(0, null));
		case LOGICAL_NOT:
		default:
			break;
		}		

		return bottom();
	}

	@Override
	protected Interval evalBinaryExpression(BinaryOperator operator, Interval left, Interval right) {
		if (left.isBottom() || right.isBottom())
			return bottom();

		switch (operator) {
		case COMPARISON_EQ:
		case COMPARISON_GE:
		case COMPARISON_GT:
		case COMPARISON_LE:
		case COMPARISON_LT:
		case COMPARISON_NE:
		case LOGICAL_AND:
		case LOGICAL_OR:
		case STRING_CONCAT:
		case STRING_CONTAINS:
		case STRING_ENDS_WITH:
		case STRING_EQUALS:
		case STRING_INDEX_OF:
		case STRING_STARTS_WITH:
			break;
		case NUMERIC_ADD: return new Interval(left.getInterval().plus(right.getInterval()));
		case NUMERIC_SUB: return new Interval(left.getInterval().diff(right.getInterval()));
		case NUMERIC_MUL: return new Interval(left.getInterval().mul(right.getInterval()));
		case NUMERIC_DIV: break;
		case NUMERIC_MOD:
			return top();
		default:
			break;
		}

		return bottom();
	}

	@Override
	protected Interval evalTernaryExpression(TernaryOperator operator, Interval left, Interval middle, Interval right) {
		return bottom();
	}

	@Override
	protected Interval lubAux(Interval other) throws SemanticException {
		return new Interval(getInterval().lubAux(other.getInterval()));
	}

	@Override
	protected Interval wideningAux(Interval other) throws SemanticException {
		return new Interval(other.getInterval().wideningAux(getInterval()));
	}

	@Override
	protected boolean lessOrEqualAux(Interval other) throws SemanticException {
		return getInterval().isLessThen(other.getInterval());
	}	
}
