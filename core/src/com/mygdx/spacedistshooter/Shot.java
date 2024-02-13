package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;

public class Shot extends SpaceObject{

    public Shot(float x, float y) {
        width = height = 200;
        this.x = x;
        this.y = y;
        vy = 8f;
    }

    boolean outOfScreen(){
        return y > SCR_HEIGHT+height/2;
    }
}
