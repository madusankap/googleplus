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
      /*  <property name="uri.var.useServiceAccount" expression="$func:useServiceAccount" />
        <property name="uri.var.serviceAccountEmail" expression="$func:serviceAccountEmail" />
        <property name="uri.var.certificatePassword" expression="$func:certificatePassword" />
        <property name="uri.var.clientId" expression="$func:clientId" />
        <property name="uri.var.clientSecret" expression="$func:clientSecret" />
        <property name="uri.var.accessToken" expression="$func:accessToken" />
        <property name="uri.var.refreshToken" expression="$func:refreshToken" />*/
        messageContext.setProperty(GoogleplusUtil.StringConstants.USE_SERVICE_ACCOUNT, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.USE_SERVICE_ACCOUNT));
        messageContext.setProperty(GoogleplusUtil.StringConstants.SERVICE_ACCOUNT_EMAIL, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.SERVICE_ACCOUNT_EMAIL));
        messageContext.setProperty(GoogleplusUtil.StringConstants.CERTIFICATE_PASSWORD, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.CERTIFICATE_PASSWORD));
        messageContext.setProperty(GoogleplusUtil.StringConstants.CLIENT_ID, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.CLIENT_ID));
        messageContext.setProperty(GoogleplusUtil.StringConstants.CLIENT_SECRET, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.CLIENT_SECRET));
        messageContext.setProperty(GoogleplusUtil.StringConstants.ACCESS_TOKEN, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.ACCESS_TOKEN));
        messageContext.setProperty(GoogleplusUtil.StringConstants.REFRESH_TOKEN, ConnectorUtils.lookupTemplateParamater(messageContext,GoogleplusUtil.StringConstants.REFRESH_TOKEN));
    }
}
