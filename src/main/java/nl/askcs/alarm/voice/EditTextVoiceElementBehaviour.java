package nl.askcs.alarm.voice;

import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.RecognitionForInputUtteranceCompletedEvent;
import nl.askcs.alarm.ui.activity.BaseActivity;
import nl.askcs.alarm.util.L;

import java.util.HashMap;

/**
 * Default VoiceElementBehaviour for an EditText. Call {@link #trigger(String)} when a
 * {@link nl.askcs.alarm.event.VoiceActionTriggeredEvent} for this EditText is fired. If
 * {@link android.widget.EditText#isEnabled()} == true, '"bewerk " + match + ". Hint: " + EditText#getHint()' is read
 * by the TTS. When the utterance is done, speech recognition is activated for the user to speak the input. After input,
 * the input is read by the TTS for the user to verify if the input is okay.
 */
public class EditTextVoiceElementBehaviour extends VoiceElementBehaviour {

    protected EditText editText;

    public EditTextVoiceElementBehaviour(String tag, EditText editText, BaseActivity baseActivity) {
        super(baseActivity);
        this.editText = editText;

        TITLE_UTTERANCE_ID_BEFORE_EDITING = tag + "_before_editing";
        TITLE_UTTERANCE_ID_IS_INPUT_OKAY = tag + "_is_input_okay";
    }

    public final String
            TITLE_UTTERANCE_ID_BEFORE_EDITING,
            TITLE_UTTERANCE_ID_IS_INPUT_OKAY;

    public static final int
            STATE_TITLE_BEFORE_EDITING = 1,
            STATE_TITLE_RECORDING_INPUT = 2,
            STATE_TITLE_IS_INPUT_OKAY = 3,
            STATE_TITLE_RECORDING_IS_INPUT_OKAY = 4;

    private int currentState = STATE_TITLE_BEFORE_EDITING;

    @Override
    public void trigger(String match) {

        if(editText.isEnabled()) {
            BusProvider.getBus().register(this);
            playInstructionsBeforeInput(match);
        } else {
            iVoiceCallbacks.getTTS().speak(match + " kan niet worden bewerkt", TextToSpeech.QUEUE_FLUSH, null);
        }


    }

    @Override
    public void onStart(String utteranceId) {
        L.d("Utterance with utteranceId {0} started", utteranceId);
    }

    @Override
    public void onDone(String utteranceId) {

        L.d("Utterance with utteranceId {0} done", utteranceId);

        if(utteranceId.equals(TITLE_UTTERANCE_ID_BEFORE_EDITING)) {

            L.d("Starting SR to get user input for title");

            startRecordingInput();
        }

        if(utteranceId.equals(TITLE_UTTERANCE_ID_IS_INPUT_OKAY)) {
            L.d("Starting SR to to know if user input is okay (user responds yes or no)");

            //startRecordingIfInputIsOkay();
        }
    }

    @Override
    public void onError(String utteranceId) {
        L.d("Received an error for utterance with id {0}", utteranceId);
    }

    public void playInstructionsBeforeInput(String match) {
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, TITLE_UTTERANCE_ID_BEFORE_EDITING);

        String speak = "Bewerk " + match + ". ";

        if(editText.getHint() != null) {
            speak += "Hint: " + editText.getHint() + ". ";
        }

        iVoiceCallbacks.getTTS().speak(speak, TextToSpeech.QUEUE_FLUSH, params);

        // We'll get back at onDone(), which will call startRecordingInput()
    }

    public void startRecordingInput() {
        currentState = STATE_TITLE_RECORDING_INPUT;

        // Activate speech recognition for input
        iVoiceCallbacks.activateSpeechRecognition(false);

        // We'll get back at onRecognitionForInputUtteranceCompleted
    }

    public void onInputRecorded(String input) {
        editText.setText(input);
        playInstructionsBeforeIfInputIsOkay(input);
    }

    public void playInstructionsBeforeIfInputIsOkay(String input) {

        currentState = STATE_TITLE_IS_INPUT_OKAY;

        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, TITLE_UTTERANCE_ID_IS_INPUT_OKAY);

        iVoiceCallbacks.getTTS().speak("Invoer: " + input, TextToSpeech.QUEUE_FLUSH, params);

        // We'll continue at onRecognitionForInputUtteranceCompleted, which will call startRecordingIfInputIsOkay
    }

    public void startRecordingIfInputIsOkay() {
        currentState = STATE_TITLE_RECORDING_IS_INPUT_OKAY;

        // Activate speech recognition for input
        iVoiceCallbacks.activateSpeechRecognition(false);
    }

    public void onIfInputIsOkayRecorded(String input) {
        if(input.equals("ja")) {
            onInputApproved(input);
        } else if(input.equals("nee")) {
            onInputDisapproved(input);
        } else {

        }
    }

    public void onInputApproved(String input) {
        // Does nothing by default
    }

    public void onInputDisapproved(String input) {
        startRecordingInput();
    }

    @Subscribe
    public void onRecognitionForInputUtteranceCompleted(RecognitionForInputUtteranceCompletedEvent event) {
        if(currentState == STATE_TITLE_RECORDING_INPUT) {
            onInputRecorded(event.getInput());
        }

        if(currentState == STATE_TITLE_RECORDING_IS_INPUT_OKAY) {
            //onIfInputIsOkayRecorded(event.getInput());
            // Skip the approving of the input for now
            onInputApproved(event.getInput());
        }
    }
}
