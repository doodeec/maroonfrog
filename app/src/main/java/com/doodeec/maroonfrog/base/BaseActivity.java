package com.doodeec.maroonfrog.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.doodeec.maroonfrog.R;
import com.doodeec.maroonfrog.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Dusan Bartos
 */
public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        final int layout = Util.getLayoutRes(this);
        if (layout == -1) {
            throw new IllegalStateException("Cannot inflate activity without layout. " +
                    "Check if @Layout annotation is present");
        }
        setContentView(layout);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void enableToolbarBack() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
