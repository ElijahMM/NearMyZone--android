package com.licenta.nearmyzone.CustomView;

import android.app.Dialog;
import android.content.Context;

import com.licenta.nearmyzone.R;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class ChoosePopup {

    private Dialog dialog;
    private Context context;

    private Boolean isInit = false;

    public ChoosePopup(Context context) {
        this.context = context;

    }

    public void init() {
        dialog = new Dialog(context, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.choose_popup_layout);
        dialog.setCanceledOnTouchOutside(true);
        isInit = true;
    }


    public void showPopup() {
        try {
            if (!isInit) {
                throw new Exception("First Call init");
            }
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog() {
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
