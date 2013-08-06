package nl.askcs.alarm.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model of a Helper
 */
@DatabaseTable
public class Helper {

    /**
     * ID of the helper
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Full name of the helper
     */
    @DatabaseField
    private String name;

    /**
     * Whether the helper is available or not
     */
    @DatabaseField
    private boolean available;

    /**
     * Latitude of the helper
     */
    @DatabaseField
    private double lat;

    /**
     * Longitude of the helper
     */
    @DatabaseField
    private double lon;

    /**
     * The distance the helper has to travel to reach the user
     */
    @DatabaseField
    private float distanceToUser;

    /**
     * The time it takes the helper to travel to the user
     */
    @DatabaseField
    private float travelTimeToUser;

    /**
     * Phone number of the helper
     */
    @DatabaseField
    private String phoneNumber;

    /**
     * Whether the helper can use the messaging functionality.
     */
    @DatabaseField
    private boolean canUseMessageFunctionality;

    /**
     *
     */
    @DatabaseField( foreign = true, foreignAutoRefresh = true )
    private Alarm alarm;

    public Helper() {
        // Empty constructor needed by ORMLite
    }

    public Helper(int id, String name, boolean available, double lat, double lon, float distanceToUser, float travelTimeToUser, String phoneNumber, boolean canUseMessageFunctionality) {
        this.id = id;
        this.name = name;
        this.available = available;
        this.lat = lat;
        this.lon = lon;
        this.distanceToUser = distanceToUser;
        this.travelTimeToUser = travelTimeToUser;
        this.phoneNumber = phoneNumber;
        this.canUseMessageFunctionality = canUseMessageFunctionality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(float distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public float getTravelTimeToUser() {
        return travelTimeToUser;
    }

    public void setTravelTimeToUser(float travelTimeToUser) {
        this.travelTimeToUser = travelTimeToUser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isCanUseMessageFunctionality() {
        return canUseMessageFunctionality;
    }

    public void setCanUseMessageFunctionality(boolean canUseMessageFunctionality) {
        this.canUseMessageFunctionality = canUseMessageFunctionality;
    }

    public Alarm getAlarm() {
        return this.alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
