package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

/**
 * Provides the abstract class for any voice behaviour. A behaviour can be hooked up to an element using a
 * {@link VoiceAction} by registering it at the {@link VoiceActionManager}.
 */
public abstract class VoiceElementBehaviour extends UtteranceProgressListener {

    protected IVoiceCallbacks iVoiceCallbacks;

    public VoiceElementBehaviour(IVoiceCallbacks iVoiceCallbacks) {
        this.iVoiceCallbacks = iVoiceCallbacks;
    }

    public abstract void trigger(String match);

    public TextToSpeech getTTS() {
        return iVoiceCallbacks.getTTS();
    }

    public VoiceActionManager getVoiceActionManager() {
        return iVoiceCallbacks.getVoiceActionManager();
    }
}
