package nl.askcs.alarm.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nl.askcs.alarm.R;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class AlarmInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_alarm_info, null);
    }
}
