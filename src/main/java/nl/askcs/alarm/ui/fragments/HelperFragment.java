package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import nl.askcs.alarm.R;
import nl.askcs.alarm.models.Helper;
import nl.askcs.alarm.ui.adapters.HelperListAdapter;

import java.util.ArrayList;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

public class HelperFragment extends BaseTabListFragment {

    private HelperListAdapter mAdapter;

    public static BaseTabListFragment getInstance(Context context) {
        BaseTabListFragment fragment = new HelperFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_main_helpers_title));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Helper> helpers = new ArrayList<Helper>(4);

        for (int i = 0; i < 4; i++) {
            helpers.add(new Helper(i, "Helper " + i, true, 0, 0, 12f, 30000000, "0612345678", true));
        }

        mAdapter = new HelperListAdapter(getActivity(), helpers);

        setListAdapter(mAdapter);
    }
}
