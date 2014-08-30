package org.wso2.carbon.googleplus;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.ActivityFeed;
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
        int maxResults=0;
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
            ActivityFeed feed=null;
            Plus.Activities.List listActivities=null;
            try {
             Plus plus = GoogleplusUtil.getPlusServices(messageContext);
             listActivities = plus.activities().list(userid,collection);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            if(!maxresultcheck&&!fieldscheck&&!pagetokencheck) {
                try {
                    feed = Listactivities(listActivities);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(maxresultcheck&&!fieldscheck&&!pagetokencheck) {
                try {
                    feed = Listactivities(listActivities,maxResults);
                } catch (IOException ex) {
                }

            }
            if(!maxresultcheck&&fieldscheck&&!pagetokencheck) {
                try {
                   feed=Listactivities(listActivities,fields);
                } catch (IOException e) {
                    e.printStackTrace();
                           }
            if(!maxresultcheck&&!fieldscheck&&pagetokencheck) {
                try {
                    feed=Listactivities(listActivities,"Token"+pagetoken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!maxresultcheck&&fieldscheck&&pagetokencheck) {
                try {
                  feed=  Listactivities(listActivities,fields,pagetoken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(maxresultcheck&&fieldscheck&&pagetokencheck) {
                try {
              feed= Listactivities(listActivities,maxResults,fields,pagetoken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(mandatorycheck&&maxresultcheck&&fieldscheck&&!pagetokencheck) {
                try {
                  feed=  Listactivities(listActivities,maxResults,fields);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(mandatorycheck&&maxresultcheck&&!fieldscheck&&pagetokencheck) {
                try {
                  feed= Listactivities(listActivities,maxResults,"Token"+pagetoken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

                try {
                    resultEnvelopeMap.put(StringConstants.ListActivity, feed.toPrettyString());
                    messageContext.getEnvelope().detach();
                    SOAPEnvelope soapEnvelope=GoogleplusUtil.buildResultEnvelope(StringConstants.URN_LIST_ACTIVITY,StringConstants.ListActivity,resultEnvelopeMap);
                    messageContext.setEnvelope(soapEnvelope);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }}

    private ActivityFeed Listactivities(Plus.Activities.List listActivities , int maxResults, String fields, String pagetoken) throws IOException {
        long convert=maxResults;
        listActivities.setMaxResults(convert);
        listActivities.setFields(fields);
        listActivities.setPageToken(pagetoken);
       return listActivities.execute();
    }

    private ActivityFeed Listactivities(Plus.Activities.List listActivities , String fields, String pagetoken) throws IOException {
        listActivities.setFields(fields);
        listActivities.setPageToken(pagetoken);
       return  listActivities.execute();
    }

    private ActivityFeed Listactivities(Plus.Activities.List listActivities, int maxResults, String fields) throws IOException {
        long convert=maxResults;
        listActivities.setMaxResults(convert);
        if(fields.contains("Token")){
            listActivities.setPageToken(fields.substring(4));
           return listActivities.execute();
        }else{
            listActivities.setFields(fields);
            return listActivities.execute();
        }
            }

    private ActivityFeed Listactivities(Plus.Activities.List listActivities, String fields) throws IOException {
        if(fields.contains("Token")){
            listActivities.setPageToken(fields.substring(4));
            return  listActivities.execute();
           }else{
           listActivities.setFields(fields);
            return listActivities.execute();
        }

    }

    private ActivityFeed Listactivities(Plus.Activities.List listActivities, int maxResults) throws IOException {
        long convert=maxResults;
        listActivities.setMaxResults(convert);
        return listActivities.execute();

    }

    private ActivityFeed Listactivities(Plus.Activities.List listActivities) throws IOException {
        return listActivities.execute();

    }
}
