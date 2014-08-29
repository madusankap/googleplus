package org.wso2.carbon.googleplus;
/**
 * Created by tharindud on 8/28/14.
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.transport.TransportUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class GoogleplusUtil {
    public static boolean toBoolean(final String value) throws ValidationException {

        if (!value.equalsIgnoreCase("TRUE") && !value.equalsIgnoreCase("FALSE")) {
            throw new ValidationException("Invalid value for boolean");
        }

        return Boolean.parseBoolean(value);

    }
    public static Plus getPlusServices(final MessageContext messageContext) throws IOException, GeneralSecurityException {

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        String certificatePassword = (String) messageContext.getProperty(StringConstants.CERTIFICATE_PASSWORD);
        if (certificatePassword == null || certificatePassword.isEmpty()) {
            certificatePassword = "notasecret";
        }

        String authType = (String) (messageContext.getProperty(GoogleplusUtil.StringConstants.USE_SERVICE_ACCOUNT));
        if (authType == null || authType.isEmpty()) {
            throw new GeneralSecurityException("Need to specify 'useServiceAccount' parameter");
        }
        if (!authType.equalsIgnoreCase("TRUE") && !authType.equalsIgnoreCase("FALSE")) {
            throw new GeneralSecurityException("'useServiceAccount' should be a boolean value");
        }
        boolean useServiceAccount =
                Boolean.parseBoolean((String) messageContext
                        .getProperty(GoogleplusUtil.StringConstants.USE_SERVICE_ACCOUNT));
        System.out.println("Use Service Account:"+useServiceAccount);

        GoogleCredential credential = null;
        if (useServiceAccount) {
            credential =
                    new GoogleCredential.Builder()
                            .setTransport(httpTransport)
                            .setJsonFactory(jsonFactory)
                            .setServiceAccountId(
                                    (String) messageContext
                                            .getProperty(GoogleplusUtil.StringConstants.SERVICE_ACCOUNT_EMAIL))
                            .setServiceAccountScopes(Collections.singleton(PlusScopes.PLUS_ME))
                            .setServiceAccountPrivateKey(
                                    extractPrivatekeyFromAttachment(messageContext, certificatePassword)).build();

        } else {
            credential =
                    new GoogleCredential.Builder()
                            .setTransport(httpTransport)
                            .setJsonFactory(jsonFactory)
                            .setClientSecrets(
                                    (String) messageContext.getProperty(GoogleplusUtil.StringConstants.CLIENT_ID),
                                    (String) messageContext.getProperty(GoogleplusUtil.StringConstants.CLIENT_SECRET))
                            .build();

        }
return new Plus.Builder(httpTransport, jsonFactory, credential).build();
    }

    /**
     * This method can extract private key from PKCS12 format security certificate First it will remove
     * certificate from soap attachment.
     * @param messageContext Synapse Message Context
     * @param certificatePassword this is password of certificate which offer when google provide private key
     *        to save
     * @return java.security.Privatekey The privatekey to use for authentication.
     * @throws GeneralSecurityException If a GeneralSecurity error occurs
     * @throws IOException IOException If an error occurs while reading stream
     */
    private static PrivateKey extractPrivatekeyFromAttachment(final MessageContext messageContext,
                                                              final String certificatePassword) throws GeneralSecurityException, IOException {

        char[] passwordChar = certificatePassword.toCharArray();

        org.apache.axis2.context.MessageContext axis2mc =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        DataHandler dataHandler = axis2mc.getAttachment("certificate");

        if (dataHandler == null) {
            throw new GeneralSecurityException(
                    "No certificate found - make sure you have set contentID as 'certificate'");
        }

        InputStream certificateInputStream = dataHandler.getInputStream();

        KeyStore ks = java.security.KeyStore.getInstance("PKCS12");

        ks.load(certificateInputStream, passwordChar);

        String alias = ks.aliases().nextElement();
        if (alias != null && !alias.isEmpty()) {

            if (ks.isKeyEntry(alias)) {
                return (PrivateKey) ks.getKey(alias, passwordChar);
            } else {
                throw new GeneralSecurityException(
                        "Key alias entry is not a valid key entry to extract the private key");
            }
        } else {
            throw new GeneralSecurityException("could not find alias");
        }
    }
    public static SOAPEnvelope buildResultEnvelope(final String namespace, final String resultTagName,
                                                   final Map<String, String> elements) throws IOException {

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(namespace, "urn");
        OMElement resultTag = factory.createOMElement(resultTagName, ns);

        if (elements != null) {
            Iterator<Map.Entry<String, String>> elementIterator = elements.entrySet().iterator();
            while (elementIterator.hasNext()) {
                Map.Entry<String, String> element = elementIterator.next();
                OMElement jsonObject =
                        JsonUtil.toXml(new ByteArrayInputStream(element.getValue().getBytes(Charset.defaultCharset())),
                                false);
                Iterator<?> jsonChildrenIterator = jsonObject.getChildElements();
                while (jsonChildrenIterator.hasNext()) {
                    resultTag.addChild((OMElement) jsonChildrenIterator.next());
                }
            }
        }

        return  TransportUtils.createSOAPEnvelope(resultTag);
    }
    public static final class StringConstants {
        /**
         * Specific to authentication Represent the useServiceAccount.
         */
        public static final String USE_SERVICE_ACCOUNT = "useServiceAccount";

        /**
         * Certificate password.
         */
        public static final String CERTIFICATE_PASSWORD = "certificatePassword";

        /**
         * Represent the serviceAccountEmail.
         */
        public static final String SERVICE_ACCOUNT_EMAIL = "serviceAccountEmail";

               /**
         * Represent the clientId.
         */
        public static final String CLIENT_ID = "clientId";

        /**
         * Represent the clientSecret.
         */
        public static final String CLIENT_SECRET = "clientSecret";

        /**
         * Represent the accessToken.
         */
        public static final String ACCESS_TOKEN = "accessToken";

        /**
         * Represent the refreshToken.
         */
        public static final String REFRESH_TOKEN = "refreshToken";

        public static final String Activity_Id="activityid";
        public static final String Fields="fields";
        public static final String Activity="activity";
        public static final String URN_GET_ACTIVITY="urn:wso2.connector.googleplus.getactivity";
        public static final String GET_Activity="activities";
    }
}
