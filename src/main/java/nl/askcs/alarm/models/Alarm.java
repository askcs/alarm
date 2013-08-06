package nl.askcs.alarm.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model of an Alarm.
 */
@DatabaseTable
public class Alarm {

    /**
     * ID of the alarm
     */
    @DatabaseField(generatedId = true)
    private int id;
    /**
     * Title set by the user
     */
    @DatabaseField
    private String title;
    /**
     * Whether this alarm is active
     */
    @DatabaseField
    private boolean isEnabled;
    /**
     * Characteristics for the given instructions for a volunteer helper.
     */
    @DatabaseField
    private String characteristics;
    /**
     * Instructions for the given characteristics for a volunteer helper
     */
    @DatabaseField
    private String instructions;
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
     *
     */
    @DatabaseField
    private String actionContactName;
    /**
     *
     */
    @DatabaseField
    private String actionContactPhoneNumber;
    /**
     * For all referenced helpers
     */
    @ForeignCollectionField(eager = true)
    private ForeignCollection<Helper> helpers;

    public Alarm() {
        // Empty constructor needed by ORMLite
    }

    public Alarm(int id, String title, boolean enabled, String characteristics, String instructions,
                 boolean enterPinToCancel, int countdownLengthBeforeAlarmStarts, String actionContactName,
                 String actionContactPhoneNumber, ForeignCollection<Helper> helpers) {
        this.id = id;
        this.title = title;
        this.isEnabled = enabled;
        this.characteristics = characteristics;
        this.instructions = instructions;
        this.enterPinToCancel = enterPinToCancel;
        this.countdownLengthBeforeAlarmStarts = countdownLengthBeforeAlarmStarts;
        this.actionContactName = actionContactName;
        this.actionContactPhoneNumber = actionContactPhoneNumber;
        this.helpers = helpers;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    public String getActionContactName() {
        return actionContactName;
    }

    public void setActionContactName(String actionContactName) {
        this.actionContactName = actionContactName;
    }

    public String getActionContactPhoneNumber() {
        return actionContactPhoneNumber;
    }

    public void setActionContactPhoneNumber(String actionContactPhoneNumber) {
        this.actionContactPhoneNumber = actionContactPhoneNumber;
    }

    public ForeignCollection<Helper> getHelpers() {
        return helpers;
    }

    public void setHelpers(ForeignCollection<Helper> helpers) {
        this.helpers = helpers;
    }

    public int getHelperCount() {
        return this.helpers == null ? 0 : this.helpers.size();

    }
}
