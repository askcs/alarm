package nl.askcs.alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.CountdownToAlarmFinishedEvent;
import nl.askcs.alarm.event.CountdownToAlarmStartEvent;
import nl.askcs.alarm.event.StopAlarmEvent;
import nl.askcs.alarm.fragments.*;
import nl.askcs.alarm.models.Alarm;

/**
 * This Activity manages the {@link Alarm} that is currently going off. If the Alarm has a countdown time specified,
 * the countdown will run first. When no countdown time is specified, fire the alarm immediately. Cancelling the Alarm
 * should be prevented in some cases, that's when the user has to enter a PIN code. The PIN code functionality is not
 * implemented yet.
 */
public class AlarmActivity extends SherlockFragmentActivity {

    /**
     * FragmentAdapter hosting all tabs ({@link AlarmInfoFragment}, {@link MapFragment}, {@link MessagesOverviewFragment},
     * {@link NotificationFragment}), while not displaying the countdown.
     */
    private AlarmFragmentAdapter alarmFragmentAdapter;

    /**
     * FragmentAdapter hosting the countdown ({@link CountdownToAlarmFragment}) tab.
     */
    private CountdownToAlarmFragmentAdapter countdownToAlarmFragmentAdapter;

    /**
     * The ViewPager for hosting the {@code alarmFragmentAdapter} or {@code countdownToAlarmFragmentAdapter}
     */
    private ViewPager viewPager;

    /**
     * The PageIndicator for displaying the tabbar ({@link TabPageIndicator}).
     */
    private PageIndicator pageIndicator;

    /**
     * The {@link Alarm} that this Activity manages
     */
    private Alarm alarm;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        BusProvider.getBus().register(this);

        alarmFragmentAdapter = new AlarmFragmentAdapter(getSupportFragmentManager());

        // If the alarm requires time to cancel the alarm before firing it, the amount of seconds is set in
        // alarm.getCountdownLengthBeforeAlarmStarts(). Otherwise, start the alarm immediately.

        // TODO: get Alarm instance from getIntent()
        alarm = new Alarm(0, "Mijn alarm", "Meh", "meh", "meh", false, 5, 5, 5);

        if(alarm.getCountdownLengthBeforeAlarmStarts() != 0) {
            countdownToAlarmFragmentAdapter = new CountdownToAlarmFragmentAdapter(getSupportFragmentManager());
            setCountdownLayout();
        } else {
            setAlarmLayout();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusProvider.getBus().unregister(this);
    }

    /**
     * Displays and fires the countdown using the CountdownToAlarmFragmentAdapter
     */
    private void setCountdownLayout() {

        setContentView(R.layout.viewpager_host);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(countdownToAlarmFragmentAdapter);

        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);

        BusProvider.getBus().post(new CountdownToAlarmStartEvent(alarm));
    }

    /**
     * Displays the Alarm layout using the AlarmFragmentAdapter
     */
    private void setAlarmLayout() {

        setContentView(R.layout.viewpager_host);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(alarmFragmentAdapter);

        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
    }

    /**
     * Called by Otto when someone fires the CountdownToAlarmFinishedEvent.
     * Indicates the countdown is finished by the {@link CountdownToAlarmFragment}.
     * @param event
     */
    @Subscribe
    public void onCountdownToAlarmFinished(CountdownToAlarmFinishedEvent event) {
        setAlarmLayout();
    }

    /**
     * Called by Otto when someone fires the {@link StopAlarmEvent}. Indicates the alarm should be cancelled. If the
     * Alarm requires the user to enter a PIN code {@link nl.askcs.alarm.models.Alarm#shouldEnterPinToCancel()}, ask
     * the user to enter the PIN code. Only when the valid PIN code is entered, the alarm should be cancelled.
     * @param event The event with the Alarm instance
     */
    @Subscribe
    public void onStopAlarmRequest(StopAlarmEvent event) {

        // Is the user required to enter a PIN code to stop the alarm?
        if(alarm.shouldEnterPinToCancel()) {
            // PIN code functionality not available yet
        } else {
            finish();
        }
    }

    class CountdownToAlarmFragmentAdapter extends FragmentStatePagerAdapter {

        public CountdownToAlarmFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new CountdownToAlarmFragment();
                default:
                    throw new RuntimeException("Fragment with this position does not exist. ");
            }
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.frag_alarm_info_title);
                default:
                    throw new RuntimeException("Fragment with this position does not exist. ");
            }
        }
    }

    class AlarmFragmentAdapter extends FragmentStatePagerAdapter {

        public AlarmFragmentAdapter(FragmentManager fm) {
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
