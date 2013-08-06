package nl.askcs.alarm.voice;

import android.preference.Preference;
import android.speech.tts.TextToSpeech;
import nl.askcs.alarm.util.L;

import java.util.HashMap;

/**
 * Default VoiceElementBehaviour for a Preference. Call {@link #trigger(String)} when a
 * {@link nl.askcs.alarm.event.VoiceActionTriggeredEvent} for this Preference is fired. If
 * {@link android.preference.Preference#isEnabled()} == true, 'match + "aangeklikt"' is read by the TTS. When the
 * utterance is done, {@link android.preference.Preference#getOnPreferenceClickListener()#onPreferenceClick()} is
 * called.
 */
public class PreferenceVoiceElementBehaviour extends VoiceElementBehaviour {

    private Preference preference;

    private final String PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK;


    public PreferenceVoiceElementBehaviour(String tag, Preference preference, IVoiceCallbacks settingsActivity) {
        super(settingsActivity);
        this.preference = preference;

        PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK = tag + "_trigger_feedback";
    }

    @Override
    public void trigger(String match) {
        if(preference.isEnabled()) {
            playTriggerFeedback(match);
        } else {
            iVoiceCallbacks.getTTS().speak(match + " kan niet worden aangeklikt.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void playTriggerFeedback(String match) {
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK);
        iVoiceCallbacks.getTTS().speak(match + " aangeklikt", TextToSpeech.QUEUE_FLUSH, params);
    }

    @Override
    public void onStart(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDone(String utteranceId) {
        L.d("onDone({0})", utteranceId);
        if(utteranceId.equals(PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK)) {
            L.d("Performing click on Preference. key: {0}, title: {1}", preference.getKey(), preference.getTitle());

            preference.getOnPreferenceClickListener().onPreferenceClick(preference);
        }
    }

    @Override
    public void onError(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "PreferenceVoiceElementBehaviour{" +
                "preference key=" + preference.getKey() != null ? preference.getKey() : "no key" +
                ", PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK='" + PREFERENCE_UTTERANCE_ID_TRIGGER_FEEDBACK + '\'' +
                '}';
    }
}
