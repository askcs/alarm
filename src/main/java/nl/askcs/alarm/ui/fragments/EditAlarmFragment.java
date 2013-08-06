package nl.askcs.alarm.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.j256.ormlite.dao.ForeignCollection;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.VoiceActionTriggeredEvent;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.models.Helper;
import nl.askcs.alarm.ui.activity.EditAlarmActivity;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.voice.ButtonVoiceElementBehaviour;
import nl.askcs.alarm.voice.CheckBoxVoiceElementBehaviour;
import nl.askcs.alarm.voice.EditTextVoiceElementBehaviour;
import nl.askcs.alarm.voice.VoiceElementBehaviour;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains a form to create or edit an Alarm. Layout is not according design, but contains the minimum fields to
 * create an Alarm.
 */
public class EditAlarmFragment extends BaseTabFragment implements View.OnClickListener {

    private static final int CONTACT_PICKER_RESULT_AS_ACTION_CONTACT = 1001;
    private static final int CONTACT_PICKER_RESULT_AS_HELPER = 1002;
    private static final String TAG = "EditAlarmFragment";

    /**
     * Only one UtteranceProgressListener can be set, so re-route all calls to all attached VoiceElementBehaviours
     */
    public LocalUtteranceProgressListener utteranceProgressListener = new LocalUtteranceProgressListener();

    protected CheckBox requirePin, canCancelAlarm;
    protected TextView
            actionContactName,
            actionContactPhoneNumber;

    protected EditText
            title,
            characteristics,
            instructions;

    protected Button
            chooseActionContact,
            addHelper,
            showExample,
            save;

    protected LinearLayout helpersContainer;
    protected RelativeLayout actionContact;

    protected EditTextVoiceElementBehaviour
            titleVoiceElementBehaviour,
            characteristicsVoiceElementBehaviour,
            instructionsVoiceElementBehaviour;

    protected ButtonVoiceElementBehaviour
            chooseActionContactVoiceElementBehaviour,
            addHelperVoiceElementBehaviour,
            showExampleVoiceElementBehaviour,
            saveVoiceElementBehaviour;

    protected CheckBoxVoiceElementBehaviour
               requirePinVoiceElementBehaviour,
               canCancelAlarmVoiceElementBehaviour;

    protected Alarm alarm;
    protected ArrayList<Helper> helpers = new ArrayList<Helper>();

