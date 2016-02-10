package com.cerebellio.noted;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Sam on 09/02/2016.
 */
public class ApplicationNoted extends Application {

    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

}
