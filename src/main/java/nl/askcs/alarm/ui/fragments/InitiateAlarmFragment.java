package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.StartAlarmEvent;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.activity.SettingsActivity;
import nl.askcs.alarm.util.L;

import java.sql.SQLException;
import java.util.ArrayList;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

/**
 * Displays the Alarm button, Follow Me button and Call 112 button. A notification is shown if no Alarm is configured
 * yet.
 */
public class InitiateAlarmFragment extends BaseTabFragment implements View.OnClickListener {

    private static final String TAG = "InitiateAlarmFragment";

    private ImageButton alarm;
    private Button followMe, callNationalEmergencyNumber, addNewAlarm;
    private LinearLayout newAlarmIfNoneSet;

    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new InitiateAlarmFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_main_initiate_alarm_title));
        fragment.setArguments(args);

        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_initiate_alarm, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarm = (ImageButton) view.findViewById(R.id.alarm);
        alarm.setOnClickListener(this);

        addNewAlarm = (Button) view.findViewById(R.id.button_new_alarm_from_initiate_alarm);
        addNewAlarm.setOnClickListener(this);

        followMe = (Button) view.findViewById(R.id.follow_me);
        followMe.setOnClickListener(this);

        callNationalEmergencyNumber = (Button) view.findViewById(R.id.call_national_emergency_number);
        callNationalEmergencyNumber.setOnClickListener(this);

        newAlarmIfNoneSet = (LinearLayout) view.findViewById(R.id.add_new_alarm);

        alarm.setEnabled(false);
        followMe.setEnabled(false);

        long amountOfAlarms = 0;

        try {
            amountOfAlarms = getSherlockActivity().getDao(Alarm.class, Integer.class).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(amountOfAlarms == 0) {
            newAlarmIfNoneSet.setVisibility(View.VISIBLE);
            alarm.setVisibility(View.GONE);
            alarm.post(new Runnable() {
                @Override
                public void run() {
                    getTTS().speak("Welkom bij de Alarm App. Er is nog geen alarm geconfigureerd. " +
                            "Activeer de spraakherkenning en spreek het commando 'instellingen' om naar instellingen " +
                            "te gaan.", TextToSpeech.QUEUE_FLUSH, null);
                }
            });

        } else {
            newAlarmIfNoneSet.setVisibility(View.GONE);
            alarm.setVisibility(View.VISIBLE);

            alarm.setEnabled(true);
            followMe.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        long amountOfAlarms = 0;
//
//        try {
//            amountOfAlarms = getSherlockActivity().getDao(Alarm.class, Integer.class).countOf();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if(amountOfAlarms == 0) {
//            newAlarmIfNoneSet.setVisibility(View.VISIBLE);
//            alarm.setVisibility(View.GONE);
//        } else {
//            newAlarmIfNoneSet.setVisibility(View.GONE);
//            alarm.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onVoiceElementTriggered(int id, String match, ArrayList<String> possibleMatches) {
        super.onVoiceElementTriggered(id, match, possibleMatches);

        switch (id) {
            case R.id.alarm:
                boolean listenerAvailable = alarm.performClick();
                L.i("listener is available: {0}", Boolean.toString(listenerAvailable));
                break;
            // default: // do nothing
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm:
                BusProvider.getBus().post(new StartAlarmEvent(1));
                break;
            case R.id.follow_me:

                break;
            case R.id.call_national_emergency_number:
                Toast.makeText(getActivity(), "Calling the national emergency number for a test is prohibited. You " +
                        "can go to jail for it.", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_new_alarm_from_initiate_alarm:
                startActivity(new Intent(getSherlockActivity(), SettingsActivity.class));
                break;
            default:
                L.d("onClick ended up in default case");
        }
    }
}
