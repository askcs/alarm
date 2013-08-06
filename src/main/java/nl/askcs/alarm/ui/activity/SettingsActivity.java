package nl.askcs.alarm.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.R;
import nl.askcs.alarm.database.DBHelper;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.RecognitionForCommandUtteranceCompletedEvent;
import nl.askcs.alarm.event.SpeechHasNoMatchingVoiceActionEvent;
import nl.askcs.alarm.event.VoiceActionTriggeredEvent;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.voice.IVoiceCallbacks;
import nl.askcs.alarm.voice.PreferenceVoiceElementBehaviour;
import nl.askcs.alarm.voice.VoiceActionManager;
import nl.askcs.alarm.voice.VoiceElementBehaviour;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity to host the preferences. All stuff from {@link BaseActivity} is copy/pasted here, because we can't extend
 * BaseActivity.
 */
public class SettingsActivity extends PreferenceActivity implements IVoiceCallbacks, Preference.OnPreferenceClickListener {

    private boolean alarmsPopulated;
    private Preference newAlarm;

    private PreferenceVoiceElementBehaviour newAlarmVoiceElementBehaviour;
    private PreferenceVoiceElementBehaviour profileVoiceElementBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set the factory before calling super.onCreate(). The support package tries to set a factory to make
        // fragments inflatable from XML. See {@code FragmentActivity.onCreateView}.
        voiceActionManager = new VoiceActionManager();
        getLayoutInflater().setFactory2(voiceActionManager.getVoiceTriggerInflaterFactory(this));

        super.onCreate(savedInstanceState);

        BusProvider.getBus().register(this);

        getVoiceActionManager().addVoiceAction(this, R.id.va_settings_new_alarm, R.string.va_settings_new_alarm);
        getVoiceActionManager().addVoiceAction(this, R.id.va_settings_pin_code, R.string.va_settings_pin_code);
        getVoiceActionManager().addVoiceAction(this, R.id.va_settings_profile, R.string.va_settings_profile);
        getVoiceActionManager().addVoiceAction(this, R.id.va_general_back, R.string.va_general_back);

        initTTS();

        alarmsPopulated = false;

        addPreferencesFromResource(R.xml.preferences);

        getTTS().setOnUtteranceProgressListener(utteranceProgressListener);

        profileVoiceElementBehaviour = new PreferenceVoiceElementBehaviour("profile", findPreference("profile"), this);
        utteranceProgressListener.listeners.add(profileVoiceElementBehaviour);

        newAlarm = findPreference("new_alarm");
        newAlarm.setOnPreferenceClickListener(this);
        newAlarmVoiceElementBehaviour = new PreferenceVoiceElementBehaviour("new_alarm", newAlarm, this);
        utteranceProgressListener.listeners.add(newAlarmVoiceElementBehaviour);
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateAlarms();

