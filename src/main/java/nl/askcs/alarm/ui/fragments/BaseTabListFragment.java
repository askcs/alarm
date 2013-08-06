package nl.askcs.alarm.ui.fragments;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import com.actionbarsherlock.app.SherlockListFragment;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.ui.activity.BaseActivity;
import nl.askcs.alarm.util.L;

import java.util.ArrayList;
import java.util.Arrays;

public class BaseTabListFragment extends SherlockListFragment {

    private final String TAG = getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public TextToSpeech getTTS() {
        return getSherlockActivity().getTTS();
    }

    /**
     * Called when a match if found between a speech recognition result and a trigger keyword. Does nothing by default.
     */
    @Subscribe
    public void onVoiceElementTriggered(int id, String match, ArrayList<String> possibleMatches) {
        L.i("onVoiceActionTriggered() view id: {0},  match: {1}, possible matches: {2}", Integer.toString(id),
                match, Arrays.deepToString(possibleMatches.toArray()));
    }

    @Subscribe
    public void onUtteranceCompleted(ArrayList<String> possibleMatches) {
        L.i("onUtteranceCompleted() possible matches: {0}", Arrays.deepToString(possibleMatches.toArray()));
    }

    /**
     * Trigger the speech recognition. Results will be passed to #onActivityResult()
     */
    public void activateSpeechRecognition(boolean isCommand) {
        getSherlockActivity().activateSpeechRecognition(isCommand);
    }

    @Override
    public BaseActivity getSherlockActivity() {
        return (BaseActivity) super.getSherlockActivity();
    }
}
