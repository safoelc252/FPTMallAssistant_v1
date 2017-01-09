package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class ChatWindow extends Activity {

    // Conversation history display
    private String PREFIX_ME = "ME: ";
    private String PREFIX_WATSON = "WATSON: ";

    FPTMallAssistantConversation fptMallAsst = new FPTMallAssistantConversation();
    ;
    ArrayList<String> convo_hist = new ArrayList<String>();
    ArrayAdapter<String> listview_adapter;
    List<String> responses;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen here, NG behavior observed if declared in manifest
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chatwindow);

        // DevNote: This is required to avoid crash at app launch. See link: http://bit.ly/2i74jGH
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // prepare the ListView to display data
        listview_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, convo_hist);
        ListView convohist_listview = (ListView) findViewById(R.id.convhist_listview);
        convohist_listview.setAdapter(listview_adapter);

        // Initialize convo
        responses = fptMallAsst.initHandler();
        listview_adapter.insert(PREFIX_WATSON + responses.get(0), 0);
        listview_adapter.notifyDataSetChanged();

        // Handle the button clicks
        final Button send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSendClick();
            }
        });

        // Tie keyboard with send button
        final EditText chat_input = (EditText) findViewById(R.id.chat_input);
        chat_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSendClick();
                    return true;
                }
                return false;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void onSendClick() {
        // SEND the message and wait for the response
        final EditText chat_input = (EditText) findViewById(R.id.chat_input);
        String new_request = chat_input.getText().toString();
        listview_adapter.insert(PREFIX_ME + new_request, 0);
        responses = fptMallAsst.sendRequest(new_request);

        // clear the input
        chat_input.setText("");

        // update the list
        for (ListIterator<String> iter = responses.listIterator(); iter.hasNext(); ) {
            listview_adapter.insert(PREFIX_WATSON + iter.next(), 0);
        }
        listview_adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ChatWindow Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://testcompany.fptmallassistant_v1/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ChatWindow Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://testcompany.fptmallassistant_v1/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
