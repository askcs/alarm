package nl.askcs.alarm.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.R;
import nl.askcs.alarm.database.DBHelper;
import nl.askcs.alarm.event.*;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.voice.IVoiceCallbacks;
import nl.askcs.alarm.voice.VoiceActionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * BaseActivity to be used as super class for all Activities that will use voice recognition/synthesis. Assigns a
 * default VoiceAction for the device Back button.
 */
public class BaseActivity extends SherlockFragmentActivity implements IVoiceCallbacks {

    private static final int VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_COMMAND = 3;
    private static final int VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_INPUT = 4;

    // Tag used with logging
    private final String TAG = getClass().getName();

    // The local database helper to retrieve DAO instances
    private DBHelper dbHelper = null;

    /**
     * The VoiceActionManager
     */
    private VoiceActionManager voiceActionManager;

    /**
     * The TTS instance to prevent having multiple instances.
     */
    private TextToSpeech tts;

    /**
     * Called when Android creates this Activity.
     *
     * @param savedInstanceState the saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set the factory before calling super.onCreate(). The support package tries to set a factory to make
        // fragments inflatable from XML. See {@code FragmentActivity.onCreateView}.
        voiceActionManager = new VoiceActionManager();
        getLayoutInflater().setFactory2(voiceActionManager.getVoiceTriggerInflaterFactory(this));

        super.onCreate(savedInstanceState);

        // Default VoiceAction for the Back button
        getVoiceActionManager().addVoiceAction(this, R.id.va_general_back, R.string.va_general_back);

        initTTS();

        L.d("* onCreate");
    }

    /**
     * Called when Android destroys this Activity.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        L.d("* onDestroy");

        if (tts != null) {
            if (tts.isSpeaking())
                tts.shutdown();
            tts = null;
        }

        getVoiceActionManager().destroy();

        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
        }
    }

    /**
     * Called when Android pauses this Activity.
     */
    @Override
    protected void onPause() {

        super.onPause();

        L.d("* onPause");
    }

    /**
     * Called when Android resumes this Activity.
     */
    @Override
    protected void onResume() {

        super.onResume();

        L.d("* onResume");

        initTTS();
    }

    /**
     * Get all VoiceAction with their associated views.
     *
     * @return The HashMap containing all VoiceActions
     */
    public VoiceActionManager getVoiceActionManager() {
        return voiceActionManager;
    }

    /**
     * Trigger the speech recognition. Results will be passed to #onActivityResult()
     * @param isCommand Whether the speech input should be processed as a voice command
     */
    public void activateSpeechRecognition(boolean isCommand) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "free_form");

        try {
            if(isCommand) {
                startActivityForResult(intent, VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_COMMAND);
            } else {
                startActivityForResult(intent, VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_INPUT);
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Oops! Your device doesn't support Speech to Text", 0).show();
        }
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public final void initTTS() {
        L.d("Initing TTS");
        if (tts == null)
            tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_COMMAND:

                L.d("");

                if (resultCode == RESULT_OK) {
                    ArrayList<String> possibleMatches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    // Notify the Activity the utterance is completed
                    BusProvider.getBus().post(new RecognitionForCommandUtteranceCompletedEvent(possibleMatches));

                    if (possibleMatches.size() > 0) {
                        getVoiceActionManager().checkForMatch(possibleMatches);
                    }
                } else {
                    L.w("Got result back via onActivityResult(), but code is not RESULT_OK!");
                }
                break;
            case VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH_FOR_INPUT:

                if(resultCode == RESULT_OK) {
                    String input = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);

                    BusProvider.getBus().post(new RecognitionForInputUtteranceCompletedEvent(input));
                }

                break;
            default:
                L.w("onActivityResult with requestCode {0} ended up in default");
        }
    }

    /**
     * Called when a match if found between a speech recognition result and a trigger keyword. Does nothing by default.
     */
    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {
        L.i("onVoiceActionTriggered() match: {0}", event.getMatch());
    }

    public void onSpeechHasNoMatchingVoiceAction(SpeechHasNoMatchingVoiceActionEvent event) {
        L.i("onNoMatchedVoiceAction() no keyword matched the speech recognition. event: {0}", Arrays.deepToString(event.getPossibleMatches().toArray()));
        tts.speak("Geen actie gevonden, probeer het opnieuw", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Subscribe
    public void onUtteranceCompleted(RecognitionForCommandUtteranceCompletedEvent event) {
        L.i("onUtteranceCompleted() possible matches: {0}", Arrays.deepToString(event.getPossibleMatches().toArray()));
    }

    @Override
    public void onInit(int status) {
        L.i("onInit TextToSpeech status: " + status);
    }

    // Lazily returns a DatabaseHelper.
    public DBHelper getDBHelper() {

        L.d("* getDBHelper");

        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        }

        return dbHelper;
    }

    /**
     * Returns a DAO instance for a certain model class.
     *
     * @param modelClass the model class to create the DAO for.
     * @param idClass    the unique ID of the model.
     * @param <M>        the generic type of the model.
     * @param <I>        the generic type of the id.
     * @return a DAO instance for a certain model class.
     */
    @SuppressWarnings("unchecked")
    public <M, I> Dao<M, I> getDao(Class<M> modelClass, Class<I> idClass) {
        return this.getDBHelper().getDao(modelClass, idClass);
    }

    /**
     * A utility method to retrieve the type `M` with a given `id` from the
     * local database, or `null` if an exception occurs.
     *
     * @param modelClass the model class to create the DAO for.
     * @param idClass    the unique ID of the model.
     * @param id         the identifier to query for.
     * @param <M>        the generic type of the model.
     * @param <I>        the generic type of the id.
     * @return the type `M` with a given `id` from the local database, or
     *         `null` if an exception occurs.
     */
    @SuppressWarnings("unchecked")
    public <M, I> M queryDbForId(Class<M> modelClass, Class<I> idClass, I id) {

        try {
            Dao<M, I> dao = this.getDao(modelClass, idClass);
            return dao.queryForId(id);

        } catch (SQLException e) {
            L.e("something went wrong while querying for id={0}", id);
            return null;
        }
    }
}
