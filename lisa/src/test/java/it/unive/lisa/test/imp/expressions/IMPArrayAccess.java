package it.unive.lisa.test.imp.expressions;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.BinaryNativeCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;

/**
 * An expression modeling the array element access operation
 * ({@code array[index]}). The type of this expression is the one of the
 * resolved array element.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class IMPArrayAccess extends BinaryNativeCall {

	/**
	 * Builds the array access.
	 * 
	 * @param cfg        the {@link CFG} where this operation lies
	 * @param sourceFile the source file name where this operation is defined
	 * @param line       the line number where this operation is defined
	 * @param col        the column where this operation is defined
	 * @param container  the expression representing the array reference that
	 *                       will receive the access
	 * @param location   the expression representing the accessed element
	 */
	public IMPArrayAccess(CFG cfg, String sourceFile, int line, int col, Expression container, Expression location) {
		super(cfg, sourceFile, line, col, "[]", container, location);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> binarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression left, SymbolicExpression right)
			throws SemanticException {
		if (!left.getDynamicType().isArrayType() || !left.getDynamicType().isUntyped())
			return computedState.bottom();
		// it is not possible to detect the correct type of the field without
		// resolving it. we rely on the rewriting that will happen inside heap
		// domain to translate this into a variable that will have its correct
		// type
		return computedState.smallStepSemantics(new AccessChild(getRuntimeTypes(), left, right));
	}
}
