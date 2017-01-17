package testcompany.fptmallassistant_v1;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by Cleofas.villarin on 1/6/2017.
 */
public class ChatWindow extends Activity {

    // Watson service credentials
    private String convo_username = "556c5698-f097-4e49-9dd4-c0f08aa5133e";
    private String convo_password = "F8yYOcPWgtpC";

    private String tts_username = "b132f590-2efa-46c9-b6f9-89e4a59d20f1";
    private String tts_password = "JuQESeE2Uhmf";
    private String tts_endpoint = "https://stream.watsonplatform.net/speech-to-text/api";
    private String tts_customizationID = "696d0b97-062d-4c18-b5e9-21a33e0cb341";

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

    // Info from login screen
    private String language = "";

    private MediaRecorder mRecorder;

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
        language = getIntent().getStringExtra("EXTRA_CLICK_ORIGIN");
        textToSpeechUtil = new TextToSpeechUtil(tts_username, tts_password, tts_endpoint, language, tts_customizationID);

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

        // Handle send button click
        final Button send_button = (Button) findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSendClick();
            }
        });

        // Handle record button click
        final ImageButton record_imgbutton = (ImageButton) findViewById(R.id.record_imgbutton);
        record_imgbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRecordClick();
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

    private void onRecordClick() {
        // play a chime
        MediaPlayer chimeplayer = MediaPlayer.create(this, R.raw.record_chime);
        chimeplayer.start();

        // display a toast message
        displayToastMsg("Start recording!");


        // record a message for 5secs maybe? TODO: research standard wait time of voice recog softwares


        // send the recorded message to watson
        // recover text
        // autosend the message
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile("recorded_info");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.d("ChatWindow: ", "prepare() failed");
        }

        mRecorder.start();
    }

    private void displayToastMsg(String message)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
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

        // apply voice modifications and convert the response to speech
        if (language.equals("english")) {
            allreply = alterVoiceTransformation(allreply);
            allreply = alterVoiceExpressiveness(allreply);
            //allreply = applyCustomization(allreply);
        }
        textToSpeechUtil.processText(allreply);
        textToSpeechUtil.playTTS();

        // update ListView
        listview_adapter.notifyDataSetChanged();
    }

    private String alterVoiceExpressiveness(String input)
    {
        String output = "";

        // insert voice expressiveness xml
        if (input.toLowerCase().contains("sorry"))
            output = "<speak><express-as type=\"Apology\">" + input + "</express-as></speak>";
        else
            output = "<speak><express-as type=\"GoodNews\">" + input + "</express-as></speak>";

        return output;
    }

    private String alterVoiceTransformation(String input) {
        String output = "";
        output = "<voice-transformation " +
                "type=\"Custom\" " +
                "glottal_tension=\"-100%\" " +
                "breathiness=\"50%\" " +
                "timbre=\"Sunrise\" " +
                "pitch_range=\"80%\" " +
                "pitch=\"-100%\">" +
                input + "</voice-transformation>";
        return output;
    }

    /* No Longer Used..
    // Customization is transferred to RESTful
    // customization ID: 696d0b97-062d-4c18-b5e9-21a33e0cb341
    private String applyCustomization(String input)
    {
        //  \<speak xml:lang=\"En-US\" version=\"1.0\">" + "<say-as interpret-as=\"letters\">Hello</say-as></speak>");
        //  The <phoneme alphabet="ibm" ph=".0tx.1me.0fo">tomato</phoneme> was ripe.
        //  The baby was born on <say-as interpret-as="date" format="mdy">3/4/2016</say-as>.
        //  I work at <sub alias="International Business Machines">IBM</sub>.

        String output = input;
        String opentag = "";
        String closetag = "";

        // apply correct pronounciation of cars
        if (output.toLowerCase().contains("lamborghini"))
        {
            opentag = "<phoneme alphabet=\"ipa\" ph=\"lamborˈɡiːni\">";
            closetag = "</phoneme>";
            output = output.replace("Lamborghini", opentag + "Lamborghini" + closetag);
            output = output.replace("lamborghini", opentag + "lamborghini" + closetag);
            output = output.replace("LAMBORGHINI", opentag + "LAMBORGHINI" + closetag);
        }
        if (output.toLowerCase().contains("chevrolet"))
        {
            opentag = "<phoneme alphabet=\"ipa\" ph=\"ʃɛvrəˈleɪ\">";
            closetag = "</phoneme>";
            output = output.replace("Chevrolet", opentag + "Chevrolet" + closetag);
            output = output.replace("chevrolet", opentag + "chevrolet" + closetag);
            output = output.replace("CHEVROLET", opentag + "CHEVROLET" + closetag);
        }
        if (output.toLowerCase().contains("volkswagen"))
        {
            opentag = "<phoneme alphabet=\"ipa\" ph=\"ˈfɔlksˌvaːɡŋˈ\">";
            closetag = "</phoneme>";
            output = output.replace("Volkswagen", opentag + "Volkswagen" + closetag);
            output = output.replace("volkswagen", opentag + "volkswagen" + closetag);
            output = output.replace("VOLKSWAGEN", opentag + "VOLKSWAGEN" + closetag);
        }
        if (output.toLowerCase().contains("peugeot"))
        {
            opentag = "<phoneme alphabet=\"ipa\" ph=\"puːˈʒoʊ\">";
            closetag = "</phoneme>";
            output = output.replace("Peugeot", opentag + "Peugeot" + closetag);
            output = output.replace("peugeot", opentag + "peugeot" + closetag);
            output = output.replace("PEUGEOT", opentag + "PEUGEOT" + closetag);
        }
        return output;
    }
    */
}