    public static EditAlarmFragment newInstance(int alarmId) {
        EditAlarmFragment editAlarmFragment = new EditAlarmFragment();

        Bundle b = new Bundle(1);
        b.putInt(EditAlarmActivity.EXTRA_ALARM_ID, alarmId);

        editAlarmFragment.setArguments(b);

        return editAlarmFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BusProvider.getBus().unregister(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionContactName = (TextView) view.findViewById(R.id.action_contact_name);
        actionContactPhoneNumber = (TextView) view.findViewById(R.id.action_contact_phone);

        getSherlockActivity().getTTS().setOnUtteranceProgressListener(utteranceProgressListener);

        title = (EditText) view.findViewById(R.id.title);
        titleVoiceElementBehaviour = new EditTextVoiceElementBehaviour("title", title, getSherlockActivity());
        utteranceProgressListener.listeners.add(titleVoiceElementBehaviour);

        characteristics = (EditText) view.findViewById(R.id.characteristics);
        characteristicsVoiceElementBehaviour = new EditTextVoiceElementBehaviour("characteristics", characteristics, getSherlockActivity());
        utteranceProgressListener.listeners.add(characteristicsVoiceElementBehaviour);

        instructions = (EditText) view.findViewById(R.id.instructions);
        instructionsVoiceElementBehaviour = new EditTextVoiceElementBehaviour("instructions", instructions, getSherlockActivity());
        utteranceProgressListener.listeners.add(instructionsVoiceElementBehaviour);

        chooseActionContact = (Button) view.findViewById(R.id.choose_contact);
        chooseActionContact.setOnClickListener(this);
        chooseActionContactVoiceElementBehaviour = new ButtonVoiceElementBehaviour("chooseActionContact", chooseActionContact, getSherlockActivity());
        utteranceProgressListener.listeners.add(chooseActionContactVoiceElementBehaviour);

        addHelper = (Button) view.findViewById(R.id.add_helper);
        addHelper.setOnClickListener(this);
        addHelperVoiceElementBehaviour = new ButtonVoiceElementBehaviour("addHelper", addHelper, getSherlockActivity());
        utteranceProgressListener.listeners.add(addHelperVoiceElementBehaviour);

//        showExample = (Button) view.findViewById(R.id.show_example);
//        showExample.setOnClickListener(this);
//        showExampleVoiceElementBehaviour = new ButtonVoiceElementBehaviour("showExample", showExample, getSherlockActivity());
//        utteranceProgressListener.listeners.add(showExampleVoiceElementBehaviour);

        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        saveVoiceElementBehaviour = new ButtonVoiceElementBehaviour("save", save, getSherlockActivity());
        utteranceProgressListener.listeners.add(saveVoiceElementBehaviour);

        requirePin = (CheckBox) view.findViewById(R.id.require_pin);
        requirePinVoiceElementBehaviour = new CheckBoxVoiceElementBehaviour("require_pin", requirePin, getSherlockActivity());
        utteranceProgressListener.listeners.add(requirePinVoiceElementBehaviour);

        canCancelAlarm = (CheckBox) view.findViewById(R.id.can_cancel_alarm);
        canCancelAlarmVoiceElementBehaviour = new CheckBoxVoiceElementBehaviour("can_candel_alarm", canCancelAlarm, getSherlockActivity());
        utteranceProgressListener.listeners.add(canCancelAlarmVoiceElementBehaviour);

        helpersContainer = (LinearLayout) view.findViewById(R.id.helpers);

        actionContact = (RelativeLayout) view.findViewById(R.id.action_contact);

        populateFields();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        int alarmId = 0;
        if (args != null) {
            if (args.containsKey(EditAlarmActivity.EXTRA_ALARM_ID)) {
                alarmId = args.getInt(EditAlarmActivity.EXTRA_ALARM_ID);
                alarm = getSherlockActivity().queryDbForId(Alarm.class, Integer.class, alarmId);
                L.i("Inited with alarm {0}", alarm);

                // fields will be populated when the view is created
            } else {
                L.e("The EXTRA_ALARM_ID was not found int the getArguments()");
            }
        } else { // else: do nothing: this is where the user likes to add a new alarm
            L.i("Inited with no alarm, leaving everything blank to create new alarm");
        }
    }

    protected void populateFields() {
        if (alarm != null) {
            title.setText(alarm.getTitle());
            characteristics.setText(alarm.getCharacteristics());
            instructions.setText(alarm.getInstructions());

            requirePin.setChecked(alarm.shouldEnterPinToCancel());
            //canCancelAlarm.setChecked(alarm.);

            actionContactName.setText(alarm.getActionContactName() != null ? alarm.getActionContactName() : "");
            actionContactPhoneNumber.setText(alarm.getActionContactPhoneNumber() != null ? alarm.getActionContactPhoneNumber() : "");

            if (alarm.getHelpers() != null) {
                helpers.addAll(alarm.getHelpers());
            }

        } else {
            L.w("Trying to populate fields when this.alarm == null!");
        }

        if (helpers != null) {
            if (helpers.size() != 0) {
                for (Helper helper : helpers) {
                    addHelperToScreen(helper);
                }
            }
        }
    }

    protected void addHelperToScreen(Helper helper) {
        View v = getSherlockActivity().getLayoutInflater().inflate(R.layout.settings_list_item, null);

        ((TextView) v.findViewById(R.id.title)).setText(helper.getName());
        ((TextView) v.findViewById(R.id.summary)).setText(helper.getPhoneNumber());

        helpersContainer.addView(v);
    }

    protected void saveAlarm() {

        L.d("Saving alarm {0}", title.getText().toString());

        if (alarm == null)
            alarm = new Alarm();

        alarm.setActionContactName(actionContactName.getText().toString());
        alarm.setActionContactPhoneNumber(actionContactPhoneNumber.getText().toString());
        alarm.setEnterPinToCancel(requirePin.isChecked());
        alarm.setCharacteristics(characteristics.getText().toString());
        alarm.setInstructions(instructions.getText().toString());
        alarm.setEnabled(true);
        alarm.setTitle(title.getText().toString());
        alarm.setCountdownLengthBeforeAlarmStarts(5);

        try {
            getSherlockActivity().getDao(Alarm.class, Integer.class).createOrUpdate(alarm);
        } catch (SQLException e) {
            L.e(e, "Could not create alarm. {0}");
            getSherlockActivity().finish();
            return;
        }

        // Prepare helpers
        if(helpers != null) {
            if(helpers.size() != 0) {
                if(alarm.getHelpers() == null) {
                    try {
                        ForeignCollection<Helper> emptyForeignCollection = getSherlockActivity().getDao(Alarm.class, Integer.class).getEmptyForeignCollection("helpers");
                        alarm.setHelpers(emptyForeignCollection);
                    } catch (SQLException e) {
                        L.e("Could not set an emptyForeignCollection<Helper>!");
                        e.printStackTrace();
                    }
                }

                ArrayList<Helper> createdHelpers = new ArrayList<Helper>();
                for (Helper helper : helpers) {
                    try {

                        if(helper.getId() != 0) {
                            // exists
                        } else {
                            helper.setAlarm(alarm);
                            getSherlockActivity().getDao(Helper.class, Integer.class).createIfNotExists(helper);
                        }
                    } catch (SQLException e) {
                        L.e("Could not insert helper");
                        e.printStackTrace();
                    }
                }
            }
        }

        Toast.makeText(getSherlockActivity(), "Alarm opgeslagen", Toast.LENGTH_SHORT).show();
        getSherlockActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.choose_contact:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT_AS_ACTION_CONTACT);
                break;

            case R.id.add_helper:
                Intent helperPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(helperPickerIntent, CONTACT_PICKER_RESULT_AS_HELPER);
                break;

            case R.id.save:
                saveAlarm();
                break;

            default:
                L.i("onClick ended up in default case");
        }
    }

    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {

        switch (event.getVoiceAction().getId()) {
            case 1: // title
                titleVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 2: // kenmerken
                characteristicsVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 3: // aanwijzingen
                instructionsVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 4: // helper toevoegen
                addHelperVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 5: // contactpersoon selecteren
                chooseActionContactVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 6: // opslaan
                saveVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 7:
                requirePinVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case 8:
                canCancelAlarmVoiceElementBehaviour.trigger(event.getMatch());
                break;
            case R.id.va_general_back:
                getSherlockActivity().onBackPressed();
                break;
            default:
                L.d("event ended up in default case! {0}", event);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_create_alarm, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT_AS_ACTION_CONTACT:

                    try {

                        Uri resultUri = data.getData();
                        String rawId = resultUri.getLastPathSegment();

                        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String[] projection = new String[]{
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        };
                        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?";
                        String[] selectionArgs = new String[]{rawId};

                        Cursor people = getSherlockActivity().getContentResolver().query(
                                uri,
                                projection,
                                selection,
                                selectionArgs,
                                null
                        );

                        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        people.moveToFirst();
                        do {
                            String name = people.getString(indexName);
                            String phoneNumber = people.getString(indexNumber);
                            actionContactName.setText(name == null ? "" : name);
                            actionContactPhoneNumber.setText(phoneNumber == null ? "" : phoneNumber);
                        } while (people.moveToNext());

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    break;
                case CONTACT_PICKER_RESULT_AS_HELPER:
                    int id = 0;
                    String name = "", phoneNumber = "";
                    boolean isAvailable = true, canUseMessageFunctionality = false;
                    double lat = 0, lon = 0;
                    float travelTimeToUser = 0, distanceToUser = 0;

                    try {

                        Uri resultUri = data.getData();
                        String rawId = resultUri.getLastPathSegment();

                        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String[] projection = new String[]{
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        };
                        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?";
                        String[] selectionArgs = new String[]{rawId};

                        Cursor people = getSherlockActivity().getContentResolver().query(
                                uri,
                                projection,
                                selection,
                                selectionArgs,
                                null
                        );

                        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        people.moveToFirst();
                        do {
                            name = people.getString(indexName);
                            phoneNumber = people.getString(indexNumber);
                        } while (people.moveToNext());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //getSherlockActivity().getDao(Helper.class, Integer.class).
                    Helper helper = new Helper(0, name, isAvailable, lat, lon, distanceToUser, travelTimeToUser, phoneNumber, canUseMessageFunctionality);

                    helpers.add(helper);

                    addHelperToScreen(helper);

                    break;
                default:
                    L.i("requestCode {0} ended up in the 'default' case of this switch!");
            }
        }
    }

    class LocalUtteranceProgressListener extends UtteranceProgressListener {

        public ArrayList<VoiceElementBehaviour> listeners = new ArrayList<VoiceElementBehaviour>();

        @Override
        public void onStart(String utteranceId) {
            if (listeners != null)
                if (listeners.size() != 0)
                    for (VoiceElementBehaviour veb : listeners)
                        veb.onStart(utteranceId);
        }

        @Override
        public void onDone(String utteranceId) {
            L.d("utteranceProgressListener onDone({0})", utteranceId);
            if (listeners != null) {
                if (listeners.size() != 0) {
                    for (VoiceElementBehaviour veb : listeners) {
                        veb.onDone(utteranceId);
                    }
                }
            }
        }

        @Override
        public void onError(String utteranceId) {
            if (listeners != null)
                if (listeners.size() != 0)
                    for (VoiceElementBehaviour veb : listeners)
                        veb.onError(utteranceId);
        }
    }
}