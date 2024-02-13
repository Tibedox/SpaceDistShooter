package com.mygdx.spacedistshooter;

import com.badlogic.gdx.utils.TimeUtils;

public class SpaceObject {
    int type;
    float x, y;
    float vx, vy;
    float width, height;
    int phase, nPhases;
    long timeLastPhase, timePhaseInterval;

    void move() {
        x += vx;
        y += vy;
    }

    void changePhase(){
        if(TimeUtils.millis() > timeLastPhase+timePhaseInterval) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
    }

    float getX(){
        return x-width/2;
    }

    float getY(){
        return y-height/2;
    }

    boolean overlap(SpaceObject o){
        return Math.abs(x-o.x) < width/3 + o.width/2 & Math.abs(y-o.y) < height/3 + o.height/3;
    }
}
