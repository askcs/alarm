package nl.askcs.alarm.event;

/**
 * Fired when the speech recognition is completed, after it was activated using
 * {@link nl.askcs.alarm.voice.IVoiceCallbacks#activateSpeechRecognition(boolean)} with false as parameter.
 */
public class RecognitionForInputUtteranceCompletedEvent {

    private String input;

    public RecognitionForInputUtteranceCompletedEvent() {
    }

    public RecognitionForInputUtteranceCompletedEvent(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
