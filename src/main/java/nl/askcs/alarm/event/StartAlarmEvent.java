package nl.askcs.alarm.event;

public class StartAlarmEvent {

    private int alarmId;

    public StartAlarmEvent(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmId() {
        return alarmId;
    }
}
