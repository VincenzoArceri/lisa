package it.unive.lisa.symbolic.value;

import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.util.collections.ExternalSet;

/**
 * An identifier of a synthetic program variable that represents a resolved
 * memory location.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class HeapIdentifier extends Identifier {

	/**
	 * Builds the identifier.
	 * 
	 * @param types the runtime types of this expression
	 * @param name  the name of the identifier
	 */
	public HeapIdentifier(ExternalSet<Type> types, String name) {
		super(types, name);
	}

	@Override
	public String toString() {
		return "hid$" + getName();
	}

}
