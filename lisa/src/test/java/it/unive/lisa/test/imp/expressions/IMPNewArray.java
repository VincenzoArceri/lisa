package it.unive.lisa.test.imp.expressions;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.test.imp.types.ArrayType;
import java.util.Collection;

/**
 * An expression modeling the array allocation operation
 * ({@code new type[...]}). The type of this expression is the {@link Type} of
 * the array's elements. Note that the dimensions of the array are ignored. This
 * expression corresponds to a {@link HeapAllocation}.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class IMPNewArray extends NativeCall {

	/**
	 * Builds the array allocation.
	 * 
	 * @param cfg        the {@link CFG} where this operation lies
	 * @param sourceFile the source file name where this operation is defined
	 * @param line       the line number where this operation is defined
	 * @param col        the column where this operation is defined
	 * @param type       the type of the array's elements
	 * @param dimensions the dimensions of the array
	 */
	public IMPNewArray(CFG cfg, String sourceFile, int line, int col, Type type, Expression[] dimensions) {
		super(cfg, sourceFile, line, col, "new[]", ArrayType.lookup(type, dimensions.length));
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		return computedState.smallStepSemantics(new HeapAllocation(getRuntimeTypes()));
	}

	@Override
	public <H extends HeapDomain<H>> AnalysisState<H, TypeEnvironment> callTypeInference(
			AnalysisState<H, TypeEnvironment> computedState, CallGraph callGraph,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		return computedState.smallStepSemantics(new HeapAllocation(Caches.types().mkSingletonSet(getStaticType())));
	}
}
