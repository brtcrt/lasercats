package com.lasercats.GameObjects;

import java.util.ArrayList;

public interface Detector {
    public void setActivatables(ArrayList<Activatable> activatables);
    public boolean isTriggered();
    public void addActivatable(Activatable a);
}
