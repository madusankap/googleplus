package org.wso2.carbon.googleplus;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tharindud on 8/28/14.
 */
public class GooglePlusGetActivities extends AbstractConnector {
    Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        String activityid=(String)getParameter(messageContext, GoogleplusUtil.StringConstants.Activity_Id);
        System.out.println("activityid:"+activityid);
        String fields=(String)getParameter(messageContext,GoogleplusUtil.StringConstants.Fields);
        if(!("").equals(activityid)){
                        try {
                            if(null==fields || ("").equals(fields)){
                              getActivity(messageContext,activityid);
                            }else{
                                getActivity(messageContext,activityid,fields);
                            }

            }catch (Exception ex){
System.out.println(ex.getMessage());
                         }
        }

    }
    void getActivity(MessageContext messageContext,String activityid) throws IOException, GeneralSecurityException, XMLStreamException {
        Plus plus= GoogleplusUtil.getPlusServices(messageContext);
        Activity activity = plus.activities().get(activityid).setPrettyPrint(true).execute();
        resultEnvelopeMap.put(GoogleplusUtil.StringConstants.Activity, activity.toPrettyString());
        messageContext.getEnvelope().detach();
        SOAPEnvelope soapEnvelope=GoogleplusUtil.buildResultEnvelope(GoogleplusUtil.StringConstants.URN_GET_ACTIVITY,GoogleplusUtil.StringConstants.GET_Activity,resultEnvelopeMap);
        messageContext.setEnvelope(soapEnvelope);
    }
    void getActivity(MessageContext messageContext,String activityid,String fields) throws IOException, GeneralSecurityException, XMLStreamException {
        Plus plus= GoogleplusUtil.getPlusServices(messageContext);
        Activity activity = plus.activities().get(activityid).setPrettyPrint(true).setFields(fields).execute();
        resultEnvelopeMap.put(GoogleplusUtil.StringConstants.Activity,activity.toPrettyString());
        messageContext.getEnvelope().detach();
        SOAPEnvelope soapEnvelope=GoogleplusUtil.buildResultEnvelope(GoogleplusUtil.StringConstants.URN_GET_ACTIVITY,GoogleplusUtil.StringConstants.GET_Activity,resultEnvelopeMap);
        messageContext.setEnvelope(soapEnvelope);
    }
}
