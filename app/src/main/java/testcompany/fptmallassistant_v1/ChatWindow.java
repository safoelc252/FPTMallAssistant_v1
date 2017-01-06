package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import java.util.List;


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

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // initialize conversation here
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11);
        service.setUsernameAndPassword("556c5698-f097-4e49-9dd4-c0f08aa5133e", "F8yYOcPWgtpC");


        String workspaceId = "a1681b35-87d9-4479-845e-da2c6cc11b63";
        String input = "Reserve.";
        Log.d("ChatWindow", input);
        // call Conversation Service with the input

        MessageRequest newMessage = new MessageRequest.Builder().inputText(input).build();
        MessageResponse response = service.message(workspaceId, newMessage).execute();
        Log.d("ChatWindow", response.getText().toString());

        input = "Reserve.";

        SystemClock.sleep(200);

        Log.d("ChatWindow", input);
        MessageRequest newMessage1 = new MessageRequest.Builder().inputText(input).build();
        MessageResponse response1 = service.message(workspaceId, newMessage1).execute();
        Log.d("ChatWindow",response1.toString());
        //Log.d("ChatWindow",response.getText().toString());

        /****** ALTERNATE APPROACH - USES ASYNC CALL
        MessageRequest newMessage = new MessageRequest.Builder().inputText(input).build();
        service.message(workspaceId, newMessage).enqueue(new ServiceCallback<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                //System.out.println(response);
                Log.d("ChatWindow",response.toString());
                ((TextView) findViewById(R.id.textView)).setText(response.toString());
            }

            @Override
            public void onFailure(Exception e) { }
        });
        ************************************/
    }
}
