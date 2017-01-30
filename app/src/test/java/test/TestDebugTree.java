package test;

import android.util.Log;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class TestDebugTree extends Timber.DebugTree {

    @Override protected void log(int priority, String tag, String message, Throwable t) {
//        super.log(priority, tag, message, t);
        if (t != null) {
            System.out.println("Exception: " + t.toString() + " - " + t.getMessage());
        }
        System.out.println(stringifyPriority(priority) + ": " + tag + "|" + message);
    }

    private String stringifyPriority(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return "V";

            case Log.DEBUG:
                return "D";

            case Log.INFO:
                return "I";

            case Log.WARN:
                return "W";

            case Log.ERROR:
                return "E";

            default:
                return "U";
        }
    }
}
