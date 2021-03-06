package it.unive.lisa.test.imp.expressions;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.UnaryNativeCall;
import it.unive.lisa.cfg.type.BooleanType;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.test.imp.types.BoolType;

/**
 * An expression modeling the logical not operation ({@code !}). The operand's
 * type must be instance of {@link BooleanType}. The type of this expression is
 * the {@link BoolType}.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class IMPNot extends UnaryNativeCall {

	/**
	 * Builds the logical not.
	 * 
	 * @param cfg        the {@link CFG} where this operation lies
	 * @param sourceFile the source file name where this operation is defined
	 * @param line       the line number where this operation is defined
	 * @param col        the column where this operation is defined
	 * @param expression the operand of this operation
	 */
	public IMPNot(CFG cfg, String sourceFile, int line, int col, Expression expression) {
		super(cfg, sourceFile, line, col, "!", BoolType.INSTANCE, expression);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> unarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression expr) throws SemanticException {
		// we allow untyped for the type inference phase
		if (!expr.getDynamicType().isBooleanType() && !expr.getDynamicType().isUntyped())
			return computedState.bottom();

		return computedState.smallStepSemantics(
				new UnaryExpression(Caches.types().mkSingletonSet(BoolType.INSTANCE), expr, UnaryOperator.LOGICAL_NOT));
	}
}
