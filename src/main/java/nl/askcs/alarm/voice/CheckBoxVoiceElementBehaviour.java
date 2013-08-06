package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import android.widget.CheckBox;
import nl.askcs.alarm.event.BusProvider;

import java.util.HashMap;

/**
 * Default VoiceElementBehaviour for a CheckBox. Call {@link #trigger(String)} when a
 * {@link nl.askcs.alarm.event.VoiceActionTriggeredEvent} for this CheckBox is fired. If
 * {@link android.widget.CheckBox#isEnabled()} == true, the 'match + "aangeklikt"' is read by the TTS. When the
 * utterance is done, {@link android.widget.CheckBox#setChecked(boolean)} with the opposite state of
 * {@link android.widget.CheckBox#isChecked()}.
 */
public class CheckBoxVoiceElementBehaviour extends VoiceElementBehaviour {

    private CheckBox checkBox;

    private final String CHECKBOX_UTTERANCE_ID_TRIGGER_FEEDBACK;

    public CheckBoxVoiceElementBehaviour(String tag, CheckBox checkBox, IVoiceCallbacks baseActivity) {
        super(baseActivity);
        this.checkBox = checkBox;

        CHECKBOX_UTTERANCE_ID_TRIGGER_FEEDBACK = tag + "_trigger_feedback";
    }

    @Override
    public void trigger(String match) {
        if(checkBox.isEnabled()) {
            BusProvider.getBus().register(this);
            playInstructionsBeforeChangingIsChecked(match);
        } else {
            iVoiceCallbacks.getTTS().speak(match + " kan niet aan of uit worden gevinkt", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void playInstructionsBeforeChangingIsChecked(String match) {
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, CHECKBOX_UTTERANCE_ID_TRIGGER_FEEDBACK);

        final boolean newState = !checkBox.isChecked();

        // Post as Runnable in case the CheckBox is not in the layout yet
        checkBox.post(new Runnable() {
            @Override
            public void run() {
                checkBox.setChecked(newState);
            }
        });
        iVoiceCallbacks.getTTS().speak(match + " " + (newState ? "aangevinkt" : "uitgevinkt"), TextToSpeech.QUEUE_FLUSH, params);
    }


    @Override
    public void onStart(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDone(String utteranceId) {

    }

    @Override
    public void onError(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
