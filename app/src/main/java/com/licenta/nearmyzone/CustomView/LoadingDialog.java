package com.licenta.nearmyzone.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.licenta.nearmyzone.R;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class LoadingDialog {

    private Dialog dialog;
    private Context context;
    private String title;
    private ProgressBar progressBar;


    public LoadingDialog(Context context, String title) {
        this.context = context;
        this.title = title;
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView desc = (TextView) dialog.findViewById(R.id.loading_dialog_view_title);
        desc.setText(title);
        progressBar = (ProgressBar) dialog.findViewById(R.id.loading_dialog_view_progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF691216,
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }


    public void showLoadingDialog() {
        try {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissLoadingDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog = null;
                }
            }
        }
    }

}