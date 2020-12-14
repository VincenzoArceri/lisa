package it.unive.lisa.test.imp.acadia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import it.unive.lisa.analysis.FunctionalLattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class UpperBoundsEnvironment extends FunctionalLattice<UpperBoundsEnvironment, Identifier, UpperBounds>
implements ValueDomain<UpperBoundsEnvironment> {

	public UpperBoundsEnvironment() {
		this(new UpperBounds(new HashSet<>()));
	}

	protected UpperBoundsEnvironment(UpperBounds lattice) {
		this(lattice, new HashMap<>());
	}

	protected UpperBoundsEnvironment(UpperBounds lattice, Map<Identifier, UpperBounds> function) {
		super(lattice, function);
	}

	@Override
	public boolean isTop() {
		return function == null && lattice.isTop();
	}

	@Override
	public boolean isBottom() {
		return function == null && lattice.isBottom();
	}
	@Override
	public UpperBoundsEnvironment top() {
		return new UpperBoundsEnvironment(lattice.top(), null);
	}

	@Override
	public UpperBoundsEnvironment bottom() {
		return new UpperBoundsEnvironment(lattice.bottom(), null);
	}

	@Override
	public UpperBoundsEnvironment assign(Identifier id, ValueExpression expression) throws SemanticException {

		if (expression instanceof Identifier) {
			Identifier rhs = (Identifier) expression;
			UpperBounds yUB = new UpperBounds(getState(rhs).getIdentifiers());

			Map<Identifier, UpperBounds> func;
			if (function == null)
				func = new HashMap<>();
			else
				func = new HashMap<>(function);

			func.put(id, yUB);
			return new UpperBoundsEnvironment(lattice, func).closure();
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			switch(binary.getOperator()) {
			case NUMERIC_ADD:
				if (binary.getLeft() instanceof Identifier && !binary.getLeft().equals(id) && binary.getRight() instanceof Constant) {
					Identifier y = (Identifier) binary.getLeft();
					Constant cons = (Constant) binary.getRight();

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						UpperBounds yUB = new UpperBounds(getState(y).getIdentifiers());
						UpperBounds xUB = new UpperBounds(getState(id).getIdentifiers());

						Map<Identifier, UpperBounds> func;
						if (function == null)
							func = new HashMap<>();
						else
							func = new HashMap<>(function);

						if (c < 0) {
							xUB.addIdentifiers(yUB);
							xUB.addIdentifier(y);
							func.put(id, xUB);

							return new UpperBoundsEnvironment(lattice, func);
						}

						if (c > 0) {
							yUB.addIdentifier(id);
							func.put(y, yUB);
							UpperBoundsEnvironment res = new UpperBoundsEnvironment(lattice, func);
							res.forgetIdentifier(id);
							return res.closure();
						}
					}
				} 
			case NUMERIC_SUB:
				if (binary.getLeft() instanceof Identifier && !binary.getLeft().equals(id) && binary.getRight() instanceof Constant) {
					Identifier y = (Identifier) binary.getLeft();
					Constant cons = (Constant) binary.getRight();

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						UpperBounds yUB = new UpperBounds(getState(y).getIdentifiers());
						UpperBounds xUB = new UpperBounds(getState(id).getIdentifiers());

						Map<Identifier, UpperBounds> func;
						if (function == null)
							func = new HashMap<>();
						else
							func = new HashMap<>(function);

						if (c > 0) {
							xUB.addIdentifiers(yUB);
							xUB.addIdentifier(y);
							func.put(id, xUB);

							return new UpperBoundsEnvironment(lattice, func).closure();
						}

						if (c < 0) {
							yUB.addIdentifier(id);
							func.put(y, yUB);
							UpperBoundsEnvironment res = new UpperBoundsEnvironment(lattice, func);
							res.forgetIdentifier(id);
							return res.closure();
						}
					}
				} 

			default:
				break;
			}

		}

		return forgetIdentifier(id);
	}

	@Override
	public UpperBoundsEnvironment smallStepSemantics(ValueExpression expression) throws SemanticException {
		return new UpperBoundsEnvironment(lattice, function);
	}

	@Override
	public UpperBoundsEnvironment assume(ValueExpression expression) throws SemanticException {
		if (isBottom())
			return bottom();

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			switch(binary.getOperator()) {
			case COMPARISON_EQ:
				if (binary.getLeft() instanceof Identifier && binary.getRight() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					Identifier y = (Identifier) binary.getRight();

					UpperBounds xUB = new UpperBounds(getState(x).getIdentifiers());
					UpperBounds yUB = new UpperBounds(getState(y).getIdentifiers());

					Map<Identifier, UpperBounds> func;
					if (function == null) 
						func = new HashMap<>();
					else
						func = new HashMap<>(function);

					xUB.addIdentifiers(yUB);
					yUB.addIdentifiers(xUB);

					func.put(x, xUB);
					func.put(x, yUB);
					return new UpperBoundsEnvironment(new UpperBounds(new HashSet<>()), func).closure();	
				}
			case COMPARISON_GE:
				break;
			case COMPARISON_GT:
				break;
			case COMPARISON_LE:
				break;
			case COMPARISON_LT:
				if (binary.getLeft() instanceof Identifier && binary.getRight() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					Identifier y = (Identifier) binary.getRight();

					UpperBounds xUB = new UpperBounds(getState(x).getIdentifiers());
					UpperBounds yUB = new UpperBounds(getState(y).getIdentifiers());

					Map<Identifier, UpperBounds> func;
					if (function == null) 
						func = new HashMap<>();
					else
						func = new HashMap<>(function);

					xUB.addIdentifiers(yUB);
					xUB.addIdentifier(y);

					func.put(x, xUB);
					return new UpperBoundsEnvironment(new UpperBounds(new HashSet<>()), func).closure();	
				}
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
		}

		return new UpperBoundsEnvironment(lattice, function);
	}

	@Override
	public UpperBoundsEnvironment forgetIdentifier(Identifier id) throws SemanticException {
		if (function == null)
			return new UpperBoundsEnvironment(lattice, function);

		UpperBoundsEnvironment result = new UpperBoundsEnvironment(lattice, new HashMap<>(function));
		if (result.function.containsKey(id))
			result.function.remove(id);

		return result;
	}

	private UpperBoundsEnvironment closureAux() {
		if (isTop() || isBottom())
			return new UpperBoundsEnvironment(lattice, function);

		UpperBoundsEnvironment clos = new UpperBoundsEnvironment(lattice, function);

		for (Identifier x : getKeys())
			for (Identifier y : getKeys())
				for (Identifier z : getKeys())
					if (getState(y).containsIdentifier(x) && getState(z).containsIdentifier(y))
						clos.addRelation(z, x);
		return clos;
	}

	private UpperBoundsEnvironment closure() {
		UpperBoundsEnvironment previous = new UpperBoundsEnvironment(lattice, function);
		UpperBoundsEnvironment closure = previous;

		do {
			previous = closure;
			closure = previous.closureAux();
		} while(!previous.equals(closure));

		return previous;
	}


	private void addRelation(Identifier x, Identifier y) {
		getState(x).addIdentifier(y);
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression) throws SemanticException {
		return Satisfiability.UNKNOWN;
	}

	@Override
	public String representation() {
		if (isTop())
			return "TOP";

		if (isBottom())
			return "BOTTOM";

		StringBuilder builder = new StringBuilder();
		for (Entry<Identifier, UpperBounds> entry : function.entrySet())
			builder.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");

		return builder.toString().trim();	
	}
}
