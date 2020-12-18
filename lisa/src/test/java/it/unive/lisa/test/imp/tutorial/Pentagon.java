package it.unive.lisa.test.imp.tutorial;

import it.unive.lisa.analysis.ValueCartesianProduct;
import it.unive.lisa.analysis.nonrelational.ValueEnvironment;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.test.imp.tutorial.interval.Interval;
import it.unive.lisa.test.imp.tutorial.upperbounds.UpperBoundsEnvironment;

public class Pentagon extends ValueCartesianProduct<ValueEnvironment<Interval>, UpperBoundsEnvironment> {

	public Pentagon() {
		super(new ValueEnvironment<Interval>(new Interval()), new UpperBoundsEnvironment());
	}

	@Override
	public String representation() {
		String result = "";
		for (Identifier x : left.getKeys())
			result += x + ": (" + left.getState(x) + ", " + right.getState(x) + ")";
		return result;
	}
}
