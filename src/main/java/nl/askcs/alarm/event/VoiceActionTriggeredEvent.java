package nl.askcs.alarm.event;

import nl.askcs.alarm.voice.VoiceAction;

import java.util.ArrayList;

/**
 *
 */
public class VoiceActionTriggeredEvent {
    private VoiceAction voiceAction;
    private String match;
    private ArrayList<String> possibleMatches;

    public VoiceActionTriggeredEvent() {

    }

    public VoiceActionTriggeredEvent(VoiceAction voiceAction, String match, ArrayList<String> possibleMatches) {
        this.voiceAction = voiceAction;
        this.match = match;
        this.possibleMatches = possibleMatches;
    }

    public VoiceAction getVoiceAction() {
        return voiceAction;
    }

    public String getMatch() {
        return match;
    }

    public ArrayList<String> getPossibleMatches() {
        return possibleMatches;
    }

    @Override
    public String toString() {
        return "VoiceActionTriggeredEvent{" +
                "voiceAction=" + voiceAction +
                ", match='" + match + '\'' +
                ", possibleMatches=" + possibleMatches +
                '}';
    }
}
