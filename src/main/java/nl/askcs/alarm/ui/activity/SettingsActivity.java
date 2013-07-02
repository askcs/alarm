package nl.askcs.alarm.ui.activity;

import android.R;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import nl.askcs.alarm.ui.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, new SettingsFragment());
        ft.commit();
    }
}
