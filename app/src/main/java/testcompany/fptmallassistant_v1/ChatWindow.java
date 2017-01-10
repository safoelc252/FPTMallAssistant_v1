package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class ChatWindow extends Activity {

    // Watson credentials
    private String username = "556c5698-f097-4e49-9dd4-c0f08aa5133e";
    private String password = "F8yYOcPWgtpC";

    private ConversationServiceUtil FPTMallAssistant;
    private String workspaceID = "a1681b35-87d9-4479-845e-da2c6cc11b63"; // FPT Mall Assistant workspace ID

    // Conversation history display
    private String PREFIX_ME = "ME: ";
    private String PREFIX_WATSON = "WATSON: ";

    // ListView objects
    private ArrayList<String> convo_hist = new ArrayList<String>();
    private ArrayAdapter<String> listview_adapter;
    private List<String> responses;

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
        FPTMallAssistant = new ConversationServiceUtil(username, password, workspaceID);
        responses = FPTMallAssistant.sendRequest(""); // send an empty string for convo init
        for (ListIterator<String> iter = responses.listIterator(); iter.hasNext(); ) {
            listview_adapter.insert(PREFIX_WATSON + iter.next(), 0);
        }
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
    }

    private void onSendClick() {
        // SEND the message and wait for the response
        final EditText chat_input = (EditText) findViewById(R.id.chat_input);
        String new_request = chat_input.getText().toString();
        listview_adapter.insert(PREFIX_ME + new_request, 0);
        listview_adapter.notifyDataSetChanged();
        responses = FPTMallAssistant.sendRequest(new_request);

        // update the list
        for (ListIterator<String> iter = responses.listIterator(); iter.hasNext(); ) {
            listview_adapter.insert(PREFIX_WATSON + iter.next(), 0);
        }
        listview_adapter.notifyDataSetChanged();

        // clear the input
        chat_input.setText("");
    }

}
