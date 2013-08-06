package nl.askcs.alarm.voice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.SpeechHasNoMatchingVoiceActionEvent;
import nl.askcs.alarm.event.VoiceActionTriggeredEvent;
import nl.askcs.alarm.util.L;

import java.util.*;

/**
 * Manages all VoiceActions. Note: call {@link #destroy()} when destroying the Activity. See {@see #destroy()}. Never
 * instantiate this class yourself, but retrieve it using
 * {@link nl.askcs.alarm.ui.activity.BaseActivity#getVoiceActionManager()}.
 */
public class VoiceActionManager {

    /**
     * HashMap to store all {@link VoiceAction}s.
     */
    private HashMap<Integer, VoiceAction> voiceActions;

    public VoiceActionManager() {
        BusProvider.getBus().register(this);
        voiceActions = new HashMap<Integer, VoiceAction>();
    }

    /**
     * Call this method when destroying the Activity, otherwise a memory leak with Otto to this instance will occur
     */
    public void destroy() {
        BusProvider.getBus().unregister(this);
    }

    /**
     *
     * @return content of the HashMap containing all {@link VoiceAction}s
     */
    public HashMap<Integer, VoiceAction> getVoiceActions() {
        return voiceActions;
    }

    /**
     * Add a {@link VoiceAction} using an int resource as ID and string resource. A VoiceAction has an ID to determine
     * later on what VoiceAction we are dealing with.
     * @param context Used to access the Resources object
     * @param id The unique ID for this VoiceAction, an ID from the resources object
     * @param stringResourceId string or string-array containing a normal or regex string to match this voiceaction
     *                         against speech input
     */
    public void addVoiceAction(Context context, int id, int stringResourceId) {
        voiceActions.put(id, new VoiceAction(id, context.getString(stringResourceId)));
        L.d("Adding VoiceAction: {0}", voiceActions.get(id));
    }

    /**
     * Add a {@link VoiceAction} with its unique ID and String to match against speech input. A VoiceAction has an ID
     * to determine later on what VoiceAction we are dealing with.
     * @param id The unique ID for this VoiceAction, just a normal int
     * @param match a normal or regex string to match this voiceaction
     *                         against speech input
     */
    public void addVoiceAction(int id, String match) {
        voiceActions.put(id, new VoiceAction(id, match));
        L.d("Adding VoiceAction: {0}", voiceActions.get(id));
    }

    /**
     * Add a {@link VoiceAction} with its unique ID and String to match against speech input. A VoiceAction has an ID
     * to determine later on what VoiceAction we are dealing with.
     * @param id The unique ID for this VoiceAction, just a normal int
     * @param voiceAction The VoiceAction
     */
    public void addVoiceAction(int id, VoiceAction voiceAction) {
        voiceActions.put(id, voiceAction);
        L.d("Adding VoiceAction: {0}", voiceActions.get(id));
    }

    /**
     * Compares all trigger keywords from {@link VoiceAction#getTriggerKeyWords()} with param
     * {@code possibleMatches}. If a match is found, the {@link VoiceActionTriggeredEvent} is fired
     * (using Otto EventBus) * and the method will terminate right away. Register your component to listen to the
     * VoiceActionTriggeredEvent. If no match is found, a {@link SpeechHasNoMatchingVoiceActionEvent} is fired.
     * @param possibleMatches List of recognized speech results
     */
    public void checkForMatch(ArrayList<String> possibleMatches) {
        // Do we have any VoiceActions and possible matches? If not, inform the user
        if (voiceActions.size() != 0 && possibleMatches.size() != 0) {

            // Loop through all VoiceActions to look if any of the possibleMatches matches them
            int size = voiceActions.size();
            Iterator<Map.Entry<Integer, VoiceAction>> voiceActionsIterator = voiceActions.entrySet().iterator();

            while (voiceActionsIterator.hasNext()) {

                VoiceAction tempVoiceAction = voiceActionsIterator.next().getValue();

                // If null, just skip
                if (tempVoiceAction != null) {

                    // Loop through all the trigger keywords of this VoiceAction
                    for (String keyword : tempVoiceAction.getTriggerKeyWords()) {

                        // Loop through all possible matches for this trigger keyword
                        for (String candidate : possibleMatches) {

                            // If we have a match, fire the VoiceActionTriggeredEvent and terminate this function
                            if (candidate.matches(keyword)) {
                                // match!
                                BusProvider.getBus().post(new VoiceActionTriggeredEvent(tempVoiceAction, candidate, possibleMatches));

                                // And away we go!
                                return;
                            }
                        }
                    }
                } // else: just skip
            }

            // If we arrived here, none of the trigger keywords match the spoken input
            BusProvider.getBus().post(new SpeechHasNoMatchingVoiceActionEvent(possibleMatches));
        } else {
            L.w("No voiceActions or possibleMatches found!");
        }
    }

