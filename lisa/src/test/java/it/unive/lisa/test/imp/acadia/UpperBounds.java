package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.analysis.InverseSetLattice;
import it.unive.lisa.symbolic.value.Identifier;
import java.util.HashSet;
import java.util.Set;

public class UpperBounds extends InverseSetLattice<UpperBounds, Identifier> {

	protected UpperBounds(Set<Identifier> elements) {
		super(elements);
	}

	@Override
	public UpperBounds top() {
		return new UpperBounds(new HashSet<>());
	}

	@Override
	public UpperBounds bottom() {
		return new UpperBounds(null);
	}

	@Override
	public boolean isTop() {
		return elements.isEmpty();
	}

	@Override
	public boolean isBottom() {
		return elements == null;
	}

	@Override
	protected UpperBounds mk(Set<Identifier> set) {
		Set<Identifier> res = new HashSet<>(set);
		return new UpperBounds(res);
	}

	public void addIdentifier(Identifier id) {
		elements.add(id);
	}

	public void addIdentifiers(UpperBounds ids) {
		elements.addAll(ids.elements);
	}

	public boolean containsIdentifier(Identifier id) {
		return isTop() || isBottom() ? false : elements.contains(id);
	}

	public Set<Identifier> getIdentifiers() {
		return elements;
	}
}
