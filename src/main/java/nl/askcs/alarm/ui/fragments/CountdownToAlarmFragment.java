package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import nl.askcs.alarm.R;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.event.CountdownToAlarmFinishedEvent;
import nl.askcs.alarm.event.StopAlarmEvent;
import nl.askcs.alarm.models.Alarm;

import java.util.Timer;
import java.util.TimerTask;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

/**
 * Displays a countdown, giving the user the ability to stop the Alarm if it was triggered by accident. Also utters
 * the amount of seconds left every second.
 */
public class CountdownToAlarmFragment extends BaseTabFragment {

    // Timer for the countdown
    private Timer countdownTimer;

    // TextView to show the user how many seconds are left
    private TextView countdownLabel;

    // Amount of seconds that the countdown will last
    private int countdownSecondsInitial;

    // Amount of seconds to fo before the countdown is done
    private int countdownSecondsToGo;

    // Handler for posting tasks from the Timer
    private Handler countdownHandler;

    // Whether the countdown is finished or not
    private boolean countdownDone;

    // The user can cancel the countdown with this button. Cancelling could require verification via a PIN code.
    private Button cancel;

    // The alarm related to this countdown
    private Alarm alarm;


    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new CountdownToAlarmFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_alarm_info_title));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sis) {
        return inflater.inflate(R.layout.frag_countdown_to_alarm, null);
    }

    @Override
    public void onViewCreated(View view, Bundle sis) {
        super.onViewCreated(view, sis);

        countdownLabel = (TextView) view.findViewById(R.id.countdown_label);

        cancel = (Button) view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new StopAlarmEvent(1));
            }
        });

        countdownHandler = new Handler();

        countdownSecondsToGo = sis != null ? sis.containsKey("countdown-seconds-to-go") ? sis.getInt("countdown-seconds-to-go") : 5 : 5;
        countdownDone = sis != null ? sis.containsKey("countdown-done") ? sis.getBoolean("countdown-done") : false : false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (countdownDone) {
            onCountdownDone();
        } else {
            startCountdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Cancel the countdown
        // TODO: is this required behaviour?
        if (!countdownDone)
            countdownTimer.cancel();
        countdownTimer = null;
    }

    /**
     * Init and fire up the countdown
     */
    private void startCountdown() {
        countdownTimer = new Timer();
        countdownSecondsInitial = countdownSecondsToGo;
        countdownLabel.setText(Integer.toString(countdownSecondsInitial));
        countdownTimer.schedule(countdownTimerTask, 1000, 1000);

        getTTS().speak(Integer.toString(countdownSecondsInitial), TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Called when the Timer reached 0. Fire CountdownToAlarmFinishedEvent to let the Activity know it is time to
     * show the alarm information
     */
    private void onCountdownDone() {

        countdownDone = true;

        if (countdownTimer != null)
            countdownTimer.cancel();

        countdownTimer = null;

        BusProvider.getBus().post(new CountdownToAlarmFinishedEvent(alarm));
    }

    /**
     * Called every 1000 ms when the countdown is running
     */
    private TimerTask countdownTimerTask = new TimerTask() {

        @Override
        public void run() {
            if (countdownLabel != null) {
                countdownHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        // Show the remaining seconds to the user
                        countdownLabel.setText(Integer.toString(countdownSecondsToGo));

                        // Don't speak when the 0 is reached. The fragment will be destroyed, so the speech will be cut-off
                        if (countdownSecondsToGo != 0) {
                            getTTS().speak(Integer.toString(countdownSecondsToGo), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }

            if (countdownSecondsToGo == 0) {
                countdownHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        onCountdownDone();
                    }
                });
            } else {
                countdownSecondsToGo--;
            }
        }
    };
}
