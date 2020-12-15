package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.analysis.ValueCartesianProduct;
import it.unive.lisa.analysis.nonrelational.ValueEnvironment;
import it.unive.lisa.symbolic.value.Identifier;

public class ParityXSign extends ValueCartesianProduct<ValueEnvironment<Parity>,  ValueEnvironment<Sign>> {

	public ParityXSign() {
		this(new ValueEnvironment<Parity>(new Parity()), new ValueEnvironment<Sign>(new Sign()));
	}
	
	protected ParityXSign(ValueEnvironment<Parity> parity, ValueEnvironment<Sign> valueEnvironment) {
		super(parity, valueEnvironment);
	}
	
	@Override
	public String toString() {
		return representation();
	}
	
	@Override
	public String representation() {
		String result = "";
		for (Identifier x : left.getKeys())
			result += x + ": (" + left.getState(x) + ", " + right.getState(x) + ")";
		return result;
	}
}