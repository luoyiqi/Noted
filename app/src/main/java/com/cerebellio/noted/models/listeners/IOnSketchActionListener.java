package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Sketch;

/**
 * Interface which notifies when an action has been taken
 * on a {@link Sketch}
 */
public interface IOnSketchActionListener {
    void onChange();
}
