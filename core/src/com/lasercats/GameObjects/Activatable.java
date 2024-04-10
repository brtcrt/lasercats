package com.lasercats.GameObjects;

import java.util.ArrayList;

public interface Activatable {
    public void setDetectors(ArrayList<Detector> detectors);
    public void activate();
    public void deactivate();
}
