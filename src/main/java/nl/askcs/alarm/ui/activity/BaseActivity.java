package nl.askcs.alarm.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.database.DBHelper;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.VoiceElementTriggeredEvent;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.util.VoiceAttributes;
import nl.askcs.alarm.util.collection.ViewVoiceAttributesMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class BaseActivity extends SherlockFragmentActivity implements TextToSpeech.OnInitListener {

    private static final int VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH = 3;

    // Tag used with logging
    private final String TAG = getClass().getName();

    // The local database helper to retrieve DAO instances
    private DBHelper dbHelper = null;

    private ViewVoiceAttributesMap viewVoiceAttributes;
    private TextToSpeech tts;

    /**
     * Called when Android creates this Activity.
     *
     * @param savedInstanceState the saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewVoiceAttributes = new ViewVoiceAttributesMap();

        tts = new TextToSpeech(this, this);

        L.d(TAG, "* onCreate");
    }

    /**
     * Called when Android destroys this Activity.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        L.d(TAG, "* onDestroy");

        if(tts != null) {
            if(tts.isSpeaking())
                tts.shutdown();
            tts = null;
        }

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

        L.d(TAG, "* onPause");

        if(tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    /**
     * Called when Android resumes this Activity.
     */
    @Override
    protected void onResume() {

        super.onResume();

        L.d(TAG, "* onResume");

        if(tts == null) tts = new TextToSpeech(this, this);
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

/*    @Override
    public LayoutInflater getLayoutInflater() {
        LayoutInflater li = super.getLayoutInflater().cloneInContext(this);
        li.setFactory2(voiceTriggerInflaterFactory);
        return li;
    }*/

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
            Toast.makeText(this, "Oops! Your device doesn't support Speech to Text", 0).show();
        }
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == VOICE_ACTIVITY_REQUEST_CODE_RECOGNIZE_SPEECH) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> possibleMatches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if(possibleMatches.size() > 0) {
                    checkForMatch(possibleMatches);
                }
            } else {
                // tts.speak();
            }
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
                        BusProvider.getBus().post(new VoiceElementTriggeredEvent(entry.getKey().getId(), candidate, possibleMatches));
                        return;
                    }
                }
            }

            tts.speak("Geen actie gevonden, probeer het opnieuw", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.w(TAG, "There is no view having VoiceAttributes!");
        }
    }

    /**
     * Called when a match if found between a speech recognition result and a trigger keyword. Does nothing by default.
     */
    @Subscribe
    public void onVoiceElementTriggered(VoiceElementTriggeredEvent event) {
        L.i(TAG, "onVoiceElementTriggered() match: {0}", event.getMatch());
    }

    public void onUtteranceCompleted(ArrayList<String> possibleMatches) {
        L.i(TAG, "onUtteranceCompleted() possible matches: {0}", Arrays.deepToString(possibleMatches.toArray()));
    }

    @Override
    public void onInit(int status) {
        Log.i(TAG, "onInit TextToSpeech status: " + status);
    }

    // Lazily returns a DatabaseHelper.
    private DBHelper getDBHelper() {

        L.d(TAG, "* getDBHelper");

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
            L.e(TAG, "something went wrong while querying for id={0}", id);
            return null;
        }
    }

    /*private LayoutInflater.Factory2 voiceTriggerInflaterFactory = new LayoutInflater.Factory2()
    {
        protected View createViewByInflater(String name, Context context, AttributeSet attrs) {
            try {
                String prefix = "android.widget.";
                if ((name=="View") || (name=="ViewGroup"))
                    prefix = "android.view.";
                if (name.contains("."))
                    prefix = null;

                L.i(TAG, "Inflating {0}", name);
                if(attrs != null){
                    int size = attrs.getAttributeCount();
                    if(size != 0) {
                        for(int i = 0; i < size; i++) {
                            L.i(TAG, " - attr: name = {0}, value = {1}", attrs.getAttributeName(i), attrs.getAttributeValue(i));
                        }
                    } else {
                        L.i(TAG, "Attribute count = {0}", size);
                    }
                }
                L.i(TAG, "/ {0}", name);

                View view = LayoutInflater.from(BaseActivity.this).createView(name, prefix, attrs);
                return view;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
        {
            View view = createViewByInflater(name, context, attrs);
            if (view == null) return null;

            L.i(TAG, "Inflating {0}", name);
            if(attrs != null){
                int size = attrs.getAttributeCount();
                if(size != 0) {
                    for(int i = 0; i < size; i++) {
                        L.i(TAG, " - attr: name = {0}, value = {1}", attrs.getAttributeName(i), attrs.getAttributeValue(i));
                    }
                } else {
                    L.i(TAG, "Attribute count = {0}", size);
                }
            }
            L.i(TAG, "/ {0}", name);

            if (attrs.getAttributeCount() > 0)
            {
                String str = attrs.getAttributeValue("http://leonjoosse.nl/leon", "trigger_keywords");
                int i = attrs.getAttributeResourceValue("http://leonjoosse.nl/leon", "trigger_keywords", 0);

                // don't forget to put all strings in lowercase!

                if ((str != null) && (i != 0))
                {
                    ArrayList<String> arr = new ArrayList<String>(Arrays.asList(view.getResources().getStringArray(i)));

                    int count = arr.size();
                    for(int j = 0; j < count; j++) {
                        arr.set(j, arr.get(j).toLowerCase());
                    }

                    viewVoiceAttributes.put(view, new VoiceAttributes(arr));
                    Log.i(TAG, "Keywordsarray found at " + name);
                }
                else if ((str != null) && (i == 0))
                {
                    ArrayList<String> arr = new ArrayList<String>();
                    arr.add(str.toLowerCase());
                    viewVoiceAttributes.put(view, new VoiceAttributes(arr));
                    Log.i(TAG, "Keyword found at " + name);
                }
            }

            return view;
        }

        public View onCreateView(String name, Context context, AttributeSet attrs)
        {
            return onCreateView(null, name, context, attrs);
        }
    };*/
}
