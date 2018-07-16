package com.citu.litenote.utils;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.citu.litenote.R;

/**
 * Created by shemchavez on 3/13/2018.
 */

public class SnackBarUtilities {

    public static void showSnackBarShort(Context context, ViewGroup viewGroup, boolean success, String message) {
        Snackbar snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBarView.setBackgroundColor(context.getColor(success ? R.color.materialGreen : R.color.materialRed));
        } else {
            snackBarView.setBackgroundColor(context.getResources().getColor(success ? R.color.materialGreen : R.color.materialRed));
        }
        snackbar.show();
    }
}
