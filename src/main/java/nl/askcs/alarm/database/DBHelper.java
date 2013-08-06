package nl.askcs.alarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nl.askcs.alarm.R;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.models.Helper;
import nl.askcs.alarm.util.L;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * A database helper extending `OrmLiteSqliteOpenHelper` used to get
 * DAO objects from to query and update the local SQLite tables.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    // The log tag.
    private static final String TAG = DBHelper.class.getName();

    // The database name.
    private static final String DATABASE_NAME = "askcs_alarm.db";

    // The database version.
    private static final int DATABASE_VERSION = 5;

    // A map acting as a cache of DAO instances.
    private Map<Class, Dao> daoMap = null;

    /**
     * Creates a new instance of this `OrmLiteSqliteOpenHelper`.
     *
     * @param context the context from which this class was instantiated.
     */
    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);

        daoMap = new HashMap<Class, Dao>();
    }

    /**
     * Creates a new database and adding some default values to it.
     *
     * @param db               the database.
     * @param connectionSource the connection source.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        L.d("onCreate");

        try {
            for (Class<?> tableClass : DatabaseConfigUtil.CLASSES) {
                L.d("creating a table for class: " + tableClass.getName());
                TableUtils.createTable(connectionSource, tableClass);
            }

        } catch (SQLException e) {
            L.e("Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when {@link this#DATABASE_VERSION} is changed.
     *
     * @param db               the database to upgrade.
     * @param connectionSource the connection source to the database.
     * @param oldVersion       the previous database version.
     * @param newVersion       the new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {

        L.d("Starting database upgrade from oldVersion ({0}) to newVersion ({1})", oldVersion, newVersion);

        switch(newVersion) {
            case 5:
                if(oldVersion >= 1 && oldVersion <= 4) {
                    try {
                        TableUtils.dropTable(connectionSource, Alarm.class, false);
                        TableUtils.dropTable(connectionSource, Helper.class, false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    L.wtf("newVersion ({0}) recognized, but upgrading from oldVersion ({1}) not supported!", newVersion, oldVersion);
                    throw new UnsupportedOperationException();
                }
                break;
            case 4:
                if(oldVersion >= 1 && oldVersion <= 3) {
                    try {
                        TableUtils.dropTable(connectionSource, Alarm.class, false);
                        TableUtils.dropTable(connectionSource, Helper.class, false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    L.wtf("newVersion ({0}) recognized, but upgrading from oldVersion ({1}) not supported!", newVersion, oldVersion);
                    throw new UnsupportedOperationException();
                }
                break;
            case 3:
                if(oldVersion == 1 || oldVersion == 2) {
                    try {
                        TableUtils.dropTable(connectionSource, Alarm.class, false);
                        TableUtils.dropTable(connectionSource, Helper.class, false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    L.wtf("newVersion ({0}) recognized, but upgrading from oldVersion ({1}) not supported!", newVersion, oldVersion);
                    throw new UnsupportedOperationException();
                }
                break;
            case 2:
                if(oldVersion == 1) {
                    try {
                        TableUtils.dropTable(connectionSource, Alarm.class, false);
                        TableUtils.dropTable(connectionSource, Helper.class, false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    L.wtf("newVersion ({0}) recognized, but upgrading from oldVersion ({1}) not supported!", newVersion, oldVersion);
                    throw new UnsupportedOperationException();
                }
                break;
            default:
                L.wtf("upgrading to newVersion ({0}) not implemented!", newVersion);
                throw new NotImplementedException();
        }

        L.d("Done upgrading database from oldVersion ({0}) to newVersion ({1})", oldVersion, newVersion);
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

        L.d("retrieving dao: Dao<" + modelClass + ", " + idClass + ">");

        Dao<M, I> dao = daoMap.get(modelClass);

        if (dao == null) {

            try {
                // Create a new DAO.
                dao = getDao(modelClass);

                // Put the DAO in the cached map.
                daoMap.put(modelClass, dao);

            } catch (SQLException e) {
                L.e(e, "Could not create a DAO for: " + modelClass);
            }
        }

        return dao;
    }

    /**
     * Closes the connection to the database.
     */
    @Override
    public void close() {
        super.close();
    }
}