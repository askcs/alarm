package nl.askcs.alarm.event;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import nl.askcs.alarm.util.L;

/**
 * Maintains a singleton instance for obtaining the event bus over
 * which messages are passed from UI components (such as Activities
 * and Fragments) to Services, and back.
 */
public final class BusProvider {

    // The singleton of the Bus instance which can be used from
    // any thread in the app.
    private static final Bus BUS = new Bus() {

        private final Handler mainThread = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {

            if (Looper.myLooper() == Looper.getMainLooper()) {
                L.i("BusProvider", " a firedEvent: {0}", event.getClass().getName());
                super.post(event);
            } else {
                L.i("BusProvider", " b firedEvent: {0}", event.getClass().getName());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        post(event);
                    }
                });
            }
        }
    };

    /**
     * Returns a singleton instance for obtaining the event bus over
     * which messages are passed from UI components to Services, and
     * back.
     *
     * @return a singleton instance for obtaining the event bus over
     *         which messages are passed from UI components to Services, and
     *         back.
     */
    public static Bus getBus() {
        return BUS;
    }

    // No need to instantiate this class.
    private BusProvider() {
    }
}