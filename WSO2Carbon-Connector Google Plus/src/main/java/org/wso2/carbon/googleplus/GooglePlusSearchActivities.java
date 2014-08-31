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
 * Created by tharindud on 8/31/14.
 */
public class GooglePlusSearchActivities extends AbstractConnector {
    private Map<String,String> resultEnvelopeMap =  new HashMap<String, String>();

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        String query=(String)getParameter(messageContext, StringConstants.Query);
        String language=(String)getParameter(messageContext, StringConstants.Language);
        String orderBy=(String)getParameter(messageContext, StringConstants.ORDER_BY);

        long maxResults=10;
        try {
            maxResults = GoogleplusUtil.toInteger((String) getParameter(messageContext, StringConstants.Max_Results));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        String fields=(String)getParameter(messageContext, StringConstants.Fields);
        String pagetoken=(String)getParameter(messageContext, StringConstants.Page_Token);
//check mandatory parameters userid and collection
        boolean Mandatory_Check=(query!=null||!query.equals(""));
        boolean Language_Check=(language!=null||!language.equals(""));
        boolean Maxresult_Check=(1<=maxResults&&maxResults<=100);
        boolean Fields_Check=((fields!=null||!fields.equals("")));
        boolean Pagetoken_Check=((pagetoken!=null||!pagetoken.equals("")));
        boolean Orderby_Check=((fields!=null&&(fields.equals("best")||fields.equals("recent"))));

        if(Mandatory_Check){
            try {
                Plus plus = GoogleplusUtil.getPlusServices(messageContext);
                Plus.Activities.Search searchActivities = plus.activities().search(query);
            if(Language_Check){searchActivities.setLanguage(language);}
                if(Maxresult_Check){searchActivities.setMaxResults(maxResults);}
                if(Fields_Check){searchActivities.setFields(fields);}
                if(Pagetoken_Check){searchActivities.setPageToken(pagetoken);}
                if(Orderby_Check){searchActivities.setOrderBy(orderBy);}
                resultEnvelopeMap.put(StringConstants.SearchActivity,searchActivities.execute().toPrettyString());
                messageContext.getEnvelope().detach();
                SOAPEnvelope soapEnvelope=GoogleplusUtil.buildResultEnvelope(StringConstants.URN_SEARCH_ACTIVITY,StringConstants.SearchActivity,resultEnvelopeMap);
                messageContext.setEnvelope(soapEnvelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

        }


    }
 }
