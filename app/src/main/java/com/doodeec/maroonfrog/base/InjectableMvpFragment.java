package com.doodeec.maroonfrog.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doodeec.maroonfrog.dagger.AbstractStateHolder;
import com.doodeec.maroonfrog.util.Util;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import butterknife.ButterKnife;

/**
 * @author Dusan Bartos
 */
public abstract class InjectableMvpFragment<V extends BaseMvpView, P extends RxMvpBasePresenter<V>>
        extends MvpFragment<V, P> implements BaseMvpView {

    private ProgressDialog progressDialog;
    protected AbstractStateHolder stateHolder;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int layout = Util.getLayoutRes(this);
        if (layout == -1) {
            throw new IllegalStateException("Cannot inflate fragment without layout. " +
                    "Check if @Layout annotation is present");
        }

        return inflater.inflate(layout, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        inject();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if fragment registered a stateHolder, this will dispatch the event
        if (stateHolder != null) {
            stateHolder.onSaveState(outState);
        }
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //if fragment registered a stateHolder, this will dispatch the event
        if (stateHolder != null && savedInstanceState != null) {
            stateHolder.onRestoreState(savedInstanceState);
        }
    }

    /**
     * This method fills presenter with additional bundle extra (from intent) and arguments (fragment)
     * input data
     *
     * @param presenter presenter instance, this instance will be returned right away just to make
     *                  this method chainable
     *
     * @return input instance
     */
    protected P preparePresenter(P presenter) {
        if (getActivity() != null) {
            presenter.setIntentExtra(getActivity().getIntent().getExtras());
        }
        presenter.setFragmentArgs(getArguments());
        return presenter;
    }

    public boolean onMenuItemClick(int id) {
        //if presenter can consume menu clicks, propagate this event
        if (presenter instanceof IMenuClickablePresenter) {
            return ((IMenuClickablePresenter) presenter).onMenuItemClick(id);
        }
        return false;
    }

    public void toast(@StringRes int text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    public void closeActivity() {
        getActivity().finish();
    }

    public void showLoading(int message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(getContext(), null, getString(message), true, false);
        }
    }

    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override public void setTitle(String title) {
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override public void setSubtitle(String subtitle) {
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
            }
        }
    }

    /**
     * This is the place for injecting DI stuff
     * Probably the best place to initialize scoped Dagger Component if necessary
     *
     * And a place where stateHolder can be initialized
     */
    protected abstract void inject();
}
