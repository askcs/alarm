package nl.askcs.alarm.event;

import java.util.ArrayList;

/**
 * Fired when the speech recognition is completed, after it was activated using
 * {@link nl.askcs.alarm.voice.IVoiceCallbacks#activateSpeechRecognition(boolean)} with true as parameter, but no match
 * was found in the {@link nl.askcs.alarm.voice.VoiceActionManager#checkForMatch(java.util.ArrayList)}.
 */
public class SpeechHasNoMatchingVoiceActionEvent {

    private ArrayList<String> possibleMatches;

    public SpeechHasNoMatchingVoiceActionEvent() {
        // Empty constructor for Otto
    }

    public SpeechHasNoMatchingVoiceActionEvent(ArrayList<String> possibleMatches) {
        this.possibleMatches = possibleMatches;
    }

    public ArrayList<String> getPossibleMatches() {
        return possibleMatches;
    }
}
