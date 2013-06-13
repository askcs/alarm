package nl.askcs.alarm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import nl.askcs.alarm.AlarmActivity;
import nl.askcs.alarm.R;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class InitiateAlarmFragment extends Fragment implements View.OnClickListener {

    private ImageButton alarm;
    private Button followMe, callNationalEmergencyNumber;

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
                startActivity(new Intent(getActivity(), AlarmActivity.class));
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
