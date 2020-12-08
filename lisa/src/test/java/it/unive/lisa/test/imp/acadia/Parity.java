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

public class Parity extends BaseNonRelationalValueDomain<Parity> {

	private enum Values { 
		ODD, 
		EVEN, 
		TOP, 
		BOT
	} 

	private final Values parity;

	private Parity(Values parity) {
		this.parity = parity;
	}

	public Parity() {
		this(Values.TOP);
	}

	@Override
	public Parity top() {
		return new Parity(Values.TOP);
	}

	@Override
	public boolean isTop() {
		return getParity() == Values.TOP;
	}

	@Override
	public boolean isBottom() {
		return getParity() == Values.BOT;
	}

	@Override
	public Parity bottom() {
		return new Parity(Values.BOT);
	}

	@Override
	public String representation() {
		switch (parity) {
		case BOT: return "BOT";
		case ODD: return "Odd";
		case EVEN: return "Even";
		case TOP: return "TOP";
		default: break;
		}

		return "Parity error";
	}

	public Values getParity() {
		return parity;
	}

	@Override
	protected Parity evalNullConstant() {
		return bottom();
	}

	@Override
	protected Parity evalNonNullConstant(Constant constant) {
		if (constant.getValue() instanceof Integer) {
			Integer i = (Integer) constant.getValue();
			return i % 2 == 0 ? even() : odd();
		}

		return bottom();
	}

	private Parity odd() {
		return new Parity(Values.ODD);
	}

	private Parity even() {
		return new Parity(Values.EVEN);
	}

	private boolean isEven() {
		return parity == Values.EVEN;
	}

	private boolean isOdd() {
		return parity == Values.ODD;
	}

	@Override
	protected Parity evalTypeConversion(Type type, Parity arg) {
		return bottom();
	}

	@Override
	protected Parity evalUnaryExpression(UnaryOperator operator, Parity arg) {

		switch (operator) {
		case NUMERIC_NEG:
			return arg;
		case STRING_LENGTH: 
			return top();
		case LOGICAL_NOT:
		default:
			break;
		}		

		return bottom();
	}

	@Override
	protected Parity evalBinaryExpression(BinaryOperator operator, Parity left, Parity right) {
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
		case NUMERIC_ADD:
		case NUMERIC_SUB:
			if (right.isTop() || left.isTop())
				return top();
			if (right.equals(left))
				return even();
			else
				return odd();
		case NUMERIC_MUL:
			if (left.isEven() || right.isEven())
				return even();
			else if (left.isTop() || right.isTop())
				return top();
			else
				return odd();
		case NUMERIC_DIV:
			if (right.isTop() || left.isTop())
				return top();
			else if (left.isOdd())
				return odd();
			else if (right.isOdd())
				return even();
			else 
				return top();
		case NUMERIC_MOD:
			return top();
		default:
			break;
		}

		return bottom();
	}

	@Override
	protected Parity evalTernaryExpression(TernaryOperator operator, Parity left, Parity middle, Parity right) {
		return bottom();
	}

	@Override
	protected Parity lubAux(Parity other) throws SemanticException {
		return equals(other) ? other: top();
	}

	@Override
	protected Parity wideningAux(Parity other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(Parity other) throws SemanticException {
		return equals(other);
	}

	@Override
	protected Satisfiability satisfiesIdentifier(Identifier identifier) {
		return Satisfiability.BOTTOM;
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
	protected Satisfiability satisfiesTypeConversion(Type type, Parity right) {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, Parity arg) {
		return Satisfiability.BOTTOM;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, Parity left, Parity right) {
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, Parity left, Parity middle, Parity right) {
		return Satisfiability.BOTTOM;
	}
}

