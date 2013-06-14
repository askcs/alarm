package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import nl.askcs.alarm.R;
import nl.askcs.alarm.ui.activity.AlarmActivity;

public class InitiateAlarmFragment extends BaseTabFragment implements View.OnClickListener {

    private ImageButton alarm;
    private Button followMe, callNationalEmergencyNumber;

    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new InitiateAlarmFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_main_helpers_title));
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
        followMe = (Button) view.findViewById(R.id.follow_me);
        callNationalEmergencyNumber = (Button) view.findViewById(R.id.call_national_emergency_number);

        alarm.setOnClickListener(this);
        followMe.setOnClickListener(this);
        callNationalEmergencyNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm:
                startActivity(
                        new Intent(getActivity(), AlarmActivity.class)
                                .putExtra(AlarmActivity.EXTRA_ALARM_ID, 1));
                break;
            case R.id.follow_me:

                break;
            case R.id.call_national_emergency_number:
                Toast.makeText(getActivity(), "Calling the national emergency number is prohibited, because it is " +
                        "illegal to do it just for a test. You can go to jail for it.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
