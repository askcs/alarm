package nl.askcs.alarm.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import nl.askcs.alarm.adapters.HelperListAdapter;
import nl.askcs.alarm.models.Helper;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
public class HelperFragment extends ListFragment {

    private HelperListAdapter mAdapter;

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
