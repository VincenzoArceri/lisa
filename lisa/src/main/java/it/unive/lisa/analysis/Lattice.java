package it.unive.lisa.analysis;

/**
 * An interface for elements that follow a lattice structure. Implementers of
 * this interface should inherit from {@link BaseLattice}, unless explicitly
 * needed.
 * 
 * @param <L> the concrete {@link Lattice} instance
 *
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public interface Lattice<L extends Lattice<L>> {

	/**
	 * Performs the least upper bound operation between this lattice element and the
	 * given one. This operation is commutative.
	 * 
	 * @param other the other lattice element
	 * @return the least upper bound
	 */
	L lub(L other);

	/**
	 * Performs the widening operation between this lattice element and the given
	 * one. This operation is not commutative.
	 * 
	 * @param other the other lattice element
	 * @return the least upper bound
	 */
	L widening(L other);

	/**
	 * Yields {@code true} if and only if this lattice element is in relation with
	 * (usually represented through &le;) the given one. This operation is not
	 * commutative.
	 * 
	 * @param other the other lattice element
	 * @return {@code true} if and only if that condition holds
	 */
	boolean lessOrEqual(L other);

	/**
	 * Yields the top element of this lattice. The returned element should be unique
	 * across different calls to this method, since {@link #isTop()} uses reference
	 * equality by default. If the value returned by this method is not a singleton,
	 * override {@link #isTop()} accordingly to provide a coherent test.
	 * 
	 * @return the top element
	 */
	L top();

	/**
	 * Yields the bottom element of this lattice. The returned element should be
	 * unique across different calls to this method, since {@link #isBottom()} uses
	 * reference equality by default. If the value returned by this method is not a
	 * singleton, override {@link #isBottom()} accordingly to provide a coherent
	 * test.
	 * 
	 * @return the bottom element
	 */
	L bottom();

	/**
	 * Yields {@code true} if and only if this object represents the top of the
	 * lattice. The default implementation of this method uses reference equality
	 * between {@code this} and the value returned by {@link #top()}, thus assuming
	 * that the top element is a singleton. If this is not the case, override this
	 * method accordingly to provide a coherent test.
	 * 
	 * @return {@code true} if this is the top of the lattice
	 */
	public default boolean isTop() {
		return this == top();
	}

	/**
	 * Yields {@code true} if and only if this object represents the bottom of the
	 * lattice. The default implementation of this method uses reference equality
	 * between {@code this} and the value returned by {@link #bottom()}, thus
	 * assuming that the bottom element is a singleton. If this is not the case,
	 * override this method accordingly to provide a coherent test.
	 * 
	 * @return {@code true} if this is the bottom of the lattice
	 */
	public default boolean isBottom() {
		return this == bottom();
	}
}