package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;
import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_WIDTH;

import com.badlogic.gdx.utils.TimeUtils;

public class Ship extends SpaceObject{

    public Ship(int nPhases) {
        width = height = 200;
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/10;
        this.nPhases = nPhases;
        timePhaseInterval = 40;
    }

    @Override
    void move() {
        super.move();
        changePhase();
        outOfScreen();
    }

    void outOfScreen(){
        if (x < width/2){
            vx = 0;
            x = width/2;
        }
        if (x > SCR_WIDTH-width/2) {
            vx = 0;
            x = SCR_WIDTH-width/2;
        }
    }

    void touch(float touchX){
        vx = (touchX - x) / 20;
    }
}
