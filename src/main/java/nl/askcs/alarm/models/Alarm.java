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
    private int timeToCancelBeforeAlarmStarts;

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
}
