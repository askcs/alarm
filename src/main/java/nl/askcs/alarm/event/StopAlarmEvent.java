package nl.askcs.alarm.event;

public class StopAlarmEvent {

    private int alarmId;

    public StopAlarmEvent(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmId() {
        return alarmId;
    }
}
