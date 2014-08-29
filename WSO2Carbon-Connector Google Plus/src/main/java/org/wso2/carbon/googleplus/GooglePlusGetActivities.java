package org.wso2.carbon.googleplus;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

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
                                Plus plus= GoogleplusUtil.getPlusServices(messageContext);
                                Activity activity = plus.activities().get(activityid).setPrettyPrint(true).execute();
                                System.out.println("else\n"+activity.toString());
                                resultEnvelopeMap.put(GoogleplusUtil.StringConstants.Activity,activity.toString());
                                messageContext.setEnvelope(GoogleplusUtil.buildResultEnvelope(GoogleplusUtil.StringConstants.URN_GET_ACTIVITY,GoogleplusUtil.StringConstants.GET_Activity,resultEnvelopeMap));
                            }else{

                                Plus plus= GoogleplusUtil.getPlusServices(messageContext);
                                Activity activity = plus.activities().get(activityid).setPrettyPrint(true).setFields(fields).execute();
                                System.out.println("if:\n"+activity.toString());
                                resultEnvelopeMap.put(GoogleplusUtil.StringConstants.Activity,activity.toString());
                                messageContext.setEnvelope(GoogleplusUtil.buildResultEnvelope(GoogleplusUtil.StringConstants.URN_GET_ACTIVITY,GoogleplusUtil.StringConstants.GET_Activity,resultEnvelopeMap));

                            }

            }catch (Exception ex){
System.out.println(ex.getMessage());
                         }
        }

    }
}
