package nl.askcs.alarm.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.CountdownToAlarmFinishedEvent;
import nl.askcs.alarm.event.CountdownToAlarmStartEvent;
import nl.askcs.alarm.event.StopAlarmEvent;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.TabFragmentAdapter;
import nl.askcs.alarm.ui.fragments.*;

/**
 * This Activity manages the {@link Alarm} that is currently going off. If the Alarm has a countdown time specified,
 * the countdown will run first. When no countdown time is specified, fire the alarm immediately. Cancelling the Alarm
 * should be prevented in some cases, that's when the user has to enter a PIN code. The PIN code functionality is not
 * implemented yet.
 */
public class AlarmActivity extends BaseActivity {

    public static final String TAG = "AlarmActivity";
    public static final String EXTRA_ALARM_ID = "alarm_id";
    public static final String STATE_COUNTDOWN_FINISHED = "state_countdown_finished";
    public static final String STATE_CURRENT_TAB_INDEX = "state_current_tab_index";

    /**
     * The ViewPager for hosting the {@code fragmentAdapter} or {@code countdownToAlarmFragmentAdapter}
     */
    private ViewPager viewPager;

    /**
     * The PageIndicator for displaying the tabbar ({@link TabPageIndicator}).
     */
    private TabPageIndicator pageIndicator;

    /**
     * The {@link Alarm} that this Activity manages
     */
    private Alarm alarm;

    /**
     * Boolean to indicate whether the countdown before the alarm is fired is finished. If no countdown time is
     * specified, this value is true.
     */
    private boolean countdownFinished;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(R.layout.viewpager_host);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));
        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        int alarmId = 0;
        if(extras != null) {
            if(extras.containsKey(EXTRA_ALARM_ID)) {
                alarmId = extras.getInt(EXTRA_ALARM_ID);
                alarm = queryDbForId(Alarm.class, Integer.class, alarmId);
            } else {
                Log.e(TAG, "The EXTRA_ALARM_ID was not found int the intent.getExtras()! " +
                        "We need that to fire an alarm!");
            }
        } else {
            Log.e(TAG, "The EXTRA_ALARM_ID was not found int the intent.getExtras(), because the intent.getExtras() " +
                    "is null! We need that to fire an alarm!");
        }

        if(alarmId == 0) {
            // TODO: catch this exception
        } else {

        }

        if(alarm.getCountdownLengthBeforeAlarmStarts() != 0) {
            setCountdownLayout();
        } else {

            countdownFinished = true;
            setAlarmLayout();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_COUNTDOWN_FINISHED, countdownFinished);
        //outState.putInt(STATE_CURRENT_TAB_INDEX, );
    }

    public Alarm getAlarm() {
        return alarm;
    }

    /**
     * Displays and fires the countdown using the CountdownToAlarmFragmentAdapter
     */
    private void setCountdownLayout() {

        viewPager.setAdapter(
                new TabFragmentAdapter(getSupportFragmentManager(),
                        CountdownToAlarmFragment.getInstance(this)));

        pageIndicator.notifyDataSetChanged();

        BusProvider.getBus().post(new CountdownToAlarmStartEvent(alarm));
    }

    /**
     * Displays the Alarm layout using the AlarmFragmentAdapter
     */
    private void setAlarmLayout() {

        viewPager.setAdapter(
                new TabFragmentAdapter(getSupportFragmentManager(),
                        AlarmInfoFragment.getInstance(this),
                        MapFragment.getInstance(this),
                        MessagesOverviewFragment.getInstance(this),
                        NotificationFragment.getInstance(this)));

        pageIndicator.notifyDataSetChanged();
    }

    /**
     * Called by Otto when someone fires the CountdownToAlarmFinishedEvent.
     * Indicates the countdown is finished by the {@link nl.askcs.alarm.ui.fragments.CountdownToAlarmFragment}.
     * @param event
     */
    @Subscribe
    public void onCountdownToAlarmFinished(CountdownToAlarmFinishedEvent event) {
        countdownFinished = true;
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
}
