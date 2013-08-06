package nl.askcs.alarm.event;

/**
 * Fired when an {@link nl.askcs.alarm.models.Alarm} is about to stop.
 */
public class StopAlarmEvent {

    private int alarmId;

    public StopAlarmEvent() {
        // Empty constructor for Otto
    }

    public StopAlarmEvent(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmId() {
        return alarmId;
    }
}
