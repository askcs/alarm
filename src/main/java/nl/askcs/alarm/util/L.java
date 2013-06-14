package nl.askcs.alarm.util;

import android.util.Log;

import java.text.MessageFormat;

/**
 * A utility class that is a simple wrapper around the various static
 * log-methods of the android.util.Log class. The only difference with
 * this class is that its `message` parameter is a MessageFormat-aware
 * string. What this means is that instead of Android's way of logging:
 *
 * <pre>
 * Log.i(TAG, "this is p1: " + p1 + ", and this is p2: " + p2);
 * </pre>
 *
 * you can do this:
 *
 * <pre>
 * Log.i(TAG, "this is p1: {0}, and this is p2: {1}", p1, p2);
 * </pre>
 *
 * More info on how MessageFormat works, see:
 * http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html
 */
public class L {

    /**
     * Logs `verbose` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int v(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.v(tag, form.format(args));
    }

    /**
     * Logs `debug` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int d(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.d(tag, form.format(args));
    }

    /**
     * Logs `info` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int i(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.i(tag, form.format(args));
    }

    /**
     * Logs `warn` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int w(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.w(tag, form.format(args));
    }

    /**
     * Logs `error` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int e(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.e(tag, form.format(args));
    }

    /**
     * Logs `error` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int e(String tag, String message, Throwable throwable, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.e(tag, form.format(args), throwable);
    }

    /**
     * Logs `what-a-terrible-failure` messages.
     *
     * @param tag     the log TAG, or source, of the log message.
     * @param message the java.util.MessageFormat-aware string.
     * @param args    the arguments of the MessageFormat-aware
     *                string, `message`.
     * @return the number of bytes written.
     */
    public static int wtf(String tag, String message, Object... args) {
        MessageFormat form = new MessageFormat(message);
        return Log.wtf(tag, form.format(args));
    }
}