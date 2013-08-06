package nl.askcs.alarm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import nl.askcs.alarm.R;
import nl.askcs.alarm.database.DBHelper;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.activity.AlarmActivity;
import nl.askcs.alarm.util.L;

import java.sql.SQLException;

public class ActiveAlarmService extends Service {

    private static final String TAG = ActiveAlarmService.class.getName();

    private static final int SERVICE_ID = 74939;

    private NotificationManager notificationManager = null;

    private boolean isRunning = false;

    private DBHelper dbHelper = null;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    // Not used: We're using Otto to pass events around
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Only do this if we're not yet running.
        if (!isRunning) {

            L.d("starting service");

            try {
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = getInitNotification();
                super.startForeground(SERVICE_ID, notification);

                BusProvider.getBus().register(this);

                isRunning = true;

                //startScheduledWork();

            } catch (Exception e) {
                L.w("something went wrong while starting on foreground: ", e);
            }
        }

        return START_STICKY;
    }

    private Notification getInitNotification() {
        if (notificationManager == null) {
            L.w("notificationManager == null");
            return null;
        }

        String alarmName = "unknown";

        try {
            Dao<Alarm, Integer> dao = getDao(Alarm.class, Integer.class);
            Alarm alarm = dao.queryForId(1);
            alarmName = alarm.getTitle();
        } catch (SQLException e) {
            L.e("Could not retrieve USER_KEY from local DB: ", e);
        }

        Notification notification = this.createNotification(
                R.drawable.ic_launcher,
                getString(R.string.app_name),
                "Actief alarm: " + alarmName,
                false);

        notificationManager.notify(SERVICE_ID, notification);

        return notification;
    }

    /*
     * Creates a new Notification.
     *
     * @param icon       the icon the Notification should display.
     * @param title      the title of the Notification.
     * @param message    the message of the Notification.
     * @param autoCancel indicates if the Notification is to be cancelled when pressed.
     * @return a new Notification.
     */
    private Notification createNotification(int icon, String title, String message, boolean autoCancel) {

        if (notificationManager == null) {
            L.w("notificationManager == null");
            return null;
        }

        Intent intent = new Intent(this, AlarmActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(autoCancel)
                .setContentIntent(pendingIntent)
                .getNotification();
    }

    /**
     * Returns a DAO instance for a certain model class.
     *
     * @param modelClass the model class to create the DAO for.
     * @param idClass    the unique ID of the model.
     * @param <M>        the generic type of the model.
     * @param <I>        the generic type of the id.
     * @return a DAO instance for a certain model class.
     */
    @SuppressWarnings("unchecked")
    public <M, I> Dao<M, I> getDao(Class<M> modelClass, Class<I> idClass) {

        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);

        return dbHelper.getDao(modelClass, idClass);
    }
}
