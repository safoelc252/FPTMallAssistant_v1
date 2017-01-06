package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;


/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class ChatWindow extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen here, NG behavior observed if declard in manifest
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chatwindow);

        // initialize conversation here
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        service.setUsernameAndPassword("556c5698-f097-4e49-9dd4-c0f08aa5133e", "F8yYOcPWgtpC");

        /*
        MessageRequest newMessage = new MessageRequest.Builder().inputText("Hi").build();
        MessageResponse response = service.message("a1681b35-87d9-4479-845e-da2c6cc11b63", newMessage).execute();
        //((TextView)findViewById(R.id.textView)).setText("haha");
        //System.out.println(response);
        */

        String workspaceId = "a1681b35-87d9-4479-845e-da2c6cc11b63";
        String input = "Hello";
        // call Conversation Service with the input and tone-aware context
        MessageRequest newMessage = new MessageRequest.Builder().inputText(input).build();
        service.message(workspaceId, newMessage).enqueue(new ServiceCallback<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                //System.out.println(response);
                ((TextView) findViewById(R.id.textView)).setText(response.toString());
            }

            @Override
            public void onFailure(Exception e) { }
        });
    }
}
