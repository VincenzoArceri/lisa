package it.unive.lisa.analysis.heap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import it.unive.lisa.analysis.BaseHeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.HeapIdentifier;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.ValueExpression;

public class ClassBasedHeap extends BaseHeapDomain<ClassBasedHeap> {

	private static final ClassBasedHeap TOP = new ClassBasedHeap();
	private static final ClassBasedHeap BOTTOM = new ClassBasedHeap(null);
	private final Collection<ValueExpression> rewritten;
	
	private static final Set<String> ids = new HashSet<String>();

	public ClassBasedHeap() {
		this(Collections.singleton(new Skip()));
	}

	protected ClassBasedHeap(Collection<ValueExpression> rewritten) {
		this.rewritten = rewritten;
	}

	@Override
	public ClassBasedHeap assign(Identifier id, SymbolicExpression expression) throws SemanticException {
		if (expression instanceof ValueExpression) {
			return mk(this, (ValueExpression) expression);
		} else {
			return smallStepSemantics(expression);
		}
	}

	@Override
	public ClassBasedHeap assume(SymbolicExpression expression) throws SemanticException {
		return new ClassBasedHeap(rewritten);
	}

	@Override
	public ClassBasedHeap forgetIdentifier(Identifier id) throws SemanticException {
		return new ClassBasedHeap(rewritten);
	}

	@Override
	public Satisfiability satisfies(SymbolicExpression expression) throws SemanticException {
		return Satisfiability.UNKNOWN;
	}

	@Override
	public String representation() {
		return ids.toString();
	}

	@Override
	public ClassBasedHeap top() {
		return TOP;
	}

	@Override
	public ClassBasedHeap bottom() {
		return BOTTOM;
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
	protected ClassBasedHeap mk(ClassBasedHeap reference, ValueExpression expression) {
//		return new ClassBasedHeap(CollectionUtils.union(reference.rewritten, Collections.singleton(expression)));
//		if (reference.isTop())
		return new ClassBasedHeap(Collections.singleton(expression));
//		else
//			return new ClassBasedHeap(CollectionUtils.union(reference.rewritten, Collections.singleton(expression)));
	}

	@Override
	protected ClassBasedHeap semanticsOf(HeapExpression expression) {

		if (expression instanceof HeapAllocation) {
			HeapAllocation alloc = (HeapAllocation) expression;
			String classPointer = alloc.getDynamicType().toString();
			ids.add(classPointer);
			return new ClassBasedHeap(
					Collections.singleton(new HeapIdentifier(Caches.types().mkSingletonSet(alloc.getDynamicType()), classPointer)));
		}

		if (expression instanceof HeapReference) {
			HeapReference ref = (HeapReference) expression;
		}

		if (expression instanceof AccessChild) {
			AccessChild acc = (AccessChild) expression;
			String classPointer = acc.getDynamicType().toString();
			ids.add(classPointer);
			return new ClassBasedHeap(
					Collections.singleton(new HeapIdentifier(Caches.types().mkSingletonSet(acc.getDynamicType()), classPointer)));
		}
		
		return top();
	}

	@Override
	protected ClassBasedHeap lubAux(ClassBasedHeap other) throws SemanticException {
		return new ClassBasedHeap(CollectionUtils.union(rewritten, other.rewritten));
	}

	@Override
	protected ClassBasedHeap wideningAux(ClassBasedHeap other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(ClassBasedHeap other) throws SemanticException {
		return true;
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
		ClassBasedHeap other = (ClassBasedHeap) obj;

		if (rewritten == null) {
			if (other.rewritten != null)
				return false;
		} else if (!rewritten.equals(other.rewritten))
			return false;
		return true;
	}
}
