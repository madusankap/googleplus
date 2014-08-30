package org.wso2.carbon.googleplus;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

/**
 * Created by tharindud on 8/29/14.
 */
public class GooglePLusConfig extends AbstractConnector {
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        messageContext.setProperty(StringConstants.USE_SERVICE_ACCOUNT, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.USE_SERVICE_ACCOUNT));
        messageContext.setProperty(StringConstants.SERVICE_ACCOUNT_EMAIL, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.SERVICE_ACCOUNT_EMAIL));
        messageContext.setProperty(StringConstants.CERTIFICATE_PASSWORD, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.CERTIFICATE_PASSWORD));
        messageContext.setProperty(StringConstants.CLIENT_ID, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.CLIENT_ID));
        messageContext.setProperty(StringConstants.CLIENT_SECRET, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.CLIENT_SECRET));
        messageContext.setProperty(StringConstants.ACCESS_TOKEN, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.ACCESS_TOKEN));
        messageContext.setProperty(StringConstants.REFRESH_TOKEN, ConnectorUtils.lookupTemplateParamater(messageContext,StringConstants.REFRESH_TOKEN));
    }
}
