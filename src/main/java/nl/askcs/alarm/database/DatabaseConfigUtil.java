package nl.askcs.alarm.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.models.Helper;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * A utility class to create ORMLite config files.
 * <p/>
 * For reference, the following types can be used in the model classes:
 * <p/>
 * ORMLite             | Java
 * --------------------+---------------
 * STRING              | String
 * LONG_STRING         | String
 * BOOLEAN             | boolean
 * DATE                | java.util.Date
 * CHAR                | char
 * BYTE                | byte
 * BYTE_ARRAY          | byte[]
 * SHORT               | short
 * INTEGER             | int
 * LONG                | long
 * FLOAT               | float
 * DOUBLE              | double
 * SERIALIZABLE        | Serializable
 * BIG_DECIMAL_NUMERIC | BigDecimal
 * <p/>
 * http://ormlite.com/data_types.shtml
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    // The logger. We cannot use Android's Log class since this is a standalone command line app.
    private static final Logger logger = Logger.getLogger(DatabaseConfigUtil.class.getName());

    /**
     * The name of the generated ORMLite config file.
     */
    public static final String CONFIG_FILE_NAME = "ormlite_config.txt";

    /**
     * An array of Class-es which will be stored in the local DB.
     */
    public static final Class<?>[] CLASSES = new Class[]{
            Alarm.class,
            Helper.class
    };

    /**
     * A main method that needs to be executed when a new model class is
     * introduced to the code base.
     *
     * @param args command line parameters (which are ignored).
     * @throws IOException  when the config file cannot be written to `res/raw/`.
     * @throws SQLException when one of the Class-es in `CLASSES` contains invalid
     *                      SQL annotations.
     */
    public static void main(String[] args) throws IOException, SQLException {

        File rawFolder = new File("res/raw");

        // Check is `res/raw` exists ...
        if (!rawFolder.exists()) {

            // ... if not create it.
            boolean rawCreated = rawFolder.mkdirs();

            if (!rawCreated) {
                logger.warning("could not create a 'raw' folder inside 'res/'" +
                        " from DatabaseConfigUtil: no DB-config file created!");
                System.exit(1);
            } else {
                logger.info("created folder `res/raw/`");
            }
        }

        writeConfigFile(CONFIG_FILE_NAME, CLASSES);
    }
}