package nl.askcs.alarm.util.collection;

import android.view.View;
import nl.askcs.alarm.util.VoiceAttributes;

import java.util.HashMap;

public class ViewVoiceAttributesMap extends HashMap<View, VoiceAttributes> {
    private static final long serialVersionUID = -3041243005439154205L;

    /**
     * Returns true or false whether there is a VoiceAttributes instance available for the View.
     * @param view
     * @return True when a VoiceAttributes object is the value, false if HashMap.containsKey == false
     */
    public boolean hasVoiceAttributes(View view) {
        return this.containsKey(view);
    }
}
