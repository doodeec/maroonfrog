package com.doodeec.maroonfrog.util;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.doodeec.maroonfrog.base.Layout;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 */
public final class Util {

    private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

    //GC sometimes does not release image references soon enough before OOM exception,
    //so this is a little helper which eliminates OOM a bit
    private static final int GC_HANDLER_DEBOUNCE = 1000;
    private static Handler gcHandler = new Handler();
    private static Runnable gcHandlerTask = () -> {
        Timber.v("Trying to free unused references | calling GC");
        // this is just a gentle reminder that it would be nice to trigger GC, it won't
        // necessarily run right away
        System.gc();
    };

    private static DisplayMetrics displayMetrics;

    private static int getAnnotationValue(Object o, final Map<Class, Integer> cache, Class<?> clazz) {
        final Class pathType = o.getClass();
        Integer res = cache.get(pathType);
        if (res == null) {
            final Annotation annotation = pathType.getAnnotation(clazz);
            if (annotation != null) {
                try {
                    final Method m = annotation.getClass().getDeclaredMethod("value", (Class[]) null);
                    res = (Integer) m.invoke(annotation, (Object[]) null);
                    cache.put(pathType, res);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return res != null ? res : -1;
    }

    /**
     * Reads {@link Layout} annotation value of given object
     *
     * @return layout resource if found, else -1
     */
    public static int getLayoutRes(Object o) {
        return getAnnotationValue(o, PATH_LAYOUT_CACHE, Layout.class);
    }

    private static int maxWidth(Context context) {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(displayMetrics);
        }

        return displayMetrics.widthPixels;
    }

    /**
     * Shorthand for attaching image from url to target view
     *
     * @param target      target view
     * @param sourceUrl   image url
     * @param placeholder placeholder drawable
     */
    public static void setImage(ImageView target, String sourceUrl, @DrawableRes int placeholder) {
        if (target == null) {
            Timber.e("Cannot attach image to empty target");
            return;
        }

        Picasso.with(target.getContext()).cancelRequest(target);

        final int targetPlaceholder = placeholder == -1 ? android.R.color.transparent : placeholder;
        Timber.d("Starting image loading: %s", sourceUrl);
        Picasso.with(target.getContext())
                .load(sourceUrl)
                .placeholder(targetPlaceholder)
                .fit()
                .into(target);
    }

    /**
     * Releases reference to the image/icon used in ImageView
     * Internally initializes debounced GC event and cancels all pending Picasso requests for this
     * target
     *
     * @param target view
     */
    public static void releaseImage(ImageView target) {
        if (target == null) return;
        Picasso.with(target.getContext()).cancelRequest(target);
        target.setImageDrawable(null);
        target.setTag(null);
        // try to free remaining bitmap references
        // this is for the purpose of preventing OOM errors
        // when images are released for GC, they are sometimes not collected soon enough to free
        // memory, and they accumulate a lot of memory until the next GC event will occur
        initGc();
    }

    private static void initGc() {
        gcHandler.removeCallbacks(gcHandlerTask);
        gcHandler.postDelayed(gcHandlerTask, GC_HANDLER_DEBOUNCE);
    }

    public static String formatPrice(double price) {
        return String.format(Locale.ENGLISH, "%.2f €", price);
    }

    /**
     * Method for adapting text size by the length of the value
     * If value is longer than threshold, smaller text size than default will be applied
     * @param textView view
     * @param threshold number of characters to determine default/small text size
     * @param defaultSize default text size
     * @param smallSize small text size
     */
    public static void adaptTextSize(TextView textView, int threshold,
                                     @DimenRes int defaultSize, @DimenRes int smallSize) {
        if (textView.getText().length() > threshold) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getResources().getDimensionPixelSize(smallSize));
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getResources().getDimensionPixelSize(defaultSize));
        }
    }
}
