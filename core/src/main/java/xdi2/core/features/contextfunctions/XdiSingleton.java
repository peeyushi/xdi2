package xdi2.core.features.contextfunctions;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiSingleton extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -1976646316893343570L;

	protected XdiSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntitySingleton.isValid(contextNode) || 
				XdiAttributeSingleton.isValid(contextNode) ||
				XdiPersonalSingleton.isValid(contextNode) ||
				XdiOrganizationalSingleton.isValid(contextNode) ||
				XdiRelativeSingleton.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI singleton.
	 * @return The XDI singleton.
	 */
	public static XdiSingleton fromContextNode(ContextNode contextNode) {

		XdiSingleton xdiSingleton;

		if ((xdiSingleton = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiPersonalSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiOrganizationalSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiRelativeSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntitySingleton.isValidArcXri(arcXri) || 
				XdiAttributeSingleton.isValidArcXri(arcXri) ||
				XdiPersonalSingleton.isValidArcXri(arcXri) ||
				XdiOrganizationalSingleton.isValidArcXri(arcXri) ||
				XdiRelativeSingleton.isValidArcXri(arcXri);
	}
}
