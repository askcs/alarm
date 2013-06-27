package nl.askcs.alarm.util;

import java.util.ArrayList;
import java.util.Arrays;

public class VoiceAttributes {
	private ArrayList<String> triggerKeyWords;

    public VoiceAttributes(String... triggerKeywords) {
        this.triggerKeyWords = new ArrayList<String>(Arrays.asList(triggerKeywords));
    }

	public VoiceAttributes(ArrayList<String> triggerKeyWords) {
		this.triggerKeyWords = triggerKeyWords;
	}

	public ArrayList<String> getTriggerKeyWords() {
		return triggerKeyWords;
	}
}
