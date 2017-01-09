package testcompany.fptmallassistant_v1;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by Cleofas.villarin on 1/9/2017.
 */
public class FPTMallAssistantConversation {

    // Watson credentials
    private String username = "556c5698-f097-4e49-9dd4-c0f08aa5133e";
    private String password = "F8yYOcPWgtpC";

    // Workspace ID of FPT Mall Assistant
    private String workspaceID = "a1681b35-87d9-4479-845e-da2c6cc11b63";

    ConversationService service;
    Map<String, Object> convo_context;

    public FPTMallAssistantConversation() {
        // initialize conversation here
        service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        service.setUsernameAndPassword(username, password);
    }

    public List<String> initHandler()
    {
        // send convo start to watson, or simply pass an empty string request. return the message response.
        MessageRequest newMessage = new MessageRequest.Builder().inputText("").build();
        MessageResponse svc_response = service.message(workspaceID, newMessage).execute();
        convo_context = svc_response.getContext();
        return svc_response.getText();
    }

    public List<String> sendRequest(String svc_request)
    {
        // send a message request to watson. return a list of responses
        MessageRequest newMessage = new MessageRequest.Builder().inputText(svc_request).context(convo_context).build();
        MessageResponse svc_response = service.message(workspaceID, newMessage).execute();
        convo_context = svc_response.getContext();
        return svc_response.getText();
    }
}
