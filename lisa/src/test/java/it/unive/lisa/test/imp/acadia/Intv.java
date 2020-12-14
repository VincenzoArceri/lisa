package it.unive.lisa.test.imp.acadia;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Integer interval lattice.
 * 
 * @author Luca Negrini
 */
public class Intv {

	private static final Intv TOP = new Intv() {
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		@Override
		public int hashCode() {
			return "TOP".hashCode();
		}
	};

	private static final Intv BOTTOM = new Intv() {
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		@Override
		public int hashCode() {
			return "BOTTOM".hashCode();
		}

		@Override
		public String toString() {
			return "BOTTOM";
		}
	};

	/**
	 * Low value. Null means -infinity
	 */
	private final Integer low;

	/**
	 * High value. Null means +infinity
	 */
	private final Integer high;

	private Intv() {
		low = null;
		high = null;
	}

	/**
	 * Builds a new interval. For both parameters, a null value means infinity.
	 * 
	 * @param low  the lower bound
	 * @param high the upper bound
	 */
	public Intv(Integer low, Integer high) {
		this.low = low;
		this.high = high;
	}

	/**
	 * Returns the lower bound.
	 * 
	 * @return the lower bound
	 */
	public Integer getLow() {
		return low;
	}

	/**
	 * Yields true if and only if the lower bound of this interval is -infinity.
	 * 
	 * @return true only if that condition holds
	 */
	public boolean lowIsMinusInfinity() {
		return low == null;
	}

	/**
	 * Returns the higher bound.
	 * 
	 * @return the higher bound
	 */
	public Integer getHigh() {
		return high;
	}

	public static Intv mkTop() {
		return TOP;
	}
	
	public static Intv mkBottom() {
		return BOTTOM;
	}
	
	public boolean isTop() {
		return this == TOP;
	}

	public boolean isBottom() {
		return this == BOTTOM;
	}
	
	/**
	 * Yields true if and only if the upper bound of this interval is +infinity.
	 * 
	 * @return true only if that condition holds
	 */
	public boolean highIsPlusInfinity() {
		return high == null;
	}

	public Intv bottom() {
		return getBottom();
	}

	/**
	 * Yields the unique bottom element.
	 * 
	 * @return the bottom element
	 */
	public static Intv getBottom() {
		return BOTTOM;
	}

	public Intv top() {
		return getTop();
	}

	/**
	 * Yields the unique top element.
	 * 
	 * @return the top element
	 */
	public static Intv getTop() {
		return TOP;
	}

	protected Intv lubAux(Intv other) {
		Integer newLow = lowIsMinusInfinity() || other.lowIsMinusInfinity() ? null : Math.min(low, other.low);
		Integer newHigh = highIsPlusInfinity() || other.highIsPlusInfinity() ? null : Math.max(high, other.high);
		return new Intv(newLow, newHigh);
	}

	protected Intv wideningAux(Intv other) {
		Integer maxI1 = highIsPlusInfinity() ? Integer.MAX_VALUE : high;
		Integer maxI2 = other.highIsPlusInfinity() ? Integer.MAX_VALUE : other.high;
		Integer minI1 = lowIsMinusInfinity() ? Integer.MIN_VALUE : low;
		Integer minI2 = other.lowIsMinusInfinity() ? Integer.MIN_VALUE : other.low;

		Integer newLow, newHigh;
		if (minI2 < minI1)
			newLow = null;
		else
			newLow = low;

		if (maxI2 > maxI1)
			newHigh = null;
		else
			newHigh = high;
		return new Intv(newLow, newHigh);
	}

	private boolean inRelationWith(Intv other) {
		boolean lowIsFine = false;
		if (low == other.low)
			lowIsFine = true;
		else if (other.lowIsMinusInfinity())
			lowIsFine = true;
		else if (!lowIsMinusInfinity() && low > other.low)
			lowIsFine = true;

		boolean highIsFine = false;
		if (high == other.high)
			highIsFine = true;
		else if (other.highIsPlusInfinity())
			highIsFine = true;
		else if (!highIsPlusInfinity() && high < other.high)
			highIsFine = true;

		return lowIsFine && highIsFine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
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
		Intv other = (Intv) obj;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + (lowIsMinusInfinity() ? "-Inf" : low) + ", " + (highIsPlusInfinity() ? "+Inf" : high) + "]";
	}

	public Intv plus(Intv other) {
		Integer newLow, newHigh;

		if (lowIsMinusInfinity() || other.lowIsMinusInfinity())
			newLow = null;
		else
			newLow = low + other.low;

		if (highIsPlusInfinity() || other.highIsPlusInfinity())
			newHigh = null;
		else
			newHigh = high + other.high;

		return new Intv(newLow, newHigh);
	}

