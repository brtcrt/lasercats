package com.lasercats.GameObjects;

public interface Activatable {
    public void activate();
    public void deactivate();
    public int getActivationCount();
}
