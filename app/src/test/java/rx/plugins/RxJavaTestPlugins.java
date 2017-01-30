package rx.plugins;

/**
 * @author Dusan Bartos
 */
public final class RxJavaTestPlugins extends RxJavaPlugins {

    public static void resetPlugins(){
        getInstance().reset();
    }
}
