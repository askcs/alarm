package nl.askcs.alarm.event;

import nl.askcs.alarm.models.Alarm;

/**
 * An event to indicate a countdown for an alarm should start.
 */
public class CountdownToAlarmStartEvent {

    // The alarm that has to go off after the countdown is finished
    public final Alarm alarm;

    /**
     *
     *
     * @param alarm The alarm that has to go off after the countdown is finished
     */
    public CountdownToAlarmStartEvent(Alarm alarm) {
        this.alarm = alarm;
    }
}
