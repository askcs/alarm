package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nl.askcs.alarm.R;

import static nl.askcs.alarm.ui.TabFragmentAdapter.ARG_TAB_TITLE;

public class AlarmInfoFragment extends BaseTabFragment {

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
}