        initTTS();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Alarms could be changed while the activity is not in the foreground (e.g. by the back-end)
        alarmsPopulated = false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.activity_edit_alarm, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.speak:
                getTTS().stop();
                activateSpeechRecognition(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateAlarms() {

        if(alarmsPopulated) {
            L.d("Alarms already populated");
            return;
        }

        dispopulateAlarms();

        // Retrieve the PreferenceCategory that will host the alarms
        PreferenceCategory pc = (PreferenceCategory) findPreference("alarms");

        // Get all alarms from the database
        List<Alarm> alarms = null;
        try {
            alarms = getDao(Alarm.class, Integer.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the alarms to the list, otherwise continue
        if (alarms != null) {

            // Create for each alarm a SwitchPreference (to enable/disable it)
            for (Alarm alarm : alarms) {

                // The base entry
                SwitchPreference sp = new SwitchPreference(this);

                // Use the alarm.getTitle as title for the entry
                sp.setTitle(alarm.getTitle());

                // Set the Switch to on or off based on alarm.isEnabled
                sp.setChecked(true);

                // Set the key, following this pattern: 'alarm-key-[id]' where [id] is alarm.getId()
                sp.setKey("alarm-key-" + alarm.getId());

                // Display additional information
                sp.setSummaryOn("Helpers: " + alarm.getHelperCount() + ", PIN: " + (alarm.shouldEnterPinToCancel() ? "ja" : "nee"));
                sp.setSummaryOff("Helpers: " + alarm.getHelperCount() + ", PIN: " + (alarm.shouldEnterPinToCancel() ? "ja" : "nee"));

                // Attach the onPreferenceClickListener
                sp.setOnPreferenceClickListener(this);

                // Add the entry to the list
                pc.addPreference(sp);
            }
        }

        // Done!
        alarmsPopulated = true;
    }

    private void dispopulateAlarms() {
        // Remove all entries from the PreferenceCategory that hosts the alarms
        ((PreferenceCategory) findPreference("alarms")).removeAll();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        // Terminate when the android:key is not set
        if(preference.hasKey() == false) {
            L.d("onPreferenceClick, but preference has no key");
            return false;
        }

        L.d("onPreferenceClick key={0}", preference.getKey());

        if(preference.getKey().equals("new_alarm")) {
            // Start the activity to add a new alarm
            startActivity(new Intent(this, EditAlarmActivity.class));

            return true;
        }

        // If the user clicked an alarm from the PreferenceCategory.getKey() == 'alarms'
        if(preference.getKey().startsWith("alarm-key-")) {

            // Retrieve the id of the alarm
            int id = Integer.parseInt(preference.getKey().substring(10));

            // Start the activity to edit the alarm
            startActivity(new Intent(this, EditAlarmActivity.class).putExtra(EditAlarmActivity.EXTRA_ALARM_ID, id));

            return true;
        }

        return false;
    }

    /**
     * Called when Android destroys this Activity.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        L.d("* onDestroy");

        BusProvider.getBus().unregister(this);

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


    /* BASE STUFF, do not edit! */

    private static final int VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH = 3;

    // Tag used with logging
    private final String TAG = getClass().getName();

    // The local database helper to retrieve DAO instances
    private DBHelper dbHelper = null;

    private VoiceActionManager voiceActionManager;
    private TextToSpeech tts;


    /**
     *
     * @return The VoiceActionManager instance
     */
    public VoiceActionManager getVoiceActionManager() {
        return voiceActionManager;
    }

    /**
     * Trigger the speech recognition. Results will be passed to #onActivityResult()
     */
    public void activateSpeechRecognition(boolean isCommand) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "free_form");

        try {
            startActivityForResult(intent, VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Oops! Your device doesn't support Speech to Text", 0).show();
        }
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    @Override
    public void initTTS() {
        L.d("Initing TTS");
        if (tts == null)
            tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH) {
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
        }
    }

    /**
     * Called when a match if found between a speech recognition result and a trigger keyword. Does nothing by default.
     */
    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {
        L.i("onVoiceActionTriggered() match: {0}", event.getMatch());

        switch(event.getVoiceAction().getId()) {
            case R.id.va_general_back:
                onBackPressed();
                break;
            case R.id.va_settings_new_alarm:
                newAlarmVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case R.id.va_settings_pin_code:
                getTTS().speak("pin code kan niet via stem worden ingesteld", TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.va_settings_profile:
                profileVoiceElementBehaviour.trigger(event.getMatch());
                break;
        }
    }

    public void onSpeechHasNoMatchingVoiceAction(SpeechHasNoMatchingVoiceActionEvent event) {
        L.i("onNoMatchedVoiceAction() no keyword matched the speech recognition. event: {0}", Arrays.deepToString(event.getPossibleMatches().toArray()));
        tts.speak("Geen actie gevonden, probeer het opnieuw", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Subscribe
    public void onUtteranceCompleted(RecognitionForCommandUtteranceCompletedEvent event) {
        L.d("onUtteranceCompleted() possible matches: {0}", Arrays.deepToString(event.getPossibleMatches().toArray()));
    }

    @Override
    public void onInit(int status) {
        L.d("onInit TextToSpeech status: " + status);
    }

    // Lazily returns a DatabaseHelper.
    private DBHelper getDBHelper() {

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

    /**
     * Only one listener can be set
     */
    public LocalUtteranceProgressListener utteranceProgressListener = new LocalUtteranceProgressListener();

    class LocalUtteranceProgressListener extends UtteranceProgressListener {

        public ArrayList<VoiceElementBehaviour> listeners = new ArrayList<VoiceElementBehaviour>();

        @Override
        public void onStart(String utteranceId) {
            if(listeners != null)
                if(listeners.size() != 0)
                    for(VoiceElementBehaviour veb : listeners)
                        veb.onStart(utteranceId);
        }

        @Override
        public void onDone(String utteranceId) {
            L.d("utteranceProgressListener onDone({0})", utteranceId);
            logListeners();
            if(listeners != null) {
                if(listeners.size() != 0) {
                    for(VoiceElementBehaviour veb : listeners) {
                        veb.onDone(utteranceId);
                    }
                }
            }
        }

        @Override
        public void onError(String utteranceId) {
            if(listeners != null)
                if(listeners.size() != 0)
                    for(VoiceElementBehaviour veb : listeners)
                        veb.onError(utteranceId);
        }

        protected void logListeners() {
            L.d("Listeners present: {0}", Arrays.deepToString(listeners.toArray()));
        }
    }
}
