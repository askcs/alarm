package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.StartAlarmEvent;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.util.VoiceAttributes;

import java.util.ArrayList;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

public class InitiateAlarmFragment extends BaseTabFragment implements View.OnClickListener {

    private static final String TAG = "InitiateAlarmFragment";

    private ImageButton alarm;
    private Button followMe, callNationalEmergencyNumber;

    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new InitiateAlarmFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_main_initiate_alarm_title));
        fragment.setArguments(args);

        return fragment;
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
        registerViewForVoiceAttributes(alarm, new VoiceAttributes("activeer alarm", "alarm"));

        followMe = (Button) view.findViewById(R.id.follow_me);
        followMe.setOnClickListener(this);
        registerViewForVoiceAttributes(followMe, new VoiceAttributes("activeer volg mij", "volg mij"));

        callNationalEmergencyNumber = (Button) view.findViewById(R.id.call_national_emergency_number);
        callNationalEmergencyNumber.setOnClickListener(this);
        registerViewForVoiceAttributes(callNationalEmergencyNumber, new VoiceAttributes("bel 1 1 2", "alarmnummer", "bel alarmnummer", "bel alarmnummer 1 1 2"));
    }

    @Override
    public void onVoiceElementTriggered(int id, String match, ArrayList<String> possibleMatches) {
        super.onVoiceElementTriggered(id, match, possibleMatches);

        switch(id) {
            case R.id.alarm:
                boolean listenerAvailable = alarm.performClick();
                L.i(TAG, "listener is available: {0}", Boolean.toString(listenerAvailable));
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
        }
    }
}
