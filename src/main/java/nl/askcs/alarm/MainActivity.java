package nl.askcs.alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.fragments.*;

/**
 * The default entrypoint for the user via the App Drawer. Hosts 5 fragments where the user can
 * (1) initiate an alarm ({@link InitiateAlarmFragment}),
 * (2) look up a list of its assigned helpers ({@link HelperFragment}),
 * (3) search on a map for nearby helpers (only visible when the helper allows that) ({@link MapFragment}),
 * (4) see its inbox ({@link MessagesOverviewFragment}) and
 * (5) see a list of received notifications ({@link NotificationFragment}).
 *
 * @author : Leon Joosse
 *
 */
public class MainActivity extends SherlockFragmentActivity {

    private MainActivityFragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private PageIndicator pageIndicator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_host);

        fragmentAdapter = new MainActivityFragmentAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentAdapter);

        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
    }

    /**
     * A {@link FragmentStatePagerAdapter} to manage all fragments within the {@link MainActivity}.
     */
    class MainActivityFragmentAdapter extends FragmentStatePagerAdapter {

        public MainActivityFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new InitiateAlarmFragment();
                case 1: return new HelperFragment();
                case 2: return new MapFragment();
                case 3: return new MessagesOverviewFragment();
                case 4: return new NotificationFragment();
                default:
                    throw new RuntimeException("Fragment does not exist. ");
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.frag_main_initiate_alarm_title);
                case 1: return getString(R.string.frag_main_helpers_title);
                case 2: return getString(R.string.frag_main_maps_title);
                case 3: return getString(R.string.frag_main_messages_overview_title);
                case 4: return getString(R.string.frag_main_notifications_title);
                default:
                    throw new RuntimeException("Fragment does not exist. ");
            }
        }
    }
}
