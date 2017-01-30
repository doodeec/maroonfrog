package com.doodeec.maroonfrog.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Dusan Bartos
 */
public class HorizontalDividerDecoration extends RecyclerView.ItemDecoration {

    private int inset;

    public HorizontalDividerDecoration(Context context, @DimenRes int insets) {
        inset = context.getResources().getDimensionPixelSize(insets);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        outRect.set(0, inset, 0, inset);
        outRect.set(inset, 0, inset, 0);
    }
}