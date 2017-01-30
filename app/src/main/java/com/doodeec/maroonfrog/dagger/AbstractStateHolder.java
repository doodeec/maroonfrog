package com.doodeec.maroonfrog.dagger;

import android.os.Bundle;

/**
 * Abstract State holder which can be used i.e. for handling scoped state in fragments during orientation/configuration change
 *
 * @author Dusan Bartos
 */
public abstract class AbstractStateHolder {

    /**
     * Dispatches fragment lifecycle related state events to this holder
     * called from {@link com.doodeec.maroonfrog.base.InjectableMvpFragment}
     *
     * Here you can implement your own logic of saving the state bundle
     */
    public abstract void onSaveState(Bundle instanceState);

    /**
     * Dispatches fragment lifecycle related state events to this holder
     * called from {@link com.doodeec.maroonfrog.base.InjectableMvpFragment}
     *
     * Here you can implement your own logic of restoring the state bundle
     */
    public abstract void onRestoreState(Bundle instanceState);
}
