package testcompany.fptmallassistant_v1;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by Cleofas.villarin on 1/9/2017.
 */
public class ConversationServiceUtil {
    // Workspace ID of Conversation
    private String workspaceID;

    private ConversationService service;
    private Map<String, Object> convo_context;

    public ConversationServiceUtil(String username, String password, String workspaceid) {
        // initialize conversation here
        service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        service.setUsernameAndPassword(username, password);
        workspaceID = workspaceid;
    }

    public List<String> sendRequest(String request)
    {
        // send a message request to watson. return a list of responses
        MessageRequest msgRequest = new MessageRequest.Builder().inputText(request).context(convo_context).build();
        MessageResponse msgResponse = service.message(workspaceID, msgRequest).execute();
        convo_context = msgResponse.getContext();
        return msgResponse.getText();
    }
}
