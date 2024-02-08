package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.SCR_HEIGHT;

public class Shot extends SpaceObject{
    public Shot(Ship ship) {
        width = height = ship.width;
        x = ship.x;
        y = ship.y;
        vy = 8f;
    }

    boolean outOfScreen(){
        return y > SCR_HEIGHT+height/2;
    }
}
