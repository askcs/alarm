package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import android.widget.Button;
import nl.askcs.alarm.ui.activity.BaseActivity;
import nl.askcs.alarm.util.L;

import java.util.HashMap;

/**
 * Default VoiceElementBehaviour for a Button. Call {@link #trigger(String)} when a
 * {@link nl.askcs.alarm.event.VoiceActionTriggeredEvent} for this Button is fired. If
 * {@link android.widget.Button#isEnabled()} == true, the 'match + "aangeklikt"' is read by the TTS. When the
 * utterance is done, {@link android.widget.Button#performClick()} is called.
 */
public class ButtonVoiceElementBehaviour extends VoiceElementBehaviour {

    private Button button;

    private final String BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK;

    public ButtonVoiceElementBehaviour(String tag, Button button, BaseActivity baseActivity) {
        super(baseActivity);
        this.button = button;

        BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK = tag + "_trigger_feedback";
    }

    @Override
    public void trigger(String match) {
        if(button.isEnabled()) {
            playTriggerFeedback(match);
        } else {
            iVoiceCallbacks.getTTS().speak(match + " kan niet worden aangeklikt.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void playTriggerFeedback(String match) {
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK);
        iVoiceCallbacks.getTTS().speak(match + " aangeklikt", TextToSpeech.QUEUE_FLUSH, params);
    }

    @Override
    public void onStart(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDone(String utteranceId) {
        L.d("onDone({0})", utteranceId);
        if(utteranceId.equals(BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK)) {
            L.d("Performing click on Button");

            button.post(new Runnable() {
                @Override
                public void run() {
                    button.performClick();
                }
            });
        }
    }

    @Override
    public void onError(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "ButtonVoiceElementBehaviour{" +
                "button text=" + (button.getText() != null ? button.getText() : "'empty'") +
                ", BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK='" + BUTTON_UTTERANCE_ID_TRIGGER_FEEDBACK + '\'' +
                '}';
    }
}
