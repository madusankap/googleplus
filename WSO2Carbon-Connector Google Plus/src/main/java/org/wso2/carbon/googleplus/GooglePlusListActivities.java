package org.wso2.carbon.googleplus;

import com.google.api.services.plus.Plus;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tharindud on 8/30/14.
 */
public class GooglePlusListActivities extends AbstractConnector {
    Map<String, String> resultEnvelopeMap = new HashMap<String, String>();
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        String userid=(String)getParameter(messageContext, StringConstants.User_Id);
        String collection=(String)getParameter(messageContext, StringConstants.Collection);

        long maxResults=20;
        try {
            maxResults = GoogleplusUtil.toInteger((String) getParameter(messageContext, StringConstants.Max_Results));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        String fields=(String)getParameter(messageContext, StringConstants.Fields);
        String pagetoken=(String)getParameter(messageContext, StringConstants.Page_Token);
//check mandatory parameters userid and collection
            boolean mandatorycheck=((userid!=null||!userid.equals(""))&&(collection!=null && collection.equalsIgnoreCase("public")));
            boolean maxresultcheck=(1<=maxResults&&maxResults<=100);
            boolean fieldscheck=((fields!=null||!fields.equals("")));
            boolean pagetokencheck=((pagetoken!=null||!pagetoken.equals("")));
        /*
        * mandatory parameters only
        * userid and collection
        * */
        if(mandatorycheck) {
                     try {
             Plus plus = GoogleplusUtil.getPlusServices(messageContext);
                Plus.Activities.List listActivities = plus.activities().list(userid,collection);
             if(fieldscheck){listActivities.setFields(fields);}

            if(maxresultcheck){
                listActivities.setMaxResults(maxResults);
           }
            if(pagetokencheck){
                listActivities.setPageToken(pagetoken);
            }
          resultEnvelopeMap.put(StringConstants.ListActivity, listActivities.execute().toPrettyString());
                    messageContext.getEnvelope().detach();
                    SOAPEnvelope soapEnvelope=GoogleplusUtil.buildResultEnvelope(StringConstants.URN_LIST_ACTIVITY,StringConstants.ListActivity,resultEnvelopeMap);
                    messageContext.setEnvelope(soapEnvelope);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
