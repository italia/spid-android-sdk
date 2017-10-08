package openSAML.code;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Condition;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.OneTimeUse;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.saml2.core.impl.AuthnRequestMarshaller;
import org.opensaml.saml2.core.impl.EncryptedAssertionMarshaller;
import org.opensaml.saml2.encryption.Encrypter;
import org.opensaml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.security.MetadataCredentialResolver;
import org.opensaml.security.MetadataCredentialResolverFactory;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.EncryptionException;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This is a demo class which creates a valid SAML 2.0 Assertion.
 */
public class SPAuthnRequest
{
	
	private static XMLObjectBuilderFactory builderFactory;
 
	public SPAuthnRequest() {
		try {
			DefaultBootstrap.bootstrap();
			System.out.println("Initialized the libraries");
		
		} catch (Exception ce) {
			ce.printStackTrace();
		}
 	}
	
	public static XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException{
		
		if(builderFactory == null){
			// OpenSAML 2.3
			DefaultBootstrap.bootstrap();
			builderFactory = Configuration.getBuilderFactory();
		}
 
		return builderFactory;
	}

	public String samlWriterPoste() throws MarshallingException, ParserConfigurationException, SAXException, IOException, MetadataProviderException, SecurityException, NoSuchAlgorithmException, KeyException, EncryptionException {
		try {
    		SAMLInputContainer input = new SAMLInputContainer();
			input.strIssuer = "https://localhost";
			//input.strNameID = "Anvesha Sinha";
			//input.strNameQualifier = "openSAML";
			//input.sessionId = "abcdedf1234567";
 
			//Map<String, String> customAttributes = new HashMap<String, String>();
			//customAttributes.put("FirstName", "Anvesha");
			//customAttributes.put("LastName", "Sinha");
 
			//input.attributes = customAttributes;
 
			AuthnRequest authnRequest = SPAuthnRequest.buildDefaultAuthnRequest(input);
			AuthnRequestMarshaller marshaller = new AuthnRequestMarshaller();
			Element plaintextElement = marshaller.marshall(authnRequest);
			String originalAuthnRequestString = XMLHelper.nodeToString(plaintextElement);
			
	
			return originalAuthnRequestString;
			
//			//Do the encryption
//			X509Credential credential = getCredential("C:/Users/anveshas/FederationMetadata.xml"); 
//			System.out.println(credential.getEntityCertificate().getPublicKey());
//			
//			EncryptedAssertion encryptedAssertion = encrypt(authnRequest, credential);
//			EncryptedAssertionMarshaller marshallerEncryptedAssertion = new EncryptedAssertionMarshaller();
//			Element plaintextEncryptedAssertion = marshallerEncryptedAssertion.marshall(encryptedAssertion);
//			String encryptedAssertionString = XMLHelper.nodeToString(plaintextEncryptedAssertion);
//			System.out.println("Encrypted Assertion String: " + encryptedAssertionString);
//			
//			return encryptedAssertionString;
			
 		} 
    	finally {
    		
    	}
	}
	
	public static AuthnRequest buildDefaultAuthnRequest(SAMLInputContainer input)
	{
		try
		{

			DateTime now = new DateTime(DateTimeZone.forID("Europe/Rome"));
			System.out.println(now);
			
			// Create Issuer
			@SuppressWarnings("unchecked")
			SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) SPAuthnRequest.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
			Issuer issuer = (Issuer) issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "samlp");
			issuer.setValue(input.getStrIssuer());
			
