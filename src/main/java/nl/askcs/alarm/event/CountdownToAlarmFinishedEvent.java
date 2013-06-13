package nl.askcs.alarm.event;

import nl.askcs.alarm.models.Alarm;

/**
 * An event to indicate a countdown for an alarm finished. The given alarm should go off after this event.
 */
public class CountdownToAlarmFinishedEvent {

    // The alarm that has to go off
    public final Alarm alarm;

    /**
     *
     *
     * @param alarm The alarm that has to go off
     */
    public CountdownToAlarmFinishedEvent(Alarm alarm) {
        this.alarm = alarm;
    }
}
