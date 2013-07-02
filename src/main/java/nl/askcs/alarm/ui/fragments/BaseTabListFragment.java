package nl.askcs.alarm.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import nl.askcs.alarm.ui.activity.BaseActivity;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.util.VoiceAttributes;
import nl.askcs.alarm.util.collection.ViewVoiceAttributesMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class BaseTabListFragment extends SherlockListFragment {
    private static final int VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH = 3;
    private final String TAG = getClass().getName();

    private ViewVoiceAttributesMap viewVoiceAttributes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewVoiceAttributes = new ViewVoiceAttributesMap();
    }

    /**
     * Get all VoiceAttributes with their associated views.
     * @return The ViewVoiceAttributesMap
     */
    public ViewVoiceAttributesMap getVoiceAttributes() {
        return viewVoiceAttributes;
    }

    /**
     * Get VoiceAttributes for a specific View.
     * @param view The View whose VoiceAttributes you'd like to have
     * @return The VoiceAttributes or null if none registered with the view
     */
    public VoiceAttributes getVoiceAttributes(View view) {
        return viewVoiceAttributes.get(view);
    }

    /**
     * Register VoiceAttributes for a specific View
     * @param view The View you'd like to associate the VoiceAttributes with
     * @param voiceAttributes The VoiceAttributes you'd like to associate with the view
     */
    public void registerViewForVoiceAttributes(View view, VoiceAttributes voiceAttributes) {
        if(view == null || voiceAttributes == null) {
            throw new NullPointerException("registerViewForVoiceAttributes() " +
                    view == null ? " view parameter is null" : "" +
                    voiceAttributes == null ? " voiceAttributes parameter is null" : "");
        }

        viewVoiceAttributes.put(view, voiceAttributes);
    }

    /**
     * Unregister VoiceAttributes for a specific View
     * @param view The View you'd like to dissociate the VoiceAttributes with
     * @return The VoiceAttributes that were dissociated with the view or null if none related
     */
    public VoiceAttributes unregisterViewForVoiceAttributes(View view) {
        return viewVoiceAttributes.remove(view);
    }

    public TextToSpeech getTTS() {
        return ((BaseActivity) getActivity()).getTTS();
    }

    /**
     * Called when a match if found between a speech recognition result and a trigger keyword. Does nothing by default.
     */
    public void onVoiceElementTriggered(int id, String match, ArrayList<String> possibleMatches) {
        L.i(TAG, "onVoiceElementTriggered() view id: {0},  match: {1}, possible matches: {2}", Integer.toString(id),
                match, Arrays.deepToString(possibleMatches.toArray()));
    }

    public void onUtteranceCompleted(ArrayList<String> possibleMatches) {
        L.i(TAG, "onUtteranceCompleted() possible matches: {0}", Arrays.deepToString(possibleMatches.toArray()));
    }

    /**
     * Trigger the speech recognition. Results will be passed to #onActivityResult()
     */
    public void activateSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "free_form");

        try {
            startActivityForResult(intent, VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH);
        } catch(ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Oops! Your device doesn't support Speech to Text", 0).show();
        }
    }

    /**
     * Compares all trigger keywords from {@link VoiceAttributes#getTriggerKeyWords()} with param possibleMatches
     * @param possibleMatches
     */
    protected void checkForMatch(ArrayList<String> possibleMatches) {

        onUtteranceCompleted(possibleMatches);

        if(viewVoiceAttributes.size() != 0 && possibleMatches.size() != 0) {
            for(String candidate : possibleMatches) {

                for(Map.Entry<View, VoiceAttributes> entry : viewVoiceAttributes.entrySet()) {
                    if(entry.getValue().getTriggerKeyWords().contains(candidate)) {
                        // match!
                        onVoiceElementTriggered(entry.getKey().getId(), candidate, possibleMatches);
                        return;
                    }
                }
            }

            getTTS().speak("Geen actie gevonden, probeer het opnieuw", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.w(TAG, "There is no view having VoiceAttributes!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH) {
            if(resultCode == BaseActivity.RESULT_OK) {
                ArrayList<String> possibleMatches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if(possibleMatches.size() > 0) {
                    checkForMatch(possibleMatches);
                }
            } else {
                // tts.speak();
            }
        }
    }

    @Override
    public BaseActivity getSherlockActivity() {
        return (BaseActivity) super.getSherlockActivity();
    }
}
