package nl.askcs.alarm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.StartAlarmEvent;
import nl.askcs.alarm.event.VoiceElementTriggeredEvent;
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

    private ViewPager viewPager;
    private PageIndicator pageIndicator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getBus().register(this);

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

        // Setup one test alarm for this mock-up
        Alarm a = new Alarm(1, "My Alarm", "", "", "", false, 5, 5, 5);
        try {
            getDao(Alarm.class, Integer.class).createIfNotExists(a);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
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
                ((BaseTabFragment) ((TabFragmentAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()))
                        .activateSpeechRecognition();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onVoiceElementTriggered(VoiceElementTriggeredEvent event) {
        super.onVoiceElementTriggered(event);

        switch (event.getId()) {
            case R.id.alarm:
                BusProvider.getBus().post(new StartAlarmEvent(1));
                break;
        }
    }

    @Subscribe
    public void onStartAlarmEvent(StartAlarmEvent event) {
        L.i("MainActivity", "received Event: {0}", event.getClass().getName());
        startActivity(new Intent(this, AlarmActivity.class).putExtra(AlarmActivity.EXTRA_ALARM_ID, event.getAlarmId()));
    }
}
