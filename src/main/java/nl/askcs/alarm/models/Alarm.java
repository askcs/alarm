package nl.askcs.alarm.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 4-6-13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class Alarm {

    /**
     * ID of the alarm
     */
    @DatabaseField( id = true)
    private int id;

    /**
     * Title set by the user
     */
    @DatabaseField
    private String title;

    /**
     * Message that will be sent to VIPs. VIPs are set per alarm by the user.
     */
    @DatabaseField
    private String messageForVIP;

    /**
     * Message that will be sent to acquaintances. An acquaintance is a potential helper.
     */
    @DatabaseField
    private String messageForAcquaintances;

    /**
     * Message that will be sent to volunteers.
     */
    @DatabaseField
    private String messageForVolunteers;

    /**
     * If the cancel button is pressed of the alarm in alarm mode, the app will ask for a PIN code to cancel the alarm.
     * This prevents someone with the wrong intentions to cancel the alarm (for example in the violence type of alarm).
     */
    @DatabaseField
    private boolean enterPinToCancel;

    /**
     * Amount of seconds the user has to cancel the alarm in case he/she triggered it by accident. The alarm will start
     * after this amount of seconds.
     */
    @DatabaseField
    private int countdownLengthBeforeAlarmStarts;

    /**
     * The amount of acquaintances to alert. This is specified by the user
     */
    @DatabaseField
    private int amountOfAcquaintances;

    /**
     * The amount of volunteers to alert. This is specified by the user
     */
    @DatabaseField
    private int amountOfVolunteers;

    public Alarm(int id, String title, String messageForVIP, String messageForAcquaintances,
                 String messageForVolunteers, boolean enterPinToCancel, int timeToCancelBeforeAlarmStarts,
                 int amountOfAcquaintances, int amountOfVolunteers) {
        this.id = id;
        this.title = title;
        this.messageForVIP = messageForVIP;
        this.messageForAcquaintances = messageForAcquaintances;
        this.messageForVolunteers = messageForVolunteers;
        this.enterPinToCancel = enterPinToCancel;
        this.countdownLengthBeforeAlarmStarts = timeToCancelBeforeAlarmStarts;
        this.amountOfAcquaintances = amountOfAcquaintances;
        this.amountOfVolunteers = amountOfVolunteers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessageForVIP() {
        return messageForVIP;
    }

    public void setMessageForVIP(String messageForVIP) {
        this.messageForVIP = messageForVIP;
    }

    public String getMessageForAcquaintances() {
        return messageForAcquaintances;
    }

    public void setMessageForAcquaintances(String messageForAcquaintances) {
        this.messageForAcquaintances = messageForAcquaintances;
    }

    public String getMessageForVolunteers() {
        return messageForVolunteers;
    }

    public void setMessageForVolunteers(String messageForVolunteers) {
        this.messageForVolunteers = messageForVolunteers;
    }

    public boolean shouldEnterPinToCancel() {
        return enterPinToCancel;
    }

    public void setEnterPinToCancel(boolean enterPinToCancel) {
        this.enterPinToCancel = enterPinToCancel;
    }

    public int getCountdownLengthBeforeAlarmStarts() {
        return countdownLengthBeforeAlarmStarts;
    }

    public void setCountdownLengthBeforeAlarmStarts(int countdownLengthBeforeAlarmStarts) {
        this.countdownLengthBeforeAlarmStarts = countdownLengthBeforeAlarmStarts;
    }

    public int getAmountOfAcquaintances() {
        return amountOfAcquaintances;
    }

    public void setAmountOfAcquaintances(int amountOfAcquaintances) {
        this.amountOfAcquaintances = amountOfAcquaintances;
    }

    public int getAmountOfVolunteers() {
        return amountOfVolunteers;
    }

    public void setAmountOfVolunteers(int amountOfVolunteers) {
        this.amountOfVolunteers = amountOfVolunteers;
    }
}
