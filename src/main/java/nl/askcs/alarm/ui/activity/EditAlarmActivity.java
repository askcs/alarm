package nl.askcs.alarm.ui.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.VoiceActionTriggeredEvent;
import nl.askcs.alarm.ui.fragments.EditAlarmFragment;

public class EditAlarmActivity extends BaseActivity {

    public static final String EXTRA_ALARM_ID = "alarm_id";
    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getBus().register(this);

        Bundle b = getIntent().getExtras();
        alarmId = 0;

        if(b != null) {
            alarmId = b.getInt(EXTRA_ALARM_ID, 0);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if(alarmId > 0) {
            ft.add(android.R.id.content, EditAlarmFragment.newInstance(alarmId));
        } else {
            ft.add(android.R.id.content, new EditAlarmFragment());
        }

        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusProvider.getBus().unregister(this);
        getVoiceActionManager().getVoiceActions().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_edit_alarm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.speak:
                getTTS().stop();
                activateSpeechRecognition(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {
        super.onVoiceActionTriggered(event);
    }
}
