package it.unive.lisa.test.imp.expressions;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.BinaryNativeCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.test.imp.types.StringType;
import it.unive.lisa.util.collections.ExternalSet;

/**
 * An expression modeling the addition operation ({@code +}). If both operands'
 * dynamic type (according to {@link SymbolicExpression#getDynamicType()}) is a
 * {@link it.unive.lisa.cfg.type.StringType} (according to
 * {@link Type#isStringType()}), then this operation translates to a string
 * concatenation of its operands, and its type is {@link StringType}. Otherwise,
 * both operands' types must be instances of {@link NumericType}, and the type
 * of this expression (i.e., a numerical sum) is the common numerical type of
 * its operands, according to
 * {@link BinaryNumericalOperation#commonNumericalType(SymbolicExpression, SymbolicExpression)}.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class IMPAdd extends BinaryNativeCall implements BinaryNumericalOperation {

	/**
	 * Builds the addition.
	 * 
	 * @param cfg        the {@link CFG} where this operation lies
	 * @param sourceFile the source file name where this operation is defined
	 * @param line       the line number where this operation is defined
	 * @param col        the column where this operation is defined
	 * @param left       the left-hand side of this operation
	 * @param right      the right-hand side of this operation
	 */
	public IMPAdd(CFG cfg, String sourceFile, int line, int col, Expression left, Expression right) {
		super(cfg, sourceFile, line, col, "+", left, right);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> binarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression left, SymbolicExpression right)
			throws SemanticException {
		BinaryOperator op;
		ExternalSet<Type> types;
		if (left.getDynamicType().isStringType() && right.getDynamicType().isStringType()) {
			op = BinaryOperator.STRING_CONCAT;
			types = Caches.types().mkSingletonSet(StringType.INSTANCE);
		} else if ((left.getDynamicType().isNumericType() || left.getDynamicType().isUntyped())
				&& (right.getDynamicType().isNumericType() || right.getDynamicType().isUntyped())) {
			op = BinaryOperator.NUMERIC_ADD;
			types = commonNumericalType(left, right);
		} else
			return computedState.bottom();

		return computedState
				.smallStepSemantics(new BinaryExpression(types, left, right, op));
	}
}
