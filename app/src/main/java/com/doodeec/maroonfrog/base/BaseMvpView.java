package com.doodeec.maroonfrog.base;

import android.content.Intent;
import android.support.annotation.StringRes;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * @author Dusan Bartos
 */
public interface BaseMvpView extends MvpView {

    void toast(@StringRes int text);

    void toast(String text);

    void openActivity(Intent intent);

    void closeActivity();

    void showLoading(int message);

    void hideLoading();

    void setTitle(String title);

    void setSubtitle(String title);
}
