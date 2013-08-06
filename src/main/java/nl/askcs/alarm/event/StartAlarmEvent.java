package nl.askcs.alarm.event;

/**
 * Fired when an {@link nl.askcs.alarm.models.Alarm} is about to start.
 */
public class StartAlarmEvent {

    private int alarmId;

    public StartAlarmEvent() {
        // Empty constructor for Otto
    }

    public StartAlarmEvent(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmId() {
        return alarmId;
    }
}