			@SuppressWarnings("unchecked")
			SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>) SPAuthnRequest.getSAMLBuilder().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
			Conditions conditions = (Conditions) conditionsBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Conditions", "saml2");
			conditions.setNotBefore(now);
			conditions.setNotOnOrAfter(now);
			//conditions.getConditions().add(condition);
			
			@SuppressWarnings("unchecked")
			SAMLObjectBuilder<RequestedAuthnContext> requestedAuthContextBuilder = (SAMLObjectBuilder<RequestedAuthnContext>) SPAuthnRequest.getSAMLBuilder().getBuilder(RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
			RequestedAuthnContext requestAuthnContext = (RequestedAuthnContext) requestedAuthContextBuilder.buildObject();
			requestAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);
	        
			@SuppressWarnings("unchecked")
			SAMLObjectBuilder<AuthnContextClassRef> authContextClassRefBuilder = (SAMLObjectBuilder<AuthnContextClassRef>) SPAuthnRequest.getSAMLBuilder().getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
			AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) authContextClassRefBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "AuthnContextClassRef", "saml2");
			authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:SpidL1");
			
			requestAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
			
			@SuppressWarnings("unchecked")
			SAMLObjectBuilder<AuthnRequest> authRequestBuilder = (SAMLObjectBuilder<AuthnRequest>) SPAuthnRequest.getSAMLBuilder().getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
			AuthnRequest authnRequest = (AuthnRequest) authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest", "samlp");
			authnRequest.setID("_281a887e-90c5-4498-ae40-3606d30a2d8e");
			authnRequest.setAssertionConsumerServiceIndex(3);
			authnRequest.setAttributeConsumingServiceIndex(1);
			authnRequest.setIssueInstant(now);
			authnRequest.setVersion(SAMLVersion.VERSION_20);
			authnRequest.setIssuer(issuer);
			authnRequest.setConditions(conditions);
			authnRequest.setRequestedAuthnContext(requestAuthnContext);
			
			return authnRequest;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	 
	public static X509Credential getCredential(String federationMetadata) throws ParserConfigurationException, SAXException, IOException, MetadataProviderException, SecurityException {
		
		//The Meta data resolver helps to extract public credentials from meta data

		//First we create a meta data provider.
		InputStream metadataInputStream = new FileInputStream(federationMetadata);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

		Document metadataDocument = docBuilder.parse(metadataInputStream);
		Element metadataRoot = metadataDocument.getDocumentElement();
		metadataInputStream.close();
		
		DOMMetadataProvider idpMetadataProvider = new DOMMetadataProvider(metadataRoot);
		idpMetadataProvider.setRequireValidMetadata(true);
		idpMetadataProvider.setParserPool(new BasicParserPool());
		idpMetadataProvider.initialize();

		//Resolve the credential
		MetadataCredentialResolverFactory credentialResolverFactory = MetadataCredentialResolverFactory.getFactory();
		 
		MetadataCredentialResolver credentialResolver = credentialResolverFactory.getInstance(idpMetadataProvider);
		 
		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
		criteriaSet.add(new EntityIDCriteria("http://colo-pm2.adx.isi.edu/adfs/services/trust"));
		criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
 
		X509Credential credential = (X509Credential)credentialResolver.resolveSingle(criteriaSet);
		return credential;
	}
	
	public EncryptedAssertion encrypt(Assertion assertion, X509Credential receiverCredential) throws EncryptionException, NoSuchAlgorithmException, KeyException {
		    
			Credential symmetricCredential = SecurityHelper.getSimpleCredential(SecurityHelper.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
	   
			//The EncryptionParameters contain a reference to the shared key and the algorithm to use
		    EncryptionParameters encParams = new EncryptionParameters();
		    encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
		    encParams.setEncryptionCredential(symmetricCredential);
		    
		    //The KeyEncryptionParameters contain the receiver's public key
		    KeyEncryptionParameters kek = new KeyEncryptionParameters();
		    kek.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
		    kek.setEncryptionCredential(receiverCredential);
		       
		    Encrypter encrypter = new Encrypter(encParams, kek);
		    encrypter.setKeyPlacement(KeyPlacement.INLINE);
		       
		    EncryptedAssertion encrypted = encrypter.encrypt(assertion);
		    
		    return encrypted;
//		    response.getEncryptedAssertions().add(encrypted);
//		       
//		    response.getAssertions().clear();
//		    System.out.println(response);
		  }
	
	/*	public void doAuthenticationRedirect(final HttpServletResponse response, final HttpSession currentSession, final String gotoURL, final SAMLMetaData metaData) throws IllegalArgumentException, SecurityException, IllegalAccessException {
	  AuthnRequest authnRequest = generateAuthnRequest(metaData);
	 
	  SAMLUtil.logSAMLObject(authnRequest);
	 
	  // Save the request ID to session for future validation
	  currentSession.setAttribute("AuthnRequestID", authnRequest.getID());
	  currentSession.setAttribute("goto", gotoURL);
	 
	  HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(response, true);
	  BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject>();  
	  context.setPeerEntityEndpoint(getEndpointFromMetaData());
	  context.setOutboundSAMLMessage(authnRequest);
	  context.setOutboundSAMLMessageSigningCredential(getSigningCredential());
	  context.setOutboundMessageTransport(responseAdapter);
	 
	  HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
	 
	  try {
	   encoder.encode(context);
	  } catch (MessageEncodingException e) {
	  
	  }
	 }
	 
private AuthnRequest generateAuthnRequest(final SAMLMetaData metaData) throws IllegalArgumentException, SecurityException, IllegalAccessException {
	 
	  AuthnRequest authnRequest = SAMLUtil.buildSAMLObjectWithDefaultName(AuthnRequest.class);
	 
	  authnRequest.setForceAuthn(true);
	  authnRequest.setIsPassive(false);
	  authnRequest.setIssueInstant(new DateTime());
	  for (SingleSignOnService sss : metaData.getIdpEntityDescriptor().getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getSingleSignOnServices()) {
	   if (sss.getBinding().equals(SAMLConstants.SAML2_REDIRECT_BINDING_URI)) {
	    authnRequest.setDestination(sss.getLocation());
	   }
	  }
	  authnRequest.setProtocolBinding(SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
	 
	  String deployURL = getDeployURL();
	  if (deployURL.charAt(deployURL.length() - 1) == '/') {
	   deployURL = deployURL.substring(0, deployURL.length() - 1);
	  }
	  authnRequest.setAssertionConsumerServiceURL(deployURL + SAMLMetaData.CONSUMER_PATH);
	 
	  authnRequest.setID(SAMLUtil.getSecureRandomIdentifier());
	 
	  Issuer issuer = SAMLUtil.buildSAMLObjectWithDefaultName(Issuer.class);
	  issuer.setValue(getSPEntityId());
	  authnRequest.setIssuer(issuer);
	 
	  NameIDPolicy nameIDPolicy = SAMLUtil.buildSAMLObjectWithDefaultName(NameIDPolicy.class);
	  nameIDPolicy.setSPNameQualifier(getSPEntityId());
	  nameIDPolicy.setAllowCreate(true);
	  nameIDPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
	 
	  authnRequest.setNameIDPolicy(nameIDPolicy);
	 
	  RequestedAuthnContext requestedAuthnContext = SAMLUtil.buildSAMLObjectWithDefaultName(RequestedAuthnContext.class);
	  requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);
	 
	  AuthnContextClassRef authnContextClassRef = SAMLUtil.buildSAMLObjectWithDefaultName(AuthnContextClassRef.class);
	  authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
	 
	  requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
	  authnRequest.setRequestedAuthnContext(requestedAuthnContext);
	 
	  return authnRequest;
	 }
*/
	
	/**
	 * Builds a SAML Attribute of type String
	 * @param name
	 * @param value
	 * @param builderFactory
	 * @return
	 * @throws ConfigurationException
	 */
	/*public static Attribute buildStringAttribute(String name, String value, XMLObjectBuilderFactory builderFactory) throws ConfigurationException
	{
		@SuppressWarnings("unchecked")
		SAMLObjectBuilder<Attribute> attrBuilder = (SAMLObjectBuilder<Attribute>) getSAMLBuilder().getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
		 Attribute attrFirstName = (Attribute) attrBuilder.buildObject();
		 attrFirstName.setName(name);
 
		 // Set custom Attributes
		 @SuppressWarnings("unchecked")
		 XMLObjectBuilder<XSString> stringBuilder = (XMLObjectBuilder<XSString>) getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
		 XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
		 attrValueFirstName.setValue(value);
 
		 attrFirstName.getAttributeValues().add(attrValueFirstName);
		return attrFirstName;
	}*/
 
	/**
	 * Helper method which includes some basic SAML fields which are part of almost every SAML Assertion.
	 *
	 * @param input
	 * @return
	 */
	/*public static Assertion buildDefaultAssertion(SAMLInputContainer input)
	{
		try
		{
	         // Create the NameIdentifier
			@SuppressWarnings("unchecked")
	         SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>) SPAuthnRequest.getSAMLBuilder().getBuilder(NameID.DEFAULT_ELEMENT_NAME);
	         NameID nameId = (NameID) nameIdBuilder.buildObject();
	         nameId.setValue(input.getStrNameID());
	         nameId.setNameQualifier(input.getStrNameQualifier());
	         nameId.setFormat(NameID.UNSPECIFIED);
 
	         // Create the SubjectConfirmation
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<SubjectConfirmationData> confirmationMethodBuilder = (SAMLObjectBuilder<SubjectConfirmationData>)  SPAuthnRequest.getSAMLBuilder().getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
	         SubjectConfirmationData confirmationMethod = (SubjectConfirmationData) confirmationMethodBuilder.buildObject();
	         DateTime now = new DateTime();
	         confirmationMethod.setNotBefore(now);
	         confirmationMethod.setNotOnOrAfter(now.plusMinutes(2));
 
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<SubjectConfirmation> subjectConfirmationBuilder = (SAMLObjectBuilder<SubjectConfirmation>) SPAuthnRequest.getSAMLBuilder().getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
	         SubjectConfirmation subjectConfirmation = (SubjectConfirmation) subjectConfirmationBuilder.buildObject();
	         subjectConfirmation.setSubjectConfirmationData(confirmationMethod);
 
	         // Create the Subject
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<Subject> subjectBuilder = (SAMLObjectBuilder<Subject>) SPAuthnRequest.getSAMLBuilder().getBuilder(Subject.DEFAULT_ELEMENT_NAME);
	         Subject subject = (Subject) subjectBuilder.buildObject();
 
	         subject.setNameID(nameId);
	         subject.getSubjectConfirmations().add(subjectConfirmation);
 
	         // Create Authentication Statement
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<AuthnStatement> authStatementBuilder = (SAMLObjectBuilder<AuthnStatement>) SPAuthnRequest.getSAMLBuilder().getBuilder(AuthnStatement.DEFAULT_ELEMENT_NAME);
	         AuthnStatement authnStatement = (AuthnStatement) authStatementBuilder.buildObject();
	         //authnStatement.setSubject(subject);
	         //authnStatement.setAuthenticationMethod(strAuthMethod);
	         DateTime now2 = new DateTime();
	         authnStatement.setAuthnInstant(now2);
	         authnStatement.setSessionIndex(input.getSessionId());
	         authnStatement.setSessionNotOnOrAfter(now2.plus(input.getMaxSessionTimeoutInMinutes()));

	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<AuthnContext> authContextBuilder = (SAMLObjectBuilder<AuthnContext>) SPAuthnRequest.getSAMLBuilder().getBuilder(AuthnContext.DEFAULT_ELEMENT_NAME);
	         AuthnContext authnContext = (AuthnContext) authContextBuilder.buildObject();
 
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<AuthnContextClassRef> authContextClassRefBuilder = (SAMLObjectBuilder<AuthnContextClassRef>) SPAuthnRequest.getSAMLBuilder().getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
	         AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) authContextClassRefBuilder.buildObject();
	         authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:Password"); // TODO not sure exactly about this
 
			authnContext.setAuthnContextClassRef(authnContextClassRef);
	        authnStatement.setAuthnContext(authnContext);
 
	        // Builder Attributes
	        @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<AttributeStatement> attrStatementBuilder = (SAMLObjectBuilder<AttributeStatement>) SPAuthnRequest.getSAMLBuilder().getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
	         AttributeStatement attrStatement = (AttributeStatement) attrStatementBuilder.buildObject();
 
	      // Create the attribute statement
	         @SuppressWarnings("unchecked")
	         Map<String, String> attributes = (Map<String, String>) input.getAttributes();
	         if(attributes != null){
	        	 Set<String> keySet = attributes.keySet();
	        	 for (String key : keySet)
				{
	        		 Attribute attrFirstName = buildStringAttribute(key, attributes.get(key), getSAMLBuilder());
	        		 attrStatement.getAttributes().add(attrFirstName);
				}
	         }
 
	         // Create the do-not-cache condition
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<OneTimeUse> doNotCacheConditionBuilder = (SAMLObjectBuilder<OneTimeUse>) SPAuthnRequest.getSAMLBuilder().getBuilder(OneTimeUse.DEFAULT_ELEMENT_NAME);
	         Condition condition = (Condition) doNotCacheConditionBuilder.buildObject();
 
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>) SPAuthnRequest.getSAMLBuilder().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
	         Conditions conditions = (Conditions) conditionsBuilder.buildObject();
	         conditions.getConditions().add(condition);
 
	         // Create Issuer
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) SPAuthnRequest.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
	         Issuer issuer = (Issuer) issuerBuilder.buildObject();
	         issuer.setValue(input.getStrIssuer());
 
	         // Create the assertion
	         @SuppressWarnings("unchecked")
	         SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>) SPAuthnRequest.getSAMLBuilder().getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
	         Assertion assertion = (Assertion) assertionBuilder.buildObject();
	         assertion.setIssuer(issuer);
	         assertion.setIssueInstant(now);
	         assertion.setVersion(SAMLVersion.VERSION_20);
 
	         assertion.getAuthnStatements().add(authnStatement);
	         assertion.getAttributeStatements().add(attrStatement);
	         assertion.setConditions(conditions);
 
			return assertion;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	*/
		
	public static class SAMLInputContainer
	{

		private String strIssuer;
		private String strNameID;
		private String strNameQualifier;
		private String sessionId;
		private int maxSessionTimeoutInMinutes = 15; // default is 15 minutes

		private Map attributes;

		/**
		 * Returns the strIssuer.
		 *
		 * @return the strIssuer
		 */
		public String getStrIssuer()
		{
			return strIssuer;
		}

		/**
		 * Sets the strIssuer.
		 *
		 * @param strIssuer
		 *            the strIssuer to set
		 */
		public void setStrIssuer(String strIssuer)
		{
			this.strIssuer = strIssuer;
		}

		/**
		 * Returns the strNameID.
		 *
		 * @return the strNameID
		 */
		public String getStrNameID()
		{
			return strNameID;
		}

		/**
		 * Sets the strNameID.
		 *
		 * @param strNameID
		 *            the strNameID to set
		 */
		public void setStrNameID(String strNameID)
		{
			this.strNameID = strNameID;
		}

		/**
		 * Returns the strNameQualifier.
		 *
		 * @return the strNameQualifier
		 */
		public String getStrNameQualifier()
		{
			return strNameQualifier;
		}

		/**
		 * Sets the strNameQualifier.
		 *
		 * @param strNameQualifier
		 *            the strNameQualifier to set
		 */
		public void setStrNameQualifier(String strNameQualifier)
		{
			this.strNameQualifier = strNameQualifier;
		}

		/**
		 * Sets the attributes.
		 *
		 * @param attributes
		 *            the attributes to set
		 */
		public void setAttributes(Map attributes)
		{
			this.attributes = attributes;
		}

		/**
		 * Returns the attributes.
		 *
		 * @return the attributes
		 */
		public Map getAttributes()
		{
			return attributes;
		}

		/**
		 * Sets the sessionId.
		 * @param sessionId the sessionId to set
		 */
		public void setSessionId(String sessionId)
		{
			this.sessionId = sessionId;
		}

		/**
		 * Returns the sessionId.
		 * @return the sessionId
		 */
		public String getSessionId()
		{
			return sessionId;
		}

		/**
		 * Sets the maxSessionTimeoutInMinutes.
		 * @param maxSessionTimeoutInMinutes the maxSessionTimeoutInMinutes to set
		 */
		public void setMaxSessionTimeoutInMinutes(int maxSessionTimeoutInMinutes)
		{
			this.maxSessionTimeoutInMinutes = maxSessionTimeoutInMinutes;
		}

		/**
		 * Returns the maxSessionTimeoutInMinutes.
		 * @return the maxSessionTimeoutInMinutes
		 */
		public int getMaxSessionTimeoutInMinutes()
		{
			return maxSessionTimeoutInMinutes;
		}

	}

}