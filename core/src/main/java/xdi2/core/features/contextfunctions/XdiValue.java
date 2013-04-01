package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI value (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiValue extends XdiSubGraph {

	private static final long serialVersionUID = -5769813522592588864L;

	protected XdiValue(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI value.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI value.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValueArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI value bound to a given context node.
	 * @param contextNode The context node that is an XDI value.
	 * @return The XDI value.
	 */
	public static XdiValue fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiValue(contextNode);
	}

	/*
	 * Instance methods
	 */

	/*
	 * Methods for XDI value XRIs
	 */

	public static XDI3SubSegment createValueArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ATTRIBUTE_VALUE.charAt(0) + arcXri + XDI3Constants.CF_ATTRIBUTE_VALUE.charAt(1));
	}

	/**
	 * Checks if a given XRI is an XDI value XRI.
	 * @param arcXri An XDI value XRI.
	 * @return True, if the XRI is an XDI value XRI.
	 */
	public static boolean isValueArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		
		if (arcXri.hasCs()) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ATTRIBUTE_VALUE.equals(arcXri.getXRef().getCf())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiValueIterator extends NotNullIterator<XdiValue> {

		public MappingContextNodeXdiValueIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiValue> (contextNodes) {

				@Override
				public XdiValue map(ContextNode contextNode) {

					return XdiValue.fromContextNode(contextNode);
				}
			});
		}
	}
}
