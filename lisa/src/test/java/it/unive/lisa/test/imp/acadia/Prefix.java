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

public class Prefix extends BaseNonRelationalValueDomain<Prefix> {

	private static final Prefix TOP = new Prefix("");
	private static final Prefix BOTTOM = new Prefix();

	private String prefix;

	private Prefix(String prefix) {
		this.prefix = prefix;
	}

	public Prefix() {
		this.prefix = null;
	}

	@Override
	public Prefix top() {
		return TOP;
	}

	@Override
	public Prefix bottom() {
		return BOTTOM;
	}

	@Override
	public String representation() {
		return isBottom() ? "BOT" : prefix.isEmpty() ? "TOP" : prefix;
	}

	@Override
	protected Prefix evalNullConstant() {
		return bottom();
	}

	@Override
	protected Prefix evalNonNullConstant(Constant constant) {
		if (constant.getValue() instanceof String)
			return new Prefix((String) constant.getValue());
		return bottom();
	}

	@Override
	protected Prefix evalTypeConversion(Type type, Prefix arg) {
		return bottom();
	}

	@Override
	protected Prefix evalUnaryExpression(UnaryOperator operator, Prefix arg) {
		switch (operator) {
		case LOGICAL_NOT:
			break;
		case NUMERIC_NEG:
			break;
		case STRING_LENGTH:
			break;
		default:
			break;
		}

		return bottom();
	}

	@Override
	protected Prefix evalBinaryExpression(BinaryOperator operator, Prefix left, Prefix right) {
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
		case NUMERIC_ADD:
		case NUMERIC_DIV:
		case NUMERIC_MOD:
		case NUMERIC_MUL:
		case NUMERIC_SUB:
		case STRING_CONTAINS:
		case STRING_ENDS_WITH:
		case STRING_EQUALS:
		case STRING_INDEX_OF:
		case STRING_STARTS_WITH:
			break;
		case STRING_CONCAT:
			return left;
		default:
			break;
		}

		return bottom();
	}

	private String greatestCommonPrefix(String a, String b) {
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}

	@Override
	protected Prefix evalTernaryExpression(TernaryOperator operator, Prefix left, Prefix middle, Prefix right) {
		if (left.isBottom() || right.isBottom() || middle.isBottom())
			return bottom();

		switch (operator) {
		case STRING_REPLACE:
			break;
		case STRING_SUBSTRING:
			break;
		default:
			break;
		}

		return bottom();
	}

	@Override
	protected Prefix lubAux(Prefix other) throws SemanticException {
		return new Prefix(greatestCommonPrefix(prefix, other.prefix));
	}

	@Override
	protected Prefix wideningAux(Prefix other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(Prefix other) throws SemanticException {
		return other.prefix.startsWith(prefix);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		Prefix other = (Prefix) obj;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
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
	protected Satisfiability satisfiesTypeConversion(Type type, Prefix right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, Prefix arg) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, Prefix left, Prefix right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, Prefix left, Prefix middle,
			Prefix right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}
}
