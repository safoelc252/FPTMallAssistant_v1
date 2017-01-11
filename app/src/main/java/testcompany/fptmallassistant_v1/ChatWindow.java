package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.media.AudioManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class ChatWindow extends Activity {

    // Watson credentials
    private String convo_username = "556c5698-f097-4e49-9dd4-c0f08aa5133e";
    private String convo_password = "F8yYOcPWgtpC";

    private String tts_username = "b132f590-2efa-46c9-b6f9-89e4a59d20f1";
    private String tts_password = "JuQESeE2Uhmf";
    private String tts_endpoint = "https://stream.watsonplatform.net/speech-to-text/api";

    private ConversationServiceUtil FPTMallAssistant;
    private String workspaceIDEN = "a1681b35-87d9-4479-845e-da2c6cc11b63"; // English FPT Mall Assistant workspace ID
    private String workspaceIDJP = "63aff1e6-a2d0-4c74-a802-04a8ee6da44c"; // Japanese FPT Mall Assistant workspace ID
    private TextToSpeechUtil textToSpeechUtil;

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

        // to enable volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // prepare the ListView to display data
        listview_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, convo_hist);
        ListView convohist_listview = (ListView) findViewById(R.id.convhist_listview);
        convohist_listview.setAdapter(listview_adapter);

        // Initialize TTS
        String language = getIntent().getStringExtra("EXTRA_CLICK_ORIGIN");
        textToSpeechUtil = new TextToSpeechUtil(tts_username, tts_password, tts_endpoint, language);

        // Initialize convo
        String workspaceID = "";
        if (language.equals("english"))
            workspaceID = workspaceIDEN;
        else if (language.equals("japanese"))
            workspaceID = workspaceIDJP;
        else
            workspaceID = workspaceIDEN;
        FPTMallAssistant = new ConversationServiceUtil(convo_username, convo_password, workspaceID);
        ConvoResponseHandler(FPTMallAssistant.sendRequest("")); // send an empty string for convo init

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
        ConvoResponseHandler(FPTMallAssistant.sendRequest(new_request));

        // clear the input
        chat_input.setText("");
    }

    private void ConvoResponseHandler(List<String> responses)
    {
        String reply;
        String allreply = "";
        // update the list
        for (ListIterator<String> iter = responses.listIterator(); iter.hasNext(); ) {
            reply = iter.next();
            listview_adapter.insert(PREFIX_WATSON + reply, 0);

            if (allreply.isEmpty())
                allreply = reply;
            else
                allreply = allreply.concat(" " + reply);

        }

        // convert the response to speech
        textToSpeechUtil.processText(allreply);
        textToSpeechUtil.playTTS();

        // update ListView
        listview_adapter.notifyDataSetChanged();
    }
}
