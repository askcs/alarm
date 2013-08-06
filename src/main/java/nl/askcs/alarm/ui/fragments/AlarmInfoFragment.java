package nl.askcs.alarm.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import nl.askcs.alarm.R;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.models.Helper;
import nl.askcs.alarm.ui.activity.AlarmActivity;
import nl.askcs.alarm.ui.adapters.HelperListAdapter;
import nl.askcs.alarm.util.L;

import java.util.ArrayList;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

public class AlarmInfoFragment extends BaseTabFragment implements View.OnClickListener {

    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new AlarmInfoFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_alarm_info_title));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_alarm_info, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout helperContainer = (LinearLayout) view.findViewById(R.id.helpers_on_their_way);
        HelperListAdapter hla = new HelperListAdapter(getSherlockActivity(), new ArrayList<Helper>(getAlarm().getHelpers()));

        for(int i = 0; i < hla.getCount(); i++) {
            helperContainer.addView(hla.getView(i, null, null));
        }

        // Title
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getAlarm().getTitle());

        // contact name
        TextView actionContact = (TextView) view.findViewById(R.id.action_contact_name);
        actionContact.setText(getAlarm().getActionContactName());

        // characteristics
        TextView characteristics = (TextView) view.findViewById(R.id.characteristics);
        characteristics.setText(getAlarm().getCharacteristics());

        // instructions
        TextView instructions = (TextView) view.findViewById(R.id.instructions);
        instructions.setText(getAlarm().getInstructions());

        view.findViewById(R.id.action_call_contact).setOnClickListener(this);
        view.findViewById(R.id.action_button_call_national_alarm_number).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.action_button_call_national_alarm_number:
                Toast.makeText(getSherlockActivity(), "Einde scenario! Telefoongesprek naar 112 niet gestart omdat dit een test is.", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_call_contact:
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getAlarm().getActionContactPhoneNumber()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Toast.makeText(getSherlockActivity(), "Einde scenario! Telefoongesprek niet gestart omdat dit een test is.", Toast.LENGTH_LONG).show();
                    L.e(activityException, "Call failed!");
                }
                break;
        }
    }

    @Override
    public AlarmActivity getSherlockActivity() {
        return (AlarmActivity) super.getSherlockActivity();
    }

    protected Alarm getAlarm() {
        return this.getSherlockActivity().getAlarm();
    }
}
