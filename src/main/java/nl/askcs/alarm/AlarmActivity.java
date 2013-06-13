package nl.askcs.alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.fragments.*;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class AlarmActivity extends SherlockFragmentActivity {

    private AlarmActivityFragmentAdapter fragmentAdapter;
    private ViewPager viewPager;
    private PageIndicator pageIndicator;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_host);

        fragmentAdapter = new AlarmActivityFragmentAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentAdapter);

        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
    }

    class AlarmActivityFragmentAdapter extends FragmentStatePagerAdapter {

        public AlarmActivityFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new AlarmInfoFragment();
                case 1: return new MapFragment();
                case 2: return new MessagesOverviewFragment();
                case 3: return new NotificationFragment();
                default:
                    throw new RuntimeException("Fragment with this position does not exist. ");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.frag_alarm_info_title);
                case 1: return getString(R.string.frag_alarm_maps_title);
                case 2: return getString(R.string.frag_alarm_messages_overview_title);
                case 3: return getString(R.string.frag_alarm_notifications_title);
                default:
                    throw new RuntimeException("Fragment with this position does not exist. ");
            }
        }
    }
}
