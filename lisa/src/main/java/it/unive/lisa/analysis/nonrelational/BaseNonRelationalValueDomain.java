package it.unive.lisa.analysis.nonrelational;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.TypeConversion;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * Base implementation for {@link NonRelationalValueDomain}s. This class extends
 * {@link BaseLattice} and implements
 * {@link NonRelationalValueDomain#eval(it.unive.lisa.symbolic.SymbolicExpression, it.unive.lisa.analysis.FunctionalLattice)} 
 * and {@link NonRelationalValueDomain#satisfies(SymbolicExpression, ValueEnvironment)},
 * by taking care of the recursive computation of inner expressions evaluation.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 * 
 * @param <T> the concrete type of this domain
 */
public abstract class BaseNonRelationalValueDomain<T extends BaseNonRelationalValueDomain<T>> extends BaseLattice<T>
implements NonRelationalValueDomain<T> {

	@Override
	public final Satisfiability satisfies(SymbolicExpression expression, ValueEnvironment<T> environment) {	
		if (expression instanceof Identifier)
			return satisfiesIdentifier((Identifier) expression);

		if (expression instanceof NullConstant)
			return satisfiesNullConstant();

		if (expression instanceof Constant)
			return satisfiesNonNullConstant((Constant) expression);

		if (expression instanceof Skip)
			return Satisfiability.UNKNOWN;

		if (expression instanceof TypeConversion) {
			TypeConversion conv = (TypeConversion) expression;

			T arg = eval((ValueExpression) conv.getOperand(), environment);
			return satisfiesTypeConversion(conv.getToType(), arg);
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			if (unary.getOperator() == UnaryOperator.LOGICAL_NOT)
				return satisfies(unary.getExpression(), environment).negate();
			else {			
				T arg = eval((ValueExpression) unary.getExpression(), environment);
				if (arg.isBottom())
					return Satisfiability.BOTTOM;
				return satisfiesUnaryExpression(unary.getOperator(), arg);
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			if (binary.getOperator() == BinaryOperator.LOGICAL_AND) 
				return satisfies(binary.getLeft(), environment).and(satisfies(binary.getRight(), environment));

			else if (binary.getOperator() == BinaryOperator.LOGICAL_OR) 
				return satisfies(binary.getLeft(), environment).or(satisfies(binary.getRight(), environment));

			else {
				T left = eval((ValueExpression) binary.getLeft(), environment);
				T right = eval((ValueExpression) binary.getRight(), environment);
				if (left.isBottom() || right.isBottom())
					return Satisfiability.BOTTOM;
				return satisfiesBinaryExpression(binary.getOperator(), left, right);
			}
		}

		if (expression instanceof TernaryExpression) {
			TernaryExpression ternary = (TernaryExpression) expression;

			T left = eval((ValueExpression) ternary.getLeft(), environment);
			T middle = eval((ValueExpression) ternary.getMiddle(), environment);
			T right = eval((ValueExpression) ternary.getRight(), environment);

			if (left.isBottom() || middle.isBottom() ||right.isBottom())
				return Satisfiability.BOTTOM;
			
			return satisfiesTernaryExpression(ternary.getOperator(), left, middle, right);
		}

		return Satisfiability.UNKNOWN;	
	}

	@Override
	public final T eval(ValueExpression expression, ValueEnvironment<T> environment) {
		if (expression instanceof Identifier) 
			return environment.getState((Identifier) expression);

		if (expression instanceof NullConstant)
			return evalNullConstant();

		if (expression instanceof Constant)
			return evalNonNullConstant((Constant) expression);

		if (expression instanceof Skip)
			return bottom();

		if (expression instanceof TypeConversion) {
			TypeConversion conv = (TypeConversion) expression;

			T arg = eval((ValueExpression) conv.getOperand(), environment);
			if (arg.isTop() || arg.isBottom())
				return arg;

			return evalTypeConversion(conv.getToType(), arg);
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			T arg = eval((ValueExpression) unary.getExpression(), environment);
			if (arg.isTop() || arg.isBottom())
				return arg;

			return evalUnaryExpression(unary.getOperator(), arg);
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			T left = eval((ValueExpression) binary.getLeft(), environment);
			if (left.isTop() || left.isBottom())
				return left;

			T right = eval((ValueExpression) binary.getRight(), environment);
			if (right.isTop() || right.isBottom())
				return right;

			return evalBinaryExpression(binary.getOperator(), left, right);
		}

		if (expression instanceof TernaryExpression) {
			TernaryExpression ternary = (TernaryExpression) expression;

			T left = eval((ValueExpression) ternary.getLeft(), environment);
			if (left.isTop() || left.isBottom())
				return left;

			T middle = eval((ValueExpression) ternary.getMiddle(), environment);
			if (middle.isTop() || middle.isBottom())
				return middle;

			T right = eval((ValueExpression) ternary.getRight(), environment);
			if (right.isTop() || right.isBottom())
				return right;

			return evalTernaryExpression(ternary.getOperator(), left, middle, right);
		}

		return bottom();
	}

	/**
	 * Yields the evaluation of the null constant {@link NullConstant}.
	 * 
	 * @return the evaluation of the constant
	 */
	protected abstract T evalNullConstant();

	/**
	 * Yields the evaluation of the given non-null constant.
	 * 
	 * @param constant the constant to evaluate
	 * 
	 * @return the evaluation of the constant
	 */
	protected abstract T evalNonNullConstant(Constant constant);

	/**
	 * Yields the evaluation of a {@link TypeConversion} converting an
	 * expression whose abstract value is {@code arg} to the given {@link Type}.
	 * It is guaranteed that {@code arg} is neither {@link #top()} or
	 * {@link #bottom()}.
	 * 
	 * @param type the type to cast {@code arg} to
	 * @param arg  the instance of this domain representing the abstract value
	 *                 of the expresion's argument
	 * 
	 * @return the evaluation of the expression
	 */
	protected abstract T evalTypeConversion(Type type, T arg);

	/**
	 * Yields the evaluation of a {@link UnaryExpression} applying
	 * {@code operator} to an expression whose abstract value is {@code arg}. It
	 * is guaranteed that {@code arg} is neither {@link #top()} or
	 * {@link #bottom()}.
	 * 
	 * @param operator the operator applied by the expression
	 * @param arg      the instance of this domain representing the abstract
	 *                     value of the expresion's argument
	 * 
	 * @return the evaluation of the expression
	 */
	protected abstract T evalUnaryExpression(UnaryOperator operator, T arg);

	/**
	 * Yields the evaluation of a {@link BinaryExpression} applying
	 * {@code operator} to two expressions whose abstract value are {@code left}
	 * and {@code right}, respectively. It is guaranteed that both {@code left}
	 * and {@code right} are neither {@link #top()} or {@link #bottom()}.
	 * 
	 * @param operator the operator applied by the expression
	 * @param left     the instance of this domain representing the abstract
	 *                     value of the left-hand side argument
	 * @param right    the instance of this domain representing the abstract
	 *                     value of the right-hand side argument
	 * 
	 * @return the evaluation of the expression
	 */
	protected abstract T evalBinaryExpression(BinaryOperator operator, T left, T right);

	/**
	 * Yields the evaluation of a {@link TernaryExpression} applying
	 * {@code operator} to two expressions whose abstract value are
	 * {@code left}, {@code middle} and {@code right}, respectively. It is
	 * guaranteed that both {@code left} and {@code right} are neither
	 * {@link #top()} or {@link #bottom()}.
	 * 
	 * @param operator the operator applied by the expression
	 * @param left     the instance of this domain representing the abstract
	 *                     value of the left-hand side argument
	 * @param middle   the instance of this domain representing the abstract
	 *                     value of the middle argument
	 * @param right    the instance of this domain representing the abstract
	 *                     value of the right-hand side argument
	 * 
	 * @return the evaluation of the expression
	 */
	protected abstract T evalTernaryExpression(TernaryOperator operator, T left, T middle, T right);

	/**
	 * Yields the satisfiability of the identifier {@code identifier}
	 * on thi abstract domains
	 * 
	 * @param identifier	the identifier whose satisfiability is to be evaluated
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesIdentifier(Identifier identifier);
	
	/**
	 * Yields the satisfiability of the null constant {@link NullConstant}. 
	 * 
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesNullConstant();
	
	/**
	 * Yields the satisfiability of the given non-null constant.
	 * 
	 * @param constant the constant to satisfied
	 * @return {@link Satisfiability#SATISFIED} is the constant is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 *         
	 */
	protected abstract Satisfiability satisfiesNonNullConstant(Constant constant);
	
	/**
	 * Yields the satisfiability of a {@link TypeConversion} converting an
	 * expression whose abstract value is {@code arg} to the given {@link Type}.
	 * This method returns an instance of {@link Satisfiability} and 
	 * it is guaranteed that {@code arg} is not {@link #bottom()}.
	 * 
	 * @param type the type to cast {@code arg} to
	 * @param arg  the instance of this domain representing the abstract
	 *                 value of the expression's argument
	 * 
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesTypeConversion(Type type, T right);
	
	/**
	 * Yields the satisfiability of a {@link UnaryExpression} applying
	 * {@code operator} to an expression whose abstract value is {@code arg},
	 * returning an instance of {@link Satisfiability}. It is guaranteed that
	 * {@code operator} is not {@link UnaryOperator#LOGICAL_NOT} and {@code arg} 
	 * is not {@link #bottom()}.
	 * 
	 * @param operator	the unary operator applied by the expression
	 * @param arg		an instance of this abstract domain representing the argument
	 * 					of the unary expression
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesUnaryExpression(UnaryOperator operator, T arg);
	
	/**
	 * Yields the satisfiability of a {@link BinaryExpression} applying
	 * {@code operator} to two expressions whose abstract values is {@code left},
	 * and {@code right}, corresponding to the abstract values of the left-hand side 
	 * and the right-hand side arguments of the expression, respectively. 
	 * This method returns an instance of {@link Satisfiability}. 
	 * It is guaranteed that {@code operator} is neither{@link BinaryOperator#LOGICAL_AND} nor
	 * {@link BinaryOperator#LOGICAL_OR}, and that  both {@code left} and {@code right} 
	 * are not {@link #bottom()}.
	 * 
	 * @param operator	the binary operator applied by the expression
	 * @param left		an instance of this abstract domain representing the argument
	 * 					of the left-hand side of the binary expression
 	 * @param right		an instance of this abstract domain representing the argument
	 * 					of the right-hand side of the binary expression
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesBinaryExpression(BinaryOperator operator, T left, T right);
	
	/**
	 * Yields the satisfiability of a {@link TernaryExpression} applying
	 * {@code operator} to three expressions whose abstract values are{@code left},
	 * {@code middle} and {@code right}, corresponding to the abstract values of the 
	 * left-most side, the middle, the right-most side arguments of the ternary 
	 * expression, respectively. This method returns an instance of {@link Satisfiability}. 
	 * It is guaranteed that {@code left}, {@code middle} and {@code right} are not {@link #bottom()}.
	 * 
	 * @param operator	the ternary operator applied by the expression
	 * @param left		an instance of this abstract domain representing the argument
	 * 					of the left-most side of the ternary expression	
	 * @param middle	an instance of this abstract domain representing the argument
	 * 					in the middle of the ternary expression
 	 * @param right		an instance of this abstract domain representing the argument
	 * 					of the right-most side of the ternary expression
	 * @return {@link Satisfiability#SATISFIED} is the expression is satisfied by
	 *         this domain, {@link Satisfiability#NOT_SATISFIED} if it
	 *         is not satisfied, or {@link Satisfiability#UNKNOWN} if it is either
	 *         impossible to determine if it satisfied, or if it is satisfied by
	 *         some values and not by some others (this is equivalent to a TOP
	 *         boolean value)
	 */
	protected abstract Satisfiability satisfiesTernaryExpression(TernaryOperator operator, T left, T middle, T right);
}
