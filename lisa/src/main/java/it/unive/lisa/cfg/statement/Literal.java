package it.unive.lisa.cfg.statement;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ExpressionStore;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.value.Constant;
import java.util.Objects;

/**
 * A literal, representing a constant value.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class Literal extends Expression {

	/**
	 * The value of this literal
	 */
	private final Object value;

	/**
	 * Builds a typed literal, consisting of a constant value. The location
	 * where this literal happens is unknown (i.e. no source file/line/column is
	 * available).
	 * 
	 * @param cfg        the cfg that this literal belongs to
	 * @param value      the value of this literal
	 * @param staticType the type of this literal
	 */
	public Literal(CFG cfg, Object value, Type staticType) {
		this(cfg, null, -1, -1, value, staticType);
	}

	/**
	 * Builds a typed literal, consisting of a constant value, happening at the
	 * given location in the program.
	 * 
	 * @param cfg        the cfg that this expression belongs to
	 * @param sourceFile the source file where this expression happens. If
	 *                       unknown, use {@code null}
	 * @param line       the line number where this expression happens in the
	 *                       source file. If unknown, use {@code -1}
	 * @param col        the column where this expression happens in the source
	 *                       file. If unknown, use {@code -1}
	 * @param value      the value of this literal
	 * @param staticType the type of this literal
	 */
	public Literal(CFG cfg, String sourceFile, int line, int col, Object value, Type staticType) {
		super(cfg, sourceFile, line, col, staticType);
		Objects.requireNonNull(value, "The value of a literal cannot be null");
		this.value = value;
	}

	/**
	 * Yields the value of this literal.
	 * 
	 * @return the value of this literal
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public int setOffset(int offset) {
		return this.offset = offset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean isEqualTo(Statement st) {
		if (this == st)
			return true;
		if (getClass() != st.getClass())
			return false;
		if (!super.isEqualTo(st))
			return false;
		Literal other = (Literal) st;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public <H extends HeapDomain<H>> AnalysisState<H, TypeEnvironment> typeInference(
			AnalysisState<H, TypeEnvironment> entryState, CallGraph callGraph,
			ExpressionStore<AnalysisState<H, TypeEnvironment>> expressions) throws SemanticException {
		AnalysisState<H, TypeEnvironment> typing = entryState
				.smallStepSemantics(new Constant(getStaticType(), getValue()));
		setRuntimeTypes(typing.getState().getValueState().getLastComputedTypes().getRuntimeTypes());
		return typing;
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> semantics(
			AnalysisState<H, V> entryState, CallGraph callGraph, ExpressionStore<AnalysisState<H, V>> expressions)
			throws SemanticException {
		return entryState.smallStepSemantics(new Constant(getStaticType(), getValue()));
	}
}
