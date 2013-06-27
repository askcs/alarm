package nl.askcs.alarm.event;

import java.util.ArrayList;

/**
 *
 */
public class VoiceElementTriggeredEvent {
    private int id;
    private String match;
    private ArrayList<String> possibleMatches;

    public VoiceElementTriggeredEvent() {

    }

    public VoiceElementTriggeredEvent(int id, String match, ArrayList<String> possibleMatches) {
        this.id = id;
        this.match = match;
        this.possibleMatches = possibleMatches;
    }

    public int getId() {
        return id;
    }

    public String getMatch() {
        return match;
    }

    public ArrayList<String> getPossibleMatches() {
        return possibleMatches;
    }
}
