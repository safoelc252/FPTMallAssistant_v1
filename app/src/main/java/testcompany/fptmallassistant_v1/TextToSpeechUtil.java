package testcompany.fptmallassistant_v1;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

/**
 * Created by Cleofas.villarin on 1/10/2017.
 */
public class TextToSpeechUtil {

    private TextToSpeech service;

    public TextToSpeechUtil(String username, String password)
    {
        service = new TextToSpeech();
        service.setUsernameAndPassword(username, password);


    }
}
