package it.unive.lisa.cfg.statement;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;
import it.unive.lisa.symbolic.SymbolicExpression;
import java.util.Collection;

/**
 * A {@link NativeCall} with a exactly two arguments.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public abstract class BinaryNativeCall extends NativeCall {

	/**
	 * Builds the untyped native call. The location where this call happens is
	 * unknown (i.e. no source file/line/column is available). The static type
	 * of this call is {@link Untyped}.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param constructName the name of the construct invoked by this native
	 *                          call
	 * @param left          the first parameter of this call
	 * @param right         the second parameter of this call
	 */
	protected BinaryNativeCall(CFG cfg, String constructName, Expression left, Expression right) {
		super(cfg, constructName, left, right);
	}

	/**
	 * Builds the native call. The location where this call happens is unknown
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param constructName the name of the construct invoked by this native
	 *                          call
	 * @param staticType    the static type of this call
	 * @param left          the first parameter of this call
	 * @param right         the second parameter of this call
	 */
	protected BinaryNativeCall(CFG cfg, String constructName, Type staticType, Expression left, Expression right) {
		super(cfg, constructName, staticType, left, right);
	}

	/**
	 * Builds the untyped native call, happening at the given location in the
	 * program. The static type of this call is {@link Untyped}.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param sourceFile    the source file where this expression happens. If
	 *                          unknown, use {@code null}
	 * @param line          the line number where this expression happens in the
	 *                          source file. If unknown, use {@code -1}
	 * @param col           the column where this expression happens in the
	 *                          source file. If unknown, use {@code -1}
	 * @param constructName the name of the construct invoked by this native
	 *                          call
	 * @param left          the first parameter of this call
	 * @param right         the second parameter of this call
	 */
	protected BinaryNativeCall(CFG cfg, String sourceFile, int line, int col, String constructName,
			Expression left, Expression right) {
		super(cfg, sourceFile, line, col, constructName, left, right);
	}

	/**
	 * Builds the native call, happening at the given location in the program.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param sourceFile    the source file where this expression happens. If
	 *                          unknown, use {@code null}
	 * @param line          the line number where this expression happens in the
	 *                          source file. If unknown, use {@code -1}
	 * @param col           the column where this expression happens in the
	 *                          source file. If unknown, use {@code -1}
	 * @param constructName the name of the construct invoked by this native
	 *                          call
	 * @param staticType    the static type of this call
	 * @param left          the first parameter of this call
	 * @param right         the second parameter of this call
	 */
	protected BinaryNativeCall(CFG cfg, String sourceFile, int line, int col, String constructName, Type staticType,
			Expression left, Expression right) {
		super(cfg, sourceFile, line, col, constructName, staticType, left, right);
	}

	@Override
	public final <H extends HeapDomain<H>> AnalysisState<H, TypeEnvironment> callTypeInference(
			AnalysisState<H, TypeEnvironment> computedState, CallGraph callGraph,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		AnalysisState<H, TypeEnvironment> result = null;
		for (SymbolicExpression expr1 : params[0])
			for (SymbolicExpression expr2 : params[1]) {
				AnalysisState<H, TypeEnvironment> tmp = binarySemantics(computedState, callGraph, expr1, expr2);
				if (result == null)
					result = tmp;
				else
					result = result.lub(tmp);
			}

		setRuntimeTypes(result.getState().getValueState().getLastComputedTypes().getRuntimeTypes());
		return result;
	}

	@Override
	public final <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		AnalysisState<H, V> result = null;
		for (SymbolicExpression expr1 : params[0])
			for (SymbolicExpression expr2 : params[1]) {
				AnalysisState<H, V> tmp = binarySemantics(computedState, callGraph, expr1, expr2);
				if (result == null)
					result = tmp;
				else
					result = result.lub(tmp);
			}
		return result;
	}

	/**
	 * Computes the semantics of the call, after the semantics of the parameters
	 * have been computed. Meta variables from the parameters will be forgotten
	 * after this call returns.
	 * 
	 * @param <H>           the type of the heap analysis
	 * @param <V>           the type of the value analysis
	 * @param computedState the entry state that has been computed by chaining
	 *                          the parameters' semantics evaluation
	 * @param callGraph     the call graph of the program to analyze
	 * @param left          the symbolic expressions representing the computed
	 *                          value of the first parameter of this call
	 * @param right         the symbolic expressions representing the computed
	 *                          value of the second parameter of this call
	 * 
	 * @return the {@link AnalysisState} representing the abstract result of the
	 *             execution of this call
	 * 
	 * @throws SemanticException if something goes wrong during the computation
	 */
	protected abstract <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> binarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression left, SymbolicExpression right)
			throws SemanticException;
}