	public Intv diff(Intv other) {
		return plus(other.mul(new Intv(-1, -1)));
	}

	public Intv mul(Intv other) {
		SortedSet<Integer> boundSet = new TreeSet<>();
		Integer low1 = low;
		Integer high1 = high;
		Integer low2 = other.low;
		Integer high2 = other.high;

		AtomicBoolean lowInf = new AtomicBoolean(false), highInf = new AtomicBoolean(false);

		// low1low2
		multiplyBounds(boundSet, low1, low2, lowInf, highInf);

		// x1y2
		multiplyBounds(boundSet, low1, high2, lowInf, highInf);

		// x2y1
		multiplyBounds(boundSet, high1, low2, lowInf, highInf);

		// x2y2
		multiplyBounds(boundSet, high1, high2, lowInf, highInf);

		return new Intv(lowInf.get() ? null : boundSet.first(), highInf.get() ? null : boundSet.last());
	}

	private void multiplyBounds(SortedSet<Integer> boundSet, Integer low1, Integer low2, AtomicBoolean lowInf,
			AtomicBoolean highInf) {
		if (low1 == null) {
			if (low2 == null)
				// -inf * -inf = +inf
				highInf.set(true);
			else {
				if (low2 > 0)
					// -inf * positive
					lowInf.set(true);
				else if (low2 < 0)
					// -inf * negative
					highInf.set(true);
				else
					boundSet.add(0);
			}
		} else if (low2 == null) {
			if (low1 > 0)
				// -inf * positive
				lowInf.set(true);
			else if (low1 < 0)
				// -inf * negative
				highInf.set(true);
			else
				boundSet.add(0);
		} else
			boundSet.add(low1 * low2);
	}

	public List<Integer> getIntergers() {
		if (isFinite())
			return IntStream.range(low, high + 1).boxed().collect(Collectors.toList());

		return new ArrayList<>();
	}


	public boolean isFinite() {
		return !lowIsMinusInfinity() && !highIsPlusInfinity();
	}

	public boolean isZero() {
		return low == 0 && high == 0;
	}

	public boolean contains(int n) {
		if (isFinite())
			return n >= low && n <= high;
			else if (lowIsMinusInfinity() && !highIsPlusInfinity())
				return n <= high;
			else if (!lowIsMinusInfinity() && highIsPlusInfinity())
				return n >= low;
				else
					return true;
	}

	public Boolean isEqualTo(Intv other) {
		if (!equals(other) && (other.inRelationWith(this) || inRelationWith(other)))
			// one contains the other
			return null;
		return equals(other);
	}

	public Boolean isLessThen(Intv other) {
		if (equals(other))
			return true;
		if (other.isFinite() && isFinite()) {
			if (other.low <= this.low && other.high >= this.high)
				return true;
		}
		
		if (other.inRelationWith(this) || inRelationWith(other))
			return false;
		if (other.lowIsMinusInfinity() && highIsPlusInfinity())
			return false; // TODO
		if (high < other.low)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}


	public Intv minusOne() {
		return new Intv(-1, -1);
	}

	public Intv mk(int value) {
		return new Intv(value, value);
	}

	public Intv makeGreaterThan(Intv other) {
		if (!other.isFinite())
			return this;
		if (!lowIsMinusInfinity() && low > other.high)
			return this;
		if (highIsPlusInfinity() || high >= other.high + 1)
			return new Intv(other.high + 1, high);
		return mk(other.high + 1);
	}

	public Intv makeGreaterOrEqualThan(Intv other) {
		if (!other.isFinite())
			return this;
		if (!lowIsMinusInfinity() && low >= other.high)
			return this;
		if (highIsPlusInfinity() || high >= other.high)
			return new Intv(other.high, high);
		return mk(other.high);
	}

	public Intv makeLessThan(Intv other) {
		if (!other.isFinite())
			return this;
		if (!highIsPlusInfinity() && high < other.low)
			return this;
		if (lowIsMinusInfinity() || low <= other.low - 1)
			return new Intv(low, other.low - 1);
		return mk(other.low - 1);
	}

	public Intv makeLessOrEqualThan(Intv other) {
		if (!other.isFinite())
			return this;
		if (!highIsPlusInfinity() && high <= other.low)
			return this;
		if (lowIsMinusInfinity() || low <= other.low)
			return new Intv(low, other.low);
		return mk(other.low);
	}
}