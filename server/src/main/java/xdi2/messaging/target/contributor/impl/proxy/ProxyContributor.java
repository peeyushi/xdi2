package xdi2.messaging.target.contributor.impl.proxy;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can answer request by forwarding them to another XDI endpoint.
 */
@ContributorXri(addresses={"{{=@+*!$}}$keypair", "{{(=@+*!$)}}$keypair", "$keypair", "{{=@+*!$}}<$key>", "{{(=@+*!$)}}<$key>", "<$key>"})
public class ProxyContributor extends AbstractContributor implements Prototype<ProxyContributor> {

	private static final Logger log = LoggerFactory.getLogger(ProxyContributor.class);

	private String toAddress;
	
	public ProxyContributor() {

	}

	/*
	 * Prototype
	 */

	@Override
	public ProxyContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);
	}

	/*
	 * Contributor methods
	 */

	@Override
	public boolean executeDoOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! XRI_S_DO_KEYPAIR.equals(operation.getOperationXri()) && ! XRI_S_DO_KEY.equals(operation.getOperationXri())) return false;

		// check parameters

		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		if (! XDIDictionaryConstants.XRI_S_IS_TYPE.equals(arcXri)) return false;

		XDI3Segment dataTypeXri = relativeTargetStatement.getTargetContextNodeXri();

		String keyAlgorithm;
		Integer keyLength;

		keyAlgorithm = getKeyAlgorithm(dataTypeXri);
		if (keyAlgorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + dataTypeXri, null, executionContext);

		keyLength = getKeyLength(dataTypeXri);
		if (keyLength == null) throw new Xdi2MessagingException("Invalid key length: " + dataTypeXri, null, executionContext);

		if (log.isDebugEnabled()) log.debug("keyAlgorithm: " + keyAlgorithm + ", keyLength: " + keyLength);

		// key pair or symmetric key?

		if (XRI_S_DO_KEYPAIR.equals(operation.getOperationXri())) {

			// generate key pair

			KeyPair keyPair;

			try {

				KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(keyAlgorithm);
				keyPairGen.initialize(keyLength.intValue());
				keyPair = keyPairGen.generateKeyPair();
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while creating key pair: " + ex.getMessage(), ex, executionContext);
			}

			if (log.isDebugEnabled()) log.debug("Created key pair: " + keyPair.getClass().getSimpleName());

			// add it to the graph

			ContextNode contextNode = this.getTargetGraph().setDeepContextNode(contributorsXri);
			if (! XdiAbstractEntity.isValid(contextNode)) throw new Xdi2MessagingException("Can only create a key pair on an entity.", null, executionContext);
			XdiEntity keyPairXdiEntity = XdiAbstractEntity.fromContextNode(contextNode);
			XdiAttributeSingleton publicKeyXdiAttribute = keyPairXdiEntity.getXdiAttributeSingleton(XDI3SubSegment.create("$public"), true).getXdiAttributeSingleton(XDI3SubSegment.create("$key"), true);
			XdiAttributeSingleton privateKeyXdiAttribute = keyPairXdiEntity.getXdiAttributeSingleton(XDI3SubSegment.create("$private"), true).getXdiAttributeSingleton(XDI3SubSegment.create("$key"), true);
			publicKeyXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
			privateKeyXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
			DataTypes.setDataType(contextNode, dataTypeXri);
		} else if (XRI_S_DO_KEY.equals(operation.getOperationXri())) {

			// generate symmetric key

			SecretKey secretKey;

			try {

				KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
				keyGen.init(keyLength.intValue());
				secretKey = keyGen.generateKey(); 
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while creating symmetric key: " + ex.getMessage(), ex, executionContext);
			}

			if (log.isDebugEnabled()) log.debug("Created symmetric key: " + secretKey.getClass().getSimpleName());

			// add it to the graph

			ContextNode contextNode = this.getTargetGraph().setDeepContextNode(contributorsXri);
			if (! XdiAbstractAttribute.isValid(contextNode)) throw new Xdi2MessagingException("Can only create a symmetric key on an attribute.", null, executionContext);
			XdiAttribute symmetricKeyXdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			symmetricKeyXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(secretKey.getEncoded()));
			DataTypes.setDataType(contextNode, dataTypeXri);
		}

		// done

		return false;
	}
}
