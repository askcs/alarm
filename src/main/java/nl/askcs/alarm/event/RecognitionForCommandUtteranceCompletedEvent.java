package nl.askcs.alarm.event;

import java.util.ArrayList;

/**
 * Fired when the speech recognition is completed, after it was activated using
 * {@link nl.askcs.alarm.voice.IVoiceCallbacks#activateSpeechRecognition(boolean)} with true as parameter.
 */
public class RecognitionForCommandUtteranceCompletedEvent {

    private ArrayList<String> possibleMatches;

    public RecognitionForCommandUtteranceCompletedEvent() {
        // Empty constructor for Otto
    }

    public RecognitionForCommandUtteranceCompletedEvent(ArrayList<String> possibleMatches) {
        this.possibleMatches = possibleMatches;
    }

    public ArrayList<String> getPossibleMatches() {
        return possibleMatches;
    }
}
