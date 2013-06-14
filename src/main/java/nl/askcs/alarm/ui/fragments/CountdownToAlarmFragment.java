package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 13-6-13
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
public class CountdownToAlarmFragment extends BaseTabFragment {

    private Timer countdownTimer;
    private TextView countdownLabel;
    private int countdownSecondsInitial, countdownSecondsToGo;
    private Handler countdownHandler;
    private boolean countdownDone;
    private Button cancel;
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
                BusProvider.getBus().post(new StopAlarmEvent());
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

        if (!countdownDone)
            countdownTimer.cancel();
        countdownTimer = null;
    }

    private void startCountdown() {
        countdownTimer = new Timer();
        countdownSecondsInitial = countdownSecondsToGo;
        countdownLabel.setText(Integer.toString(countdownSecondsInitial));
        countdownTimer.schedule(countdownTimerTask, 1000, 1000);
    }

    private void onCountdownDone() {

        countdownDone = true;

        if (countdownTimer != null)
            countdownTimer.cancel();

        countdownTimer = null;

        BusProvider.getBus().post(new CountdownToAlarmFinishedEvent(alarm));
    }

    private TimerTask countdownTimerTask = new TimerTask() {

        @Override
        public void run() {
            if (countdownLabel != null) {
                countdownHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        countdownLabel.setText(Integer.toString(countdownSecondsToGo));
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