    /**
     * The LayoutInflater.Factory2 implementation to find the voice_control:id and voice_control:trigger_keywords at any
     * View element. If a behaviour is available (e.g. {@link ButtonVoiceElementBehaviour} for a
     * {@link android.widget.Button}, than the behaviour is registered for the Button at the {@link VoiceActionManager}.
     * @param context Used to access the Resources object and LayoutInflater
     * @return An implementation of the {@link LayoutInflater.Factory2}
     */
    public LayoutInflater.Factory2 getVoiceTriggerInflaterFactory(Context context) {
        return new LayoutInflater.Factory2() {
            protected View createViewByInflater(String name, Context context, AttributeSet attrs) {
                try {
                    String prefix = "android.widget.";
                    if ((name == "View") || (name == "ViewGroup"))
                        prefix = "android.view.";
                    if (name.contains("."))
                        prefix = null;

//                    L.i("Inflating {0}", name);
//                    if (attrs != null) {
//                        int size = attrs.getAttributeCount();
//                        if (size != 0) {
//                            for (int i = 0; i < size; i++) {
//                                L.i(" - attr: name = {0}, value = {1}", attrs.getAttributeName(i), attrs.getAttributeValue(i));
//                            }
//                        } else {
//                            L.i("Attribute count = {0}", size);
//                        }
//                    }
//                    L.i("/ {0}", name);

                    // Inflate the View by retrieving the LayoutInflater. The LayoutInflater will contain this Factory2
                    View view = LayoutInflater.from(context).createView(name, prefix, attrs);
                    return view;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

                // Inflate the View by retrieving the LayoutInflater. The LayoutInflater will contain this Factory2
                View view = createViewByInflater(name, context, attrs);
                if (view == null) return null;

/*            L.i("Inflating {0}", name);
            if(attrs != null){
                int size = attrs.getAttributeCount();
                if(size != 0) {
                    for(int i = 0; i < size; i++) {
                        L.i(" - attr: name = {0}, value = {1}", attrs.getAttributeName(i), attrs.getAttributeValue(i));
                    }
                } else {
                    L.i("Attribute count = {0}", size);
                }
            }
            L.i("/ {0}", name);*/

                // If the attributecount > 0, try to find the voice_control attributes
                // Else, just ignore and return the View
                if (attrs.getAttributeCount() > 0) {

                    // Fetch the id
                    int id = attrs.getAttributeIntValue("http://leonjoosse.nl/leon", "id", 0);

                    // if the voice_control:id is not present, take the android:id
                    if(id == 0) {
                        attrs.getIdAttributeResourceValue(0);
                    }

                    // Fetch the trigger keywords

                    // Retrieve the keywords as string or string array.
                    // First, fetch as string
                    String str = attrs.getAttributeValue("http://leonjoosse.nl/leon", "trigger_keywords");

                    // And then as string array (well, not the array itself, but the reference to the array)
                    int i = attrs.getAttributeResourceValue("http://leonjoosse.nl/leon", "trigger_keywords", 0);

                    // if the android:id still == 0 and the trigger_keywords are present, there is no valid IS set
                    // So, throw them an error!
                    if(id == 0 && (str != null || i != 0)) {
                        throw new RuntimeException("Voice actions need an id and should be > 0");
                    }

                    // If i is not 0, then we're dealing with a string-array, else it is just a string
                    if ((str != null) && (i != 0)) {
                        // Fetch the contents of the string-array
                        ArrayList<String> arr = new ArrayList<String>(Arrays.asList(view.getResources().getStringArray(i)));

                        // Convert all to lowercase
                        int count = arr.size();
                        for (int j = 0; j < count; j++) {
                            arr.set(j, arr.get(j).toLowerCase());
                        }

                        addVoiceAction(id, new VoiceAction(id, view, arr));
                    } else if ((str != null) && (i == 0)) {
                        addVoiceAction(id, new VoiceAction(id, view, str.toLowerCase()));
                    }
                }

                return view;
            }

            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return onCreateView(null, name, context, attrs);
            }
        };
    }
}
