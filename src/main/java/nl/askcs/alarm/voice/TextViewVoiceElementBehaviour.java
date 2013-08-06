package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;

/**
 * Default VoiceElementBehaviour for a TextView. Call {@link #trigger(String)} when a
 * {@link nl.askcs.alarm.event.VoiceActionTriggeredEvent} for this TextView is fired. If
 * {@link android.widget.TextView#isEnabled()} == true, the 'TextView#getText()' is read by the TTS. In case the
 * TextView#getText() == null, than 'match + " kan niet worden voorgelezen omdat deze geen inhoud heeft."'.
 */
public class TextViewVoiceElementBehaviour extends VoiceElementBehaviour {

    private TextView textView;

    public TextViewVoiceElementBehaviour(String tag, TextView textView, IVoiceCallbacks baseActivity) {
        super(baseActivity);
        this.textView = textView;
    }

    @Override
    public void trigger(String match) {
        if(textView.isEnabled()) {
            String speak = match + (textView.getText() != null ? textView.getText() : " kan niet worden voorgelezen omdat deze geen inhoud heeft.");
            iVoiceCallbacks.getTTS().speak(speak, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            iVoiceCallbacks.getTTS().speak(match + " kan niet worden aangeklikt.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onStart(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDone(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onError(String utteranceId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
