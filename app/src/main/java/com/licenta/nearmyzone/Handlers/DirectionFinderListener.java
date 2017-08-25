package com.licenta.nearmyzone.Handlers;

import com.licenta.nearmyzone.Models.GDirection.Route;

import java.util.List;

/**
 * Created by Morgenstern on 08/25/2017.
 */

public interface DirectionFinderListener {
        void onDirectionFinderStart();
        void onDirectionFinderSuccess(List<Route> route);

}
