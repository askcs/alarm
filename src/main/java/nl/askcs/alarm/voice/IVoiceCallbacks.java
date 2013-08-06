package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import nl.askcs.alarm.event.RecognitionForCommandUtteranceCompletedEvent;
import nl.askcs.alarm.event.SpeechHasNoMatchingVoiceActionEvent;
import nl.askcs.alarm.event.VoiceActionTriggeredEvent;

/**
 * Defines callbacks or methods frequently used by {@link nl.askcs.alarm.ui.activity.BaseActivity},
 * {@link nl.askcs.alarm.ui.fragments.BaseTabFragment} and {@link VoiceActionManager} subclasses of
 * {@link VoiceElementBehaviour}. To receive events, don't forget to use the {@link com.squareup.otto.Subscribe}
 * annotation at the methods
 * {@link #onVoiceActionTriggered(nl.askcs.alarm.event.VoiceActionTriggeredEvent)},
 * {@link #onSpeechHasNoMatchingVoiceAction(nl.askcs.alarm.event.SpeechHasNoMatchingVoiceActionEvent)} and
 * {@link #onUtteranceCompleted(nl.askcs.alarm.event.RecognitionForCommandUtteranceCompletedEvent)}.
 */
public interface IVoiceCallbacks extends TextToSpeech.OnInitListener{

    /**
     * @return instance of {@link VoiceActionManager}
     */
    VoiceActionManager getVoiceActionManager();

    /**
     * Activates the speech recognition. If {@code isCommand} == true, a {@link VoiceActionTriggeredEvent} or
     * {@link SpeechHasNoMatchingVoiceActionEvent} is fired, otherwise, a
     * {@link nl.askcs.alarm.event.RecognitionForInputUtteranceCompletedEvent} is fired.
     * @param isCommand True if the input is a command, false if to be consumed as input (for e.g. an EditText)
     */
    void activateSpeechRecognition(boolean isCommand);

    /**
     * Returns the TextToSpeech instance. Use {@link TextToSpeech#speak(String, int, java.util.HashMap)}.
     * @return TextToSpeech instance
     */
    TextToSpeech getTTS();
    void initTTS();

    /**
     * Use the annotation {@link com.squareup.otto.Subscribe} to receive this event.
     * @param event
     */
    void onVoiceActionTriggered(VoiceActionTriggeredEvent event);

    /**
     * Use the annotation {@link com.squareup.otto.Subscribe} to receive this event.
     * @param event
     */
    void onSpeechHasNoMatchingVoiceAction(SpeechHasNoMatchingVoiceActionEvent event);

    /**
     * Use the annotation {@link com.squareup.otto.Subscribe} to receive this event.
     * @param event
     */
    void onUtteranceCompleted(RecognitionForCommandUtteranceCompletedEvent event);
}
