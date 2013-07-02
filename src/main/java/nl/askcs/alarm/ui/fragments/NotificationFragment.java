package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import nl.askcs.alarm.R;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

public class NotificationFragment extends BaseTabListFragment {

    public static BaseTabListFragment getInstance(Context context) {
        BaseTabListFragment fragment = new NotificationFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_alarm_notifications_title));
        fragment.setArguments(args);

        return fragment;
    }
}
