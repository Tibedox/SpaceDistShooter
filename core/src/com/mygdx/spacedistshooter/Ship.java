package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

public class Ship extends SpaceObject{
    int lifes = 3;
     boolean isAlive;

    public Ship(int nPhases) {
        type = TYPE_SHIP;
        width = height = 200;
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/10;
        this.nPhases = nPhases;
        timePhaseInterval = 40;
        isAlive = true;
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
