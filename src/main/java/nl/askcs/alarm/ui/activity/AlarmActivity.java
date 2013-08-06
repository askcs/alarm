package nl.askcs.alarm.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import com.viewpagerindicator.TabPageIndicator;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.*;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.models.Helper;
import nl.askcs.alarm.ui.adapters.TabFragmentAdapter;
import nl.askcs.alarm.ui.fragments.*;
import nl.askcs.alarm.util.L;
import nl.askcs.alarm.voice.ButtonVoiceElementBehaviour;
import nl.askcs.alarm.voice.TextViewVoiceElementBehaviour;

import java.util.ArrayList;

/**
 * This Activity manages the {@link Alarm} that is currently going off. If the Alarm has a countdown time specified,
 * the countdown will run first. When no countdown time is specified, the Alarm is fired immediately. Cancelling the
 * Alarm should be prevented in some cases, that's when the user has to enter a PIN code. <br /><b>Note: </b>The PIN code functionality is
 * not implemented yet.
 */
public class AlarmActivity extends BaseActivity {

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

        BusProvider.getBus().register(this);

        setContentView(R.layout.viewpager_host);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));
        pageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        int alarmId = 0;
        if (extras != null) {
            if (extras.containsKey(EXTRA_ALARM_ID)) {
                alarmId = extras.getInt(EXTRA_ALARM_ID);
                alarm = queryDbForId(Alarm.class, Integer.class, alarmId);
            } else {
                L.e("The EXTRA_ALARM_ID was not found int the intent.getExtras()! " +
                        "We need that to fire an alarm!");
            }
        } else {
            L.e("The EXTRA_ALARM_ID was not found int the intent.getExtras(), because the intent.getExtras() " +
                    "is null! We need that to fire an alarm!");
        }

        if (alarmId == 0) {
            throw new RuntimeException("No alarm provided! ");
        } // just continue as normal

        if (alarm.getCountdownLengthBeforeAlarmStarts() != 0) {
            setCountdownLayout();
        } else {

            countdownFinished = true;
            setAlarmLayout();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getBus().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_COUNTDOWN_FINISHED, countdownFinished);
        //outState.putInt(STATE_CURRENT_TAB_INDEX, );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getSupportMenuInflater().inflate(R.menu.alarm, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.speak:
                getTTS().stop();
                activateSpeechRecognition(true);
                return true;
            case R.id.cancel_alarm:
                BusProvider.getBus().post(new StopAlarmEvent(alarm.getId()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void setAlarmLayout() {

        viewPager.setAdapter(
                new TabFragmentAdapter(getSupportFragmentManager(),
                        AlarmInfoFragment.getInstance(this),
                        MapFragment.getInstance(this),
                        MessagesOverviewFragment.getInstance(this),
                        NotificationFragment.getInstance(this)));

        pageIndicator.notifyDataSetChanged();

        getVoiceActionManager().addVoiceAction(this, R.id.va_alarm_read_all_helpers, R.string.va_alarm_read_all_helpers);
        getVoiceActionManager().addVoiceAction(this, R.id.va_alarm_exit, R.string.va_alarm_exit);

        String speak;

        speak = alarm.getTitle();
        speak += ". Instructies. Bij deze kenmerken: " + getAlarm().getCharacteristics() + ". Doe het volgende: " + getAlarm().getInstructions();

        getTTS().speak(speak, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Subscribe
    public void onVoiceActionTriggered(VoiceActionTriggeredEvent event) {
        super.onVoiceActionTriggered(event);

        switch(event.getVoiceAction().getId()) {
            case 10: // title
                new TextViewVoiceElementBehaviour("title", (TextView) findViewById(R.id.title), this).trigger(event.getMatch());
                break;
            case 11: // aanwijzingen
                new TextViewVoiceElementBehaviour("aanwijzingen", (TextView) findViewById(R.id.characteristics), this).trigger(event.getMatch());
                break;
            case 12: // instructies
                new TextViewVoiceElementBehaviour("instructies", (TextView) findViewById(R.id.instructions), this).trigger(event.getMatch());
                break;
            case 13: // bel contact
                new ButtonVoiceElementBehaviour("bel_contact", (Button) findViewById(R.id.action_call_contact), this).trigger(event.getMatch());
                try {
                    getTTS().stop();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + alarm.getActionContactPhoneNumber()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Toast.makeText(this, "Telefoongesprek kan niet worden gestart. Telefoon app niet gevonden", Toast.LENGTH_LONG).show();
                    L.e(activityException, "Call failed!");
                }
                break;
            case 14: // bel 112
                new ButtonVoiceElementBehaviour("bel_112", (Button) findViewById(R.id.action_button_call_national_alarm_number), this).trigger(event.getMatch());
                getTTS().speak("Telefoongesprek naar 112 niet gestart omdat dit een test is.", TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(this, "Telefoongesprek naar 112 niet gestart omdat dit een test is.", Toast.LENGTH_LONG).show();
                break;
            case R.id.va_alarm_read_all_helpers:
                ArrayList<Helper> helpers = new ArrayList<Helper>(alarm.getHelpers());
                if(helpers != null) {
                    if(helpers.size() != 0) {
                        String speak = event.getMatch() + ". Er zijn " + helpers.size() + " helpers onderweg. Dat zijn: ";

                        for (Helper helper : helpers) {
                            speak += helper.getName() + ", ";
                        }

                        speak += ". Einde helperlijst.";

                        getTTS().speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        getTTS().speak(event.getMatch() + ". Er zijn nog geen helpers onderweg", TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    getTTS().speak(event.getMatch() + ". Er zijn nog geen helpers onderweg", TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            case R.id.va_alarm_exit:
                BusProvider.getBus().post(new StopAlarmEvent(alarm.getId()));
                break;
        }
    }

    /**
     * Called by Otto when someone fires the CountdownToAlarmFinishedEvent.
     * Indicates the countdown is finished by the {@link nl.askcs.alarm.ui.fragments.CountdownToAlarmFragment}.
     *
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
     *
     * @param event The event with the Alarm instance
     */
    @Subscribe
    public void onStopAlarmRequest(StopAlarmEvent event) {

        // Is the user required to enter a PIN code to stop the alarm?
        if (alarm.shouldEnterPinToCancel()) {
            // PIN code functionality not available yet
            finish();
        } else {
            finish();
        }
    }
}
