package nl.askcs.alarm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.*;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.adapters.TabFragmentAdapter;
import nl.askcs.alarm.ui.fragments.*;
import nl.askcs.alarm.util.L;

import java.sql.SQLException;

/**
 * The default entrypoint for the user via the App Drawer. Hosts 5 fragments where the user can <br />
 * (1) initiate an alarm ({@link nl.askcs.alarm.ui.fragments.InitiateAlarmFragment}), <br />
 * (2) look up a list of its assigned helpers ({@link nl.askcs.alarm.ui.fragments.HelperFragment}), <br />
 * (3) search on a map for nearby helpers (only visible when the helper allows that) ({@link nl.askcs.alarm.ui.fragments.MapFragment}), <br />
 * (4) see its inbox ({@link nl.askcs.alarm.ui.fragments.MessagesOverviewFragment}) and <br />
 * (5) see a list of received notifications ({@link nl.askcs.alarm.ui.fragments.NotificationFragment}).
 *
 * @author : Leon Joosse
 */
public class MainActivity extends BaseActivity {

    private final String TAG = getClass().getName();

    private ViewPager viewPager;
    private PageIndicator pageIndicator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getBus().register(this);

        getVoiceActionManager().addVoiceAction(this, R.id.va_main_go_to_alarm_tab,          R.string.va_main_go_to_alarm_tab);
        getVoiceActionManager().addVoiceAction(this, R.id.va_main_go_to_helper_tab,         R.string.va_main_go_to_helper_tab);
        getVoiceActionManager().addVoiceAction(this, R.id.va_main_go_to_map_tab,            R.string.va_main_go_to_map_tab);
        getVoiceActionManager().addVoiceAction(this, R.id.va_main_go_to_messages_tab,       R.string.va_main_go_to_messages_tab);
        getVoiceActionManager().addVoiceAction(this, R.id.va_main_go_to_notifications_tab,  R.string.va_main_go_to_notifications_tab);
        getVoiceActionManager().addVoiceAction(this, R.id.va_main_open_settings,            R.string.va_main_open_settings);

        setContentView(R.layout.viewpager_host);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager(),
                InitiateAlarmFragment.getInstance(this),
                HelperFragment.getInstance(this),
                MapFragment.getInstance(this),
                MessagesOverviewFragment.getInstance(this),
                NotificationFragment.getInstance(this)));

        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
        getVoiceActionManager().destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.speak:
                getTTS().stop();
                activateSpeechRecognition(true);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {
        super.onVoiceActionTriggered(event);

        switch (event.getVoiceAction().getId()) {
            case R.id.alarm:
                try {
                    BusProvider.getBus().post(new StartAlarmEvent(getDao(Alarm.class, Integer.class).queryForAll().get(0).getId()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No Alarm to be fired!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.va_main_go_to_alarm_tab:
                viewPager.setCurrentItem(0);
                break;
            case R.id.va_main_go_to_helper_tab:
                viewPager.setCurrentItem(1);
                break;
            case R.id.va_main_go_to_map_tab:
                viewPager.setCurrentItem(2);
                break;
            case R.id.va_main_go_to_messages_tab:
                viewPager.setCurrentItem(3);
                break;
            case R.id.va_main_go_to_notifications_tab:
                viewPager.setCurrentItem(4);
                break;
            case R.id.va_main_open_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.va_general_back:

                break;
            default:
                L.i("VoiceAction triggered, but not consumed: {0}", event.getVoiceAction());
        }
    }

    @Subscribe
    public void onStartAlarmEvent(StartAlarmEvent event) {
        L.i("MainActivity", "received Event: {0}", event.getClass().getName());
        startActivity(new Intent(this, AlarmActivity.class).putExtra(AlarmActivity.EXTRA_ALARM_ID, event.getAlarmId()));
    }

    @Override
    @Subscribe
    public void onSpeechHasNoMatchingVoiceAction(SpeechHasNoMatchingVoiceActionEvent event) {
        super.onSpeechHasNoMatchingVoiceAction(event);
    }

    @Override
    @Subscribe
    public void onUtteranceCompleted(RecognitionForCommandUtteranceCompletedEvent event) {
        super.onUtteranceCompleted(event);
    }
}
