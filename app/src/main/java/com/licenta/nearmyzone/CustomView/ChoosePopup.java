package com.licenta.nearmyzone.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.licenta.nearmyzone.R;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class ChoosePopup {

    private Dialog dialog;
    private Context context;

    private Boolean isInit = false;

    private TextView hotelTextView;
    private TextView busTextView;
    private TextView trainTextView;
    private TextView restaurantTextView;

    public ChoosePopup(Context context) {
        this.context = context;

    }


    public void init() {
        dialog = new Dialog(context, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.choose_popup_layout);
        dialog.setCanceledOnTouchOutside(true);
        hotelTextView = (TextView) dialog.findViewById(R.id.pop_up_check_in_view_hotel);
        restaurantTextView = (TextView) dialog.findViewById(R.id.pop_up_check_in_view_restaurant);
        busTextView = (TextView) dialog.findViewById(R.id.pop_up_check_in_view_bus);
        trainTextView = (TextView) dialog.findViewById(R.id.pop_up_check_in_view_trani);
        isInit = true;
    }

    public void setBusClickListener(View.OnClickListener clickListener) {
        busTextView.setOnClickListener(clickListener);
    }

    public void setAtmClickListener(View.OnClickListener clickListener) {
        trainTextView.setOnClickListener(clickListener);
    }

    public void setRestaurantClickListener(View.OnClickListener clickListener) {
        restaurantTextView.setOnClickListener(clickListener);
    }

    public void setHotelClickListener(View.OnClickListener clickListener) {
        hotelTextView.setOnClickListener(clickListener);
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
