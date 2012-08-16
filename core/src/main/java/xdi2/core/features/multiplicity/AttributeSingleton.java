package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI attribute singleton according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class AttributeSingleton extends AbstractMultiplicitySingleton {

	private static final long serialVersionUID = -5769813522592588864L;

	protected AttributeSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return 
				Multiplicity.isAttributeSingletonArcXri(contextNode.getArcXri()) ||
				(
				Multiplicity.isAttributeCollectionMemberArcXri(contextNode.getArcXri()) &&
				AttributeCollection.isValid(contextNode.getContextNode())
				);
	}

	/**
	 * Factory method that creates an XDI attribute singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute singleton.
	 * @return The XDI attribute singleton.
	 */
	public static AttributeSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new AttributeSingleton(contextNode);
	}
}