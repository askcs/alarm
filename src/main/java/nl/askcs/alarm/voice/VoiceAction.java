package nl.askcs.alarm.voice;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * VoiceAction containing the ID and the possible trigger keywords. A trigger keyword can also be a regex string.
 */
public class VoiceAction {

    private int id;
    private View relatedView;
    private ArrayList<String> triggerKeyWords;

    public VoiceAction(int id, View relatedView, ArrayList<String> triggerKeyWords) {
        this.id = id;
        this.relatedView = relatedView;
        this.triggerKeyWords = triggerKeyWords;
    }

    public VoiceAction(int id, View relatedView, String... triggerKeywords) {
        this(id, relatedView, new ArrayList<String>(Arrays.asList(triggerKeywords)));
    }

    public VoiceAction(int id, View relatedView, String triggerKeyword) {
        this(id, relatedView, new String[]{triggerKeyword});
    }

    public VoiceAction(int id, ArrayList<String> triggerKeyWords) {
        this(id, null, triggerKeyWords);
    }

    public VoiceAction(int id, String... triggerKeywords) {
        this(id, null, new ArrayList<String>(Arrays.asList(triggerKeywords)));
    }

    public VoiceAction(int id, String triggerKeyword) {
        this(id, null, triggerKeyword);
    }

    public ArrayList<String> getTriggerKeyWords() {
        return triggerKeyWords;
    }

    public int getId() {
        return id;
    }

    public View getRelatedView() {
        return relatedView;
    }

    @Override
    public String toString() {
        return "VoiceAction { " +
                "id='" + id + "'" +
                ", relatedView='" + relatedView + "'" +
                ", triggerKeywords='" + Arrays.deepToString(triggerKeyWords.toArray()) + "'" +
                "}";
    }
}
