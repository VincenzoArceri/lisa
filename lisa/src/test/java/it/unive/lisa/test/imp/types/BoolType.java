package it.unive.lisa.test.imp.types;

import it.unive.lisa.cfg.type.BooleanType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * The {@link it.unive.lisa.cfg.type.BooleanType} of the IMP language. The only
 * singleton instance of this class can be retrieved trough field
 * {@link #INSTANCE}. Instances of this class are equal to all other classes
 * that implement the {@link it.unive.lisa.cfg.type.BooleanType} interface.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class BoolType implements BooleanType {

	/**
	 * The unique singleton instance of this type.
	 */
	public static final BoolType INSTANCE = new BoolType();

	private BoolType() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other.isBooleanType() || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other.isBooleanType() ? this : Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return "bool";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof BooleanType;
	}

	@Override
	public int hashCode() {
		return BooleanType.class.getName().hashCode();
	}
}
