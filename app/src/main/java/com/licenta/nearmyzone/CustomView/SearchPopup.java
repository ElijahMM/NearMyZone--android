package com.licenta.nearmyzone.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.licenta.nearmyzone.R;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class SearchPopup {

    private Dialog dialog;
    private Context context;

    private TextView dissmissButton;
    private TextView searchButton;

    private Boolean isInit = false;

    public SearchPopup(Context context) {
        this.context = context;

    }

    public void init() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.search_popup_layout);
        dialog.setCanceledOnTouchOutside(true);
        dissmissButton = (TextView) dialog.findViewById(R.id.pop_up_search_button_dismiss);
        searchButton = (TextView) dialog.findViewById(R.id.pop_up_search_button_SendM);

        dissmissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        isInit = true;
    }

    public void setSearchClickListner(View.OnClickListener searchClickListner) {
       searchButton.setOnClickListener(searchClickListner);

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
