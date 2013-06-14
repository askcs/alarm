package nl.askcs.alarm.ui.activity;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import nl.askcs.alarm.database.DBHelper;
import nl.askcs.alarm.event.BusProvider;
import nl.askcs.alarm.util.L;

import java.sql.SQLException;

public class BaseActivity extends SherlockFragmentActivity {

    // Tag used with logging
    private final String TAG = getClass().getName();

    // The local database helper to retrieve DAO instances
    private DBHelper dbHelper = null;

    /**
     * Called when Android creates this Activity.
     *
     * @param savedInstanceState the saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        L.d(TAG, "* onCreate");
    }

    /**
     * Called when Android destroys this Activity.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        L.d(TAG, "* onDestroy");

        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
        }
    }

    /**
     * Called when Android pauses this Activity.
     */
    @Override
    protected void onPause() {

        super.onPause();

        L.d(TAG, "* onPause");

        BusProvider.getBus().unregister(this);
    }

    /**
     * Called when Android resumes this Activity.
     */
    @Override
    protected void onResume() {

        super.onResume();

        L.d(TAG, "* onResume");

        BusProvider.getBus().register(this);
    }

    // Lazily returns a DatabaseHelper.
    private DBHelper getDBHelper() {

        L.d(TAG, "* getDBHelper");

        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        }

        return dbHelper;
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
        return this.getDBHelper().getDao(modelClass, idClass);
    }

    /**
     * A utility method to retrieve the type `M` with a given `id` from the
     * local database, or `null` if an exception occurs.
     *
     * @param modelClass the model class to create the DAO for.
     * @param idClass    the unique ID of the model.
     * @param id         the identifier to query for.
     * @param <M>        the generic type of the model.
     * @param <I>        the generic type of the id.
     * @return the type `M` with a given `id` from the local database, or
     *         `null` if an exception occurs.
     */
    @SuppressWarnings("unchecked")
    public <M, I> M queryDbForId(Class<M> modelClass, Class<I> idClass, I id) {

        try {
            Dao<M, I> dao = this.getDao(modelClass, idClass);
            return dao.queryForId(id);

        } catch (SQLException e) {
            L.e(TAG, "something went wrong while querying for id={0}", id);
            return null;
        }
    }
}
