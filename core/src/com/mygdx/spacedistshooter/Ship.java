package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;

import com.badlogic.gdx.utils.TimeUtils;

public class Ship extends SpaceObject{
    int phase, nPhases;
    long timeLastPhase, timePhaseInterval = 40;

    public Ship() {
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/10;
        width = 250;
        height = 250;
        nPhases = 7;
    }

    @Override
    void move() {
        super.move();
        changePhase();
    }

    void changePhase(){
        if(TimeUtils.millis() > timeLastPhase+timePhaseInterval) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
    }

}
