package com.mygdx.spacedistshooter;

import static com.mygdx.spacedistshooter.SpaceDistShooter.*;

public class Stars extends SpaceObject {
    public Stars(float y) {
        width = SCR_WIDTH;
        height = SCR_HEIGHT;
        x = 0;
        this.y = y;
        vx = 0;
        vy = -2;
    }

    @Override
    void move() {
        super.move();
        outOfScreen();
    }

    void outOfScreen(){
        if (y < -SCR_HEIGHT) y = SCR_HEIGHT;
    }
}
