package it.unive.lisa.symbolic.types;

import it.unive.lisa.cfg.type.BooleanType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * An internal implementation of the {@link BooleanType} interface that can be
 * used by domains that need a concrete instance of that interface.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class BoolType implements BooleanType {

	/**
	 * The singleton instance of this class.
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
