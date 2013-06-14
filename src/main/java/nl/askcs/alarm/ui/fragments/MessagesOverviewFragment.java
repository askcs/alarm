package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import nl.askcs.alarm.R;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
public class MessagesOverviewFragment extends BaseTabListFragment {

    public static BaseTabListFragment getInstance(Context context) {
        BaseTabListFragment fragment = new MessagesOverviewFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_alarm_messages_overview_title));
        fragment.setArguments(args);

        return fragment;
    }
}
