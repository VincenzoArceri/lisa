package it.unive.lisa.test.imp.tutorial.string;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.BaseNonRelationalValueDomain;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;
import java.util.HashSet;
import java.util.Set;

public class CharInclusion extends BaseNonRelationalValueDomain<CharInclusion> {

	private static final CharInclusion TOP = new CharInclusion(null, null);
	private static final CharInclusion BOTTOM = new CharInclusion();

	private Set<Character> must;
	private Set<Character> may;

	private CharInclusion(Set<Character> must, Set<Character> may) {
		this.must = must;
		this.may = may;
	}

	private CharInclusion() {
		this.must = new HashSet<Character>();
		this.may = new HashSet<Character>();
	}

	@Override
	public CharInclusion top() {
		return TOP;
	}

	@Override
	public CharInclusion bottom() {
		return BOTTOM;
	}

	@Override
	public String representation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CharInclusion evalNullConstant() {
		return bottom();
	}

	@Override
	protected CharInclusion evalNonNullConstant(Constant constant) {
		if (constant.getValue() instanceof String) {
			Set<Character> chars = chars((String) constant.getValue());
			return new CharInclusion(chars, chars);
		}

		return bottom();
	}

	@Override
	protected CharInclusion evalTypeConversion(Type type, CharInclusion arg) {
		return bottom();
	}

	@Override
	protected CharInclusion evalUnaryExpression(UnaryOperator operator, CharInclusion arg) {

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

	public Set<Character> getMust() {
		return must;
	}

	public void setMust(Set<Character> must) {
		this.must = must;
	}

	public Set<Character> getMay() {
		return may;
	}

	public void setMay(Set<Character> may) {
		this.may = may;
	}

	@Override
	protected CharInclusion evalBinaryExpression(BinaryOperator operator, CharInclusion left, CharInclusion right) {

		switch (operator) {
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
			break;
		case NUMERIC_DIV:
			break;
		case NUMERIC_MOD:
			break;
		case NUMERIC_MUL:
			break;
		case NUMERIC_SUB:
			break;
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
	protected CharInclusion evalTernaryExpression(TernaryOperator operator, CharInclusion left, CharInclusion middle,
			CharInclusion right) {
		switch (operator) {
		case STRING_REPLACE:
			break;
		case STRING_SUBSTRING:
			return new CharInclusion(may, new HashSet<Character>());
		default:
			break;
		}

		return bottom();
	}

	@Override
	protected CharInclusion lubAux(CharInclusion other) throws SemanticException {
		Set<Character> mayLub = new HashSet<Character>();
		Set<Character> mustLub = new HashSet<Character>();
		mayLub.addAll(may);
		mayLub.addAll(other.getMay());

		for (Character c : must)
			if (other.getMust().contains(c))
				mustLub.add(c);
			else
				mayLub.add(c);

		for (Character c : other.getMust())
			if (!must.contains(c))
				mayLub.add(c);

		return new CharInclusion(may, must);
	}

	@Override
	protected CharInclusion wideningAux(CharInclusion other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(CharInclusion other) throws SemanticException {
		return other.getMay().containsAll(may) && other.getMust().containsAll(must);
	}

	private Set<Character> chars(String s) {
		Set<Character> result = new HashSet<Character>();
		for (int i = 0; i < s.length(); i++)
			result.add(s.charAt(i));
		return result;
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
	protected Satisfiability satisfiesTypeConversion(Type type, CharInclusion right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, CharInclusion arg) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, CharInclusion left,
			CharInclusion right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, CharInclusion left,
			CharInclusion middle, CharInclusion right) {
		// TODO Auto-generated method stub
		return Satisfiability.UNKNOWN;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((may == null) ? 0 : may.hashCode());
		result = prime * result + ((must == null) ? 0 : must.hashCode());
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
		CharInclusion other = (CharInclusion) obj;
		if (may == null) {
			if (other.may != null)
				return false;
		} else if (!may.equals(other.may))
			return false;
		if (must == null) {
			if (other.must != null)
				return false;
		} else if (!must.equals(other.must))
			return false;
		return true;
	}
}
