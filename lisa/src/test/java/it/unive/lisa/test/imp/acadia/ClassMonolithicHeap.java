package it.unive.lisa.test.imp.acadia;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import it.unive.lisa.analysis.BaseHeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.value.HeapIdentifier;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * A monolithic heap implementation that abstracts all heap locations to a
 * unique identifier.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class ClassMonolithicHeap extends BaseHeapDomain<ClassMonolithicHeap> {

	private static final ClassMonolithicHeap TOP = new ClassMonolithicHeap();

	private static final ClassMonolithicHeap BOTTOM = new ClassMonolithicHeap();

	private final Map<Identifier, Type> heap;

	private final Collection<ValueExpression> rewritten;

	public ClassMonolithicHeap() {
		this(new Skip());
	}

	private ClassMonolithicHeap(ValueExpression rewritten) {
		this(Collections.singleton(rewritten));
	}

	private ClassMonolithicHeap(Collection<ValueExpression> rewritten) {
		this.rewritten = rewritten;
		this.heap = new HashMap<>();
	}
	
	private ClassMonolithicHeap(Map<Identifier, Type> heap, Collection<ValueExpression> rewritten) {
		this.rewritten = rewritten;
		this.heap = heap;
	}

	@Override
	public Collection<ValueExpression> getRewrittenExpressions() {
		return rewritten;
	}

	@Override
	public List<HeapReplacement> getSubstitution() {
		return Collections.emptyList();
	}

	@Override
	public ClassMonolithicHeap assign(Identifier id, SymbolicExpression expression) throws SemanticException {
		//System.err.println(id + " " + expression + " " + expression.getClass() + " " + expression.getDynamicType());

		if (expression instanceof HeapAllocation) {
			return new ClassMonolithicHeap(new HeapIdentifier(expression.getTypes(), expression.getDynamicType().toString()));
		}
		
		// the only thing that we do is rewrite the expression if needed
		return smallStepSemantics(expression);
	}

	@Override
	protected ClassMonolithicHeap mk(ClassMonolithicHeap reference, ValueExpression expression) {
		return new ClassMonolithicHeap(expression);
	}

	@Override
	protected ClassMonolithicHeap semanticsOf(HeapExpression expression) {
		// any expression accessing an area of the heap or instantiating a new one
		// is modeled through the monolith
		System.err.println(expression + " " + expression.getClass() + " " + expression.getDynamicType().isArrayType());
		return new ClassMonolithicHeap(new HeapIdentifier(expression.getTypes(), expression.getDynamicType().toString()));
	}

	@Override
	public ClassMonolithicHeap assume(SymbolicExpression expression) throws SemanticException {
		// the only thing that we do is rewrite the expression if needed
		return smallStepSemantics(expression);
	}

	@Override
	public Satisfiability satisfies(SymbolicExpression expression) throws SemanticException {
		// we leave the decision to the value domain
		return Satisfiability.UNKNOWN;
	}

	@Override
	public ClassMonolithicHeap forgetIdentifier(Identifier id) throws SemanticException {
		return new ClassMonolithicHeap(rewritten);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ClassMonolithicHeap lubAux(ClassMonolithicHeap other) throws SemanticException {
		Map<Identifier, Type> res = new HashMap<Identifier, Type>();
		res.putAll(heap);
		res.putAll(other.heap);
		return new ClassMonolithicHeap(res, CollectionUtils.union(rewritten, other.rewritten));
	}

	@Override
	protected ClassMonolithicHeap wideningAux(ClassMonolithicHeap other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(ClassMonolithicHeap other) throws SemanticException {
		return true;
	}

	@Override
	public ClassMonolithicHeap top() {
		return TOP;
	}

	@Override
	public ClassMonolithicHeap bottom() {
		return BOTTOM;
	}

	@Override
	public String representation() {
		return heap.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rewritten == null) ? 0 : rewritten.hashCode());
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
		ClassMonolithicHeap other = (ClassMonolithicHeap) obj;
		if (rewritten == null) {
			if (other.rewritten != null)
				return false;
		} else if (!rewritten.equals(other.rewritten))
			return false;
		return true;
	}
}
