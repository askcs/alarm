package nl.askcs.alarm.util;

import android.util.Log;

import java.text.MessageFormat;

/**
 * A utility class that is a simple wrapper around the various static
 * log-methods of the android.utiLog.Log class. The difference with this
 * class is that its `message` parameter is a MessageFormat-aware
 * string and the TAG is automatically created.
 * <p/>
 * What this means is that instead of Android's way of logging:
 * <p/>
 * <pre>
 * Log.i("this is p1: " + p1 + ", and this is p2: " + p2);
 * </pre>
 * <p/>
 * you can do this:
 * <p/>
 * <pre>
 * Log.i("this is p1: {0}, and this is p2: {1}", p1, p2);
 * </pre>
 * <p/>
 * More info on how MessageFormat works, see:
 * http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html
 */
public class L {

    // The format of the automatically created tag.
    private static final String TAG_FORMAT = "line=%d: %s#%s";

    private static final String[] NO_ARGS = {};

    public static int v(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.v(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int v(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.v(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int d(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.d(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int d(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.d(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int i(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.i(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int i(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.i(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int w(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.w(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int w(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.w(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int e(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.e(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int e(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.e(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int wtf(String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.wtf(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int wtf(Throwable throwable, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.wtf(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    /**
     * Creates a tag from the trace of the class from which
     * the Log-call was called.
     *
     * @return a tag from the trace of the class from which
     *         the Log-call was called.
     */
    private static String createTag() {
        //return "TAG";
        try {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StackTraceElement trace = traces[4];

            return String.format(TAG_FORMAT,
                    trace.getLineNumber(),
                    trace.getClassName(),
                    trace.getMethodName()
            );
        }
        catch (Exception e) {
            // Should not happen.
            return "UNKNOWN-TAG";
        }
    }
}