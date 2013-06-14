package nl.askcs.alarm.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.R;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.TabFragmentAdapter;
import nl.askcs.alarm.ui.fragments.*;

import java.sql.SQLException;

/**
 * The default entrypoint for the user via the App Drawer. Hosts 5 fragments where the user can
 * (1) initiate an alarm ({@link nl.askcs.alarm.ui.fragments.InitiateAlarmFragment}),
 * (2) look up a list of its assigned helpers ({@link nl.askcs.alarm.ui.fragments.HelperFragment}),
 * (3) search on a map for nearby helpers (only visible when the helper allows that) ({@link nl.askcs.alarm.ui.fragments.MapFragment}),
 * (4) see its inbox ({@link nl.askcs.alarm.ui.fragments.MessagesOverviewFragment}) and
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
}
