package testcompany.fptmallassistant_v1;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.util.Log;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Cleofas.villarin on 1/10/2017.
 */
public class TextToSpeechUtil {

    private TextToSpeech service;
    private Voice voice;
    private AudioTrack track;
    private int sampleRate;
    private String customizationID = null;

    public void setVoice(Voice voice)
    {
        this.voice = voice;
    }

    public Voice getVoice()
    {
        return this.voice;
    }

    public TextToSpeechUtil(String username, String password, String endPoint, String lang, String customizationID)
    {
        service = new TextToSpeech();
        service.setUsernameAndPassword(username, password);
        if(StringUtils.isEmpty(endPoint))
        {
            service.setEndPoint(endPoint);
        }

        this.customizationID = customizationID;

        if (lang.equals("english"))
            setVoice(Voice.EN_ALLISON);
        else if (lang.equals("japanese"))
            setVoice(Voice.JA_EMI);
        else
            setVoice(Voice.EN_ALLISON);
        // es-LA_SofiaVoice,pt-BR_IsabelaVoice,en-US_MichaelVoice,ja-JP_EmiVoice,en-US_AllisonVoice,fr-FR_ReneeVoice,it-IT_FrancescaVoice
        // es-ES_LauraVoice,de-DE_BirgitVoice,es-ES_EnriqueVoice,de-DE_DieterVoice,en-US_LisaVoice,en-GB_KateVoice,es-US_SofiaVoice
    }

    public void processText(final String text)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream result = service.synthesize(text, getVoice(), null, customizationID).execute();
                    int minBufferSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);

                    byte[] data = null;
                    data = analyzeWavData(result);
                    track = new AudioTrack(AudioManager.STREAM_MUSIC,
                            sampleRate,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            minBufferSize,
                            AudioTrack.MODE_STREAM);


                    if (track != null)
                        track.play();

                    track.write(data, 0, data.length);
                    result.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    //play text to speech (open to modifications for code quality)
    public void playTTS() {//throws JSONException {
        if (track != null)
            track.play();
    }

    public int getStatus() {
        return track.getState();
    }

    public void stopTTS() throws JSONException {
        if (track != null)
            track.stop();
    }

    //convert inputstream to byte array
    public byte[] analyzeWavData(InputStream i) {
        try {
            int headSize = 44, metaDataSize = 48;
            byte[] data = IOUtils.toByteArray(i);

            if (data.length < headSize) {
                throw new IOException("Wrong Wav header");
            }

            if (this.sampleRate == 0 && data.length > 28) {
                this.sampleRate = readInt(data, 24); // 24 is the position of sample rate in wav format
            }

            int destPos = headSize + metaDataSize;
            int rawLength = data.length - destPos;

            byte[] d = new byte[rawLength];
            System.arraycopy(data, destPos, d, 0, rawLength);
            return d;
        } catch (IOException e) {

        }
        return new byte[0];
    }

    //read int used for retrieval of sample rate based on given conditions
    protected static int readInt(final byte[] data, final int offset) {
        return (data[offset] & 0xff) |
                ((data[offset + 1] & 0xff) << 8) |
                ((data[offset + 2] & 0xff) << 16) |
                (data[offset + 3] << 24); // no 0xff on the last one to keep the sign
    }
}
